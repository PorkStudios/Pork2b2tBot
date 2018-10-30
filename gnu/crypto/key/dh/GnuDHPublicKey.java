/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.Registry;
import gnu.crypto.key.dh.DHKeyPairRawCodec;
import gnu.crypto.key.dh.GnuDHKey;
import java.math.BigInteger;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPublicKey;

public class GnuDHPublicKey
extends GnuDHKey
implements DHPublicKey {
    private BigInteger y;

    public static GnuDHPublicKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_DH_PUBLIC_KEY[0]) {
            DHKeyPairRawCodec codec = new DHKeyPairRawCodec();
            return (GnuDHPublicKey)codec.decodePublicKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public byte[] getEncoded() {
        return this.getEncoded(1);
    }

    public BigInteger getY() {
        return this.y;
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new DHKeyPairRawCodec().encodePublicKey(this);
                break;
            }
            default: {
                throw new IllegalArgumentException("format");
            }
        }
        return result;
    }

    public GnuDHPublicKey(BigInteger q, BigInteger p, BigInteger g, BigInteger y) {
        super(q, p, g);
        this.y = y;
    }
}

