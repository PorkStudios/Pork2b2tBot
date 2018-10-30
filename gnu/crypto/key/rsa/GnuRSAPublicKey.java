/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.rsa;

import gnu.crypto.Registry;
import gnu.crypto.key.rsa.GnuRSAKey;
import gnu.crypto.key.rsa.RSAKeyPairRawCodec;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

public class GnuRSAPublicKey
extends GnuRSAKey
implements PublicKey,
RSAPublicKey {
    public static GnuRSAPublicKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_RSA_PUBLIC_KEY[0]) {
            RSAKeyPairRawCodec codec = new RSAKeyPairRawCodec();
            return (GnuRSAPublicKey)codec.decodePublicKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new RSAKeyPairRawCodec().encodePublicKey(this);
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
        if (!(obj instanceof RSAPublicKey)) {
            return false;
        }
        RSAPublicKey that = (RSAPublicKey)obj;
        boolean bl = false;
        if (super.equals(that) && this.getPublicExponent().equals(that.getPublicExponent())) {
            bl = true;
        }
        return bl;
    }

    public GnuRSAPublicKey(BigInteger n, BigInteger e) {
        super(n, e);
    }
}

