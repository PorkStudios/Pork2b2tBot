/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

public class MD5
extends BaseHash {
    private static final int BLOCK_SIZE = 64;
    private static final String DIGEST0 = "D41D8CD98F00B204E9800998ECF8427E";
    private static Boolean valid;
    private int h0;
    private int h1;
    private int h2;
    private int h3;

    public Object clone() {
        return new MD5(this);
    }

    protected synchronized void transform(byte[] in, int i) {
        int X0 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X1 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X2 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X3 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X4 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X5 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X6 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X7 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X8 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X9 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X10 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X11 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X12 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X13 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X14 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i++] << 24;
        int X15 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | in[i] << 24;
        int A = this.h0;
        int B = this.h1;
        int C = this.h2;
        int D = this.h3;
        A += (B & C | ~ B & D) + X0 + -680876936;
        A = B + (A << 7 | A >>> -7);
        D += (A & B | ~ A & C) + X1 + -389564586;
        D = A + (D << 12 | D >>> -12);
        C += (D & A | ~ D & B) + X2 + 606105819;
        C = D + (C << 17 | C >>> -17);
        B += (C & D | ~ C & A) + X3 + -1044525330;
        B = C + (B << 22 | B >>> -22);
        A += (B & C | ~ B & D) + X4 + -176418897;
        A = B + (A << 7 | A >>> -7);
        D += (A & B | ~ A & C) + X5 + 1200080426;
        D = A + (D << 12 | D >>> -12);
        C += (D & A | ~ D & B) + X6 + -1473231341;
        C = D + (C << 17 | C >>> -17);
        B += (C & D | ~ C & A) + X7 + -45705983;
        B = C + (B << 22 | B >>> -22);
        A += (B & C | ~ B & D) + X8 + 1770035416;
        A = B + (A << 7 | A >>> -7);
        D += (A & B | ~ A & C) + X9 + -1958414417;
        D = A + (D << 12 | D >>> -12);
        C += (D & A | ~ D & B) + X10 + -42063;
        C = D + (C << 17 | C >>> -17);
        B += (C & D | ~ C & A) + X11 + -1990404162;
        B = C + (B << 22 | B >>> -22);
        A += (B & C | ~ B & D) + X12 + 1804603682;
        A = B + (A << 7 | A >>> -7);
        D += (A & B | ~ A & C) + X13 + -40341101;
        D = A + (D << 12 | D >>> -12);
        C += (D & A | ~ D & B) + X14 + -1502002290;
        C = D + (C << 17 | C >>> -17);
        B += (C & D | ~ C & A) + X15 + 1236535329;
        B = C + (B << 22 | B >>> -22);
        A += (B & D | C & ~ D) + X1 + -165796510;
        A = B + (A << 5 | A >>> -5);
        D += (A & C | B & ~ C) + X6 + -1069501632;
        D = A + (D << 9 | D >>> -9);
        C += (D & B | A & ~ B) + X11 + 643717713;
        C = D + (C << 14 | C >>> -14);
        B += (C & A | D & ~ A) + X0 + -373897302;
        B = C + (B << 20 | B >>> -20);
        A += (B & D | C & ~ D) + X5 + -701558691;
        A = B + (A << 5 | A >>> -5);
        D += (A & C | B & ~ C) + X10 + 38016083;
        D = A + (D << 9 | D >>> -9);
        C += (D & B | A & ~ B) + X15 + -660478335;
        C = D + (C << 14 | C >>> -14);
        B += (C & A | D & ~ A) + X4 + -405537848;
        B = C + (B << 20 | B >>> -20);
        A += (B & D | C & ~ D) + X9 + 568446438;
        A = B + (A << 5 | A >>> -5);
        D += (A & C | B & ~ C) + X14 + -1019803690;
        D = A + (D << 9 | D >>> -9);
        C += (D & B | A & ~ B) + X3 + -187363961;
        C = D + (C << 14 | C >>> -14);
        B += (C & A | D & ~ A) + X8 + 1163531501;
        B = C + (B << 20 | B >>> -20);
        A += (B & D | C & ~ D) + X13 + -1444681467;
        A = B + (A << 5 | A >>> -5);
        D += (A & C | B & ~ C) + X2 + -51403784;
        D = A + (D << 9 | D >>> -9);
        C += (D & B | A & ~ B) + X7 + 1735328473;
        C = D + (C << 14 | C >>> -14);
        B += (C & A | D & ~ A) + X12 + -1926607734;
        B = C + (B << 20 | B >>> -20);
        A += (B ^ C ^ D) + X5 + -378558;
        A = B + (A << 4 | A >>> -4);
        D += (A ^ B ^ C) + X8 + -2022574463;
        D = A + (D << 11 | D >>> -11);
        C += (D ^ A ^ B) + X11 + 1839030562;
        C = D + (C << 16 | C >>> -16);
        B += (C ^ D ^ A) + X14 + -35309556;
        B = C + (B << 23 | B >>> -23);
        A += (B ^ C ^ D) + X1 + -1530992060;
        A = B + (A << 4 | A >>> -4);
        D += (A ^ B ^ C) + X4 + 1272893353;
        D = A + (D << 11 | D >>> -11);
        C += (D ^ A ^ B) + X7 + -155497632;
        C = D + (C << 16 | C >>> -16);
        B += (C ^ D ^ A) + X10 + -1094730640;
        B = C + (B << 23 | B >>> -23);
        A += (B ^ C ^ D) + X13 + 681279174;
        A = B + (A << 4 | A >>> -4);
        D += (A ^ B ^ C) + X0 + -358537222;
        D = A + (D << 11 | D >>> -11);
        C += (D ^ A ^ B) + X3 + -722521979;
        C = D + (C << 16 | C >>> -16);
        B += (C ^ D ^ A) + X6 + 76029189;
        B = C + (B << 23 | B >>> -23);
        A += (B ^ C ^ D) + X9 + -640364487;
        A = B + (A << 4 | A >>> -4);
        D += (A ^ B ^ C) + X12 + -421815835;
        D = A + (D << 11 | D >>> -11);
        C += (D ^ A ^ B) + X15 + 530742520;
        C = D + (C << 16 | C >>> -16);
        B += (C ^ D ^ A) + X2 + -995338651;
        B = C + (B << 23 | B >>> -23);
        A += (C ^ (B | ~ D)) + X0 + -198630844;
        A = B + (A << 6 | A >>> -6);
        D += (B ^ (A | ~ C)) + X7 + 1126891415;
        D = A + (D << 10 | D >>> -10);
        C += (A ^ (D | ~ B)) + X14 + -1416354905;
        C = D + (C << 15 | C >>> -15);
        B += (D ^ (C | ~ A)) + X5 + -57434055;
        B = C + (B << 21 | B >>> -21);
        A += (C ^ (B | ~ D)) + X12 + 1700485571;
        A = B + (A << 6 | A >>> -6);
        D += (B ^ (A | ~ C)) + X3 + -1894986606;
        D = A + (D << 10 | D >>> -10);
        C += (A ^ (D | ~ B)) + X10 + -1051523;
        C = D + (C << 15 | C >>> -15);
        B += (D ^ (C | ~ A)) + X1 + -2054922799;
        B = C + (B << 21 | B >>> -21);
        A += (C ^ (B | ~ D)) + X8 + 1873313359;
        A = B + (A << 6 | A >>> -6);
        D += (B ^ (A | ~ C)) + X15 + -30611744;
        D = A + (D << 10 | D >>> -10);
        C += (A ^ (D | ~ B)) + X6 + -1560198380;
        C = D + (C << 15 | C >>> -15);
        B += (D ^ (C | ~ A)) + X13 + 1309151649;
        B = C + (B << 21 | B >>> -21);
        A += (C ^ (B | ~ D)) + X4 + -145523070;
        A = B + (A << 6 | A >>> -6);
        D += (B ^ (A | ~ C)) + X11 + -1120210379;
        D = A + (D << 10 | D >>> -10);
        C += (A ^ (D | ~ B)) + X2 + 718787259;
        C = D + (C << 15 | C >>> -15);
        B += (D ^ (C | ~ A)) + X9 + -343485551;
        B = C + (B << 21 | B >>> -21);
        this.h0 += A;
        this.h1 += B;
        this.h2 += C;
        this.h3 += D;
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
        byte[] result = new byte[]{(byte)this.h0, (byte)(this.h0 >>> 8), (byte)(this.h0 >>> 16), (byte)(this.h0 >>> 24), (byte)this.h1, (byte)(this.h1 >>> 8), (byte)(this.h1 >>> 16), (byte)(this.h1 >>> 24), (byte)this.h2, (byte)(this.h2 >>> 8), (byte)(this.h2 >>> 16), (byte)(this.h2 >>> 24), (byte)this.h3, (byte)(this.h3 >>> 8), (byte)(this.h3 >>> 16), (byte)(this.h3 >>> 24)};
        return result;
    }

    protected void resetContext() {
        this.h0 = 1732584193;
        this.h1 = -271733879;
        this.h2 = -1732584194;
        this.h3 = 271733878;
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = new Boolean(DIGEST0.equals(Util.toString(new MD5().digest())));
        }
        return valid;
    }

    public MD5() {
        super("md5", 16, 64);
    }

    private MD5(MD5 md) {
        this();
        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.count = md.count;
        this.buffer = (byte[])md.buffer.clone();
    }
}

