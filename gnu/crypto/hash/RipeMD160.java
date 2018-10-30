/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class RipeMD160
extends BaseHash {
    private static final int BLOCK_SIZE = 64;
    private static final String DIGEST0 = "9C1185A5C5E9FC54612808977EE8F548B2258D31";
    private static final int[] R;
    private static final int[] Rp;
    private static final int[] S;
    private static final int[] Sp;
    private static Boolean valid;
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;
    private int[] X;

    public Object clone() {
        return new RipeMD160(this);
    }

    protected void transform(byte[] in, int offset) {
        int s;
        int Dp;
        int Ep;
        int Ap;
        int Cp;
        int T;
        int Bp;
        int i = 0;
        while (i < 16) {
            this.X[i] = in[offset++] & 255 | (in[offset++] & 255) << 8 | (in[offset++] & 255) << 16 | in[offset++] << 24;
            ++i;
        }
        int A = Ap = this.h0;
        int B = Bp = this.h1;
        int C = Cp = this.h2;
        int D = Dp = this.h3;
        int E = Ep = this.h4;
        i = 0;
        while (i < 16) {
            s = S[i];
            T = A + (B ^ C ^ D) + this.X[i];
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = Sp[i];
            T = Ap + (Bp ^ (Cp | ~ Dp)) + this.X[Rp[i]] + 1352829926;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
            ++i;
        }
        while (i < 32) {
            s = S[i];
            T = A + (B & C | ~ B & D) + this.X[R[i]] + 1518500249;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = Sp[i];
            T = Ap + (Bp & Dp | Cp & ~ Dp) + this.X[Rp[i]] + 1548603684;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
            ++i;
        }
        while (i < 48) {
            s = S[i];
            T = A + ((B | ~ C) ^ D) + this.X[R[i]] + 1859775393;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = Sp[i];
            T = Ap + ((Bp | ~ Cp) ^ Dp) + this.X[Rp[i]] + 1836072691;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
            ++i;
        }
        while (i < 64) {
            s = S[i];
            T = A + (B & D | C & ~ D) + this.X[R[i]] + -1894007588;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = Sp[i];
            T = Ap + (Bp & Cp | ~ Bp & Dp) + this.X[Rp[i]] + 2053994217;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
            ++i;
        }
        while (i < 80) {
            s = S[i];
            T = A + (B ^ (C | ~ D)) + this.X[R[i]] + -1454113458;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = Sp[i];
            T = Ap + (Bp ^ Cp ^ Dp) + this.X[Rp[i]];
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
            ++i;
        }
        T = this.h1 + C + Dp;
        this.h1 = this.h2 + D + Ep;
        this.h2 = this.h3 + E + Ap;
        this.h3 = this.h4 + A + Bp;
        this.h4 = this.h0 + B + Cp;
        this.h0 = T;
    }

    protected byte[] padBuffer() {
        int n = (int)(this.count % 64L);
        int padding = n < 56 ? 56 - n : 120 - n;
        byte[] result = new byte[padding + 8];
        result[0] = -128;
        long bits = this.count << 3;
        result[padding++] = (byte)bits;
        result[padding++] = (byte)(bits >>> 8);
        result[padding++] = (byte)(bits >>> 16);
        result[padding++] = (byte)(bits >>> 24);
        result[padding++] = (byte)(bits >>> 32);
        result[padding++] = (byte)(bits >>> 40);
        result[padding++] = (byte)(bits >>> 48);
        result[padding] = (byte)(bits >>> 56);
        return result;
    }

    protected byte[] getResult() {
        byte[] result = new byte[]{(byte)this.h0, (byte)(this.h0 >>> 8), (byte)(this.h0 >>> 16), (byte)(this.h0 >>> 24), (byte)this.h1, (byte)(this.h1 >>> 8), (byte)(this.h1 >>> 16), (byte)(this.h1 >>> 24), (byte)this.h2, (byte)(this.h2 >>> 8), (byte)(this.h2 >>> 16), (byte)(this.h2 >>> 24), (byte)this.h3, (byte)(this.h3 >>> 8), (byte)(this.h3 >>> 16), (byte)(this.h3 >>> 24), (byte)this.h4, (byte)(this.h4 >>> 8), (byte)(this.h4 >>> 16), (byte)(this.h4 >>> 24)};
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
            valid = new Boolean(DIGEST0.equals(Util.toString(new RipeMD160().digest())));
        }
        return valid;
    }

    private final /* synthetic */ void this() {
        this.X = new int[16];
    }

    public RipeMD160() {
        super("ripemd160", 20, 64);
        this.this();
    }

    private RipeMD160(RipeMD160 md) {
        this();
        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.h4 = md.h4;
        this.count = md.count;
        this.buffer = (byte[])md.buffer.clone();
    }

    private static final {
        int[] arrn = new int[80];
        arrn[1] = 1;
        arrn[2] = 2;
        arrn[3] = 3;
        arrn[4] = 4;
        arrn[5] = 5;
        arrn[6] = 6;
        arrn[7] = 7;
        arrn[8] = 8;
        arrn[9] = 9;
        arrn[10] = 10;
        arrn[11] = 11;
        arrn[12] = 12;
        arrn[13] = 13;
        arrn[14] = 14;
        arrn[15] = 15;
        arrn[16] = 7;
        arrn[17] = 4;
        arrn[18] = 13;
        arrn[19] = 1;
        arrn[20] = 10;
        arrn[21] = 6;
        arrn[22] = 15;
        arrn[23] = 3;
        arrn[24] = 12;
        arrn[26] = 9;
        arrn[27] = 5;
        arrn[28] = 2;
        arrn[29] = 14;
        arrn[30] = 11;
        arrn[31] = 8;
        arrn[32] = 3;
        arrn[33] = 10;
        arrn[34] = 14;
        arrn[35] = 4;
        arrn[36] = 9;
        arrn[37] = 15;
        arrn[38] = 8;
        arrn[39] = 1;
        arrn[40] = 2;
        arrn[41] = 7;
        arrn[43] = 6;
        arrn[44] = 13;
        arrn[45] = 11;
        arrn[46] = 5;
        arrn[47] = 12;
        arrn[48] = 1;
        arrn[49] = 9;
        arrn[50] = 11;
        arrn[51] = 10;
        arrn[53] = 8;
        arrn[54] = 12;
        arrn[55] = 4;
        arrn[56] = 13;
        arrn[57] = 3;
        arrn[58] = 7;
        arrn[59] = 15;
        arrn[60] = 14;
        arrn[61] = 5;
        arrn[62] = 6;
        arrn[63] = 2;
        arrn[64] = 4;
        arrn[66] = 5;
        arrn[67] = 9;
        arrn[68] = 7;
        arrn[69] = 12;
        arrn[70] = 2;
        arrn[71] = 10;
        arrn[72] = 14;
        arrn[73] = 1;
        arrn[74] = 3;
        arrn[75] = 8;
        arrn[76] = 11;
        arrn[77] = 6;
        arrn[78] = 15;
        arrn[79] = 13;
        R = arrn;
        int[] arrn2 = new int[80];
        arrn2[0] = 5;
        arrn2[1] = 14;
        arrn2[2] = 7;
        arrn2[4] = 9;
        arrn2[5] = 2;
        arrn2[6] = 11;
        arrn2[7] = 4;
        arrn2[8] = 13;
        arrn2[9] = 6;
        arrn2[10] = 15;
        arrn2[11] = 8;
        arrn2[12] = 1;
        arrn2[13] = 10;
        arrn2[14] = 3;
        arrn2[15] = 12;
        arrn2[16] = 6;
        arrn2[17] = 11;
        arrn2[18] = 3;
        arrn2[19] = 7;
        arrn2[21] = 13;
        arrn2[22] = 5;
        arrn2[23] = 10;
        arrn2[24] = 14;
        arrn2[25] = 15;
        arrn2[26] = 8;
        arrn2[27] = 12;
        arrn2[28] = 4;
        arrn2[29] = 9;
        arrn2[30] = 1;
        arrn2[31] = 2;
        arrn2[32] = 15;
        arrn2[33] = 5;
        arrn2[34] = 1;
        arrn2[35] = 3;
        arrn2[36] = 7;
        arrn2[37] = 14;
        arrn2[38] = 6;
        arrn2[39] = 9;
        arrn2[40] = 11;
        arrn2[41] = 8;
        arrn2[42] = 12;
        arrn2[43] = 2;
        arrn2[44] = 10;
        arrn2[46] = 4;
        arrn2[47] = 13;
        arrn2[48] = 8;
        arrn2[49] = 6;
        arrn2[50] = 4;
        arrn2[51] = 1;
        arrn2[52] = 3;
        arrn2[53] = 11;
        arrn2[54] = 15;
        arrn2[56] = 5;
        arrn2[57] = 12;
        arrn2[58] = 2;
        arrn2[59] = 13;
        arrn2[60] = 9;
        arrn2[61] = 7;
        arrn2[62] = 10;
        arrn2[63] = 14;
        arrn2[64] = 12;
        arrn2[65] = 15;
        arrn2[66] = 10;
        arrn2[67] = 4;
        arrn2[68] = 1;
        arrn2[69] = 5;
        arrn2[70] = 8;
        arrn2[71] = 7;
        arrn2[72] = 6;
        arrn2[73] = 2;
        arrn2[74] = 13;
        arrn2[75] = 14;
        arrn2[77] = 3;
        arrn2[78] = 9;
        arrn2[79] = 11;
        Rp = arrn2;
        S = new int[]{11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8, 7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12, 11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5, 11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12, 9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6};
        Sp = new int[]{8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6, 9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11, 9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5, 15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8, 8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11};
    }
}

