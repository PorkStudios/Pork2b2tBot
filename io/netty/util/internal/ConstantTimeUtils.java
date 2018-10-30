/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

public final class ConstantTimeUtils {
    private ConstantTimeUtils() {
    }

    public static int equalsConstantTime(int x, int y) {
        int z = -1 ^ (x ^ y);
        z &= z >> 16;
        z &= z >> 8;
        z &= z >> 4;
        z &= z >> 2;
        z &= z >> 1;
        return z & 1;
    }

    public static int equalsConstantTime(long x, long y) {
        long z = -1L ^ (x ^ y);
        z &= z >> 32;
        z &= z >> 16;
        z &= z >> 8;
        z &= z >> 4;
        z &= z >> 2;
        z &= z >> 1;
        return (int)(z & 1L);
    }

    public static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        int b = 0;
        int end = startPos1 + length;
        while (startPos1 < end) {
            b |= bytes1[startPos1] ^ bytes2[startPos2];
            ++startPos1;
            ++startPos2;
        }
        return ConstantTimeUtils.equalsConstantTime(b, 0);
    }

    public static int equalsConstantTime(CharSequence s1, CharSequence s2) {
        if (s1.length() != s2.length()) {
            return 0;
        }
        int c = 0;
        for (int i = 0; i < s1.length(); ++i) {
            c |= s1.charAt(i) ^ s2.charAt(i);
        }
        return ConstantTimeUtils.equalsConstantTime(c, 0);
    }
}

