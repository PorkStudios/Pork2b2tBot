/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import gnu.crypto.cipher.BaseCipher;
import gnu.crypto.util.Util;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class Anubis
extends BaseCipher {
    private static final boolean DEBUG = false;
    private static final int debuglevel = 9;
    private static final int DEFAULT_BLOCK_SIZE = 16;
    private static final int DEFAULT_KEY_SIZE = 16;
    private static final String Sd = "\uba54\u2f74\u53d3\ud24d\u50ac\u8dbf\u7052\u9a4c\uead5\u97d1\u3351\u5ba6\ude48\ua899\udb32\ub7fc\ue39e\u919b\ue2bb\u416e\ua5cb\u6b95\ua1f3\ub102\uccc4\u1d14\uc363\uda5d\u5fdc\u7dcd\u7f5a\u6c5c\uf726\uffed\ue89d\u6f8e\u19a0\uf089\u0f07\uaffb\u0815\u0d04\u0164\udf76\u79dd\u3d16\u3f37\u6d38\ub973\ue935\u5571\u7b8c\u7288\uf62a\u3e5e\u2746\u0c65\u6861\u03c1\u57d6\ud958\ud866\ud73a\uc83c\ufa96\ua798\uecb8\uc7ae\u694b\uaba9\u670a\u47f2\ub522\ue5ee\ube2b\u8112\u831b\u0e23\uf545\u21ce\u492c\uf9e6\ub628\u1782\u1a8b\ufe8a\u09c9\u874e\ue12e\ue4e0\ueb90\ua41e\u8560%\uf4f1\u940b\ue775\uef34\u31d4\ud086\u7ead\ufd29\u303b\u9ff8\uc613\u0605\uc511\u777c\u7a78\u361c\u3959\u1856\ub3b0\u2420\ub292\ua3c0\u4462\u10b4\u8443\u93c2\u4abd\u8f2d\ubc9c\u6a40\ucfa2\u804f\u1fca\uaa42";
    private static final byte[] S;
    private static final int[] T0;
    private static final int[] T1;
    private static final int[] T2;
    private static final int[] T3;
    private static final int[] T4;
    private static final int[] T5;
    private static final int[] rc;
    private static final byte[] KAT_KEY;
    private static final byte[] KAT_CT;
    private static Boolean valid;

    private static final void anubis(byte[] in, int i, byte[] out, int j, int[][] K) {
        int R = K.length - 1;
        int[] Ker = K[0];
        int a0 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Ker[0];
        int a1 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Ker[1];
        int a2 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Ker[2];
        int a3 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i] & 255) ^ Ker[3];
        int r = 1;
        while (r < R) {
            Ker = K[r];
            int b0 = T0[a0 >>> 24] ^ T1[a1 >>> 24] ^ T2[a2 >>> 24] ^ T3[a3 >>> 24] ^ Ker[0];
            int b1 = T0[a0 >>> 16 & 255] ^ T1[a1 >>> 16 & 255] ^ T2[a2 >>> 16 & 255] ^ T3[a3 >>> 16 & 255] ^ Ker[1];
            int b2 = T0[a0 >>> 8 & 255] ^ T1[a1 >>> 8 & 255] ^ T2[a2 >>> 8 & 255] ^ T3[a3 >>> 8 & 255] ^ Ker[2];
            int b3 = T0[a0 & 255] ^ T1[a1 & 255] ^ T2[a2 & 255] ^ T3[a3 & 255] ^ Ker[3];
            a0 = b0;
            a1 = b1;
            a2 = b2;
            a3 = b3;
            ++r;
        }
        Ker = K[R];
        int tt = Ker[0];
        out[j++] = (byte)(S[a0 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(S[a1 >>> 24] ^ tt >>> 16);
        out[j++] = (byte)(S[a2 >>> 24] ^ tt >>> 8);
        out[j++] = (byte)(S[a3 >>> 24] ^ tt);
        tt = Ker[1];
        out[j++] = (byte)(S[a0 >>> 16 & 255] ^ tt >>> 24);
        out[j++] = (byte)(S[a1 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(S[a2 >>> 16 & 255] ^ tt >>> 8);
        out[j++] = (byte)(S[a3 >>> 16 & 255] ^ tt);
        tt = Ker[2];
        out[j++] = (byte)(S[a0 >>> 8 & 255] ^ tt >>> 24);
        out[j++] = (byte)(S[a1 >>> 8 & 255] ^ tt >>> 16);
        out[j++] = (byte)(S[a2 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(S[a3 >>> 8 & 255] ^ tt);
        tt = Ker[3];
        out[j++] = (byte)(S[a0 & 255] ^ tt >>> 24);
        out[j++] = (byte)(S[a1 & 255] ^ tt >>> 16);
        out[j++] = (byte)(S[a2 & 255] ^ tt >>> 8);
        out[j] = (byte)(S[a3 & 255] ^ tt);
    }

    public final Object clone() {
        Anubis result = new Anubis();
        result.currentBlockSize = this.currentBlockSize;
        return result;
    }

    public final Iterator blockSizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(16));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Iterator keySizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        int n = 4;
        while (n < 10) {
            al.add(new Integer(n * 32 / 8));
            ++n;
        }
        return Collections.unmodifiableList(al).iterator();
    }

    public final Object makeKey(byte[] uk, int bs) throws InvalidKeyException {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        if (uk == null) {
            throw new InvalidKeyException("Empty key");
        }
        if (uk.length % 4 != 0) {
            throw new InvalidKeyException("Key is not multiple of 32-bit.");
        }
        int N = uk.length / 4;
        if (N < 4 || N > 10) {
            throw new InvalidKeyException("Key is not 32N; 4 <= N <= 10");
        }
        int R = 8 + N;
        int[][] Ke = new int[R + 1][4];
        int[][] Kd = new int[R + 1][4];
        int[] tk = new int[N];
        int[] kk = new int[N];
        int r = 0;
        int i = 0;
        while (r < N) {
            tk[r++] = uk[i++] << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        }
        r = 0;
        while (r <= R) {
            if (r > 0) {
                kk[0] = T0[tk[0] >>> 24] ^ T1[tk[N - 1] >>> 16 & 255] ^ T2[tk[N - 2] >>> 8 & 255] ^ T3[tk[N - 3] & 255];
                kk[1] = T0[tk[1] >>> 24] ^ T1[tk[0] >>> 16 & 255] ^ T2[tk[N - 1] >>> 8 & 255] ^ T3[tk[N - 2] & 255];
                kk[2] = T0[tk[2] >>> 24] ^ T1[tk[1] >>> 16 & 255] ^ T2[tk[0] >>> 8 & 255] ^ T3[tk[N - 1] & 255];
                kk[3] = T0[tk[3] >>> 24] ^ T1[tk[2] >>> 16 & 255] ^ T2[tk[1] >>> 8 & 255] ^ T3[tk[0] & 255];
                i = 4;
                while (i < N) {
                    kk[i] = T0[tk[i] >>> 24] ^ T1[tk[i - 1] >>> 16 & 255] ^ T2[tk[i - 2] >>> 8 & 255] ^ T3[tk[i - 3] & 255];
                    ++i;
                }
                tk[0] = rc[r - 1] ^ kk[0];
                i = 1;
                while (i < N) {
                    tk[i] = kk[i];
                    ++i;
                }
            }
            int tt = tk[N - 1];
            int k0 = T4[tt >>> 24];
            int k1 = T4[tt >>> 16 & 255];
            int k2 = T4[tt >>> 8 & 255];
            int k3 = T4[tt & 255];
            int k = N - 2;
            while (k >= 0) {
                tt = tk[k];
                k0 = T4[tt >>> 24] ^ T5[k0 >>> 24 & 255] & -16777216 ^ T5[k0 >>> 16 & 255] & 16711680 ^ T5[k0 >>> 8 & 255] & 65280 ^ T5[k0 & 255] & 255;
                k1 = T4[tt >>> 16 & 255] ^ T5[k1 >>> 24 & 255] & -16777216 ^ T5[k1 >>> 16 & 255] & 16711680 ^ T5[k1 >>> 8 & 255] & 65280 ^ T5[k1 & 255] & 255;
                k2 = T4[tt >>> 8 & 255] ^ T5[k2 >>> 24 & 255] & -16777216 ^ T5[k2 >>> 16 & 255] & 16711680 ^ T5[k2 >>> 8 & 255] & 65280 ^ T5[k2 & 255] & 255;
                k3 = T4[tt & 255] ^ T5[k3 >>> 24 & 255] & -16777216 ^ T5[k3 >>> 16 & 255] & 16711680 ^ T5[k3 >>> 8 & 255] & 65280 ^ T5[k3 & 255] & 255;
                --k;
            }
            Ke[r][0] = k0;
            Ke[r][1] = k1;
            Ke[r][2] = k2;
            Ke[r][3] = k3;
            if (r == 0 || r == R) {
                Kd[R - r][0] = k0;
                Kd[R - r][1] = k1;
                Kd[R - r][2] = k2;
                Kd[R - r][3] = k3;
            } else {
                Kd[R - r][0] = T0[S[k0 >>> 24] & 255] ^ T1[S[k0 >>> 16 & 255] & 255] ^ T2[S[k0 >>> 8 & 255] & 255] ^ T3[S[k0 & 255] & 255];
                Kd[R - r][1] = T0[S[k1 >>> 24] & 255] ^ T1[S[k1 >>> 16 & 255] & 255] ^ T2[S[k1 >>> 8 & 255] & 255] ^ T3[S[k1 & 255] & 255];
                Kd[R - r][2] = T0[S[k2 >>> 24] & 255] ^ T1[S[k2 >>> 16 & 255] & 255] ^ T2[S[k2 >>> 8 & 255] & 255] ^ T3[S[k2 & 255] & 255];
                Kd[R - r][3] = T0[S[k3 >>> 24] & 255] ^ T1[S[k3 >>> 16 & 255] & 255] ^ T2[S[k3 >>> 8 & 255] & 255] ^ T3[S[k3 & 255] & 255];
            }
            ++r;
        }
        return new Object[]{Ke, Kd};
    }

    public final void encrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        int[][] K = (int[][])((Object[])k)[0];
        Anubis.anubis(in, i, out, j, K);
    }

    public final void decrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        int[][] K = (int[][])((Object[])k)[1];
        Anubis.anubis(in, i, out, j, K);
    }

    public final boolean selfTest() {
        if (valid == null) {
            boolean result = super.selfTest();
            if (result) {
                result = this.testKat(KAT_KEY, KAT_CT);
            }
            valid = new Boolean(result);
        }
        return valid;
    }

    public Anubis() {
        super("anubis", 16, 16);
    }

    private static final {
        int s;
        S = new byte[256];
        T0 = new int[256];
        T1 = new int[256];
        T2 = new int[256];
        T3 = new int[256];
        T4 = new int[256];
        T5 = new int[256];
        rc = new int[18];
        KAT_KEY = Util.toBytesFromString("000000000000000000002000000000000000000000000000");
        KAT_CT = Util.toBytesFromString("2E66AB15773F3D32FB6C697509460DF4");
        long time = System.currentTimeMillis();
        int ROOT = 285;
        int i = 0;
        while (i < 256) {
            int s4;
            int c = Sd.charAt(i >>> 1);
            s = ((i & 1) == 0 ? c >>> 8 : c) & 255;
            Anubis.S[i] = (byte)s;
            int s2 = s << 1;
            if (s2 > 255) {
                s2 ^= ROOT;
            }
            if ((s4 = s2 << 1) > 255) {
                s4 ^= ROOT;
            }
            int s6 = s4 ^ s2;
            int s8 = s4 << 1;
            if (s8 > 255) {
                s8 ^= ROOT;
            }
            Anubis.T0[i] = s << 24 | s2 << 16 | s4 << 8 | s6;
            Anubis.T1[i] = s2 << 24 | s << 16 | s6 << 8 | s4;
            Anubis.T2[i] = s4 << 24 | s6 << 16 | s << 8 | s2;
            Anubis.T3[i] = s6 << 24 | s4 << 16 | s2 << 8 | s;
            Anubis.T4[i] = s << 24 | s << 16 | s << 8 | s;
            Anubis.T5[s] = s << 24 | s2 << 16 | s6 << 8 | s8;
            ++i;
        }
        i = 0;
        s = 0;
        while (i < 18) {
            Anubis.rc[i++] = S[s++ & 255] << 24 | (S[s++ & 255] & 255) << 16 | (S[s++ & 255] & 255) << 8 | S[s++ & 255] & 255;
        }
        time = System.currentTimeMillis() - time;
    }
}

