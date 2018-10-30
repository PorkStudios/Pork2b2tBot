/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.Registry;
import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.srp6.SRPPrivateKey;
import gnu.crypto.key.srp6.SRPPublicKey;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SRPKeyPairRawCodec
implements IKeyPairCodec {
    public int getFormatID() {
        return 1;
    }

    public byte[] encodePublicKey(PublicKey key) {
        if (!(key instanceof SRPPublicKey)) {
            throw new IllegalArgumentException("key");
        }
        SRPPublicKey srpKey = (SRPPublicKey)key;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Registry.MAGIC_RAW_SRP_PUBLIC_KEY[0]);
        baos.write(Registry.MAGIC_RAW_SRP_PUBLIC_KEY[1]);
        baos.write(Registry.MAGIC_RAW_SRP_PUBLIC_KEY[2]);
        baos.write(Registry.MAGIC_RAW_SRP_PUBLIC_KEY[3]);
        baos.write(1);
        byte[] buffer = srpKey.getN().toByteArray();
        int length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = srpKey.getG().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = srpKey.getY().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        return baos.toByteArray();
    }

    public PublicKey decodePublicKey(byte[] k) {
        if (k[0] != Registry.MAGIC_RAW_SRP_PUBLIC_KEY[0] || k[1] != Registry.MAGIC_RAW_SRP_PUBLIC_KEY[1] || k[2] != Registry.MAGIC_RAW_SRP_PUBLIC_KEY[2] || k[3] != Registry.MAGIC_RAW_SRP_PUBLIC_KEY[3]) {
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
        BigInteger N = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger g = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger y = new BigInteger(1, buffer);
        return new SRPPublicKey(N, g, y);
    }

    public byte[] encodePrivateKey(PrivateKey key) {
        if (!(key instanceof SRPPrivateKey)) {
            throw new IllegalArgumentException("key");
        }
        SRPPrivateKey srpKey = (SRPPrivateKey)key;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Registry.MAGIC_RAW_SRP_PRIVATE_KEY[0]);
        baos.write(Registry.MAGIC_RAW_SRP_PRIVATE_KEY[1]);
        baos.write(Registry.MAGIC_RAW_SRP_PRIVATE_KEY[2]);
        baos.write(Registry.MAGIC_RAW_SRP_PRIVATE_KEY[3]);
        baos.write(1);
        byte[] buffer = srpKey.getN().toByteArray();
        int length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = srpKey.getG().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = srpKey.getX().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        if (srpKey.getV() != null) {
            baos.write(1);
            buffer = srpKey.getV().toByteArray();
            length = buffer.length;
            baos.write(length >>> 24);
            baos.write(length >>> 16 & 255);
            baos.write(length >>> 8 & 255);
            baos.write(length & 255);
            baos.write(buffer, 0, length);
        } else {
            baos.write(0);
        }
        return baos.toByteArray();
    }

    public PrivateKey decodePrivateKey(byte[] k) {
        if (k[0] != Registry.MAGIC_RAW_SRP_PRIVATE_KEY[0] || k[1] != Registry.MAGIC_RAW_SRP_PRIVATE_KEY[1] || k[2] != Registry.MAGIC_RAW_SRP_PRIVATE_KEY[2] || k[3] != Registry.MAGIC_RAW_SRP_PRIVATE_KEY[3]) {
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
        BigInteger N = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger g = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger x = new BigInteger(1, buffer);
        l = k[i++];
        if (l == 1) {
            l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
            buffer = new byte[l];
            System.arraycopy(k, i, buffer, 0, l);
            i += l;
            BigInteger v = new BigInteger(1, buffer);
            return new SRPPrivateKey(N, g, x, v);
        }
        return new SRPPrivateKey(N, g, x);
    }
}

