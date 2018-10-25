package org.openmuc.jmbus.key;

import org.openmuc.jmbus.SecondaryAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Decoding key provider based on {@link SecondaryAddress}.
 */
public interface IDecodingKeyProvider {

    @Nullable
    byte[] get(@Nonnull SecondaryAddress secondaryAddress);

}
