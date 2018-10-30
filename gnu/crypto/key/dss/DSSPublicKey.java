/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dss;

import gnu.crypto.Registry;
import gnu.crypto.key.dss.DSSKey;
import gnu.crypto.key.dss.DSSKeyPairRawCodec;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;

public class DSSPublicKey
extends DSSKey
implements PublicKey,
DSAPublicKey {
    private final BigInteger y;

    public static DSSPublicKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_DSS_PUBLIC_KEY[0]) {
            DSSKeyPairRawCodec codec = new DSSKeyPairRawCodec();
            return (DSSPublicKey)codec.decodePublicKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public BigInteger getY() {
        return this.y;
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new DSSKeyPairRawCodec().encodePublicKey(this);
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
        if (!(obj instanceof DSAPublicKey)) {
            return false;
        }
        DSAPublicKey that = (DSAPublicKey)obj;
        boolean bl = false;
        if (super.equals(that) && this.y.equals(that.getY())) {
            bl = true;
        }
        return bl;
    }

    public DSSPublicKey(BigInteger p, BigInteger q, BigInteger g, BigInteger y) {
        super(p, q, g);
        this.y = y;
    }
}

