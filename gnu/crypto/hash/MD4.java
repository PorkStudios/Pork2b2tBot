/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

public class MD4
extends BaseHash {
    private static final int DIGEST_LENGTH = 16;
    private static final int BLOCK_LENGTH = 64;
    private static final int A = 1732584193;
    private static final int B = -271733879;
    private static final int C = -1732584194;
    private static final int D = 271733878;
    private static final String DIGEST0 = "31D6CFE0D16AE931B73C59D7E0C089C0";
    private static Boolean valid;
    private int a;
    private int b;
    private int c;
    private int d;

    public Object clone() {
        return new MD4(this);
    }

    protected byte[] getResult() {
        byte[] digest = new byte[]{(byte)this.a, (byte)(this.a >>> 8), (byte)(this.a >>> 16), (byte)(this.a >>> 24), (byte)this.b, (byte)(this.b >>> 8), (byte)(this.b >>> 16), (byte)(this.b >>> 24), (byte)this.c, (byte)(this.c >>> 8), (byte)(this.c >>> 16), (byte)(this.c >>> 24), (byte)this.d, (byte)(this.d >>> 8), (byte)(this.d >>> 16), (byte)(this.d >>> 24)};
        return digest;
    }

    protected void resetContext() {
        this.a = 1732584193;
        this.b = -271733879;
        this.c = -1732584194;
        this.d = 271733878;
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = new Boolean(DIGEST0.equals(Util.toString(new MD4().digest())));
        }
        return valid;
    }

    protected byte[] padBuffer() {
        int n = (int)(this.count % 64L);
        int padding = n < 56 ? 56 - n : 120 - n;
        byte[] pad = new byte[padding + 8];
        pad[0] = -128;
        long bits = this.count << 3;
        pad[padding++] = (byte)bits;
        pad[padding++] = (byte)(bits >>> 8);
        pad[padding++] = (byte)(bits >>> 16);
        pad[padding++] = (byte)(bits >>> 24);
        pad[padding++] = (byte)(bits >>> 32);
        pad[padding++] = (byte)(bits >>> 40);
        pad[padding++] = (byte)(bits >>> 48);
        pad[padding] = (byte)(bits >>> 56);
        return pad;
    }

    protected void transform(byte[] in, int i) {
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
        int aa = this.a;
        int bb = this.b;
        int cc = this.c;
        int dd = this.d;
        aa += (bb & cc | ~ bb & dd) + X0;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & bb | ~ aa & cc) + X1;
        dd = dd << 7 | dd >>> -7;
        cc += (dd & aa | ~ dd & bb) + X2;
        cc = cc << 11 | cc >>> -11;
        bb += (cc & dd | ~ cc & aa) + X3;
        bb = bb << 19 | bb >>> -19;
        aa += (bb & cc | ~ bb & dd) + X4;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & bb | ~ aa & cc) + X5;
        dd = dd << 7 | dd >>> -7;
        cc += (dd & aa | ~ dd & bb) + X6;
        cc = cc << 11 | cc >>> -11;
        bb += (cc & dd | ~ cc & aa) + X7;
        bb = bb << 19 | bb >>> -19;
        aa += (bb & cc | ~ bb & dd) + X8;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & bb | ~ aa & cc) + X9;
        dd = dd << 7 | dd >>> -7;
        cc += (dd & aa | ~ dd & bb) + X10;
        cc = cc << 11 | cc >>> -11;
        bb += (cc & dd | ~ cc & aa) + X11;
        bb = bb << 19 | bb >>> -19;
        aa += (bb & cc | ~ bb & dd) + X12;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & bb | ~ aa & cc) + X13;
        dd = dd << 7 | dd >>> -7;
        cc += (dd & aa | ~ dd & bb) + X14;
        cc = cc << 11 | cc >>> -11;
        bb += (cc & dd | ~ cc & aa) + X15;
        bb = bb << 19 | bb >>> -19;
        aa += (bb & (cc | dd) | cc & dd) + X0 + 1518500249;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & (bb | cc) | bb & cc) + X4 + 1518500249;
        dd = dd << 5 | dd >>> -5;
        cc += (dd & (aa | bb) | aa & bb) + X8 + 1518500249;
        cc = cc << 9 | cc >>> -9;
        bb += (cc & (dd | aa) | dd & aa) + X12 + 1518500249;
        bb = bb << 13 | bb >>> -13;
        aa += (bb & (cc | dd) | cc & dd) + X1 + 1518500249;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & (bb | cc) | bb & cc) + X5 + 1518500249;
        dd = dd << 5 | dd >>> -5;
        cc += (dd & (aa | bb) | aa & bb) + X9 + 1518500249;
        cc = cc << 9 | cc >>> -9;
        bb += (cc & (dd | aa) | dd & aa) + X13 + 1518500249;
        bb = bb << 13 | bb >>> -13;
        aa += (bb & (cc | dd) | cc & dd) + X2 + 1518500249;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & (bb | cc) | bb & cc) + X6 + 1518500249;
        dd = dd << 5 | dd >>> -5;
        cc += (dd & (aa | bb) | aa & bb) + X10 + 1518500249;
        cc = cc << 9 | cc >>> -9;
        bb += (cc & (dd | aa) | dd & aa) + X14 + 1518500249;
        bb = bb << 13 | bb >>> -13;
        aa += (bb & (cc | dd) | cc & dd) + X3 + 1518500249;
        aa = aa << 3 | aa >>> -3;
        dd += (aa & (bb | cc) | bb & cc) + X7 + 1518500249;
        dd = dd << 5 | dd >>> -5;
        cc += (dd & (aa | bb) | aa & bb) + X11 + 1518500249;
        cc = cc << 9 | cc >>> -9;
        bb += (cc & (dd | aa) | dd & aa) + X15 + 1518500249;
        bb = bb << 13 | bb >>> -13;
        aa += (bb ^ cc ^ dd) + X0 + 1859775393;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X8 + 1859775393;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X4 + 1859775393;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X12 + 1859775393;
        bb = bb << 15 | bb >>> -15;
        aa += (bb ^ cc ^ dd) + X2 + 1859775393;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X10 + 1859775393;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X6 + 1859775393;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X14 + 1859775393;
        bb = bb << 15 | bb >>> -15;
        aa += (bb ^ cc ^ dd) + X1 + 1859775393;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X9 + 1859775393;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X5 + 1859775393;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X13 + 1859775393;
        bb = bb << 15 | bb >>> -15;
        aa += (bb ^ cc ^ dd) + X3 + 1859775393;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X11 + 1859775393;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X7 + 1859775393;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X15 + 1859775393;
        bb = bb << 15 | bb >>> -15;
        this.a += aa;
        this.b += bb;
        this.c += cc;
        this.d += dd;
    }

    public MD4() {
        super("md4", 16, 64);
    }

    private MD4(MD4 that) {
        this();
        this.a = that.a;
        this.b = that.b;
        this.c = that.c;
        this.d = that.d;
        this.count = that.count;
        this.buffer = (byte[])that.buffer.clone();
    }
}

