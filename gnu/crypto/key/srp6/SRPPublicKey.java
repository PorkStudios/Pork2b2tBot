/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.Registry;
import gnu.crypto.key.srp6.SRPAlgorithm;
import gnu.crypto.key.srp6.SRPKey;
import gnu.crypto.key.srp6.SRPKeyPairRawCodec;
import java.math.BigInteger;
import java.security.PublicKey;

public class SRPPublicKey
extends SRPKey
implements PublicKey {
    private final BigInteger Y;

    public static SRPPublicKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_SRP_PUBLIC_KEY[0]) {
            SRPKeyPairRawCodec codec = new SRPKeyPairRawCodec();
            return (SRPPublicKey)codec.decodePublicKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public BigInteger getY() {
        return this.Y;
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new SRPKeyPairRawCodec().encodePublicKey(this);
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
        if (!(obj instanceof SRPPublicKey)) {
            return false;
        }
        SRPPublicKey that = (SRPPublicKey)obj;
        boolean bl = false;
        if (super.equals(that) && this.Y.equals(that.getY())) {
            bl = true;
        }
        return bl;
    }

    public SRPPublicKey(BigInteger N, BigInteger g, BigInteger Y) {
        super(N, g);
        SRPAlgorithm.checkParams(N, g);
        this.Y = Y;
    }

    SRPPublicKey(BigInteger[] params) {
        super(params[0], params[1]);
        this.Y = params[2];
    }
}

