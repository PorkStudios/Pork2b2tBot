/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dss;

import gnu.crypto.Registry;
import gnu.crypto.key.dss.DSSKey;
import gnu.crypto.key.dss.DSSKeyPairRawCodec;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.interfaces.DSAPrivateKey;

public class DSSPrivateKey
extends DSSKey
implements PrivateKey,
DSAPrivateKey {
    private final BigInteger x;

    public static DSSPrivateKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_DSS_PRIVATE_KEY[0]) {
            DSSKeyPairRawCodec codec = new DSSKeyPairRawCodec();
            return (DSSPrivateKey)codec.decodePrivateKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public BigInteger getX() {
        return this.x;
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new DSSKeyPairRawCodec().encodePrivateKey(this);
                break;
            }
            default: {
                throw new IllegalArgumentException("format");
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DSAPrivateKey)) {
            return false;
        }
        DSAPrivateKey that = (DSAPrivateKey)obj;
        boolean bl = false;
        if (super.equals(that) && this.x.equals(that.getX())) {
            bl = true;
        }
        return bl;
    }

    public DSSPrivateKey(BigInteger p, BigInteger q, BigInteger g, BigInteger x) {
        super(p, q, g);
        this.x = x;
    }
}

