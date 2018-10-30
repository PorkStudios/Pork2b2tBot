/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.dss;

import gnu.crypto.Registry;
import gnu.crypto.sig.ISignatureCodec;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class DSSSignatureRawCodec
implements ISignatureCodec {
    public int getFormatID() {
        return 1;
    }

    public byte[] encodeSignature(Object signature) {
        BigInteger s;
        BigInteger r;
        try {
            BigInteger[] sig = (BigInteger[])signature;
            r = sig[0];
            s = sig[1];
        }
        catch (Exception x) {
            throw new IllegalArgumentException("key");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Registry.MAGIC_RAW_DSS_SIGNATURE[0]);
        baos.write(Registry.MAGIC_RAW_DSS_SIGNATURE[1]);
        baos.write(Registry.MAGIC_RAW_DSS_SIGNATURE[2]);
        baos.write(Registry.MAGIC_RAW_DSS_SIGNATURE[3]);
        baos.write(1);
        byte[] buffer = r.toByteArray();
        int length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        buffer = s.toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write(length >>> 16 & 255);
        baos.write(length >>> 8 & 255);
        baos.write(length & 255);
        baos.write(buffer, 0, length);
        return baos.toByteArray();
    }

    public Object decodeSignature(byte[] k) {
        if (k[0] != Registry.MAGIC_RAW_DSS_SIGNATURE[0] || k[1] != Registry.MAGIC_RAW_DSS_SIGNATURE[1] || k[2] != Registry.MAGIC_RAW_DSS_SIGNATURE[2] || k[3] != Registry.MAGIC_RAW_DSS_SIGNATURE[3]) {
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
        BigInteger r = new BigInteger(1, buffer);
        l = k[i++] << 24 | (k[i++] & 255) << 16 | (k[i++] & 255) << 8 | k[i++] & 255;
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger s = new BigInteger(1, buffer);
        return new BigInteger[]{r, s};
    }
}

