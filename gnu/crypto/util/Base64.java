/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Base64 {
    private static final String NAME = "Base64";
    private static final boolean DEBUG = true;
    private static final int debuglevel = 9;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte NEW_LINE = 10;
    private static final byte EQUALS_SIGN = 61;
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte[] ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] DECODABET;

    private static final void debug(String s) {
        err.println(">>> Base64: " + s);
    }

    public static final String encode(byte[] src) {
        return Base64.encode(src, 0, src.length, true);
    }

    public static final String encode(byte[] src, int off, int len, boolean breakLines) {
        int len43 = len * 4 / 3;
        int n = 0;
        if (breakLines) {
            n = len43 / 76;
        }
        byte[] outBuff = new byte[len43 + (len % 3 > 0 ? 4 : 0) + n];
        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        while (d < len2) {
            Base64.encode3to4(src, d + off, 3, outBuff, e);
            if (breakLines && (lineLength += 4) == 76) {
                outBuff[e + 4] = 10;
                ++e;
                lineLength = 0;
            }
            d += 3;
            e += 4;
        }
        if (d < len) {
            Base64.encode3to4(src, d + off, len - d, outBuff, e);
            e += 4;
        }
        return new String(outBuff, 0, e);
    }

    public static final byte[] decode(String s) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes("US-ASCII");
        return Base64.decode(bytes, 0, bytes.length);
    }

    public static byte[] decode(byte[] src, int off, int len) {
        int len34 = len * 3 / 4;
        byte[] outBuff = new byte[len34];
        int outBuffPosn = 0;
        byte[] b4 = new byte[4];
        int b4Posn = 0;
        int i = off;
        while (i < off + len) {
            byte sbiCrop = (byte)(src[i] & 127);
            byte sbiDecode = DECODABET[sbiCrop];
            if (sbiDecode >= -5) {
                if (sbiDecode >= -1) {
                    b4[b4Posn++] = sbiCrop;
                    if (b4Posn > 3) {
                        outBuffPosn += Base64.decode4to3(b4, 0, outBuff, outBuffPosn);
                        b4Posn = 0;
                        if (sbiCrop == 61) {
                            break;
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Illegal BASE-64 character at #" + i + ": " + src[i] + "(decimal)");
            }
            ++i;
        }
        byte[] result = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, result, 0, outBuffPosn);
        return result;
    }

    private static final byte[] encode3to4(byte[] src, int sOffset, int numBytes, byte[] dest, int dOffset) {
        int n = 0;
        if (numBytes > 0) {
            n = src[sOffset] << 24 >>> 8;
        }
        int n2 = 0;
        if (numBytes > 1) {
            n2 = src[sOffset + 1] << 24 >>> 16;
        }
        int n3 = 0;
        if (numBytes > 2) {
            n3 = src[sOffset + 2] << 24 >>> 24;
        }
        int inBuff = n | n2 | n3;
        switch (numBytes) {
            case 3: {
                dest[dOffset] = ALPHABET[inBuff >>> 18];
                dest[dOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                dest[dOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                dest[dOffset + 3] = ALPHABET[inBuff & 63];
                break;
            }
            case 2: {
                dest[dOffset] = ALPHABET[inBuff >>> 18];
                dest[dOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                dest[dOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                dest[dOffset + 3] = 61;
                break;
            }
            case 1: {
                dest[dOffset] = ALPHABET[inBuff >>> 18];
                dest[dOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                dest[dOffset + 2] = 61;
                dest[dOffset + 3] = 61;
                break;
            }
        }
        return dest;
    }

    private static final int decode4to3(byte[] src, int sOffset, byte[] dest, int dOffset) {
        if (src[sOffset + 2] == 61) {
            int outBuff = (DECODABET[src[sOffset]] & 255) << 18 | (DECODABET[src[sOffset + 1]] & 255) << 12;
            dest[dOffset] = (byte)(outBuff >>> 16);
            return 1;
        }
        if (src[sOffset + 3] == 61) {
            int outBuff = (DECODABET[src[sOffset]] & 255) << 18 | (DECODABET[src[sOffset + 1]] & 255) << 12 | (DECODABET[src[sOffset + 2]] & 255) << 6;
            dest[dOffset] = (byte)(outBuff >>> 16);
            dest[dOffset + 1] = (byte)(outBuff >>> 8);
            return 2;
        }
        try {
            int outBuff = (DECODABET[src[sOffset]] & 255) << 18 | (DECODABET[src[sOffset + 1]] & 255) << 12 | (DECODABET[src[sOffset + 2]] & 255) << 6 | DECODABET[src[sOffset + 3]] & 255;
            dest[dOffset] = (byte)(outBuff >> 16);
            dest[dOffset + 1] = (byte)(outBuff >> 8);
            dest[dOffset + 2] = (byte)outBuff;
            return 3;
        }
        catch (Exception x) {
            Base64.debug("" + src[sOffset] + ": " + DECODABET[src[sOffset]]);
            Base64.debug("" + src[sOffset + 1] + ": " + DECODABET[src[sOffset + 1]]);
            Base64.debug("" + src[sOffset + 2] + ": " + DECODABET[src[sOffset + 2]]);
            Base64.debug("" + src[sOffset + 3] + ": " + DECODABET[src[sOffset + 3]]);
            return -1;
        }
    }

    private Base64() {
    }

    private static final {
        byte[] arrby = new byte[127];
        arrby[0] = -9;
        arrby[1] = -9;
        arrby[2] = -9;
        arrby[3] = -9;
        arrby[4] = -9;
        arrby[5] = -9;
        arrby[6] = -9;
        arrby[7] = -9;
        arrby[8] = -9;
        arrby[9] = -5;
        arrby[10] = -5;
        arrby[11] = -9;
        arrby[12] = -9;
        arrby[13] = -5;
        arrby[14] = -9;
        arrby[15] = -9;
        arrby[16] = -9;
        arrby[17] = -9;
        arrby[18] = -9;
        arrby[19] = -9;
        arrby[20] = -9;
        arrby[21] = -9;
        arrby[22] = -9;
        arrby[23] = -9;
        arrby[24] = -9;
        arrby[25] = -9;
        arrby[26] = -9;
        arrby[27] = -9;
        arrby[28] = -9;
        arrby[29] = -9;
        arrby[30] = -9;
        arrby[31] = -9;
        arrby[32] = -5;
        arrby[33] = -9;
        arrby[34] = -9;
        arrby[35] = -9;
        arrby[36] = -9;
        arrby[37] = -9;
        arrby[38] = -9;
        arrby[39] = -9;
        arrby[40] = -9;
        arrby[41] = -9;
        arrby[42] = -9;
        arrby[43] = 62;
        arrby[44] = -9;
        arrby[45] = -9;
        arrby[46] = -9;
        arrby[47] = 63;
        arrby[48] = 52;
        arrby[49] = 53;
        arrby[50] = 54;
        arrby[51] = 55;
        arrby[52] = 56;
        arrby[53] = 57;
        arrby[54] = 58;
        arrby[55] = 59;
        arrby[56] = 60;
        arrby[57] = 61;
        arrby[58] = -9;
        arrby[59] = -9;
        arrby[60] = -9;
        arrby[61] = -1;
        arrby[62] = -9;
        arrby[63] = -9;
        arrby[64] = -9;
        arrby[66] = 1;
        arrby[67] = 2;
        arrby[68] = 3;
        arrby[69] = 4;
        arrby[70] = 5;
        arrby[71] = 6;
        arrby[72] = 7;
        arrby[73] = 8;
        arrby[74] = 9;
        arrby[75] = 10;
        arrby[76] = 11;
        arrby[77] = 12;
        arrby[78] = 13;
        arrby[79] = 14;
        arrby[80] = 15;
        arrby[81] = 16;
        arrby[82] = 17;
        arrby[83] = 18;
        arrby[84] = 19;
        arrby[85] = 20;
        arrby[86] = 21;
        arrby[87] = 22;
        arrby[88] = 23;
        arrby[89] = 24;
        arrby[90] = 25;
        arrby[91] = -9;
        arrby[92] = -9;
        arrby[93] = -9;
        arrby[94] = -9;
        arrby[95] = -9;
        arrby[96] = -9;
        arrby[97] = 26;
        arrby[98] = 27;
        arrby[99] = 28;
        arrby[100] = 29;
        arrby[101] = 30;
        arrby[102] = 31;
        arrby[103] = 32;
        arrby[104] = 33;
        arrby[105] = 34;
        arrby[106] = 35;
        arrby[107] = 36;
        arrby[108] = 37;
        arrby[109] = 38;
        arrby[110] = 39;
        arrby[111] = 40;
        arrby[112] = 41;
        arrby[113] = 42;
        arrby[114] = 43;
        arrby[115] = 44;
        arrby[116] = 45;
        arrby[117] = 46;
        arrby[118] = 47;
        arrby[119] = 48;
        arrby[120] = 49;
        arrby[121] = 50;
        arrby[122] = 51;
        arrby[123] = -9;
        arrby[124] = -9;
        arrby[125] = -9;
        arrby[126] = -9;
        DECODABET = arrby;
    }
}

