/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import gnu.crypto.Properties;
import gnu.crypto.cipher.BaseCipher;
import gnu.crypto.cipher.WeakKeyException;
import gnu.crypto.util.Util;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class DES
extends BaseCipher {
    public static final int BLOCK_SIZE = 8;
    public static final int KEY_SIZE = 8;
    private static final int[] SP1;
    private static final int[] SP2;
    private static final int[] SP3;
    private static final int[] SP4;
    private static final int[] SP5;
    private static final int[] SP6;
    private static final int[] SP7;
    private static final int[] SP8;
    private static final byte[] PARITY;
    private static final byte[] ROTARS;
    private static final byte[] PC1;
    private static final byte[] PC2;
    public static final byte[][] WEAK_KEYS;
    public static final byte[][] SEMIWEAK_KEYS;
    public static final byte[][] POSSIBLE_WEAK_KEYS;

    public static void adjustParity(byte[] kb, int offset) {
        int i = offset;
        while (i < 8) {
            byte[] arrby = kb;
            int n = i;
            byte by = 0;
            if (PARITY[kb[i] & 255] == 8) {
                by = 1;
            }
            arrby[n] = (byte)(arrby[n] ^ by);
            ++i;
        }
    }

    public static boolean isParityAdjusted(byte[] kb, int offset) {
        int w = -2004318072;
        int n = PARITY[kb[offset] & 255];
        n <<= 4;
        n |= PARITY[kb[offset + 1] & 255];
        n <<= 4;
        n |= PARITY[kb[offset + 2] & 255];
        n <<= 4;
        n |= PARITY[kb[offset + 3] & 255];
        n <<= 4;
        n |= PARITY[kb[offset + 4] & 255];
        n <<= 4;
        n |= PARITY[kb[offset + 5] & 255];
        n <<= 4;
        n |= PARITY[kb[offset + 6] & 255];
        n <<= 4;
        boolean bl = false;
        if (((n |= PARITY[kb[offset + 7] & 255]) & w) == 0) {
            bl = true;
        }
        return bl;
    }

    public static boolean isWeak(byte[] kb) {
        int i = 0;
        while (i < WEAK_KEYS.length) {
            if (Arrays.equals(WEAK_KEYS[i], kb)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public static boolean isSemiWeak(byte[] kb) {
        int i = 0;
        while (i < SEMIWEAK_KEYS.length) {
            if (Arrays.equals(SEMIWEAK_KEYS[i], kb)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public static boolean isPossibleWeak(byte[] kb) {
        int i = 0;
        while (i < POSSIBLE_WEAK_KEYS.length) {
            if (Arrays.equals(POSSIBLE_WEAK_KEYS[i], kb)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    private static final void desFunc(byte[] in, int i, byte[] out, int o, int[] key) {
        int left = (in[i++] & 255) << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i++] & 255;
        int right = (in[i++] & 255) << 24 | (in[i++] & 255) << 16 | (in[i++] & 255) << 8 | in[i] & 255;
        int work = (left >>> 4 ^ right) & 252645135;
        work = ((left ^= work << 4) >>> 16 ^ (right ^= work)) & (char)-1;
        work = ((right ^= work) >>> 2 ^ (left ^= work << 16)) & 858993459;
        work = ((right ^= work << 2) >>> 8 ^ (left ^= work)) & 16711935;
        right ^= work << 8;
        right = (right << 1 | right >>> 31 & 1) & -1;
        work = ((left ^= work) ^ right) & -1431655766;
        left ^= work;
        right ^= work;
        left = (left << 1 | left >>> 31 & 1) & -1;
        int k = 0;
        int round = 0;
        while (round < 8) {
            work = right >>> 4 | right << 28;
            int t = SP7[(work ^= key[k++]) & 63];
            t |= SP5[(work >>>= 8) & 63];
            t |= SP3[(work >>>= 8) & 63];
            t |= SP1[(work >>>= 8) & 63];
            work = right ^ key[k++];
            t |= SP8[work & 63];
            t |= SP6[(work >>>= 8) & 63];
            t |= SP4[(work >>>= 8) & 63];
            work = left >>> 4 | (left ^= (t |= SP2[(work >>>= 8) & 63])) << 28;
            t = SP7[(work ^= key[k++]) & 63];
            t |= SP5[(work >>>= 8) & 63];
            t |= SP3[(work >>>= 8) & 63];
            t |= SP1[(work >>>= 8) & 63];
            work = left ^ key[k++];
            t |= SP8[work & 63];
            t |= SP6[(work >>>= 8) & 63];
            t |= SP4[(work >>>= 8) & 63];
            right ^= (t |= SP2[(work >>>= 8) & 63]);
            ++round;
        }
        right = right << 31 | right >>> 1;
        work = (left ^ right) & -1431655766;
        left ^= work;
        left = left << 31 | left >>> 1;
        work = (left >>> 8 ^ (right ^= work)) & 16711935;
        work = ((left ^= work << 8) >>> 2 ^ (right ^= work)) & 858993459;
        work = ((right ^= work) >>> 16 ^ (left ^= work << 2)) & (char)-1;
        work = ((right ^= work << 16) >>> 4 ^ (left ^= work)) & 252645135;
        out[o++] = (byte)((right ^= work << 4) >>> 24);
        out[o++] = (byte)(right >>> 16);
        out[o++] = (byte)(right >>> 8);
        out[o++] = (byte)right;
        out[o++] = (byte)((left ^= work) >>> 24);
        out[o++] = (byte)(left >>> 16);
        out[o++] = (byte)(left >>> 8);
        out[o] = (byte)left;
    }

    public Object clone() {
        return new DES();
    }

    public Iterator blockSizes() {
        return Collections.singleton(new Integer(8)).iterator();
    }

    public Iterator keySizes() {
        return Collections.singleton(new Integer(8)).iterator();
    }

    public Object makeKey(byte[] kb, int bs) throws InvalidKeyException {
        int l;
        if (kb == null || kb.length != 8) {
            throw new InvalidKeyException("DES keys must be 8 bytes long");
        }
        if (Properties.checkForWeakKeys() && (DES.isWeak(kb) || DES.isSemiWeak(kb) || DES.isPossibleWeak(kb))) {
            throw new WeakKeyException();
        }
        long pc1m = 0L;
        long pcr = 0L;
        int i = 0;
        while (i < 56) {
            l = PC1[i];
            pc1m |= (kb[l >>> 3] & 128 >>> (l & 7)) != 0 ? 1L << 55 - i : 0L;
            ++i;
        }
        Context ctx = new Context();
        i = 0;
        while (i < 16) {
            pcr = 0L;
            int m = i << 1;
            int n = m + 1;
            int j = 0;
            while (j < 28) {
                l = j + ROTARS[i];
                pcr = l < 28 ? (pcr |= (pc1m & 1L << 55 - l) != 0L ? 1L << 55 - j : 0L) : (pcr |= (pc1m & 1L << 55 - (l - 28)) != 0L ? 1L << 55 - j : 0L);
                ++j;
            }
            j = 28;
            while (j < 56) {
                l = j + ROTARS[i];
                pcr = l < 56 ? (pcr |= (pc1m & 1L << 55 - l) != 0L ? 1L << 55 - j : 0L) : (pcr |= (pc1m & 1L << 55 - (l - 28)) != 0L ? 1L << 55 - j : 0L);
                ++j;
            }
            j = 0;
            while (j < 24) {
                if ((pcr & 1L << 55 - PC2[j]) != 0L) {
                    int[] arrn = ctx.ek;
                    int n2 = m;
                    arrn[n2] = arrn[n2] | 1 << 23 - j;
                }
                if ((pcr & 1L << 55 - PC2[j + 24]) != 0L) {
                    int[] arrn = ctx.ek;
                    int n3 = n;
                    arrn[n3] = arrn[n3] | 1 << 23 - j;
                }
                ++j;
            }
            ++i;
        }
        i = 0;
        while (i < 32) {
            ctx.dk[30 - i] = ctx.ek[i];
            ctx.dk[31 - i] = ctx.ek[i + 1];
            i += 2;
        }
        i = 0;
        while (i < 32) {
            int x = ctx.ek[i];
            int y = ctx.ek[i + 1];
            ctx.ek[i] = (x & 16515072) << 6 | (x & 4032) << 10 | (y & 16515072) >>> 10 | (y & 4032) >>> 6;
            ctx.ek[i + 1] = (x & 258048) << 12 | (x & 63) << 16 | (y & 258048) >>> 4 | y & 63;
            x = ctx.dk[i];
            y = ctx.dk[i + 1];
            ctx.dk[i] = (x & 16515072) << 6 | (x & 4032) << 10 | (y & 16515072) >>> 10 | (y & 4032) >>> 6;
            ctx.dk[i + 1] = (x & 258048) << 12 | (x & 63) << 16 | (y & 258048) >>> 4 | y & 63;
            i += 2;
        }
        return ctx;
    }

    public void encrypt(byte[] in, int i, byte[] out, int o, Object K, int bs) {
        DES.desFunc(in, i, out, o, ((Context)K).ek);
    }

    public void decrypt(byte[] in, int i, byte[] out, int o, Object K, int bs) {
        DES.desFunc(in, i, out, o, ((Context)K).dk);
    }

    public DES() {
        super("des", 8, 8);
    }

    private static final {
        int[] arrn = new int[64];
        arrn[0] = 16843776;
        arrn[2] = 65536;
        arrn[3] = 16843780;
        arrn[4] = 16842756;
        arrn[5] = 66564;
        arrn[6] = 4;
        arrn[7] = 65536;
        arrn[8] = 1024;
        arrn[9] = 16843776;
        arrn[10] = 16843780;
        arrn[11] = 1024;
        arrn[12] = 16778244;
        arrn[13] = 16842756;
        arrn[14] = 16777216;
        arrn[15] = 4;
        arrn[16] = 1028;
        arrn[17] = 16778240;
        arrn[18] = 16778240;
        arrn[19] = 66560;
        arrn[20] = 66560;
        arrn[21] = 16842752;
        arrn[22] = 16842752;
        arrn[23] = 16778244;
        arrn[24] = 65540;
        arrn[25] = 16777220;
        arrn[26] = 16777220;
        arrn[27] = 65540;
        arrn[29] = 1028;
        arrn[30] = 66564;
        arrn[31] = 16777216;
        arrn[32] = 65536;
        arrn[33] = 16843780;
        arrn[34] = 4;
        arrn[35] = 16842752;
        arrn[36] = 16843776;
        arrn[37] = 16777216;
        arrn[38] = 16777216;
        arrn[39] = 1024;
        arrn[40] = 16842756;
        arrn[41] = 65536;
        arrn[42] = 66560;
        arrn[43] = 16777220;
        arrn[44] = 1024;
        arrn[45] = 4;
        arrn[46] = 16778244;
        arrn[47] = 66564;
        arrn[48] = 16843780;
        arrn[49] = 65540;
        arrn[50] = 16842752;
        arrn[51] = 16778244;
        arrn[52] = 16777220;
        arrn[53] = 1028;
        arrn[54] = 66564;
        arrn[55] = 16843776;
        arrn[56] = 1028;
        arrn[57] = 16778240;
        arrn[58] = 16778240;
        arrn[60] = 65540;
        arrn[61] = 66560;
        arrn[63] = 16842756;
        SP1 = arrn;
        int[] arrn2 = new int[64];
        arrn2[0] = -2146402272;
        arrn2[1] = -2147450880;
        arrn2[2] = 32768;
        arrn2[3] = 1081376;
        arrn2[4] = 1048576;
        arrn2[5] = 32;
        arrn2[6] = -2146435040;
        arrn2[7] = -2147450848;
        arrn2[8] = -2147483616;
        arrn2[9] = -2146402272;
        arrn2[10] = -2146402304;
        arrn2[11] = Integer.MIN_VALUE;
        arrn2[12] = -2147450880;
        arrn2[13] = 1048576;
        arrn2[14] = 32;
        arrn2[15] = -2146435040;
        arrn2[16] = 1081344;
        arrn2[17] = 1048608;
        arrn2[18] = -2147450848;
        arrn2[20] = Integer.MIN_VALUE;
        arrn2[21] = 32768;
        arrn2[22] = 1081376;
        arrn2[23] = -2146435072;
        arrn2[24] = 1048608;
        arrn2[25] = -2147483616;
        arrn2[27] = 1081344;
        arrn2[28] = 32800;
        arrn2[29] = -2146402304;
        arrn2[30] = -2146435072;
        arrn2[31] = 32800;
        arrn2[33] = 1081376;
        arrn2[34] = -2146435040;
        arrn2[35] = 1048576;
        arrn2[36] = -2147450848;
        arrn2[37] = -2146435072;
        arrn2[38] = -2146402304;
        arrn2[39] = 32768;
        arrn2[40] = -2146435072;
        arrn2[41] = -2147450880;
        arrn2[42] = 32;
        arrn2[43] = -2146402272;
        arrn2[44] = 1081376;
        arrn2[45] = 32;
        arrn2[46] = 32768;
        arrn2[47] = Integer.MIN_VALUE;
        arrn2[48] = 32800;
        arrn2[49] = -2146402304;
        arrn2[50] = 1048576;
        arrn2[51] = -2147483616;
        arrn2[52] = 1048608;
        arrn2[53] = -2147450848;
        arrn2[54] = -2147483616;
        arrn2[55] = 1048608;
        arrn2[56] = 1081344;
        arrn2[58] = -2147450880;
        arrn2[59] = 32800;
        arrn2[60] = Integer.MIN_VALUE;
        arrn2[61] = -2146435040;
        arrn2[62] = -2146402272;
        arrn2[63] = 1081344;
        SP2 = arrn2;
        int[] arrn3 = new int[64];
        arrn3[0] = 520;
        arrn3[1] = 134349312;
        arrn3[3] = 134348808;
        arrn3[4] = 134218240;
        arrn3[6] = 131592;
        arrn3[7] = 134218240;
        arrn3[8] = 131080;
        arrn3[9] = 134217736;
        arrn3[10] = 134217736;
        arrn3[11] = 131072;
        arrn3[12] = 134349320;
        arrn3[13] = 131080;
        arrn3[14] = 134348800;
        arrn3[15] = 520;
        arrn3[16] = 134217728;
        arrn3[17] = 8;
        arrn3[18] = 134349312;
        arrn3[19] = 512;
        arrn3[20] = 131584;
        arrn3[21] = 134348800;
        arrn3[22] = 134348808;
        arrn3[23] = 131592;
        arrn3[24] = 134218248;
        arrn3[25] = 131584;
        arrn3[26] = 131072;
        arrn3[27] = 134218248;
        arrn3[28] = 8;
        arrn3[29] = 134349320;
        arrn3[30] = 512;
        arrn3[31] = 134217728;
        arrn3[32] = 134349312;
        arrn3[33] = 134217728;
        arrn3[34] = 131080;
        arrn3[35] = 520;
        arrn3[36] = 131072;
        arrn3[37] = 134349312;
        arrn3[38] = 134218240;
        arrn3[40] = 512;
        arrn3[41] = 131080;
        arrn3[42] = 134349320;
        arrn3[43] = 134218240;
        arrn3[44] = 134217736;
        arrn3[45] = 512;
        arrn3[47] = 134348808;
        arrn3[48] = 134218248;
        arrn3[49] = 131072;
        arrn3[50] = 134217728;
        arrn3[51] = 134349320;
        arrn3[52] = 8;
        arrn3[53] = 131592;
        arrn3[54] = 131584;
        arrn3[55] = 134217736;
        arrn3[56] = 134348800;
        arrn3[57] = 134218248;
        arrn3[58] = 520;
        arrn3[59] = 134348800;
        arrn3[60] = 131592;
        arrn3[61] = 8;
        arrn3[62] = 134348808;
        arrn3[63] = 131584;
        SP3 = arrn3;
        int[] arrn4 = new int[64];
        arrn4[0] = 8396801;
        arrn4[1] = 8321;
        arrn4[2] = 8321;
        arrn4[3] = 128;
        arrn4[4] = 8396928;
        arrn4[5] = 8388737;
        arrn4[6] = 8388609;
        arrn4[7] = 8193;
        arrn4[9] = 8396800;
        arrn4[10] = 8396800;
        arrn4[11] = 8396929;
        arrn4[12] = 129;
        arrn4[14] = 8388736;
        arrn4[15] = 8388609;
        arrn4[16] = 1;
        arrn4[17] = 8192;
        arrn4[18] = 8388608;
        arrn4[19] = 8396801;
        arrn4[20] = 128;
        arrn4[21] = 8388608;
        arrn4[22] = 8193;
        arrn4[23] = 8320;
        arrn4[24] = 8388737;
        arrn4[25] = 1;
        arrn4[26] = 8320;
        arrn4[27] = 8388736;
        arrn4[28] = 8192;
        arrn4[29] = 8396928;
        arrn4[30] = 8396929;
        arrn4[31] = 129;
        arrn4[32] = 8388736;
        arrn4[33] = 8388609;
        arrn4[34] = 8396800;
        arrn4[35] = 8396929;
        arrn4[36] = 129;
        arrn4[39] = 8396800;
        arrn4[40] = 8320;
        arrn4[41] = 8388736;
        arrn4[42] = 8388737;
        arrn4[43] = 1;
        arrn4[44] = 8396801;
        arrn4[45] = 8321;
        arrn4[46] = 8321;
        arrn4[47] = 128;
        arrn4[48] = 8396929;
        arrn4[49] = 129;
        arrn4[50] = 1;
        arrn4[51] = 8192;
        arrn4[52] = 8388609;
        arrn4[53] = 8193;
        arrn4[54] = 8396928;
        arrn4[55] = 8388737;
        arrn4[56] = 8193;
        arrn4[57] = 8320;
        arrn4[58] = 8388608;
        arrn4[59] = 8396801;
        arrn4[60] = 128;
        arrn4[61] = 8388608;
        arrn4[62] = 8192;
        arrn4[63] = 8396928;
        SP4 = arrn4;
        int[] arrn5 = new int[64];
        arrn5[0] = 256;
        arrn5[1] = 34078976;
        arrn5[2] = 34078720;
        arrn5[3] = 1107296512;
        arrn5[4] = 524288;
        arrn5[5] = 256;
        arrn5[6] = 1073741824;
        arrn5[7] = 34078720;
        arrn5[8] = 1074266368;
        arrn5[9] = 524288;
        arrn5[10] = 33554688;
        arrn5[11] = 1074266368;
        arrn5[12] = 1107296512;
        arrn5[13] = 1107820544;
        arrn5[14] = 524544;
        arrn5[15] = 1073741824;
        arrn5[16] = 33554432;
        arrn5[17] = 1074266112;
        arrn5[18] = 1074266112;
        arrn5[20] = 1073742080;
        arrn5[21] = 1107820800;
        arrn5[22] = 1107820800;
        arrn5[23] = 33554688;
        arrn5[24] = 1107820544;
        arrn5[25] = 1073742080;
        arrn5[27] = 1107296256;
        arrn5[28] = 34078976;
        arrn5[29] = 33554432;
        arrn5[30] = 1107296256;
        arrn5[31] = 524544;
        arrn5[32] = 524288;
        arrn5[33] = 1107296512;
        arrn5[34] = 256;
        arrn5[35] = 33554432;
        arrn5[36] = 1073741824;
        arrn5[37] = 34078720;
        arrn5[38] = 1107296512;
        arrn5[39] = 1074266368;
        arrn5[40] = 33554688;
        arrn5[41] = 1073741824;
        arrn5[42] = 1107820544;
        arrn5[43] = 34078976;
        arrn5[44] = 1074266368;
        arrn5[45] = 256;
        arrn5[46] = 33554432;
        arrn5[47] = 1107820544;
        arrn5[48] = 1107820800;
        arrn5[49] = 524544;
        arrn5[50] = 1107296256;
        arrn5[51] = 1107820800;
        arrn5[52] = 34078720;
        arrn5[54] = 1074266112;
        arrn5[55] = 1107296256;
        arrn5[56] = 524544;
        arrn5[57] = 33554688;
        arrn5[58] = 1073742080;
        arrn5[59] = 524288;
        arrn5[61] = 1074266112;
        arrn5[62] = 34078976;
        arrn5[63] = 1073742080;
        SP5 = arrn5;
        int[] arrn6 = new int[64];
        arrn6[0] = 536870928;
        arrn6[1] = 541065216;
        arrn6[2] = 16384;
        arrn6[3] = 541081616;
        arrn6[4] = 541065216;
        arrn6[5] = 16;
        arrn6[6] = 541081616;
        arrn6[7] = 4194304;
        arrn6[8] = 536887296;
        arrn6[9] = 4210704;
        arrn6[10] = 4194304;
        arrn6[11] = 536870928;
        arrn6[12] = 4194320;
        arrn6[13] = 536887296;
        arrn6[14] = 536870912;
        arrn6[15] = 16400;
        arrn6[17] = 4194320;
        arrn6[18] = 536887312;
        arrn6[19] = 16384;
        arrn6[20] = 4210688;
        arrn6[21] = 536887312;
        arrn6[22] = 16;
        arrn6[23] = 541065232;
        arrn6[24] = 541065232;
        arrn6[26] = 4210704;
        arrn6[27] = 541081600;
        arrn6[28] = 16400;
        arrn6[29] = 4210688;
        arrn6[30] = 541081600;
        arrn6[31] = 536870912;
        arrn6[32] = 536887296;
        arrn6[33] = 16;
        arrn6[34] = 541065232;
        arrn6[35] = 4210688;
        arrn6[36] = 541081616;
        arrn6[37] = 4194304;
        arrn6[38] = 16400;
        arrn6[39] = 536870928;
        arrn6[40] = 4194304;
        arrn6[41] = 536887296;
        arrn6[42] = 536870912;
        arrn6[43] = 16400;
        arrn6[44] = 536870928;
        arrn6[45] = 541081616;
        arrn6[46] = 4210688;
        arrn6[47] = 541065216;
        arrn6[48] = 4210704;
        arrn6[49] = 541081600;
        arrn6[51] = 541065232;
        arrn6[52] = 16;
        arrn6[53] = 16384;
        arrn6[54] = 541065216;
        arrn6[55] = 4210704;
        arrn6[56] = 16384;
        arrn6[57] = 4194320;
        arrn6[58] = 536887312;
        arrn6[60] = 541081600;
        arrn6[61] = 536870912;
        arrn6[62] = 4194320;
        arrn6[63] = 536887312;
        SP6 = arrn6;
        int[] arrn7 = new int[64];
        arrn7[0] = 2097152;
        arrn7[1] = 69206018;
        arrn7[2] = 67110914;
        arrn7[4] = 2048;
        arrn7[5] = 67110914;
        arrn7[6] = 2099202;
        arrn7[7] = 69208064;
        arrn7[8] = 69208066;
        arrn7[9] = 2097152;
        arrn7[11] = 67108866;
        arrn7[12] = 2;
        arrn7[13] = 67108864;
        arrn7[14] = 69206018;
        arrn7[15] = 2050;
        arrn7[16] = 67110912;
        arrn7[17] = 2099202;
        arrn7[18] = 2097154;
        arrn7[19] = 67110912;
        arrn7[20] = 67108866;
        arrn7[21] = 69206016;
        arrn7[22] = 69208064;
        arrn7[23] = 2097154;
        arrn7[24] = 69206016;
        arrn7[25] = 2048;
        arrn7[26] = 2050;
        arrn7[27] = 69208066;
        arrn7[28] = 2099200;
        arrn7[29] = 2;
        arrn7[30] = 67108864;
        arrn7[31] = 2099200;
        arrn7[32] = 67108864;
        arrn7[33] = 2099200;
        arrn7[34] = 2097152;
        arrn7[35] = 67110914;
        arrn7[36] = 67110914;
        arrn7[37] = 69206018;
        arrn7[38] = 69206018;
        arrn7[39] = 2;
        arrn7[40] = 2097154;
        arrn7[41] = 67108864;
        arrn7[42] = 67110912;
        arrn7[43] = 2097152;
        arrn7[44] = 69208064;
        arrn7[45] = 2050;
        arrn7[46] = 2099202;
        arrn7[47] = 69208064;
        arrn7[48] = 2050;
        arrn7[49] = 67108866;
        arrn7[50] = 69208066;
        arrn7[51] = 69206016;
        arrn7[52] = 2099200;
        arrn7[54] = 2;
        arrn7[55] = 69208066;
        arrn7[57] = 2099202;
        arrn7[58] = 69206016;
        arrn7[59] = 2048;
        arrn7[60] = 67108866;
        arrn7[61] = 67110912;
        arrn7[62] = 2048;
        arrn7[63] = 2097154;
        SP7 = arrn7;
        int[] arrn8 = new int[64];
        arrn8[0] = 268439616;
        arrn8[1] = 4096;
        arrn8[2] = 262144;
        arrn8[3] = 268701760;
        arrn8[4] = 268435456;
        arrn8[5] = 268439616;
        arrn8[6] = 64;
        arrn8[7] = 268435456;
        arrn8[8] = 262208;
        arrn8[9] = 268697600;
        arrn8[10] = 268701760;
        arrn8[11] = 266240;
        arrn8[12] = 268701696;
        arrn8[13] = 266304;
        arrn8[14] = 4096;
        arrn8[15] = 64;
        arrn8[16] = 268697600;
        arrn8[17] = 268435520;
        arrn8[18] = 268439552;
        arrn8[19] = 4160;
        arrn8[20] = 266240;
        arrn8[21] = 262208;
        arrn8[22] = 268697664;
        arrn8[23] = 268701696;
        arrn8[24] = 4160;
        arrn8[27] = 268697664;
        arrn8[28] = 268435520;
        arrn8[29] = 268439552;
        arrn8[30] = 266304;
        arrn8[31] = 262144;
        arrn8[32] = 266304;
        arrn8[33] = 262144;
        arrn8[34] = 268701696;
        arrn8[35] = 4096;
        arrn8[36] = 64;
        arrn8[37] = 268697664;
        arrn8[38] = 4096;
        arrn8[39] = 266304;
        arrn8[40] = 268439552;
        arrn8[41] = 64;
        arrn8[42] = 268435520;
        arrn8[43] = 268697600;
        arrn8[44] = 268697664;
        arrn8[45] = 268435456;
        arrn8[46] = 262144;
        arrn8[47] = 268439616;
        arrn8[49] = 268701760;
        arrn8[50] = 262208;
        arrn8[51] = 268435520;
        arrn8[52] = 268697600;
        arrn8[53] = 268439552;
        arrn8[54] = 268439616;
        arrn8[56] = 268701760;
        arrn8[57] = 266240;
        arrn8[58] = 266240;
        arrn8[59] = 4160;
        arrn8[60] = 4160;
        arrn8[61] = 262208;
        arrn8[62] = 268435456;
        arrn8[63] = 268701696;
        SP8 = arrn8;
        byte[] arrby = new byte[256];
        arrby[0] = 8;
        arrby[1] = 1;
        arrby[3] = 8;
        arrby[5] = 8;
        arrby[6] = 8;
        arrby[9] = 8;
        arrby[10] = 8;
        arrby[12] = 8;
        arrby[14] = 2;
        arrby[15] = 8;
        arrby[17] = 8;
        arrby[18] = 8;
        arrby[20] = 8;
        arrby[23] = 8;
        arrby[24] = 8;
        arrby[27] = 8;
        arrby[29] = 8;
        arrby[30] = 8;
        arrby[31] = 3;
        arrby[33] = 8;
        arrby[34] = 8;
        arrby[36] = 8;
        arrby[39] = 8;
        arrby[40] = 8;
        arrby[43] = 8;
        arrby[45] = 8;
        arrby[46] = 8;
        arrby[48] = 8;
        arrby[51] = 8;
        arrby[53] = 8;
        arrby[54] = 8;
        arrby[57] = 8;
        arrby[58] = 8;
        arrby[60] = 8;
        arrby[63] = 8;
        arrby[65] = 8;
        arrby[66] = 8;
        arrby[68] = 8;
        arrby[71] = 8;
        arrby[72] = 8;
        arrby[75] = 8;
        arrby[77] = 8;
        arrby[78] = 8;
        arrby[80] = 8;
        arrby[83] = 8;
        arrby[85] = 8;
        arrby[86] = 8;
        arrby[89] = 8;
        arrby[90] = 8;
        arrby[92] = 8;
        arrby[95] = 8;
        arrby[96] = 8;
        arrby[99] = 8;
        arrby[101] = 8;
        arrby[102] = 8;
        arrby[105] = 8;
        arrby[106] = 8;
        arrby[108] = 8;
        arrby[111] = 8;
        arrby[113] = 8;
        arrby[114] = 8;
        arrby[116] = 8;
        arrby[119] = 8;
        arrby[120] = 8;
        arrby[123] = 8;
        arrby[125] = 8;
        arrby[126] = 8;
        arrby[129] = 8;
        arrby[130] = 8;
        arrby[132] = 8;
        arrby[135] = 8;
        arrby[136] = 8;
        arrby[139] = 8;
        arrby[141] = 8;
        arrby[142] = 8;
        arrby[144] = 8;
        arrby[147] = 8;
        arrby[149] = 8;
        arrby[150] = 8;
        arrby[153] = 8;
        arrby[154] = 8;
        arrby[156] = 8;
        arrby[159] = 8;
        arrby[160] = 8;
        arrby[163] = 8;
        arrby[165] = 8;
        arrby[166] = 8;
        arrby[169] = 8;
        arrby[170] = 8;
        arrby[172] = 8;
        arrby[175] = 8;
        arrby[177] = 8;
        arrby[178] = 8;
        arrby[180] = 8;
        arrby[183] = 8;
        arrby[184] = 8;
        arrby[187] = 8;
        arrby[189] = 8;
        arrby[190] = 8;
        arrby[192] = 8;
        arrby[195] = 8;
        arrby[197] = 8;
        arrby[198] = 8;
        arrby[201] = 8;
        arrby[202] = 8;
        arrby[204] = 8;
        arrby[207] = 8;
        arrby[209] = 8;
        arrby[210] = 8;
        arrby[212] = 8;
        arrby[215] = 8;
        arrby[216] = 8;
        arrby[219] = 8;
        arrby[221] = 8;
        arrby[222] = 8;
        arrby[224] = 4;
        arrby[225] = 8;
        arrby[226] = 8;
        arrby[228] = 8;
        arrby[231] = 8;
        arrby[232] = 8;
        arrby[235] = 8;
        arrby[237] = 8;
        arrby[238] = 8;
        arrby[240] = 8;
        arrby[241] = 5;
        arrby[243] = 8;
        arrby[245] = 8;
        arrby[246] = 8;
        arrby[249] = 8;
        arrby[250] = 8;
        arrby[252] = 8;
        arrby[254] = 6;
        arrby[255] = 8;
        PARITY = arrby;
        ROTARS = new byte[]{1, 2, 4, 6, 8, 10, 12, 14, 15, 17, 19, 21, 23, 25, 27, 28};
        byte[] arrby2 = new byte[56];
        arrby2[0] = 56;
        arrby2[1] = 48;
        arrby2[2] = 40;
        arrby2[3] = 32;
        arrby2[4] = 24;
        arrby2[5] = 16;
        arrby2[6] = 8;
        arrby2[8] = 57;
        arrby2[9] = 49;
        arrby2[10] = 41;
        arrby2[11] = 33;
        arrby2[12] = 25;
        arrby2[13] = 17;
        arrby2[14] = 9;
        arrby2[15] = 1;
        arrby2[16] = 58;
        arrby2[17] = 50;
        arrby2[18] = 42;
        arrby2[19] = 34;
        arrby2[20] = 26;
        arrby2[21] = 18;
        arrby2[22] = 10;
        arrby2[23] = 2;
        arrby2[24] = 59;
        arrby2[25] = 51;
        arrby2[26] = 43;
        arrby2[27] = 35;
        arrby2[28] = 62;
        arrby2[29] = 54;
        arrby2[30] = 46;
        arrby2[31] = 38;
        arrby2[32] = 30;
        arrby2[33] = 22;
        arrby2[34] = 14;
        arrby2[35] = 6;
        arrby2[36] = 61;
        arrby2[37] = 53;
        arrby2[38] = 45;
        arrby2[39] = 37;
        arrby2[40] = 29;
        arrby2[41] = 21;
        arrby2[42] = 13;
        arrby2[43] = 5;
        arrby2[44] = 60;
        arrby2[45] = 52;
        arrby2[46] = 44;
        arrby2[47] = 36;
        arrby2[48] = 28;
        arrby2[49] = 20;
        arrby2[50] = 12;
        arrby2[51] = 4;
        arrby2[52] = 27;
        arrby2[53] = 19;
        arrby2[54] = 11;
        arrby2[55] = 3;
        PC1 = arrby2;
        byte[] arrby3 = new byte[48];
        arrby3[0] = 13;
        arrby3[1] = 16;
        arrby3[2] = 10;
        arrby3[3] = 23;
        arrby3[5] = 4;
        arrby3[6] = 2;
        arrby3[7] = 27;
        arrby3[8] = 14;
        arrby3[9] = 5;
        arrby3[10] = 20;
        arrby3[11] = 9;
        arrby3[12] = 22;
        arrby3[13] = 18;
        arrby3[14] = 11;
        arrby3[15] = 3;
        arrby3[16] = 25;
        arrby3[17] = 7;
        arrby3[18] = 15;
        arrby3[19] = 6;
        arrby3[20] = 26;
        arrby3[21] = 19;
        arrby3[22] = 12;
        arrby3[23] = 1;
        arrby3[24] = 40;
        arrby3[25] = 51;
        arrby3[26] = 30;
        arrby3[27] = 36;
        arrby3[28] = 46;
        arrby3[29] = 54;
        arrby3[30] = 29;
        arrby3[31] = 39;
        arrby3[32] = 50;
        arrby3[33] = 44;
        arrby3[34] = 32;
        arrby3[35] = 47;
        arrby3[36] = 43;
        arrby3[37] = 48;
        arrby3[38] = 38;
        arrby3[39] = 55;
        arrby3[40] = 33;
        arrby3[41] = 52;
        arrby3[42] = 45;
        arrby3[43] = 41;
        arrby3[44] = 49;
        arrby3[45] = 35;
        arrby3[46] = 28;
        arrby3[47] = 31;
        PC2 = arrby3;
        WEAK_KEYS = new byte[][]{Util.toBytesFromString("0101010101010101"), Util.toBytesFromString("01010101FEFEFEFE"), Util.toBytesFromString("FEFEFEFE01010101"), Util.toBytesFromString("FEFEFEFEFEFEFEFE")};
        SEMIWEAK_KEYS = new byte[][]{Util.toBytesFromString("01FE01FE01FE01FE"), Util.toBytesFromString("FE01FE01FE01FE01"), Util.toBytesFromString("1FE01FE00EF10EF1"), Util.toBytesFromString("E01FE01FF10EF10E"), Util.toBytesFromString("01E001E001F101F1"), Util.toBytesFromString("E001E001F101F101"), Util.toBytesFromString("1FFE1FFE0EFE0EFE"), Util.toBytesFromString("FE1FFE1FFE0EFE0E"), Util.toBytesFromString("011F011F010E010E"), Util.toBytesFromString("1F011F010E010E01"), Util.toBytesFromString("E0FEE0FEF1FEF1FE"), Util.toBytesFromString("FEE0FEE0FEF1FEF1")};
        POSSIBLE_WEAK_KEYS = new byte[][]{Util.toBytesFromString("1F1F01010E0E0101"), Util.toBytesFromString("011F1F01010E0E01"), Util.toBytesFromString("1F01011F0E01010E"), Util.toBytesFromString("01011F1F01010E0E"), Util.toBytesFromString("E0E00101F1F10101"), Util.toBytesFromString("FEFE0101FEFE0101"), Util.toBytesFromString("FEE01F01FEF10E01"), Util.toBytesFromString("E0FE1F01F1FE0E01"), Util.toBytesFromString("FEE0011FFEF1010E"), Util.toBytesFromString("E0FE011FF1FE010E"), Util.toBytesFromString("E0E01F1FF1F10E0E"), Util.toBytesFromString("FEFE1F1FFEFE0E0E"), Util.toBytesFromString("1F1F01010E0E0101"), Util.toBytesFromString("011F1F01010E0E01"), Util.toBytesFromString("1F01011F0E01010E"), Util.toBytesFromString("01011F1F01010E0E"), Util.toBytesFromString("01E0E00101F1F101"), Util.toBytesFromString("1FFEE0010EFEF001"), Util.toBytesFromString("1FE0FE010EF1FE01"), Util.toBytesFromString("01FEFE0101FEFE01"), Util.toBytesFromString("1FE0E01F0EF1F10E"), Util.toBytesFromString("01FEE01F01FEF10E"), Util.toBytesFromString("01E0FE1F01F1FE0E"), Util.toBytesFromString("1FFEFE1F0EFEFE0E"), Util.toBytesFromString("E00101E0F10101F1"), Util.toBytesFromString("FE1F01E0FE0E0EF1"), Util.toBytesFromString("FE011FE0FE010EF1"), Util.toBytesFromString("E01F1FE0F10E0EF1"), Util.toBytesFromString("FE0101FEFE0101FE"), Util.toBytesFromString("E01F01FEF10E01FE"), Util.toBytesFromString("E0011FFEF1010EFE"), Util.toBytesFromString("FE1F1FFEFE0E0EFE"), Util.toBytesFromString("1FFE01E00EFE01F1"), Util.toBytesFromString("01FE1FE001FE0EF1"), Util.toBytesFromString("1FE001FE0EF101FE"), Util.toBytesFromString("01E01FFE01F10EFE"), Util.toBytesFromString("0101E0E00101F1F1"), Util.toBytesFromString("1F1FE0E00E0EF1F1"), Util.toBytesFromString("1F01FEE00E01FEF1"), Util.toBytesFromString("011FFEE0010EFEF1"), Util.toBytesFromString("1F01E0FE0E01F1FE"), Util.toBytesFromString("011FE0FE010EF1FE"), Util.toBytesFromString("0101FEFE0001FEFE"), Util.toBytesFromString("1F1FFEFE0E0EFEFE"), Util.toBytesFromString("FEFEE0E0FEFEF1F1"), Util.toBytesFromString("E0FEFEE0F1FEFEF1"), Util.toBytesFromString("FEE0E0FEFEF1F1FE"), Util.toBytesFromString("E0E0FEFEF1F1FEFE")};
    }

    final class Context {
        private static final int EXPANDED_KEY_SIZE = 32;
        int[] ek = new int[32];
        int[] dk = new int[32];

        final byte[] getEncryptionKeyBytes() {
            return this.toByteArray(this.ek);
        }

        final byte[] getDecryptionKeyBytes() {
            return this.toByteArray(this.dk);
        }

        final byte[] toByteArray(int[] k) {
            byte[] result = new byte[4 * k.length];
            int i = 0;
            int j = 0;
            while (i < k.length) {
                result[j++] = (byte)(k[i] >>> 24);
                result[j++] = (byte)(k[i] >>> 16);
                result[j++] = (byte)(k[i] >>> 8);
                result[j++] = (byte)k[i];
                ++i;
            }
            return result;
        }

        Context() {
        }
    }

}

