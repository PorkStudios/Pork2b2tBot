/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.mac;

import gnu.crypto.jce.mac.MacAdapter;
import gnu.crypto.jce.spec.TMMHParameterSpec;
import gnu.crypto.mac.IMac;
import gnu.crypto.prng.IRandom;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;

public final class TMMH16Spi
extends MacAdapter {
    protected final void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!(params instanceof TMMHParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        TMMHParameterSpec spec = (TMMHParameterSpec)params;
        this.attributes.put("gnu.crypto.mac.tmmh.tag.length", spec.getTagLength());
        this.attributes.put("gnu.crypto.mac.tmmh.keystream", spec.getKeystream());
        this.attributes.put("gnu.crypto.mac.tmmh.prefix", spec.getPrefix());
        try {
            this.mac.reset();
            this.mac.init(this.attributes);
        }
        catch (IllegalArgumentException iae) {
            throw new InvalidAlgorithmParameterException(iae.getMessage());
        }
    }

    public TMMH16Spi() {
        super("tmmh16");
    }
}

