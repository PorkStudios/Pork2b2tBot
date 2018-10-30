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

public final class Twofish
extends BaseCipher {
    private static final boolean DEBUG = false;
    private static final int debuglevel = 9;
    private static final int DEFAULT_BLOCK_SIZE = 16;
    private static final int DEFAULT_KEY_SIZE = 16;
    private static final int MAX_ROUNDS = 16;
    private static final int ROUNDS = 16;
    private static final int INPUT_WHITEN = 0;
    private static final int OUTPUT_WHITEN = 4;
    private static final int ROUND_SUBKEYS = 8;
    private static final int SK_STEP = 33686018;
    private static final int SK_BUMP = 16843009;
    private static final int SK_ROTL = 9;
    private static final String[] Pm = new String[]{"\ua967\ub3e8\u04fd\ua376\u9a92\u8078\ue4dd\ud138\u0dc6\u3598\u18f7\uec6c\u4375\u3726\ufa13\u9448\uf2d0\u8b30\u8454\udf23\u195b\u3d59\uf3ae\ua282\u6301\u832e\ud951\u9b7c\ua6eb\ua5be\u160c\ue361\uc08c\u3af5\u732c\u250b\ubb4e\u896b\u536a\ub4f1\ue1e6\ubd45\ue2f4\ub666\ucc95\u0356\ud41c\u1ed7\ufbc3\u8eb5\ue9cf\ubfba\uea77\u39af\u33c9\u6271\u8179\u09ad\u24cd\uf9d8\ue5c5\ub94d\u4408\u86e7\ua11d\uaaed\u0670\ub2d2\u417b\ua011\u31c2\u2790\u20f6\u60ff\u965c\ub1ab\u9e9c\u521b\u5f93\u0aef\u9185\u49ee\u2d4f\u8f3b\u4787\u6d46\ud63e\u6964\u2ace\ucb2f\ufc97\u057a\uac7f\ud51a\u4b0e\ua75a\u2814\u3f29\u883c\u4c02\ub8da\ub017\u551f\u8a7d\u57c7\u8d74\ub7c4\u9f72\u7e15\u2212\u5807\u9934\u6e50\ude68\u65bc\udbf8\uc8a8\u2b40\udcfe\u32a4\uca10\u21f0\ud35d\u0f00\u6f9d\u3642\u4a5e\uc1e0", "\u75f3\uc6f4\udb7b\ufbc8\u4ad3\ue66b\u457d\ue84b\ud632\ud8fd\u3771\uf1e1\u300f\uf81b\u87fa\u063f\u5eba\uae5b\u8a00\ubc9d\u6dc1\ub10e\u805d\ud2d5\ua084\u0714\ub590\u2ca3\ub273\u4c54\u9274\u3651\u38b0\ubd5a\ufc60\u6296\u6c42\uf710\u7c28\u278c\u1395\u9cc7\u2446\u3b70\ucae3\u85cb\u11d0\u93b8\ua683\u20ff\u9f77\uc3cc\u036f\u08bf\u40e7\u2be2\u790c\uaa82\u413a\ueab9\ue49a\ua497\u7eda\u7a17\u6694\ua11d\u3df0\udeb3\u0b72\ua71c\uefd1\u533e\u8f33\u265f\uec76\u2a49\u8188\uee21\uc41a\uebd9\uc539\u99cd\uad31\u8b01\u1823\udd1f\u4e2d\uf948\u4ff2\u658e\u785c\u5819\u8de5\u9857\u677f\u0564\uaf63\ub6fe\uf5b7\u3ca5\ucee9\u6844\ue04d\u4369\u292e\uac15\u59a8\u0a9e\u6e47\udf34\u356a\ucfdc\u22c9\uc09b\u89d4\uedab\u12a2\u0d52\ubb02\u2fa9\ud761\u1eb4\u5004\uf6c2\u1625\u8656\u5509\ube91"};
    private static final byte[][] P = new byte[2][256];
    private static final int P_00 = 1;
    private static final int P_01 = 0;
    private static final int P_02 = 0;
    private static final int P_03 = 1;
    private static final int P_04 = 1;
    private static final int P_10 = 0;
    private static final int P_11 = 0;
    private static final int P_12 = 1;
    private static final int P_13 = 1;
    private static final int P_14 = 0;
    private static final int P_20 = 1;
    private static final int P_21 = 1;
    private static final int P_22 = 0;
    private static final int P_23 = 0;
    private static final int P_24 = 0;
    private static final int P_30 = 0;
    private static final int P_31 = 1;
    private static final int P_32 = 1;
    private static final int P_33 = 0;
    private static final int P_34 = 1;
    private static final int GF256_FDBK_2 = 180;
    private static final int GF256_FDBK_4 = 90;
    private static final int[][] MDS = new int[4][256];
    private static final int RS_GF_FDBK = 333;
    private static final byte[] KAT_KEY = Util.toBytesFromString("0000000000000000000000000000000000000000000002000000000000000000");
    private static final byte[] KAT_CT = Util.toBytesFromString("F51410475B33FBD3DB2117B5C17C82D4");
    private static Boolean valid;

    private static final int LFSR1(int x) {
        return x >> 1 ^ ((x & 1) != 0 ? 180 : 0);
    }

    private static final int LFSR2(int x) {
        return x >> 2 ^ ((x & 2) != 0 ? 180 : 0) ^ ((x & 1) != 0 ? 90 : 0);
    }

    private static final int Mx_X(int x) {
        return x ^ Twofish.LFSR2(x);
    }

    private static final int Mx_Y(int x) {
        return x ^ Twofish.LFSR1(x) ^ Twofish.LFSR2(x);
    }

    private static final int b0(int x) {
        return x & 255;
    }

    private static final int b1(int x) {
        return x >>> 8 & 255;
    }

    private static final int b2(int x) {
        return x >>> 16 & 255;
    }

    private static final int b3(int x) {
        return x >>> 24 & 255;
    }

    private static final int RS_MDS_Encode(int k0, int k1) {
        int r = k1;
        int i = 0;
        while (i < 4) {
            r = Twofish.RS_rem(r);
            ++i;
        }
        r ^= k0;
        i = 0;
        while (i < 4) {
            r = Twofish.RS_rem(r);
            ++i;
        }
        return r;
    }

    private static final int RS_rem(int x) {
        int b;
        int g2 = (b << 1 ^ (((b = x >>> 24 & 255) & 128) != 0 ? 333 : 0)) & 255;
        int g3 = b >>> 1 ^ ((b & 1) != 0 ? 166 : 0) ^ g2;
        int result = x << 8 ^ g3 << 24 ^ g2 << 16 ^ g3 << 8 ^ b;
        return result;
    }

    private static final int F32(int k64Cnt, int x, int[] k32) {
        int b0 = Twofish.b0(x);
        int b1 = Twofish.b1(x);
        int b2 = Twofish.b2(x);
        int b3 = Twofish.b3(x);
        int k0 = k32[0];
        int k1 = k32[1];
        int k2 = k32[2];
        int k3 = k32[3];
        int result = 0;
        switch (k64Cnt & 3) {
            case 1: {
                result = MDS[0][P[0][b0] & 255 ^ Twofish.b0(k0)] ^ MDS[1][P[0][b1] & 255 ^ Twofish.b1(k0)] ^ MDS[2][P[1][b2] & 255 ^ Twofish.b2(k0)] ^ MDS[3][P[1][b3] & 255 ^ Twofish.b3(k0)];
                break;
            }
            case 0: {
                b0 = P[1][b0] & 255 ^ Twofish.b0(k3);
                b1 = P[0][b1] & 255 ^ Twofish.b1(k3);
                b2 = P[0][b2] & 255 ^ Twofish.b2(k3);
                b3 = P[1][b3] & 255 ^ Twofish.b3(k3);
            }
            case 3: {
                b0 = P[1][b0] & 255 ^ Twofish.b0(k2);
                b1 = P[1][b1] & 255 ^ Twofish.b1(k2);
                b2 = P[0][b2] & 255 ^ Twofish.b2(k2);
                b3 = P[0][b3] & 255 ^ Twofish.b3(k2);
            }
            case 2: {
                result = MDS[0][P[0][P[0][b0] & 255 ^ Twofish.b0(k1)] & 255 ^ Twofish.b0(k0)] ^ MDS[1][P[0][P[1][b1] & 255 ^ Twofish.b1(k1)] & 255 ^ Twofish.b1(k0)] ^ MDS[2][P[1][P[0][b2] & 255 ^ Twofish.b2(k1)] & 255 ^ Twofish.b2(k0)] ^ MDS[3][P[1][P[1][b3] & 255 ^ Twofish.b3(k1)] & 255 ^ Twofish.b3(k0)];
                break;
            }
        }
        return result;
    }

    private static final int Fe32(int[] sBox, int x, int R) {
        return sBox[2 * Twofish._b(x, R)] ^ sBox[2 * Twofish._b(x, R + 1) + 1] ^ sBox[512 + 2 * Twofish._b(x, R + 2)] ^ sBox[512 + 2 * Twofish._b(x, R + 3) + 1];
    }

    private static final int _b(int x, int N) {
        switch (N % 4) {
            case 0: {
                return x & 255;
            }
            case 1: {
                return x >>> 8 & 255;
            }
            case 2: {
                return x >>> 16 & 255;
            }
        }
        return x >>> 24;
    }

    public final Object clone() {
        Twofish result = new Twofish();
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
        al.add(new Integer(8));
        al.add(new Integer(16));
        al.add(new Integer(24));
        al.add(new Integer(32));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Object makeKey(byte[] k, int bs) throws InvalidKeyException {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        if (k == null) {
            throw new InvalidKeyException("Empty key");
        }
        int length = k.length;
        if (length != 8 && length != 16 && length != 24 && length != 32) {
            throw new InvalidKeyException("Incorrect key length");
        }
        int k64Cnt = length / 8;
        int subkeyCnt = 40;
        int[] k32e = new int[4];
        int[] k32o = new int[4];
        int[] sBoxKey = new int[4];
        int offset = 0;
        int i = 0;
        int j = k64Cnt - 1;
        while (i < 4 && offset < length) {
            k32e[i] = k[offset++] & 255 | (k[offset++] & 255) << 8 | (k[offset++] & 255) << 16 | (k[offset++] & 255) << 24;
            k32o[i] = k[offset++] & 255 | (k[offset++] & 255) << 8 | (k[offset++] & 255) << 16 | (k[offset++] & 255) << 24;
            sBoxKey[j] = Twofish.RS_MDS_Encode(k32e[i], k32o[i]);
            ++i;
            --j;
        }
        int[] subKeys = new int[subkeyCnt];
        int q = 0;
        i = 0;
        while (i < subkeyCnt / 2) {
            int A = Twofish.F32(k64Cnt, q, k32e);
            int B = Twofish.F32(k64Cnt, q + 16843009, k32o);
            B = B << 8 | B >>> 24;
            subKeys[2 * i] = A += B;
            subKeys[2 * i + 1] = A << 9 | (A += B) >>> 23;
            ++i;
            q += 33686018;
        }
        int k0 = sBoxKey[0];
        int k1 = sBoxKey[1];
        int k2 = sBoxKey[2];
        int k3 = sBoxKey[3];
        int[] sBox = new int[1024];
        i = 0;
        while (i < 256) {
            int b3;
            int b2 = b3 = i;
            int b1 = b3;
            int b0 = b3;
            switch (k64Cnt & 3) {
                case 1: {
                    sBox[2 * i] = MDS[0][P[0][b0] & 255 ^ Twofish.b0(k0)];
                    sBox[2 * i + 1] = MDS[1][P[0][b1] & 255 ^ Twofish.b1(k0)];
                    sBox[512 + 2 * i] = MDS[2][P[1][b2] & 255 ^ Twofish.b2(k0)];
                    sBox[512 + 2 * i + 1] = MDS[3][P[1][b3] & 255 ^ Twofish.b3(k0)];
                    break;
                }
                case 0: {
                    b0 = P[1][b0] & 255 ^ Twofish.b0(k3);
                    b1 = P[0][b1] & 255 ^ Twofish.b1(k3);
                    b2 = P[0][b2] & 255 ^ Twofish.b2(k3);
                    b3 = P[1][b3] & 255 ^ Twofish.b3(k3);
                }
                case 3: {
                    b0 = P[1][b0] & 255 ^ Twofish.b0(k2);
                    b1 = P[1][b1] & 255 ^ Twofish.b1(k2);
                    b2 = P[0][b2] & 255 ^ Twofish.b2(k2);
                    b3 = P[0][b3] & 255 ^ Twofish.b3(k2);
                }
                case 2: {
                    sBox[2 * i] = MDS[0][P[0][P[0][b0] & 255 ^ Twofish.b0(k1)] & 255 ^ Twofish.b0(k0)];
                    sBox[2 * i + 1] = MDS[1][P[0][P[1][b1] & 255 ^ Twofish.b1(k1)] & 255 ^ Twofish.b1(k0)];
                    sBox[512 + 2 * i] = MDS[2][P[1][P[0][b2] & 255 ^ Twofish.b2(k1)] & 255 ^ Twofish.b2(k0)];
                    sBox[512 + 2 * i + 1] = MDS[3][P[1][P[1][b3] & 255 ^ Twofish.b3(k1)] & 255 ^ Twofish.b3(k0)];
                }
            }
            ++i;
        }
        return new Object[]{sBox, subKeys};
    }

    public final void encrypt(byte[] in, int inOffset, byte[] out, int outOffset, Object sessionKey, int bs) {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        Object[] sk = (Object[])sessionKey;
        int[] sBox = (int[])sk[0];
        int[] sKey = (int[])sk[1];
        int x0 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        int x1 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        int x2 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        int x3 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        x0 ^= sKey[0];
        x1 ^= sKey[1];
        x2 ^= sKey[2];
        x3 ^= sKey[3];
        int k = 8;
        int R = 0;
        while (R < 16) {
            int t0 = Twofish.Fe32(sBox, x0, 0);
            int t1 = Twofish.Fe32(sBox, x1, 3);
            x2 ^= t0 + t1 + sKey[k++];
            x2 = x2 >>> 1 | x2 << 31;
            x3 = x3 << 1 | x3 >>> 31;
            int n = k++;
            t0 = Twofish.Fe32(sBox, x2, 0);
            t1 = Twofish.Fe32(sBox, x3 ^= t0 + 2 * t1 + sKey[n], 3);
            x0 ^= t0 + t1 + sKey[k++];
            x0 = x0 >>> 1 | x0 << 31;
            x1 = x1 << 1 | x1 >>> 31;
            x1 ^= t0 + 2 * t1 + sKey[k++];
            R += 2;
        }
        out[outOffset++] = (byte)(x2 ^= sKey[4]);
        out[outOffset++] = (byte)(x2 >>> 8);
        out[outOffset++] = (byte)(x2 >>> 16);
        out[outOffset++] = (byte)(x2 >>> 24);
        out[outOffset++] = (byte)(x3 ^= sKey[5]);
        out[outOffset++] = (byte)(x3 >>> 8);
        out[outOffset++] = (byte)(x3 >>> 16);
        out[outOffset++] = (byte)(x3 >>> 24);
        out[outOffset++] = (byte)(x0 ^= sKey[6]);
        out[outOffset++] = (byte)(x0 >>> 8);
        out[outOffset++] = (byte)(x0 >>> 16);
        out[outOffset++] = (byte)(x0 >>> 24);
        out[outOffset++] = (byte)(x1 ^= sKey[7]);
        out[outOffset++] = (byte)(x1 >>> 8);
        out[outOffset++] = (byte)(x1 >>> 16);
        out[outOffset] = (byte)(x1 >>> 24);
    }

    public final void decrypt(byte[] in, int inOffset, byte[] out, int outOffset, Object sessionKey, int bs) {
        if (bs != 16) {
            throw new IllegalArgumentException();
        }
        Object[] sk = (Object[])sessionKey;
        int[] sBox = (int[])sk[0];
        int[] sKey = (int[])sk[1];
        int x2 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        int x3 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        int x0 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        int x1 = in[inOffset++] & 255 | (in[inOffset++] & 255) << 8 | (in[inOffset++] & 255) << 16 | (in[inOffset++] & 255) << 24;
        x2 ^= sKey[4];
        x3 ^= sKey[5];
        x0 ^= sKey[6];
        x1 ^= sKey[7];
        int k = 39;
        int R = 0;
        while (R < 16) {
            int t0 = Twofish.Fe32(sBox, x2, 0);
            int t1 = Twofish.Fe32(sBox, x3, 3);
            x1 ^= t0 + 2 * t1 + sKey[k--];
            x1 = x1 >>> 1 | x1 << 31;
            x0 = x0 << 1 | x0 >>> 31;
            t0 = Twofish.Fe32(sBox, x0 ^= t0 + t1 + sKey[k--], 0);
            t1 = Twofish.Fe32(sBox, x1, 3);
            x3 ^= t0 + 2 * t1 + sKey[k--];
            x3 = x3 >>> 1 | x3 << 31;
            x2 = x2 << 1 | x2 >>> 31;
            x2 ^= t0 + t1 + sKey[k--];
            R += 2;
        }
        out[outOffset++] = (byte)(x0 ^= sKey[0]);
        out[outOffset++] = (byte)(x0 >>> 8);
        out[outOffset++] = (byte)(x0 >>> 16);
        out[outOffset++] = (byte)(x0 >>> 24);
        out[outOffset++] = (byte)(x1 ^= sKey[1]);
        out[outOffset++] = (byte)(x1 >>> 8);
        out[outOffset++] = (byte)(x1 >>> 16);
        out[outOffset++] = (byte)(x1 >>> 24);
        out[outOffset++] = (byte)(x2 ^= sKey[2]);
        out[outOffset++] = (byte)(x2 >>> 8);
        out[outOffset++] = (byte)(x2 >>> 16);
        out[outOffset++] = (byte)(x2 >>> 24);
        out[outOffset++] = (byte)(x3 ^= sKey[3]);
        out[outOffset++] = (byte)(x3 >>> 8);
        out[outOffset++] = (byte)(x3 >>> 16);
        out[outOffset] = (byte)(x3 >>> 24);
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

    public Twofish() {
        super("twofish", 16, 16);
    }

    private static final {
        long time = System.currentTimeMillis();
        int i = 0;
        while (i < 256) {
            int c = Pm[0].charAt(i >>> 1);
            Twofish.P[0][i] = (byte)((i & 1) == 0 ? c >>> 8 : c);
            c = Pm[1].charAt(i >>> 1);
            Twofish.P[1][i] = (byte)((i & 1) == 0 ? c >>> 8 : c);
            ++i;
        }
        int[] m1 = new int[2];
        int[] mX = new int[2];
        int[] mY = new int[2];
        i = 0;
        while (i < 256) {
            int j;
            m1[0] = j = P[0][i] & 255;
            mX[0] = Twofish.Mx_X(j) & 255;
            mY[0] = Twofish.Mx_Y(j) & 255;
            m1[1] = j = P[1][i] & 255;
            mX[1] = Twofish.Mx_X(j) & 255;
            mY[1] = Twofish.Mx_Y(j) & 255;
            Twofish.MDS[0][i] = m1[1] | mX[1] << 8 | mY[1] << 16 | mY[1] << 24;
            Twofish.MDS[1][i] = mY[0] | mY[0] << 8 | mX[0] << 16 | m1[0] << 24;
            Twofish.MDS[2][i] = mX[1] | mY[1] << 8 | m1[1] << 16 | mY[1] << 24;
            Twofish.MDS[3][i] = mX[0] | m1[0] << 8 | mY[0] << 16 | mX[0] << 24;
            ++i;
        }
        time = System.currentTimeMillis() - time;
    }
}

