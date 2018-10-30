/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.mac;

import gnu.crypto.jce.mac.MacAdapter;
import gnu.crypto.jce.spec.UMac32ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;

public final class UMac32Spi
extends MacAdapter {
    protected final void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!(params instanceof UMac32ParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        if (params != null) {
            this.attributes.put("gnu.crypto.umac.nonce.material", ((UMac32ParameterSpec)params).getNonce());
        }
        try {
            super.engineInit(key, null);
        }
        catch (IllegalArgumentException iae) {
            throw new InvalidAlgorithmParameterException(iae.getMessage());
        }
    }

    public UMac32Spi() {
        super("umac32");
    }
}

