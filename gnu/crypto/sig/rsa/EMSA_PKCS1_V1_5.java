/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.rsa;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import java.io.ByteArrayOutputStream;

public class EMSA_PKCS1_V1_5
implements Cloneable {
    private static final byte[] MD2_PREFIX;
    private static final byte[] MD5_PREFIX;
    private static final byte[] SHA160_PREFIX;
    private static final byte[] SHA256_PREFIX;
    private static final byte[] SHA384_PREFIX;
    private static final byte[] SHA512_PREFIX;
    private IMessageDigest hash;
    private int hLen;
    private byte[] prefix;

    public static final EMSA_PKCS1_V1_5 getInstance(String mdName) {
        IMessageDigest hash = HashFactory.getInstance(mdName);
        String name = hash.name();
        if (!(name.equals("md2") || name.equals("md5") || name.equals("sha-160") || name.equals("sha-256") || name.equals("sha-384") || name.equals("sha-512"))) {
            throw new UnsupportedOperationException("hash with no ID");
        }
        return new EMSA_PKCS1_V1_5(hash);
    }

    public Object clone() {
        return EMSA_PKCS1_V1_5.getInstance(this.hash.name());
    }

    public byte[] encode(byte[] mHash, int emLen) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(this.prefix, 0, this.prefix.length);
        baos.write(mHash, 0, mHash.length);
        byte[] T = baos.toByteArray();
        int tLen = T.length;
        if (emLen < tLen + 11) {
            throw new IllegalArgumentException("emLen too short");
        }
        byte[] PS = new byte[emLen - tLen - 3];
        int i = 0;
        while (i < PS.length) {
            PS[i] = -1;
            ++i;
        }
        baos.reset();
        baos.write(0);
        baos.write(1);
        baos.write(PS, 0, PS.length);
        baos.write(0);
        baos.write(T, 0, tLen);
        byte[] result = baos.toByteArray();
        baos.reset();
        return result;
    }

    private EMSA_PKCS1_V1_5(IMessageDigest hash) {
        this.hash = hash;
        this.hLen = hash.hashSize();
        String name = hash.name();
        if (name.equals("md2")) {
            this.prefix = MD2_PREFIX;
        } else if (name.equals("md5")) {
            this.prefix = MD5_PREFIX;
        } else if (name.equals("sha-160")) {
            this.prefix = SHA160_PREFIX;
        } else if (name.equals("sha-256")) {
            this.prefix = SHA256_PREFIX;
        } else if (name.equals("sha-384")) {
            this.prefix = SHA384_PREFIX;
        } else if (name.equals("sha-512")) {
            this.prefix = SHA512_PREFIX;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static final {
        byte[] arrby = new byte[18];
        arrby[0] = 48;
        arrby[1] = 32;
        arrby[2] = 48;
        arrby[3] = 12;
        arrby[4] = 6;
        arrby[5] = 8;
        arrby[6] = 42;
        arrby[7] = -122;
        arrby[8] = 72;
        arrby[9] = -122;
        arrby[10] = -9;
        arrby[11] = 13;
        arrby[12] = 2;
        arrby[13] = 2;
        arrby[14] = 5;
        arrby[16] = 4;
        arrby[17] = 16;
        MD2_PREFIX = arrby;
        byte[] arrby2 = new byte[18];
        arrby2[0] = 48;
        arrby2[1] = 32;
        arrby2[2] = 48;
        arrby2[3] = 12;
        arrby2[4] = 6;
        arrby2[5] = 8;
        arrby2[6] = 42;
        arrby2[7] = -122;
        arrby2[8] = 72;
        arrby2[9] = -122;
        arrby2[10] = -9;
        arrby2[11] = 13;
        arrby2[12] = 2;
        arrby2[13] = 5;
        arrby2[14] = 5;
        arrby2[16] = 4;
        arrby2[17] = 16;
        MD5_PREFIX = arrby2;
        byte[] arrby3 = new byte[15];
        arrby3[0] = 48;
        arrby3[1] = 33;
        arrby3[2] = 48;
        arrby3[3] = 9;
        arrby3[4] = 6;
        arrby3[5] = 5;
        arrby3[6] = 43;
        arrby3[7] = 14;
        arrby3[8] = 3;
        arrby3[9] = 2;
        arrby3[10] = 26;
        arrby3[11] = 5;
        arrby3[13] = 4;
        arrby3[14] = 20;
        SHA160_PREFIX = arrby3;
        byte[] arrby4 = new byte[19];
        arrby4[0] = 48;
        arrby4[1] = 49;
        arrby4[2] = 48;
        arrby4[3] = 13;
        arrby4[4] = 6;
        arrby4[5] = 9;
        arrby4[6] = 96;
        arrby4[7] = -122;
        arrby4[8] = 72;
        arrby4[9] = 1;
        arrby4[10] = 101;
        arrby4[11] = 3;
        arrby4[12] = 4;
        arrby4[13] = 2;
        arrby4[14] = 1;
        arrby4[15] = 5;
        arrby4[17] = 4;
        arrby4[18] = 32;
        SHA256_PREFIX = arrby4;
        byte[] arrby5 = new byte[19];
        arrby5[0] = 48;
        arrby5[1] = 65;
        arrby5[2] = 48;
        arrby5[3] = 13;
        arrby5[4] = 6;
        arrby5[5] = 9;
        arrby5[6] = 96;
        arrby5[7] = -122;
        arrby5[8] = 72;
        arrby5[9] = 1;
        arrby5[10] = 101;
        arrby5[11] = 3;
        arrby5[12] = 4;
        arrby5[13] = 2;
        arrby5[14] = 2;
        arrby5[15] = 5;
        arrby5[17] = 4;
        arrby5[18] = 48;
        SHA384_PREFIX = arrby5;
        byte[] arrby6 = new byte[19];
        arrby6[0] = 48;
        arrby6[1] = 81;
        arrby6[2] = 48;
        arrby6[3] = 13;
        arrby6[4] = 6;
        arrby6[5] = 9;
        arrby6[6] = 96;
        arrby6[7] = -122;
        arrby6[8] = 72;
        arrby6[9] = 1;
        arrby6[10] = 101;
        arrby6[11] = 3;
        arrby6[12] = 4;
        arrby6[13] = 2;
        arrby6[14] = 3;
        arrby6[15] = 5;
        arrby6[17] = 4;
        arrby6[18] = 64;
        SHA512_PREFIX = arrby6;
    }
}

