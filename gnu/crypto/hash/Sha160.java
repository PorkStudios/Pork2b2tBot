/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

public class Sha160
extends BaseHash {
    private static final int BLOCK_SIZE = 64;
    private static final String DIGEST0 = "A9993E364706816ABA3E25717850C26C9CD0D89D";
    private static final int[] w = new int[80];
    private static Boolean valid;
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;

    public static final int[] G(int hh0, int hh1, int hh2, int hh3, int hh4, byte[] in, int offset) {
        return Sha160.sha(hh0, hh1, hh2, hh3, hh4, in, offset);
    }

    public Object clone() {
        return new Sha160(this);
    }

    protected void transform(byte[] in, int offset) {
        int[] result = Sha160.sha(this.h0, this.h1, this.h2, this.h3, this.h4, in, offset);
        this.h0 = result[0];
        this.h1 = result[1];
        this.h2 = result[2];
        this.h3 = result[3];
        this.h4 = result[4];
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
        byte[] result = new byte[]{(byte)(this.h0 >>> 24), (byte)(this.h0 >>> 16), (byte)(this.h0 >>> 8), (byte)this.h0, (byte)(this.h1 >>> 24), (byte)(this.h1 >>> 16), (byte)(this.h1 >>> 8), (byte)this.h1, (byte)(this.h2 >>> 24), (byte)(this.h2 >>> 16), (byte)(this.h2 >>> 8), (byte)this.h2, (byte)(this.h3 >>> 24), (byte)(this.h3 >>> 16), (byte)(this.h3 >>> 8), (byte)this.h3, (byte)(this.h4 >>> 24), (byte)(this.h4 >>> 16), (byte)(this.h4 >>> 8), (byte)this.h4};
        return result;
    }

    protected void resetContext() {
        this.h0 = 1732584193;
        this.h1 = -271733879;
        this.h2 = -1732584194;
        this.h3 = 271733878;
        this.h4 = -1009589776;
    }

    public boolean selfTest() {
        if (valid == null) {
            Sha160 md = new Sha160();
            md.update((byte)97);
            md.update((byte)98);
            md.update((byte)99);
            String result = Util.toString(md.digest());
            valid = new Boolean(DIGEST0.equals(result));
        }
        return valid;
    }

    private static final synchronized int[] sha(int hh0, int hh1, int hh2, int hh3, int hh4, byte[] in, int offset) {
        int T;
        int A = hh0;
        int B = hh1;
        int C = hh2;
        int D = hh3;
        int E = hh4;
        int r = 0;
        while (r < 16) {
            Sha160.w[r] = in[offset++] << 24 | (in[offset++] & 255) << 16 | (in[offset++] & 255) << 8 | in[offset++] & 255;
            ++r;
        }
        r = 16;
        while (r < 80) {
            T = w[r - 3] ^ w[r - 8] ^ w[r - 14] ^ w[r - 16];
            Sha160.w[r] = T << 1 | T >>> 31;
            ++r;
        }
        r = 0;
        while (r < 20) {
            T = (A << 5 | A >>> 27) + (B & C | ~ B & D) + E + w[r] + 1518500249;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
            ++r;
        }
        r = 20;
        while (r < 40) {
            T = (A << 5 | A >>> 27) + (B ^ C ^ D) + E + w[r] + 1859775393;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
            ++r;
        }
        r = 40;
        while (r < 60) {
            T = (A << 5 | A >>> 27) + (B & C | B & D | C & D) + E + w[r] + -1894007588;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
            ++r;
        }
        r = 60;
        while (r < 80) {
            T = (A << 5 | A >>> 27) + (B ^ C ^ D) + E + w[r] + -899497514;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
            ++r;
        }
        return new int[]{hh0 + A, hh1 + B, hh2 + C, hh3 + D, hh4 + E};
    }

    public Sha160() {
        super("sha-160", 20, 64);
    }

    private Sha160(Sha160 md) {
        this();
        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.h4 = md.h4;
        this.count = md.count;
        this.buffer = (byte[])md.buffer.clone();
    }
}

