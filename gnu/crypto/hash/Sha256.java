/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

public class Sha256
extends BaseHash {
    private static final int[] k = new int[]{1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};
    private static final int BLOCK_SIZE = 64;
    private static final String DIGEST0 = "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD";
    private static final int[] w = new int[64];
    private static Boolean valid;
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;
    private int h5;
    private int h6;
    private int h7;

    public static final int[] G(int hh0, int hh1, int hh2, int hh3, int hh4, int hh5, int hh6, int hh7, byte[] in, int offset) {
        return Sha256.sha(hh0, hh1, hh2, hh3, hh4, hh5, hh6, hh7, in, offset);
    }

    public Object clone() {
        return new Sha256(this);
    }

    protected void transform(byte[] in, int offset) {
        int[] result = Sha256.sha(this.h0, this.h1, this.h2, this.h3, this.h4, this.h5, this.h6, this.h7, in, offset);
        this.h0 = result[0];
        this.h1 = result[1];
        this.h2 = result[2];
        this.h3 = result[3];
        this.h4 = result[4];
        this.h5 = result[5];
        this.h6 = result[6];
        this.h7 = result[7];
    }

    protected byte[] padBuffer() {
        int n = (int)(this.count % 64L);
        int padding = n < 56 ? 56 - n : 120 - n;
        byte[] result = new byte[padding + 8];
        result[0] = -128;
        long bits = this.count << 3;
        result[padding++] = (byte)(bits >>> 56);
        result[padding++] = (byte)(bits >>> 48);
        result[padding++] = (byte)(bits >>> 40);
        result[padding++] = (byte)(bits >>> 32);
        result[padding++] = (byte)(bits >>> 24);
        result[padding++] = (byte)(bits >>> 16);
        result[padding++] = (byte)(bits >>> 8);
        result[padding] = (byte)bits;
        return result;
    }

    protected byte[] getResult() {
        return new byte[]{(byte)(this.h0 >>> 24), (byte)(this.h0 >>> 16), (byte)(this.h0 >>> 8), (byte)this.h0, (byte)(this.h1 >>> 24), (byte)(this.h1 >>> 16), (byte)(this.h1 >>> 8), (byte)this.h1, (byte)(this.h2 >>> 24), (byte)(this.h2 >>> 16), (byte)(this.h2 >>> 8), (byte)this.h2, (byte)(this.h3 >>> 24), (byte)(this.h3 >>> 16), (byte)(this.h3 >>> 8), (byte)this.h3, (byte)(this.h4 >>> 24), (byte)(this.h4 >>> 16), (byte)(this.h4 >>> 8), (byte)this.h4, (byte)(this.h5 >>> 24), (byte)(this.h5 >>> 16), (byte)(this.h5 >>> 8), (byte)this.h5, (byte)(this.h6 >>> 24), (byte)(this.h6 >>> 16), (byte)(this.h6 >>> 8), (byte)this.h6, (byte)(this.h7 >>> 24), (byte)(this.h7 >>> 16), (byte)(this.h7 >>> 8), (byte)this.h7};
    }

    protected void resetContext() {
        this.h0 = 1779033703;
        this.h1 = -1150833019;
        this.h2 = 1013904242;
        this.h3 = -1521486534;
        this.h4 = 1359893119;
        this.h5 = -1694144372;
        this.h6 = 528734635;
        this.h7 = 1541459225;
    }

    public boolean selfTest() {
        if (valid == null) {
            Sha256 md = new Sha256();
            md.update((byte)97);
            md.update((byte)98);
            md.update((byte)99);
            String result = Util.toString(md.digest());
            valid = new Boolean(DIGEST0.equals(result));
        }
        return valid;
    }

    private static final synchronized int[] sha(int hh0, int hh1, int hh2, int hh3, int hh4, int hh5, int hh6, int hh7, byte[] in, int offset) {
        int T2;
        int T;
        int A = hh0;
        int B = hh1;
        int C = hh2;
        int D = hh3;
        int E = hh4;
        int F = hh5;
        int G = hh6;
        int H = hh7;
        int r = 0;
        while (r < 16) {
            Sha256.w[r] = in[offset++] << 24 | (in[offset++] & 255) << 16 | (in[offset++] & 255) << 8 | in[offset++] & 255;
            ++r;
        }
        r = 16;
        while (r < 64) {
            T = w[r - 2];
            T2 = w[r - 15];
            Sha256.w[r] = ((T >>> 17 | T << 15) ^ (T >>> 19 | T << 13) ^ T >>> 10) + w[r - 7] + ((T2 >>> 7 | T2 << 25) ^ (T2 >>> 18 | T2 << 14) ^ T2 >>> 3) + w[r - 16];
            ++r;
        }
        r = 0;
        while (r < 64) {
            T = H + ((E >>> 6 | E << 26) ^ (E >>> 11 | E << 21) ^ (E >>> 25 | E << 7)) + (E & F ^ ~ E & G) + k[r] + w[r];
            T2 = ((A >>> 2 | A << 30) ^ (A >>> 13 | A << 19) ^ (A >>> 22 | A << 10)) + (A & B ^ A & C ^ B & C);
            H = G;
            G = F;
            F = E;
            E = D + T;
            D = C;
            C = B;
            B = A;
            A = T + T2;
            ++r;
        }
        return new int[]{hh0 + A, hh1 + B, hh2 + C, hh3 + D, hh4 + E, hh5 + F, hh6 + G, hh7 + H};
    }

    public Sha256() {
        super("sha-256", 32, 64);
    }

    private Sha256(Sha256 md) {
        this();
        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.h4 = md.h4;
        this.h5 = md.h5;
        this.h6 = md.h6;
        this.h7 = md.h7;
        this.count = md.count;
        this.buffer = (byte[])md.buffer.clone();
    }
}

