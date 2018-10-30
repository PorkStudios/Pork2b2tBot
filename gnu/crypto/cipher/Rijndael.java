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

public final class Rijndael
extends BaseCipher {
    private static final boolean DEBUG = false;
    private static final int debuglevel = 9;
    private static final int DEFAULT_BLOCK_SIZE = 16;
    private static final int DEFAULT_KEY_SIZE = 16;
    private static final String SS = "\u637c\u777b\uf26b\u6fc5\u3001\u672b\ufed7\uab76\uca82\uc97d\ufa59\u47f0\uadd4\ua2af\u9ca4\u72c0\ub7fd\u9326\u363f\uf7cc\u34a5\ue5f1\u71d8\u3115\u04c7\u23c3\u1896\u059a\u0712\u80e2\ueb27\ub275\u0983\u2c1a\u1b6e\u5aa0\u523b\ud6b3\u29e3\u2f84\u53d1\u00ed\u20fc\ub15b\u6acb\ube39\u4a4c\u58cf\ud0ef\uaafb\u434d\u3385\u45f9\u027f\u503c\u9fa8\u51a3\u408f\u929d\u38f5\ubcb6\uda21\u10ff\uf3d2\ucd0c\u13ec\u5f97\u4417\uc4a7\u7e3d\u645d\u1973\u6081\u4fdc\u222a\u9088\u46ee\ub814\ude5e\u0bdb\ue032\u3a0a\u4906\u245c\uc2d3\uac62\u9195\ue479\ue7c8\u376d\u8dd5\u4ea9\u6c56\uf4ea\u657a\uae08\uba78\u252e\u1ca6\ub4c6\ue8dd\u741f\u4bbd\u8b8a\u703e\ub566\u4803\uf60e\u6135\u57b9\u86c1\u1d9e\ue1f8\u9811\u69d9\u8e94\u9b1e\u87e9\uce55\u28df\u8ca1\u890d\ubfe6\u4268\u4199\u2d0f\ub054\ubb16";
    private static final byte[] S = new byte[256];
    private static final byte[] Si = new byte[256];
    private static final int[] T1 = new int[256];
    private static final int[] T2 = new int[256];
    private static final int[] T3 = new int[256];
    private static final int[] T4 = new int[256];
    private static final int[] T5 = new int[256];
    private static final int[] T6 = new int[256];
    private static final int[] T7 = new int[256];
    private static final int[] T8 = new int[256];
    private static final int[] U1 = new int[256];
    private static final int[] U2 = new int[256];
    private static final int[] U3 = new int[256];
    private static final int[] U4 = new int[256];
    private static final byte[] rcon = new byte[30];
    private static final int[][][] shifts = new int[][][]{{new int[2], {1, 3}, {2, 2}, {3, 1}}, {new int[2], {1, 5}, {2, 4}, {3, 3}}, {new int[2], {1, 7}, {3, 5}, {4, 4}}};
    private static final byte[] KAT_KEY = Util.toBytesFromString("0000000000000000000000010000000000000000000000000000000000000000");
    private static final byte[] KAT_CT = Util.toBytesFromString("E44429474D6FC3084EB2A6B8B46AF754");
    private static Boolean valid;

    public static final int getRounds(int ks, int bs) {
        switch (ks) {
            case 16: {
                return bs == 16 ? 10 : (bs == 24 ? 12 : 14);
            }
            case 24: {
                return bs != 32 ? 12 : 14;
            }
        }
        return 14;
    }

    private static final void rijndaelEncrypt(byte[] in, int inOffset, byte[] out, int outOffset, Object sessionKey, int bs) {
        Object[] sKey = (Object[])sessionKey;
        int[][] Ke = (int[][])sKey[0];
        int BC = bs / 4;
        int ROUNDS = Ke.length - 1;
        int n = 0;
        if (BC != 4) {
            int n2 = 0;
            if (BC == 6) {
                n2 = 1;
            }
            n = 2 - n2;
        }
        int SC = n;
        int s1 = shifts[SC][1][0];
        int s2 = shifts[SC][2][0];
        int s3 = shifts[SC][3][0];
        int[] a = new int[BC];
        int[] t = new int[BC];
        int i = 0;
        while (i < BC) {
            t[i] = (in[inOffset++] << 24 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 8 | in[inOffset++] & 255) ^ Ke[0][i];
            ++i;
        }
        int r = 1;
        while (r < ROUNDS) {
            i = 0;
            while (i < BC) {
                a[i] = T1[t[i] >>> 24] ^ T2[t[(i + s1) % BC] >>> 16 & 255] ^ T3[t[(i + s2) % BC] >>> 8 & 255] ^ T4[t[(i + s3) % BC] & 255] ^ Ke[r][i];
                ++i;
            }
            System.arraycopy(a, 0, t, 0, BC);
            ++r;
        }
        i = 0;
        while (i < BC) {
            int tt = Ke[ROUNDS][i];
            out[outOffset++] = (byte)(S[t[i] >>> 24] ^ tt >>> 24);
            out[outOffset++] = (byte)(S[t[(i + s1) % BC] >>> 16 & 255] ^ tt >>> 16);
            out[outOffset++] = (byte)(S[t[(i + s2) % BC] >>> 8 & 255] ^ tt >>> 8);
            out[outOffset++] = (byte)(S[t[(i + s3) % BC] & 255] ^ tt);
            ++i;
        }
    }

    private static final void rijndaelDecrypt(byte[] in, int inOffset, byte[] out, int outOffset, Object sessionKey, int bs) {
        Object[] sKey = (Object[])sessionKey;
        int[][] Kd = (int[][])sKey[1];
        int BC = bs / 4;
        int ROUNDS = Kd.length - 1;
        int n = 0;
        if (BC != 4) {
            int n2 = 0;
            if (BC == 6) {
                n2 = 1;
            }
            n = 2 - n2;
        }
        int SC = n;
        int s1 = shifts[SC][1][1];
        int s2 = shifts[SC][2][1];
        int s3 = shifts[SC][3][1];
        int[] a = new int[BC];
        int[] t = new int[BC];
        int i = 0;
        while (i < BC) {
            t[i] = (in[inOffset++] << 24 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 8 | in[inOffset++] & 255) ^ Kd[0][i];
            ++i;
        }
        int r = 1;
        while (r < ROUNDS) {
            i = 0;
            while (i < BC) {
                a[i] = T5[t[i] >>> 24] ^ T6[t[(i + s1) % BC] >>> 16 & 255] ^ T7[t[(i + s2) % BC] >>> 8 & 255] ^ T8[t[(i + s3) % BC] & 255] ^ Kd[r][i];
                ++i;
            }
            System.arraycopy(a, 0, t, 0, BC);
            ++r;
        }
        i = 0;
        while (i < BC) {
            int tt = Kd[ROUNDS][i];
            out[outOffset++] = (byte)(Si[t[i] >>> 24] ^ tt >>> 24);
            out[outOffset++] = (byte)(Si[t[(i + s1) % BC] >>> 16 & 255] ^ tt >>> 16);
            out[outOffset++] = (byte)(Si[t[(i + s2) % BC] >>> 8 & 255] ^ tt >>> 8);
            out[outOffset++] = (byte)(Si[t[(i + s3) % BC] & 255] ^ tt);
            ++i;
        }
    }

    private static final void aesEncrypt(byte[] in, int i, byte[] out, int j, Object key) {
        int[][] Ke = (int[][])((Object[])key)[0];
        int ROUNDS = Ke.length - 1;
        int[] Ker = Ke[0];
        int t0 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Ker[0];
        int t1 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Ker[1];
        int t2 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Ker[2];
        int t3 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Ker[3];
        int r = 1;
        while (r < ROUNDS) {
            Ker = Ke[r];
            int a0 = T1[t0 >>> 24] ^ T2[t1 >>> 16 & 255] ^ T3[t2 >>> 8 & 255] ^ T4[t3 & 255] ^ Ker[0];
            int a1 = T1[t1 >>> 24] ^ T2[t2 >>> 16 & 255] ^ T3[t3 >>> 8 & 255] ^ T4[t0 & 255] ^ Ker[1];
            int a2 = T1[t2 >>> 24] ^ T2[t3 >>> 16 & 255] ^ T3[t0 >>> 8 & 255] ^ T4[t1 & 255] ^ Ker[2];
            int a3 = T1[t3 >>> 24] ^ T2[t0 >>> 16 & 255] ^ T3[t1 >>> 8 & 255] ^ T4[t2 & 255] ^ Ker[3];
            t0 = a0;
            t1 = a1;
            t2 = a2;
            t3 = a3;
            ++r;
        }
        Ker = Ke[ROUNDS];
        int tt = Ker[0];
        out[j++] = (byte)(S[t0 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(S[t1 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(S[t2 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(S[t3 & 255] ^ tt);
        tt = Ker[1];
        out[j++] = (byte)(S[t1 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(S[t2 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(S[t3 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(S[t0 & 255] ^ tt);
        tt = Ker[2];
        out[j++] = (byte)(S[t2 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(S[t3 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(S[t0 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(S[t1 & 255] ^ tt);
        tt = Ker[3];
        out[j++] = (byte)(S[t3 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(S[t0 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(S[t1 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(S[t2 & 255] ^ tt);
    }

    private static final void aesDecrypt(byte[] in, int i, byte[] out, int j, Object key) {
        int[][] Kd = (int[][])((Object[])key)[1];
        int ROUNDS = Kd.length - 1;
        int[] Kdr = Kd[0];
        int t0 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Kdr[0];
        int t1 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Kdr[1];
        int t2 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Kdr[2];
        int t3 = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ Kdr[3];
        int r = 1;
        while (r < ROUNDS) {
            Kdr = Kd[r];
            int a0 = T5[t0 >>> 24] ^ T6[t3 >>> 16 & 255] ^ T7[t2 >>> 8 & 255] ^ T8[t1 & 255] ^ Kdr[0];
            int a1 = T5[t1 >>> 24] ^ T6[t0 >>> 16 & 255] ^ T7[t3 >>> 8 & 255] ^ T8[t2 & 255] ^ Kdr[1];
            int a2 = T5[t2 >>> 24] ^ T6[t1 >>> 16 & 255] ^ T7[t0 >>> 8 & 255] ^ T8[t3 & 255] ^ Kdr[2];
            int a3 = T5[t3 >>> 24] ^ T6[t2 >>> 16 & 255] ^ T7[t1 >>> 8 & 255] ^ T8[t0 & 255] ^ Kdr[3];
            t0 = a0;
            t1 = a1;
            t2 = a2;
            t3 = a3;
            ++r;
        }
        Kdr = Kd[ROUNDS];
        int tt = Kdr[0];
        out[j++] = (byte)(Si[t0 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(Si[t3 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(Si[t2 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(Si[t1 & 255] ^ tt);
        tt = Kdr[1];
        out[j++] = (byte)(Si[t1 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(Si[t0 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(Si[t3 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(Si[t2 & 255] ^ tt);
        tt = Kdr[2];
        out[j++] = (byte)(Si[t2 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(Si[t1 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(Si[t0 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(Si[t3 & 255] ^ tt);
        tt = Kdr[3];
        out[j++] = (byte)(Si[t3 >>> 24] ^ tt >>> 24);
        out[j++] = (byte)(Si[t2 >>> 16 & 255] ^ tt >>> 16);
        out[j++] = (byte)(Si[t1 >>> 8 & 255] ^ tt >>> 8);
        out[j++] = (byte)(Si[t0 & 255] ^ tt);
    }

    public final Object clone() {
        Rijndael result = new Rijndael();
        result.currentBlockSize = this.currentBlockSize;
        return result;
    }

    public final Iterator blockSizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(16));
        al.add(new Integer(24));
        al.add(new Integer(32));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Iterator keySizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(16));
        al.add(new Integer(24));
        al.add(new Integer(32));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Object makeKey(byte[] k, int bs) throws InvalidKeyException {
        int tt;
        if (k == null) {
            throw new InvalidKeyException("Empty key");
        }
        if (k.length != 16 && k.length != 24 && k.length != 32) {
            throw new InvalidKeyException("Incorrect key length");
        }
        if (bs != 16 && bs != 24 && bs != 32) {
            throw new IllegalArgumentException();
        }
        int ROUNDS = Rijndael.getRounds(k.length, bs);
        int BC = bs / 4;
        int[][] Ke = new int[ROUNDS + 1][BC];
        int[][] Kd = new int[ROUNDS + 1][BC];
        int ROUND_KEY_COUNT = (ROUNDS + 1) * BC;
        int KC = k.length / 4;
        int[] tk = new int[KC];
        int i = 0;
        int j = 0;
        while (i < KC) {
            tk[i++] = k[j++] << 24 | (k[j++] & 255) << 16 | (k[j++] & 255) << 8 | k[j++] & 255;
        }
        int t = 0;
        j = 0;
        while (j < KC && t < ROUND_KEY_COUNT) {
            Ke[t / BC][t % BC] = tk[j];
            Kd[ROUNDS - t / BC][t % BC] = tk[j];
            ++j;
            ++t;
        }
        int rconpointer = 0;
        while (t < ROUND_KEY_COUNT) {
            tt = tk[KC - 1];
            int[] arrn = tk;
            arrn[0] = arrn[0] ^ ((S[tt >>> 16 & 255] & 255) << 24 ^ (S[tt >>> 8 & 255] & 255) << 16 ^ (S[tt & 255] & 255) << 8 ^ S[tt >>> 24] & 255 ^ rcon[rconpointer++] << 24);
            if (KC != 8) {
                i = 1;
                j = 0;
                while (i < KC) {
                    int[] arrn2 = tk;
                    int n = i++;
                    arrn2[n] = arrn2[n] ^ tk[j++];
                }
            } else {
                i = 1;
                j = 0;
                while (i < KC / 2) {
                    int[] arrn3 = tk;
                    int n = i++;
                    arrn3[n] = arrn3[n] ^ tk[j++];
                }
                tt = tk[KC / 2 - 1];
                int[] arrn4 = tk;
                int n = KC / 2;
                arrn4[n] = arrn4[n] ^ (S[tt & 255] & 255 ^ (S[tt >>> 8 & 255] & 255) << 8 ^ (S[tt >>> 16 & 255] & 255) << 16 ^ S[tt >>> 24 & 255] << 24);
                j = KC / 2;
                i = j + 1;
                while (i < KC) {
                    int[] arrn5 = tk;
                    int n2 = i++;
                    arrn5[n2] = arrn5[n2] ^ tk[j++];
                }
            }
            j = 0;
            while (j < KC && t < ROUND_KEY_COUNT) {
                Ke[t / BC][t % BC] = tk[j];
                Kd[ROUNDS - t / BC][t % BC] = tk[j];
                ++j;
                ++t;
            }
        }
        int r = 1;
        while (r < ROUNDS) {
            j = 0;
            while (j < BC) {
                tt = Kd[r][j];
                Kd[r][j] = U1[tt >>> 24] ^ U2[tt >>> 16 & 255] ^ U3[tt >>> 8 & 255] ^ U4[tt & 255];
                ++j;
            }
            ++r;
        }
        return new Object[]{Ke, Kd};
    }

    public final void encrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 16 && bs != 24 && bs != 32) {
            throw new IllegalArgumentException();
        }
        if (bs == 16) {
            Rijndael.aesEncrypt(in, i, out, j, k);
        } else {
            Rijndael.rijndaelEncrypt(in, i, out, j, k, bs);
        }
    }

    public final void decrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 16 && bs != 24 && bs != 32) {
            throw new IllegalArgumentException();
        }
        if (bs == 16) {
            Rijndael.aesDecrypt(in, i, out, j, k);
        } else {
            Rijndael.rijndaelDecrypt(in, i, out, j, k, bs);
        }
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

    public Rijndael() {
        super("rijndael", 16, 16);
    }

    private static final {
        long time = System.currentTimeMillis();
        int ROOT = 283;
        boolean j = false;
        int i = 0;
        while (i < 256) {
            int t;
            int i8;
            int i4;
            char c = SS.charAt(i >>> 1);
            Rijndael.S[i] = (byte)((i & 1) == 0 ? c >>> 8 : c & 255);
            int s = S[i] & 255;
            Rijndael.Si[s] = (byte)i;
            int s2 = s << 1;
            if (s2 >= 256) {
                s2 ^= ROOT;
            }
            int s3 = s2 ^ s;
            int i2 = i << 1;
            if (i2 >= 256) {
                i2 ^= ROOT;
            }
            if ((i4 = i2 << 1) >= 256) {
                i4 ^= ROOT;
            }
            if ((i8 = i4 << 1) >= 256) {
                i8 ^= ROOT;
            }
            int i9 = i8 ^ i;
            int ib = i9 ^ i2;
            int id = i9 ^ i4;
            int ie = i8 ^ i4 ^ i2;
            Rijndael.T1[i] = t = s2 << 24 | s << 16 | s << 8 | s3;
            Rijndael.T2[i] = t >>> 8 | t << 24;
            Rijndael.T3[i] = t >>> 16 | t << 16;
            Rijndael.T4[i] = t >>> 24 | t << 8;
            Rijndael.U1[i] = t = ie << 24 | i9 << 16 | id << 8 | ib;
            Rijndael.T5[s] = t;
            Rijndael.T6[s] = Rijndael.U2[i] = t >>> 8 | t << 24;
            Rijndael.T7[s] = Rijndael.U3[i] = t >>> 16 | t << 16;
            Rijndael.T8[s] = Rijndael.U4[i] = t >>> 24 | t << 8;
            ++i;
        }
        int r = 1;
        Rijndael.rcon[0] = 1;
        i = 1;
        while (i < 30) {
            if ((r <<= 1) >= 256) {
                r ^= ROOT;
            }
            Rijndael.rcon[i] = (byte)r;
            ++i;
        }
        time = System.currentTimeMillis() - time;
    }
}

