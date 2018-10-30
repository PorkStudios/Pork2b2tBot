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

public final class Square
extends BaseCipher {
    private static final int DEFAULT_BLOCK_SIZE = 16;
    private static final int DEFAULT_KEY_SIZE = 16;
    private static final int ROUNDS = 8;
    private static final int ROOT = 501;
    private static final int[] OFFSET = new int[8];
    private static final String Sdata = "\ub1ce\uc395\u5aad\ue702\u4d44\ufb91\u0c87\ua150\ucb67\u54dd\u468f\ue14e\uf0fd\ufceb\uf9c4\u1a6e\u5ef5\ucc8d\u1c56\u43fe\u0761\uf875\u59ff\u0322\u8ad1\u13ee\u8800\u0e34\u1580\u94e3\uedb5\u5323\u4b47\u17a7\u9035\uabd8\ub8df\u4f57\u9a92\udb1b\u3cc8\u9904\u8ee0\ud77d\u85bb\u402c\u3a45\uf142\u6520\u4118\u7225\u9370\u3605\uf20b\ua379\uec08\u2731\u32b6\u7cb0\u0a73\u5b7b\ub781\ud20d\u6a26\u9e58\u9c83\u74b3\uac30\u7a69\u770f\uae21\uded0\u2e97\u10a4\u98a8\ud468\u2d62\u296d\u1649\u76c7\ue8c1\u9637\ue5ca\uf4e9\u6312\uc2a6\u14bc\ud328\uaf2f\ue624\u52c6\ua009\ubd8c\ucf5d\u115f\u01c5\u9f3d\ua29b\uc93b\ube51\u191f\u3f5c\ub2ef\u4acd\ubfba\u6f64\ud9f3\u3eb4\uaadc\ud506\uc07e\uf666\u6c84\u7138\ub91d\u7f9d\u488b\u2ada\ua533\u8239\ud678\u86fa\ue42b\ua91e\u8960\u6bea\u554c\uf7e2";
    private static final byte[] Se = new byte[256];
    private static final byte[] Sd = new byte[256];
    private static final int[] Te = new int[256];
    private static final int[] Td = new int[256];
    private static final byte[] KAT_KEY = Util.toBytesFromString("00000000000000000000020000000000");
    private static final byte[] KAT_CT = Util.toBytesFromString("A9DF031B4E25E89F527EFFF89CB0BEBA");
    private static Boolean valid;

    private static final void square(byte[] in, int i, byte[] out, int j, int[][] K, int[] T, byte[] S) {
        int dd;
        int bb;
        int aa;
        int cc;
        int a = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ K[0][0];
        int b = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ K[0][1];
        int c = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255) ^ K[0][2];
        int d = (in[i++] << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i] & 255) ^ K[0][3];
        int r = 1;
        while (r < 8) {
            aa = T[a >>> 24] ^ Square.rot32R(T[b >>> 24], 8) ^ Square.rot32R(T[c >>> 24], 16) ^ Square.rot32R(T[d >>> 24], 24) ^ K[r][0];
            bb = T[a >>> 16 & 255] ^ Square.rot32R(T[b >>> 16 & 255], 8) ^ Square.rot32R(T[c >>> 16 & 255], 16) ^ Square.rot32R(T[d >>> 16 & 255], 24) ^ K[r][1];
            cc = T[a >>> 8 & 255] ^ Square.rot32R(T[b >>> 8 & 255], 8) ^ Square.rot32R(T[c >>> 8 & 255], 16) ^ Square.rot32R(T[d >>> 8 & 255], 24) ^ K[r][2];
            dd = T[a & 255] ^ Square.rot32R(T[b & 255], 8) ^ Square.rot32R(T[c & 255], 16) ^ Square.rot32R(T[d & 255], 24) ^ K[r][3];
            a = aa;
            b = bb;
            c = cc;
            d = dd;
            ++r;
        }
        aa = (S[a >>> 24] << 24 | (S[b >>> 24] & 255) << 16 | (S[c >>> 24] & 255) << 8 | S[d >>> 24] & 255) ^ K[r][0];
        bb = (S[a >>> 16 & 255] << 24 | (S[b >>> 16 & 255] & 255) << 16 | (S[c >>> 16 & 255] & 255) << 8 | S[d >>> 16 & 255] & 255) ^ K[r][1];
        cc = (S[a >>> 8 & 255] << 24 | (S[b >>> 8 & 255] & 255) << 16 | (S[c >>> 8 & 255] & 255) << 8 | S[d >>> 8 & 255] & 255) ^ K[r][2];
        dd = (S[a & 255] << 24 | (S[b & 255] & 255) << 16 | (S[c & 255] & 255) << 8 | S[d & 255] & 255) ^ K[r][3];
        out[j++] = (byte)(aa >>> 24);
        out[j++] = (byte)(aa >>> 16);
        out[j++] = (byte)(aa >>> 8);
        out[j++] = (byte)aa;
        out[j++] = (byte)(bb >>> 24);
        out[j++] = (byte)(bb >>> 16);
        out[j++] = (byte)(bb >>> 8);
        out[j++] = (byte)bb;
        out[j++] = (byte)(cc >>> 24);
        out[j++] = (byte)(cc >>> 16);
        out[j++] = (byte)(cc >>> 8);
        out[j++] = (byte)cc;
        out[j++] = (byte)(dd >>> 24);
        out[j++] = (byte)(dd >>> 16);
        out[j++] = (byte)(dd >>> 8);
        out[j] = (byte)dd;
    }

    private static final void transform(int[] in, int[] out) {
        int i = 0;
        while (i < 4) {
            int l3 = in[i];
            int l2 = l3 >>> 8;
            int l1 = l3 >>> 16;
            int l0 = l3 >>> 24;
            int m = ((Square.mul(l0, 2) ^ Square.mul(l1, 3) ^ l2 ^ l3) & 255) << 24;
            m ^= ((l0 ^ Square.mul(l1, 2) ^ Square.mul(l2, 3) ^ l3) & 255) << 16;
            m ^= ((l0 ^ l1 ^ Square.mul(l2, 2) ^ Square.mul(l3, 3)) & 255) << 8;
            out[i] = m ^= (Square.mul(l0, 3) ^ l1 ^ l2 ^ Square.mul(l3, 2)) & 255;
            ++i;
        }
    }

    private static final int rot32L(int x, int s) {
        return x << s | x >>> 32 - s;
    }

    private static final int rot32R(int x, int s) {
        return x >>> s | x << 32 - s;
    }

    private static final int mul(int a, int b) {
        if (a == 0) {
            return 0;
        }
        a &= 255;
        int result = 0;
        while ((b &= 255) != 0) {
            if ((b & 1) != 0) {
                result ^= a;
            }
            b >>>= 1;
            if ((a <<= 1) <= 255) continue;
            a ^= 501;
        }
        return result & 255;
    }

    public final Object clone() {
        Square result = new Square();
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
        al.add(new Integer(16));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Object makeKey(byte[] uk, int bs) throws InvalidKeyException {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        if (uk == null) {
            throw new InvalidKeyException("Empty key");
        }
        if (uk.length != 16) {
            throw new InvalidKeyException("Key is not 128-bit.");
        }
        int[][] Ke = new int[9][4];
        int[][] Kd = new int[9][4];
        int[][] tK = new int[9][4];
        int i = 0;
        Ke[0][0] = (uk[i++] & 255) << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        tK[0][0] = Ke[0][0];
        Ke[0][1] = (uk[i++] & 255) << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        tK[0][1] = Ke[0][1];
        Ke[0][2] = (uk[i++] & 255) << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i++] & 255;
        tK[0][2] = Ke[0][2];
        Ke[0][3] = (uk[i++] & 255) << 24 | (uk[i++] & 255) << 16 | (uk[i++] & 255) << 8 | uk[i] & 255;
        tK[0][3] = Ke[0][3];
        i = 1;
        int j = 0;
        while (i < 9) {
            tK[i][0] = tK[j][0] ^ Square.rot32L(tK[j][3], 8) ^ OFFSET[j];
            tK[i][1] = tK[j][1] ^ tK[i][0];
            tK[i][2] = tK[j][2] ^ tK[i][1];
            tK[i][3] = tK[j][3] ^ tK[i][2];
            System.arraycopy(tK[i], 0, Ke[i], 0, 4);
            Square.transform(Ke[j], Ke[j]);
            ++i;
            ++j;
        }
        i = 0;
        while (i < 8) {
            System.arraycopy(tK[8 - i], 0, Kd[i], 0, 4);
            ++i;
        }
        Square.transform(tK[0], Kd[8]);
        return new Object[]{Ke, Kd};
    }

    public final void encrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        int[][] K = (int[][])((Object[])k)[0];
        Square.square(in, i, out, j, K, Te, Se);
    }

    public final void decrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        int[][] K = (int[][])((Object[])k)[1];
        Square.square(in, i, out, j, K, Td, Sd);
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

    public Square() {
        super("square", 16, 16);
    }

    private static final {
        int limit = Sdata.length();
        int i = 0;
        int j = 0;
        while (i < limit) {
            char c1 = Sdata.charAt(i);
            Square.Se[j++] = (byte)(c1 >>> 8);
            Square.Se[j++] = (byte)c1;
            ++i;
        }
        i = 0;
        while (i < 256) {
            Square.Sd[Square.Se[i] & 255] = (byte)i;
            ++i;
        }
        Square.OFFSET[0] = 1;
        i = 1;
        while (i < 8) {
            Square.OFFSET[i] = Square.mul(OFFSET[i - 1], 2);
            int[] arrn = OFFSET;
            int n = i - 1;
            arrn[n] = arrn[n] << 24;
            ++i;
        }
        int[] arrn = OFFSET;
        arrn[7] = arrn[7] << 24;
        i = 0;
        while (i < 256) {
            j = Se[i] & 255;
            int n = 0;
            if (Se[i & 3] != 0) {
                n = Square.mul(j, 2) << 24 | j << 16 | j << 8 | Square.mul(j, 3);
            }
            Square.Te[i] = n;
            j = Sd[i] & 255;
            int n2 = 0;
            if (Sd[i & 3] != 0) {
                n2 = Square.mul(j, 14) << 24 | Square.mul(j, 9) << 16 | Square.mul(j, 13) << 8 | Square.mul(j, 11);
            }
            Square.Td[i] = n2;
            ++i;
        }
    }
}

