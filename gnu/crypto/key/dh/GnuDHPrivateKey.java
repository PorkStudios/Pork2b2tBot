/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.Registry;
import gnu.crypto.key.dh.DHKeyPairRawCodec;
import gnu.crypto.key.dh.GnuDHKey;
import java.math.BigInteger;
import java.security.PrivateKey;
import javax.crypto.interfaces.DHPrivateKey;

public class GnuDHPrivateKey
extends GnuDHKey
implements DHPrivateKey {
    private final BigInteger x;

    public static GnuDHPrivateKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_DH_PRIVATE_KEY[0]) {
            DHKeyPairRawCodec codec = new DHKeyPairRawCodec();
            return (GnuDHPrivateKey)codec.decodePrivateKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public byte[] getEncoded() {
        return this.getEncoded(1);
    }

    public BigInteger getX() {
        return this.x;
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new DHKeyPairRawCodec().encodePrivateKey(this);
                break;
            }
            default: {
                throw new IllegalArgumentException("format");
            }
        }
        return result;
    }

    public GnuDHPrivateKey(BigInteger q, BigInteger p, BigInteger g, BigInteger x) {
        super(q, p, g);
        this.x = x;
    }
}

