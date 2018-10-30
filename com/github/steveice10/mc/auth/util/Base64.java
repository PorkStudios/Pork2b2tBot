/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.auth.util;

public class Base64 {
    private static final byte EQUALS_SIGN = 61;
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte[] ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};

    private Base64() {
    }

    public static byte[] encode(byte[] source) {
        if (source == null) {
            throw new NullPointerException("Cannot serialize a null array.");
        }
        byte[] outBuff = new byte[source.length / 3 * 4 + (source.length % 3 > 0 ? 4 : 0)];
        int d = 0;
        int e = 0;
        while (d < source.length - 2) {
            Base64.encode3to4(source, d, 3, outBuff, e);
            d += 3;
            e += 4;
        }
        if (d < source.length) {
            Base64.encode3to4(source, d, source.length - d, outBuff, e);
            e += 4;
        }
        if (e <= outBuff.length - 1) {
            byte[] finalOut = new byte[e];
            System.arraycopy(outBuff, 0, finalOut, 0, e);
            return finalOut;
        }
        return outBuff;
    }

    public static byte[] decode(byte[] source) {
        if (source == null) {
            throw new NullPointerException("Cannot decode null source array.");
        }
        byte[] outBuff = new byte[source.length * 3 / 4];
        byte[] b4 = new byte[4];
        int outBuffPosn = 0;
        int b4Posn = 0;
        for (int i = 0; i < source.length; ++i) {
            byte sbiDecode = DECODABET[source[i] & 255];
            if (sbiDecode >= -5) {
                if (sbiDecode < -1) continue;
                b4[b4Posn++] = source[i];
                if (b4Posn <= 3) continue;
                outBuffPosn += Base64.decode4to3(b4, 0, outBuff, outBuffPosn);
                b4Posn = 0;
                if (source[i] != 61) continue;
                break;
            }
            throw new IllegalArgumentException(String.format("Bad Base64 input character decimal %d in array position %d", source[i] & 255, i));
        }
        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }

    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
        int inBuff = (numSigBytes > 0 ? source[srcOffset] << 24 >>> 8 : 0) | (numSigBytes > 1 ? source[srcOffset + 1] << 24 >>> 16 : 0) | (numSigBytes > 2 ? source[srcOffset + 2] << 24 >>> 24 : 0);
        switch (numSigBytes) {
            case 3: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                destination[destOffset + 3] = ALPHABET[inBuff & 63];
                return destination;
            }
            case 2: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                destination[destOffset + 3] = 61;
                return destination;
            }
            case 1: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = 61;
                destination[destOffset + 3] = 61;
                return destination;
            }
        }
        return destination;
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset) {
        if (source == null) {
            throw new NullPointerException("Source array was null.");
        }
        if (destination == null) {
            throw new NullPointerException("Destination array was null.");
        }
        if (srcOffset < 0 || srcOffset + 3 >= source.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes.", source.length, srcOffset));
        }
        if (destOffset < 0 || destOffset + 2 >= destination.length) {
            throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", destination.length, destOffset));
        }
        if (source[srcOffset + 2] == 61) {
            int outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12;
            destination[destOffset] = (byte)(outBuff >>> 16);
            return 1;
        }
        if (source[srcOffset + 3] == 61) {
            int outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12 | (DECODABET[source[srcOffset + 2]] & 255) << 6;
            destination[destOffset] = (byte)(outBuff >>> 16);
            destination[destOffset + 1] = (byte)(outBuff >>> 8);
            return 2;
        }
        int outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12 | (DECODABET[source[srcOffset + 2]] & 255) << 6 | DECODABET[source[srcOffset + 3]] & 255;
        destination[destOffset] = (byte)(outBuff >> 16);
        destination[destOffset + 1] = (byte)(outBuff >> 8);
        destination[destOffset + 2] = (byte)outBuff;
        return 3;
    }
}

