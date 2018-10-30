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

public final class Khazad
extends BaseCipher {
    private static final boolean DEBUG = false;
    private static final int debuglevel = 9;
    private static final int DEFAULT_BLOCK_SIZE = 8;
    private static final int DEFAULT_KEY_SIZE = 16;
    private static final int R = 8;
    private static final String Sd = "\uba54\u2f74\u53d3\ud24d\u50ac\u8dbf\u7052\u9a4c\uead5\u97d1\u3351\u5ba6\ude48\ua899\udb32\ub7fc\ue39e\u919b\ue2bb\u416e\ua5cb\u6b95\ua1f3\ub102\uccc4\u1d14\uc363\uda5d\u5fdc\u7dcd\u7f5a\u6c5c\uf726\uffed\ue89d\u6f8e\u19a0\uf089\u0f07\uaffb\u0815\u0d04\u0164\udf76\u79dd\u3d16\u3f37\u6d38\ub973\ue935\u5571\u7b8c\u7288\uf62a\u3e5e\u2746\u0c65\u6861\u03c1\u57d6\ud958\ud866\ud73a\uc83c\ufa96\ua798\uecb8\uc7ae\u694b\uaba9\u670a\u47f2\ub522\ue5ee\ube2b\u8112\u831b\u0e23\uf545\u21ce\u492c\uf9e6\ub628\u1782\u1a8b\ufe8a\u09c9\u874e\ue12e\ue4e0\ueb90\ua41e\u8560%\uf4f1\u940b\ue775\uef34\u31d4\ud086\u7ead\ufd29\u303b\u9ff8\uc613\u0605\uc511\u777c\u7a78\u361c\u3959\u1856\ub3b0\u2420\ub292\ua3c0\u4462\u10b4\u8443\u93c2\u4abd\u8f2d\ubc9c\u6a40\ucfa2\u804f\u1fca\uaa42";
    private static final byte[] S = new byte[256];
    private static final int[] T0 = new int[256];
    private static final int[] T1 = new int[256];
    private static final int[] T2 = new int[256];
    private static final int[] T3 = new int[256];
    private static final int[] T4 = new int[256];
    private static final int[] T5 = new int[256];
    private static final int[] T6 = new int[256];
    private static final int[] T7 = new int[256];
    private static final int[][] rc = new int[9][2];
    private static final byte[] KAT_KEY = Util.toBytesFromString("00000000000000000000000000000100");
    private static final byte[] KAT_CT = Util.toBytesFromString("A0C86A1BBE2CBF4C");
    private static Boolean valid;

    private static final void khazad(byte[] in, int i, byte[] out, int j, int[][] K) {
        int k0 = K[0][0];
        int k1 = K[0][1];
        int a0 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ k0;
        int a1 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i] & 255) ^ k1;
        int r = 1;
        while (r < 8) {
            k0 = K[r][0];
            k1 = K[r][1];
            int b0 = T0[a0 >>> 24] ^ T1[a0 >>> 16 & 255] ^ T2[a0 >>> 8 & 255] ^ T3[a0 & 255] ^ T4[a1 >>> 24] ^ T5[a1 >>> 16 & 255] ^ T6[a1 >>> 8 & 255] ^ T7[a1 & 255] ^ k0;
            int b1 = T0[a1 >>> 24] ^ T1[a1 >>> 16 & 255] ^ T2[a1 >>> 8 & 255] ^ T3[a1 & 255] ^ T4[a0 >>> 24] ^ T5[a0 >>> 16 & 255] ^ T6[a0 >>> 8 & 255] ^ T7[a0 & 255] ^ k1;
            a0 = b0;
            a1 = b1;
            ++r;
        }
        k0 = K[8][0];
        k1 = K[8][1];
        out[j++] = (byte)(S[a0 >>> 24] ^ k0 >>> 24);
        out[j++] = (byte)(S[a0 >>> 16 & 255] ^ k0 >>> 16);
        out[j++] = (byte)(S[a0 >>> 8 & 255] ^ k0 >>> 8);
        out[j++] = (byte)(S[a0 & 255] ^ k0);
        out[j++] = (byte)(S[a1 >>> 24] ^ k1 >>> 24);
        out[j++] = (byte)(S[a1 >>> 16 & 255] ^ k1 >>> 16);
        out[j++] = (byte)(S[a1 >>> 8 & 255] ^ k1 >>> 8);
        out[j] = (byte)(S[a1 & 255] ^ k1);
    }

    public final Object clone() {
        Khazad result = new Khazad();
        result.currentBlockSize = this.currentBlockSize;
        return result;
    }

    public final Iterator blockSizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(8));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Iterator keySizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(16));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Object makeKey(byte[] uk, int bs) throws InvalidKeyException {
        if (bs != 8) {
            throw new IllegalArgumentException();
        }
        if (uk == null) {
            throw new InvalidKeyException("Empty key");
        }
        if (uk.length != 16) {
            throw new InvalidKeyException("Key is not 128-bit.");
        }
        int[][] Ke = new int[9][2];
        int[][] Kd = new int[9][2];
        int i = 0;
        int k20 = uk[i++] << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        int k21 = uk[i++] << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        int k10 = uk[i++] << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        int k11 = uk[i++] << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        int r = 0;
        i = 0;
        while (r <= 8) {
            int rc0 = rc[r][0];
            int rc1 = rc[r][1];
            int kr0 = T0[k10 >>> 24] ^ T1[k10 >>> 16 & 255] ^ T2[k10 >>> 8 & 255] ^ T3[k10 & 255] ^ T4[k11 >>> 24 & 255] ^ T5[k11 >>> 16 & 255] ^ T6[k11 >>> 8 & 255] ^ T7[k11 & 255] ^ rc0 ^ k20;
            int kr1 = T0[k11 >>> 24] ^ T1[k11 >>> 16 & 255] ^ T2[k11 >>> 8 & 255] ^ T3[k11 & 255] ^ T4[k10 >>> 24 & 255] ^ T5[k10 >>> 16 & 255] ^ T6[k10 >>> 8 & 255] ^ T7[k10 & 255] ^ rc1 ^ k21;
            Ke[r][0] = kr0;
            Ke[r][1] = kr1;
            k20 = k10;
            k21 = k11;
            k10 = kr0;
            k11 = kr1;
            if (r == 0 || r == 8) {
                Kd[8 - r][0] = kr0;
                Kd[8 - r][1] = kr1;
            } else {
                Kd[8 - r][0] = T0[S[kr0 >>> 24] & 255] ^ T1[S[kr0 >>> 16 & 255] & 255] ^ T2[S[kr0 >>> 8 & 255] & 255] ^ T3[S[kr0 & 255] & 255] ^ T4[S[kr1 >>> 24] & 255] ^ T5[S[kr1 >>> 16 & 255] & 255] ^ T6[S[kr1 >>> 8 & 255] & 255] ^ T7[S[kr1 & 255] & 255];
                Kd[8 - r][1] = T0[S[kr1 >>> 24] & 255] ^ T1[S[kr1 >>> 16 & 255] & 255] ^ T2[S[kr1 >>> 8 & 255] & 255] ^ T3[S[kr1 & 255] & 255] ^ T4[S[kr0 >>> 24] & 255] ^ T5[S[kr0 >>> 16 & 255] & 255] ^ T6[S[kr0 >>> 8 & 255] & 255] ^ T7[S[kr0 & 255] & 255];
            }
            ++r;
        }
        return new Object[]{Ke, Kd};
    }

    public final void encrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 8) {
            throw new IllegalArgumentException();
        }
        int[][] K = (int[][])((Object[])k)[0];
        Khazad.khazad(in, i, out, j, K);
    }

    public final void decrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 8) {
            throw new IllegalArgumentException();
        }
        int[][] K = (int[][])((Object[])k)[1];
        Khazad.khazad(in, i, out, j, K);
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

    public Khazad() {
        super("khazad", 8, 16);
    }

    private static final {
        long time = System.currentTimeMillis();
        long ROOT = 285L;
        int i = 0;
        while (i < 256) {
            int c = Sd.charAt(i >>> 1);
            int s = ((i & 1) == 0 ? c >>> 8 : c) & 255;
            Khazad.S[i] = (byte)s;
            int s2 = s << 1;
            if (s2 > 255) {
                s2 = (int)((long)s2 ^ ROOT);
            }
            int s3 = s2 ^ s;
            int s4 = s2 << 1;
            if (s4 > 255) {
                s4 = (int)((long)s4 ^ ROOT);
            }
            int s5 = s4 ^ s;
            int s6 = s4 ^ s2;
            int s7 = s6 ^ s;
            int s8 = s4 << 1;
            if (s8 > 255) {
                s8 = (int)((long)s8 ^ ROOT);
            }
            int sb = s8 ^ s2 ^ s;
            Khazad.T0[i] = s << 24 | s3 << 16 | s4 << 8 | s5;
            Khazad.T1[i] = s3 << 24 | s << 16 | s5 << 8 | s4;
            Khazad.T2[i] = s4 << 24 | s5 << 16 | s << 8 | s3;
            Khazad.T3[i] = s5 << 24 | s4 << 16 | s3 << 8 | s;
            Khazad.T4[i] = s6 << 24 | s8 << 16 | sb << 8 | s7;
            Khazad.T5[i] = s8 << 24 | s6 << 16 | s7 << 8 | sb;
            Khazad.T6[i] = sb << 24 | s7 << 16 | s6 << 8 | s8;
            Khazad.T7[i] = s7 << 24 | sb << 16 | s8 << 8 | s6;
            ++i;
        }
        i = 0;
        int j = 0;
        while (i < 9) {
            Khazad.rc[i][0] = S[j++] << 24 | (S[j++] & 255) << 16 | (S[j++] & 255) << 8 | S[j++] & 255;
            Khazad.rc[i][1] = S[j++] << 24 | (S[j++] & 255) << 16 | (S[j++] & 255) << 8 | S[j++] & 255;
            ++i;
        }
        time = System.currentTimeMillis() - time;
    }
}

