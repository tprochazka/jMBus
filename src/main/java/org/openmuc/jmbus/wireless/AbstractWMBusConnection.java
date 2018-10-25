/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus.wireless;

import org.openmuc.jmbus.SecondaryAddress;
import org.openmuc.jmbus.key.DefaultHashMapDecodingKeyProvider;
import org.openmuc.jmbus.key.IDecodingKeyProvider;
import org.openmuc.jmbus.transportlayer.TransportLayer;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract class AbstractWMBusConnection implements WMBusConnection {

    private static final int ACK = 0x3E;

    protected static final int BUFFER_LENGTH = 1000;
    protected static final int MESSAGE_FRAGEMENT_TIMEOUT = 1000;

    private TransportLayer transportLayer;

    private final WMBusMode mode;
    private final WMBusListener listener;

    IDecodingKeyProvider keyProvider = new DefaultHashMapDecodingKeyProvider();

    private volatile boolean closed;
    private final ExecutorService receiverService;

    protected AbstractWMBusConnection(WMBusMode mode, WMBusListener listener, TransportLayer tl) {
        this.listener = listener;
        this.mode = mode;
        this.transportLayer = tl;

        this.closed = true;
        this.receiverService = Executors.newSingleThreadExecutor();
    }

    @Override
    public final void close() {
        if (this.transportLayer == null || this.closed) {
            // nothing to do
            return;
        }

        try {
            this.receiverService.shutdown();
            this.transportLayer.close();
        } finally {
            this.transportLayer = null;
            this.closed = true;
        }
    }

    public final void setCustomDecryptionKeyProvider(@Nonnull IDecodingKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public final void addKey(@Nonnull SecondaryAddress address, byte[] key) {
        if (keyProvider instanceof DefaultHashMapDecodingKeyProvider) {
            ((DefaultHashMapDecodingKeyProvider)keyProvider).add(address, key);
        } else {
            throw new IllegalStateException("addKey() method can be used only with default implementation of key provider.");
        }
    }

    @Override
    public final void removeKey(@Nonnull SecondaryAddress address) {
        if (keyProvider instanceof DefaultHashMapDecodingKeyProvider) {
            ((DefaultHashMapDecodingKeyProvider)keyProvider).remove(address);
        } else {
            throw new IllegalStateException("removeKey() method can be used only with default implementation of key provider.");
        }
    }

    public final void open() throws IOException {
        if (!closed) {
            return;
        }
        try {
            transportLayer.open();

            initializeWirelessTransceiver(mode);

        } catch (IOException e) {
            transportLayer.close();

            throw e;
        }
        this.receiverService.execute(newMessageReceiver(this.transportLayer, this.listener));

        this.closed = false;
    }

    protected abstract MessageReceiver newMessageReceiver(TransportLayer transportLayer, WMBusListener listener);

    protected abstract void initializeWirelessTransceiver(WMBusMode mode) throws IOException;

    protected boolean isClosed() {
        return closed;
    }

    protected DataInputStream getInputStream() {
        return this.transportLayer.getInputStream();
    }

    protected DataOutputStream getOutputStream() {
        return this.transportLayer.getOutputStream();
    }

    protected long discardNoise() throws IOException {
        DataInputStream inputStream = getInputStream();

        if (inputStream.available() == 0) {
            return 0;
        }

        transportLayer.setTimeout(MESSAGE_FRAGEMENT_TIMEOUT);
        try {
            return inputStream.skip(500);

        } catch (InterruptedIOException e) {
            // ignore
            return 0;
        }
    }

    protected void waitForAck() throws IOException {
        transportLayer.setTimeout(MESSAGE_FRAGEMENT_TIMEOUT);

        int b = getInputStream().read();
        if (b != ACK) {
            throw new IOException(String.format("Did not receive ACK. Received 0x%02X instead.", b));
        }
    }

}
