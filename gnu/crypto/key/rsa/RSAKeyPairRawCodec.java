/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.rsa;

import gnu.crypto.Registry;
import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.rsa.GnuRSAPrivateKey;
import gnu.crypto.key.rsa.GnuRSAPublicKey;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAKeyPairRawCodec
implements IKeyPairCodec {
    public int getFormatID() {
        return 1;
    }

    public byte[] encodePublicKey(PublicKey key) {
        if (!(key instanceof GnuRSAPublicKey)) {
            throw new IllegalArgumentException("key");
        }
        GnuRSAPublicKey rsaKey = (GnuRSAPublicKey)key;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Registry.MAGIC_RAW_RSA_PUBLIC_KEY[0]);
        baos.write(Registry.MAGIC_RAW_RSA_PUBLIC_KEY[1]);
        baos.write(Registry.MAGIC_RAW_RSA_PUBLIC_KEY[2]);
        baos.write(Registry.MAGIC_RAW_RSA_PUBLIC_KEY[3]);
        baos.write(1);
        byte[] buffer = rsaKey.getModulus().toByteArray();
        int length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = rsaKey.getPublicExponent().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        return baos.toByteArray();
    }

    public PublicKey decodePublicKey(byte[] k) {
        if (k[0] != Registry.MAGIC_RAW_RSA_PUBLIC_KEY[0] || k[1] != Registry.MAGIC_RAW_RSA_PUBLIC_KEY[1] || k[2] != Registry.MAGIC_RAW_RSA_PUBLIC_KEY[2] || k[3] != Registry.MAGIC_RAW_RSA_PUBLIC_KEY[3]) {
            throw new IllegalArgumentException("magic");
        }
        if (k[4] != 1) {
            throw new IllegalArgumentException("version");
        }
        int i = 5;
        int l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        byte[] buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger n = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger e = new BigInteger(1, buffer);
        return new GnuRSAPublicKey(n, e);
    }

    public byte[] encodePrivateKey(PrivateKey key) {
        if (!(key instanceof GnuRSAPrivateKey)) {
            throw new IllegalArgumentException("key");
        }
        GnuRSAPrivateKey rsaKey = (GnuRSAPrivateKey)key;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Registry.MAGIC_RAW_RSA_PRIVATE_KEY[0]);
        baos.write(Registry.MAGIC_RAW_RSA_PRIVATE_KEY[1]);
        baos.write(Registry.MAGIC_RAW_RSA_PRIVATE_KEY[2]);
        baos.write(Registry.MAGIC_RAW_RSA_PRIVATE_KEY[3]);
        baos.write(1);
        byte[] buffer = rsaKey.getPrimeP().toByteArray();
        int length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = rsaKey.getPrimeQ().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = rsaKey.getPublicExponent().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = rsaKey.getPrivateExponent().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        return baos.toByteArray();
    }

    public PrivateKey decodePrivateKey(byte[] k) {
        if (k[0] != Registry.MAGIC_RAW_RSA_PRIVATE_KEY[0] || k[1] != Registry.MAGIC_RAW_RSA_PRIVATE_KEY[1] || k[2] != Registry.MAGIC_RAW_RSA_PRIVATE_KEY[2] || k[3] != Registry.MAGIC_RAW_RSA_PRIVATE_KEY[3]) {
            throw new IllegalArgumentException("magic");
        }
        if (k[4] != 1) {
            throw new IllegalArgumentException("version");
        }
        int i = 5;
        int l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        byte[] buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger p = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger q = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger e = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger d = new BigInteger(1, buffer);
        return new GnuRSAPrivateKey(p, q, e, d);
    }
}

