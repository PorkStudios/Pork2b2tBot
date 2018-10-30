/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.Registry;
import gnu.crypto.key.srp6.SRPAlgorithm;
import gnu.crypto.key.srp6.SRPKey;
import gnu.crypto.key.srp6.SRPKeyPairRawCodec;
import java.math.BigInteger;
import java.security.PrivateKey;

public class SRPPrivateKey
extends SRPKey
implements PrivateKey {
    private final BigInteger X;
    private final BigInteger v;

    public static SRPPrivateKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_SRP_PRIVATE_KEY[0]) {
            SRPKeyPairRawCodec codec = new SRPKeyPairRawCodec();
            return (SRPPrivateKey)codec.decodePrivateKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public BigInteger getX() {
        return this.X;
    }

    public BigInteger getV() {
        return this.v;
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new SRPKeyPairRawCodec().encodePrivateKey(this);
                break;
            }
            default: {
                throw new IllegalArgumentException("format");
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        boolean result;
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SRPPrivateKey)) {
            return false;
        }
        SRPPrivateKey that = (SRPPrivateKey)obj;
        boolean bl = false;
        if (super.equals(that) && this.X.equals(that.getX())) {
            bl = result = true;
        }
        if (this.v != null) {
            boolean bl2 = false;
            if (result && this.v.equals(that.getV())) {
                bl2 = true;
            }
            result = bl2;
        }
        return result;
    }

    public SRPPrivateKey(BigInteger N, BigInteger g, BigInteger x) {
        this(N, g, x, null);
    }

    public SRPPrivateKey(BigInteger N, BigInteger g, BigInteger x, BigInteger v) {
        super(N, g);
        SRPAlgorithm.checkParams(N, g);
        this.X = x;
        this.v = v;
    }

    SRPPrivateKey(BigInteger[] params) {
        super(params[0], params[1]);
        if (params.length == 3) {
            this.X = params[2];
            this.v = null;
        } else if (params.length == 4) {
            this.X = params[2];
            this.v = params[3];
        } else {
            throw new IllegalArgumentException("invalid number of SRP parameters");
        }
    }
}

