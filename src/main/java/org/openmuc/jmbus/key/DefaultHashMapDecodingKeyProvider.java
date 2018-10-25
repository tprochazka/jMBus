package org.openmuc.jmbus.key;

import org.openmuc.jmbus.SecondaryAddress;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Default key provider implementation base on hash map.
 */
public class DefaultHashMapDecodingKeyProvider implements IDecodingKeyProvider {

    final Map<SecondaryAddress, byte[]> keyMap = new HashMap<>();

    @Override
    public byte[] get(@Nonnull SecondaryAddress secondaryAddress) {
        return keyMap.get(secondaryAddress);
    }

    public void add(@Nonnull SecondaryAddress secondaryAddress, byte[] key) {
        keyMap.put(secondaryAddress, key);
    }

    public void remove(@Nonnull SecondaryAddress secondaryAddress) {
        keyMap.remove(secondaryAddress);
    }

}
