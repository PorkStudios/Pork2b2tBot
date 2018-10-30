/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

public final class Whirlpool
extends BaseHash {
    private static final boolean DEBUG = false;
    private static final int debuglevel = 3;
    private static final int BLOCK_SIZE = 64;
    private static final String DIGEST0 = "470F0409ABAA446E49667D4EBE12A14387CEDBD10DD17B8243CAD550A089DC0FEEA7AA40F6C2AAAB71C6EBD076E43C7CFCA0AD32567897DCB5969861049A0F5A";
    private static final int R = 10;
    private static final String Sd = "\u1823\uc6e8\u87b8\u014f\u36a6\ud2f5\u796f\u9152\u60bc\u9b8e\ua30c\u7b35\u1de0\ud7c2\u2e4b\ufe57\u1577\u37e5\u9ff0\u4ada\u58c9\u290a\ub1a0\u6b85\ubd5d\u10f4\ucb3e\u0567\ue427\u418b\ua77d\u95d8\ufbee\u7c66\udd17\u479e\uca2d\ubf07\uad5a\u8333\u6302\uaa71\uc819\u49d9\uf2e3\u5b88\u9a26\u32b0\ue90f\ud580\ubecd\u3448\uff7a\u905f\u2068\u1aae\ub454\u9322\u64f1\u7312\u4008\uc3ec\udba1\u8d3d\u9700\ucf2b\u7682\ud61b\ub5af\u6a50\u45f3\u30ef\u3f55\ua2ea\u65ba\u2fc0\ude1c\ufd4d\u9275\u068a\ub2e6\u0e1f\u62d4\ua896\uf9c5\u2559\u8472\u394c\u5e78\u388c\ud1a5\ue261\ub321\u9c1e\u43c7\ufc04\u5199\u6d0d\ufadf\u7e24\u3bab\uce11\u8f4e\ub7eb\u3c81\u94f7\ub913\u2cd3\ue76e\uc403\u5644\u7fa9\u2abb\uc153\udc0b\u9d6c\u3174\uf646\uac89\u14e1\u163a\u6909\u70b6\ud0ed\ucc42\u98a4\u285c\uf886";
    private static final long[] T0 = new long[256];
    private static final long[] T1 = new long[256];
    private static final long[] T2 = new long[256];
    private static final long[] T3 = new long[256];
    private static final long[] T4 = new long[256];
    private static final long[] T5 = new long[256];
    private static final long[] T6 = new long[256];
    private static final long[] T7 = new long[256];
    private static final long[] rc = new long[10];
    private static Boolean valid;
    private long H0;
    private long H1;
    private long H2;
    private long H3;
    private long H4;
    private long H5;
    private long H6;
    private long H7;
    private long k00;
    private long k01;
    private long k02;
    private long k03;
    private long k04;
    private long k05;
    private long k06;
    private long k07;
    private long Kr0;
    private long Kr1;
    private long Kr2;
    private long Kr3;
    private long Kr4;
    private long Kr5;
    private long Kr6;
    private long Kr7;
    private long n0;
    private long n1;
    private long n2;
    private long n3;
    private long n4;
    private long n5;
    private long n6;
    private long n7;
    private long nn0;
    private long nn1;
    private long nn2;
    private long nn3;
    private long nn4;
    private long nn5;
    private long nn6;
    private long nn7;
    private long w0;
    private long w1;
    private long w2;
    private long w3;
    private long w4;
    private long w5;
    private long w6;
    private long w7;

    public final Object clone() {
        return new Whirlpool(this);
    }

    protected final void transform(byte[] in, int offset) {
        this.n0 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.n1 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.n2 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.n3 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.n4 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.n5 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.n6 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.n7 = ((long)in[offset++] & 255L) << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
        this.k00 = this.H0;
        this.k01 = this.H1;
        this.k02 = this.H2;
        this.k03 = this.H3;
        this.k04 = this.H4;
        this.k05 = this.H5;
        this.k06 = this.H6;
        this.k07 = this.H7;
        this.nn0 = this.n0 ^ this.k00;
        this.nn1 = this.n1 ^ this.k01;
        this.nn2 = this.n2 ^ this.k02;
        this.nn3 = this.n3 ^ this.k03;
        this.nn4 = this.n4 ^ this.k04;
        this.nn5 = this.n5 ^ this.k05;
        this.nn6 = this.n6 ^ this.k06;
        this.nn7 = this.n7 ^ this.k07;
        this.w7 = 0L;
        this.w6 = 0L;
        this.w5 = 0L;
        this.w4 = 0L;
        this.w3 = 0L;
        this.w2 = 0L;
        this.w1 = 0L;
        this.w0 = 0L;
        int r = 0;
        while (r < 10) {
            this.Kr0 = T0[(int)(this.k00 >> 56 & 255L)] ^ T1[(int)(this.k07 >> 48 & 255L)] ^ T2[(int)(this.k06 >> 40 & 255L)] ^ T3[(int)(this.k05 >> 32 & 255L)] ^ T4[(int)(this.k04 >> 24 & 255L)] ^ T5[(int)(this.k03 >> 16 & 255L)] ^ T6[(int)(this.k02 >> 8 & 255L)] ^ T7[(int)(this.k01 & 255L)] ^ rc[r];
            this.Kr1 = T0[(int)(this.k01 >> 56 & 255L)] ^ T1[(int)(this.k00 >> 48 & 255L)] ^ T2[(int)(this.k07 >> 40 & 255L)] ^ T3[(int)(this.k06 >> 32 & 255L)] ^ T4[(int)(this.k05 >> 24 & 255L)] ^ T5[(int)(this.k04 >> 16 & 255L)] ^ T6[(int)(this.k03 >> 8 & 255L)] ^ T7[(int)(this.k02 & 255L)];
            this.Kr2 = T0[(int)(this.k02 >> 56 & 255L)] ^ T1[(int)(this.k01 >> 48 & 255L)] ^ T2[(int)(this.k00 >> 40 & 255L)] ^ T3[(int)(this.k07 >> 32 & 255L)] ^ T4[(int)(this.k06 >> 24 & 255L)] ^ T5[(int)(this.k05 >> 16 & 255L)] ^ T6[(int)(this.k04 >> 8 & 255L)] ^ T7[(int)(this.k03 & 255L)];
            this.Kr3 = T0[(int)(this.k03 >> 56 & 255L)] ^ T1[(int)(this.k02 >> 48 & 255L)] ^ T2[(int)(this.k01 >> 40 & 255L)] ^ T3[(int)(this.k00 >> 32 & 255L)] ^ T4[(int)(this.k07 >> 24 & 255L)] ^ T5[(int)(this.k06 >> 16 & 255L)] ^ T6[(int)(this.k05 >> 8 & 255L)] ^ T7[(int)(this.k04 & 255L)];
            this.Kr4 = T0[(int)(this.k04 >> 56 & 255L)] ^ T1[(int)(this.k03 >> 48 & 255L)] ^ T2[(int)(this.k02 >> 40 & 255L)] ^ T3[(int)(this.k01 >> 32 & 255L)] ^ T4[(int)(this.k00 >> 24 & 255L)] ^ T5[(int)(this.k07 >> 16 & 255L)] ^ T6[(int)(this.k06 >> 8 & 255L)] ^ T7[(int)(this.k05 & 255L)];
            this.Kr5 = T0[(int)(this.k05 >> 56 & 255L)] ^ T1[(int)(this.k04 >> 48 & 255L)] ^ T2[(int)(this.k03 >> 40 & 255L)] ^ T3[(int)(this.k02 >> 32 & 255L)] ^ T4[(int)(this.k01 >> 24 & 255L)] ^ T5[(int)(this.k00 >> 16 & 255L)] ^ T6[(int)(this.k07 >> 8 & 255L)] ^ T7[(int)(this.k06 & 255L)];
            this.Kr6 = T0[(int)(this.k06 >> 56 & 255L)] ^ T1[(int)(this.k05 >> 48 & 255L)] ^ T2[(int)(this.k04 >> 40 & 255L)] ^ T3[(int)(this.k03 >> 32 & 255L)] ^ T4[(int)(this.k02 >> 24 & 255L)] ^ T5[(int)(this.k01 >> 16 & 255L)] ^ T6[(int)(this.k00 >> 8 & 255L)] ^ T7[(int)(this.k07 & 255L)];
            this.Kr7 = T0[(int)(this.k07 >> 56 & 255L)] ^ T1[(int)(this.k06 >> 48 & 255L)] ^ T2[(int)(this.k05 >> 40 & 255L)] ^ T3[(int)(this.k04 >> 32 & 255L)] ^ T4[(int)(this.k03 >> 24 & 255L)] ^ T5[(int)(this.k02 >> 16 & 255L)] ^ T6[(int)(this.k01 >> 8 & 255L)] ^ T7[(int)(this.k00 & 255L)];
            this.k00 = this.Kr0;
            this.k01 = this.Kr1;
            this.k02 = this.Kr2;
            this.k03 = this.Kr3;
            this.k04 = this.Kr4;
            this.k05 = this.Kr5;
            this.k06 = this.Kr6;
            this.k07 = this.Kr7;
            this.w0 = T0[(int)(this.nn0 >> 56 & 255L)] ^ T1[(int)(this.nn7 >> 48 & 255L)] ^ T2[(int)(this.nn6 >> 40 & 255L)] ^ T3[(int)(this.nn5 >> 32 & 255L)] ^ T4[(int)(this.nn4 >> 24 & 255L)] ^ T5[(int)(this.nn3 >> 16 & 255L)] ^ T6[(int)(this.nn2 >> 8 & 255L)] ^ T7[(int)(this.nn1 & 255L)] ^ this.Kr0;
            this.w1 = T0[(int)(this.nn1 >> 56 & 255L)] ^ T1[(int)(this.nn0 >> 48 & 255L)] ^ T2[(int)(this.nn7 >> 40 & 255L)] ^ T3[(int)(this.nn6 >> 32 & 255L)] ^ T4[(int)(this.nn5 >> 24 & 255L)] ^ T5[(int)(this.nn4 >> 16 & 255L)] ^ T6[(int)(this.nn3 >> 8 & 255L)] ^ T7[(int)(this.nn2 & 255L)] ^ this.Kr1;
            this.w2 = T0[(int)(this.nn2 >> 56 & 255L)] ^ T1[(int)(this.nn1 >> 48 & 255L)] ^ T2[(int)(this.nn0 >> 40 & 255L)] ^ T3[(int)(this.nn7 >> 32 & 255L)] ^ T4[(int)(this.nn6 >> 24 & 255L)] ^ T5[(int)(this.nn5 >> 16 & 255L)] ^ T6[(int)(this.nn4 >> 8 & 255L)] ^ T7[(int)(this.nn3 & 255L)] ^ this.Kr2;
            this.w3 = T0[(int)(this.nn3 >> 56 & 255L)] ^ T1[(int)(this.nn2 >> 48 & 255L)] ^ T2[(int)(this.nn1 >> 40 & 255L)] ^ T3[(int)(this.nn0 >> 32 & 255L)] ^ T4[(int)(this.nn7 >> 24 & 255L)] ^ T5[(int)(this.nn6 >> 16 & 255L)] ^ T6[(int)(this.nn5 >> 8 & 255L)] ^ T7[(int)(this.nn4 & 255L)] ^ this.Kr3;
            this.w4 = T0[(int)(this.nn4 >> 56 & 255L)] ^ T1[(int)(this.nn3 >> 48 & 255L)] ^ T2[(int)(this.nn2 >> 40 & 255L)] ^ T3[(int)(this.nn1 >> 32 & 255L)] ^ T4[(int)(this.nn0 >> 24 & 255L)] ^ T5[(int)(this.nn7 >> 16 & 255L)] ^ T6[(int)(this.nn6 >> 8 & 255L)] ^ T7[(int)(this.nn5 & 255L)] ^ this.Kr4;
            this.w5 = T0[(int)(this.nn5 >> 56 & 255L)] ^ T1[(int)(this.nn4 >> 48 & 255L)] ^ T2[(int)(this.nn3 >> 40 & 255L)] ^ T3[(int)(this.nn2 >> 32 & 255L)] ^ T4[(int)(this.nn1 >> 24 & 255L)] ^ T5[(int)(this.nn0 >> 16 & 255L)] ^ T6[(int)(this.nn7 >> 8 & 255L)] ^ T7[(int)(this.nn6 & 255L)] ^ this.Kr5;
            this.w6 = T0[(int)(this.nn6 >> 56 & 255L)] ^ T1[(int)(this.nn5 >> 48 & 255L)] ^ T2[(int)(this.nn4 >> 40 & 255L)] ^ T3[(int)(this.nn3 >> 32 & 255L)] ^ T4[(int)(this.nn2 >> 24 & 255L)] ^ T5[(int)(this.nn1 >> 16 & 255L)] ^ T6[(int)(this.nn0 >> 8 & 255L)] ^ T7[(int)(this.nn7 & 255L)] ^ this.Kr6;
            this.w7 = T0[(int)(this.nn7 >> 56 & 255L)] ^ T1[(int)(this.nn6 >> 48 & 255L)] ^ T2[(int)(this.nn5 >> 40 & 255L)] ^ T3[(int)(this.nn4 >> 32 & 255L)] ^ T4[(int)(this.nn3 >> 24 & 255L)] ^ T5[(int)(this.nn2 >> 16 & 255L)] ^ T6[(int)(this.nn1 >> 8 & 255L)] ^ T7[(int)(this.nn0 & 255L)] ^ this.Kr7;
            this.nn0 = this.w0;
            this.nn1 = this.w1;
            this.nn2 = this.w2;
            this.nn3 = this.w3;
            this.nn4 = this.w4;
            this.nn5 = this.w5;
            this.nn6 = this.w6;
            this.nn7 = this.w7;
            ++r;
        }
        this.H0 ^= this.w0 ^ this.n0;
        this.H1 ^= this.w1 ^ this.n1;
        this.H2 ^= this.w2 ^ this.n2;
        this.H3 ^= this.w3 ^ this.n3;
        this.H4 ^= this.w4 ^ this.n4;
        this.H5 ^= this.w5 ^ this.n5;
        this.H6 ^= this.w6 ^ this.n6;
        this.H7 ^= this.w7 ^ this.n7;
    }

    protected final byte[] padBuffer() {
        int n = (int)((this.count + 33L) % 64L);
        int padding = n == 0 ? 33 : 64 - n + 33;
        byte[] result = new byte[padding];
        result[0] = -128;
        long bits = this.count * 8L;
        int i = padding - 8;
        result[i++] = (byte)(bits >>> 56);
        result[i++] = (byte)(bits >>> 48);
        result[i++] = (byte)(bits >>> 40);
        result[i++] = (byte)(bits >>> 32);
        result[i++] = (byte)(bits >>> 24);
        result[i++] = (byte)(bits >>> 16);
        result[i++] = (byte)(bits >>> 8);
        result[i] = (byte)bits;
        return result;
    }

    protected final byte[] getResult() {
        byte[] result = new byte[]{(byte)(this.H0 >>> 56), (byte)(this.H0 >>> 48), (byte)(this.H0 >>> 40), (byte)(this.H0 >>> 32), (byte)(this.H0 >>> 24), (byte)(this.H0 >>> 16), (byte)(this.H0 >>> 8), (byte)this.H0, (byte)(this.H1 >>> 56), (byte)(this.H1 >>> 48), (byte)(this.H1 >>> 40), (byte)(this.H1 >>> 32), (byte)(this.H1 >>> 24), (byte)(this.H1 >>> 16), (byte)(this.H1 >>> 8), (byte)this.H1, (byte)(this.H2 >>> 56), (byte)(this.H2 >>> 48), (byte)(this.H2 >>> 40), (byte)(this.H2 >>> 32), (byte)(this.H2 >>> 24), (byte)(this.H2 >>> 16), (byte)(this.H2 >>> 8), (byte)this.H2, (byte)(this.H3 >>> 56), (byte)(this.H3 >>> 48), (byte)(this.H3 >>> 40), (byte)(this.H3 >>> 32), (byte)(this.H3 >>> 24), (byte)(this.H3 >>> 16), (byte)(this.H3 >>> 8), (byte)this.H3, (byte)(this.H4 >>> 56), (byte)(this.H4 >>> 48), (byte)(this.H4 >>> 40), (byte)(this.H4 >>> 32), (byte)(this.H4 >>> 24), (byte)(this.H4 >>> 16), (byte)(this.H4 >>> 8), (byte)this.H4, (byte)(this.H5 >>> 56), (byte)(this.H5 >>> 48), (byte)(this.H5 >>> 40), (byte)(this.H5 >>> 32), (byte)(this.H5 >>> 24), (byte)(this.H5 >>> 16), (byte)(this.H5 >>> 8), (byte)this.H5, (byte)(this.H6 >>> 56), (byte)(this.H6 >>> 48), (byte)(this.H6 >>> 40), (byte)(this.H6 >>> 32), (byte)(this.H6 >>> 24), (byte)(this.H6 >>> 16), (byte)(this.H6 >>> 8), (byte)this.H6, (byte)(this.H7 >>> 56), (byte)(this.H7 >>> 48), (byte)(this.H7 >>> 40), (byte)(this.H7 >>> 32), (byte)(this.H7 >>> 24), (byte)(this.H7 >>> 16), (byte)(this.H7 >>> 8), (byte)this.H7};
        return result;
    }

    protected final void resetContext() {
        this.H7 = 0L;
        this.H6 = 0L;
        this.H5 = 0L;
        this.H4 = 0L;
        this.H3 = 0L;
        this.H2 = 0L;
        this.H1 = 0L;
        this.H0 = 0L;
    }

    public final boolean selfTest() {
        if (valid == null) {
            valid = new Boolean(DIGEST0.equals(Util.toString(new Whirlpool().digest())));
        }
        return valid;
    }

    public Whirlpool() {
        super("whirlpool", 20, 64);
    }

    private Whirlpool(Whirlpool md) {
        this();
        this.H0 = md.H0;
        this.H1 = md.H1;
        this.H2 = md.H2;
        this.H3 = md.H3;
        this.H4 = md.H4;
        this.H5 = md.H5;
        this.H6 = md.H6;
        this.H7 = md.H7;
        this.count = md.count;
        this.buffer = (byte[])md.buffer.clone();
    }

    private static final {
        long time = System.currentTimeMillis();
        int ROOT = 285;
        byte[] S = new byte[256];
        int i = 0;
        while (i < 256) {
            long t;
            int c = Sd.charAt(i >>> 1);
            long s = (long)((i & 1) == 0 ? c >>> 8 : c) & 255L;
            long s2 = s << 1;
            if (s2 > 255L) {
                s2 ^= (long)ROOT;
            }
            long s3 = s2 ^ s;
            long s4 = s2 << 1;
            if (s4 > 255L) {
                s4 ^= (long)ROOT;
            }
            long s5 = s4 ^ s;
            long s8 = s4 << 1;
            if (s8 > 255L) {
                s8 ^= (long)ROOT;
            }
            long s9 = s8 ^ s;
            S[i] = (byte)s;
            Whirlpool.T0[i] = t = s << 56 | s << 48 | s3 << 40 | s << 32 | s5 << 24 | s8 << 16 | s9 << 8 | s5;
            Whirlpool.T1[i] = t >>> 8 | t << 56;
            Whirlpool.T2[i] = t >>> 16 | t << 48;
            Whirlpool.T3[i] = t >>> 24 | t << 40;
            Whirlpool.T4[i] = t >>> 32 | t << 32;
            Whirlpool.T5[i] = t >>> 40 | t << 24;
            Whirlpool.T6[i] = t >>> 48 | t << 16;
            Whirlpool.T7[i] = t >>> 56 | t << 8;
            ++i;
        }
        int r = 1;
        i = 0;
        int j = 0;
        while (r < 11) {
            Whirlpool.rc[i++] = ((long)S[j++] & 255L) << 56 | ((long)S[j++] & 255L) << 48 | ((long)S[j++] & 255L) << 40 | ((long)S[j++] & 255L) << 32 | ((long)S[j++] & 255L) << 24 | ((long)S[j++] & 255L) << 16 | ((long)S[j++] & 255L) << 8 | (long)S[j++] & 255L;
            ++r;
        }
        time = System.currentTimeMillis() - time;
    }
}

