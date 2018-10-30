/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

final class HeapByteBufUtil {
    static byte getByte(byte[] memory, int index) {
        return memory[index];
    }

    static short getShort(byte[] memory, int index) {
        return (short)(memory[index] << 8 | memory[index + 1] & 255);
    }

    static short getShortLE(byte[] memory, int index) {
        return (short)(memory[index] & 255 | memory[index + 1] << 8);
    }

    static int getUnsignedMedium(byte[] memory, int index) {
        return (memory[index] & 255) << 16 | (memory[index + 1] & 255) << 8 | memory[index + 2] & 255;
    }

    static int getUnsignedMediumLE(byte[] memory, int index) {
        return memory[index] & 255 | (memory[index + 1] & 255) << 8 | (memory[index + 2] & 255) << 16;
    }

    static int getInt(byte[] memory, int index) {
        return (memory[index] & 255) << 24 | (memory[index + 1] & 255) << 16 | (memory[index + 2] & 255) << 8 | memory[index + 3] & 255;
    }

    static int getIntLE(byte[] memory, int index) {
        return memory[index] & 255 | (memory[index + 1] & 255) << 8 | (memory[index + 2] & 255) << 16 | (memory[index + 3] & 255) << 24;
    }

    static long getLong(byte[] memory, int index) {
        return ((long)memory[index] & 255L) << 56 | ((long)memory[index + 1] & 255L) << 48 | ((long)memory[index + 2] & 255L) << 40 | ((long)memory[index + 3] & 255L) << 32 | ((long)memory[index + 4] & 255L) << 24 | ((long)memory[index + 5] & 255L) << 16 | ((long)memory[index + 6] & 255L) << 8 | (long)memory[index + 7] & 255L;
    }

    static long getLongLE(byte[] memory, int index) {
        return (long)memory[index] & 255L | ((long)memory[index + 1] & 255L) << 8 | ((long)memory[index + 2] & 255L) << 16 | ((long)memory[index + 3] & 255L) << 24 | ((long)memory[index + 4] & 255L) << 32 | ((long)memory[index + 5] & 255L) << 40 | ((long)memory[index + 6] & 255L) << 48 | ((long)memory[index + 7] & 255L) << 56;
    }

    static void setByte(byte[] memory, int index, int value) {
        memory[index] = (byte)value;
    }

    static void setShort(byte[] memory, int index, int value) {
        memory[index] = (byte)(value >>> 8);
        memory[index + 1] = (byte)value;
    }

    static void setShortLE(byte[] memory, int index, int value) {
        memory[index] = (byte)value;
        memory[index + 1] = (byte)(value >>> 8);
    }

    static void setMedium(byte[] memory, int index, int value) {
        memory[index] = (byte)(value >>> 16);
        memory[index + 1] = (byte)(value >>> 8);
        memory[index + 2] = (byte)value;
    }

    static void setMediumLE(byte[] memory, int index, int value) {
        memory[index] = (byte)value;
        memory[index + 1] = (byte)(value >>> 8);
        memory[index + 2] = (byte)(value >>> 16);
    }

    static void setInt(byte[] memory, int index, int value) {
        memory[index] = (byte)(value >>> 24);
        memory[index + 1] = (byte)(value >>> 16);
        memory[index + 2] = (byte)(value >>> 8);
        memory[index + 3] = (byte)value;
    }

    static void setIntLE(byte[] memory, int index, int value) {
        memory[index] = (byte)value;
        memory[index + 1] = (byte)(value >>> 8);
        memory[index + 2] = (byte)(value >>> 16);
        memory[index + 3] = (byte)(value >>> 24);
    }

    static void setLong(byte[] memory, int index, long value) {
        memory[index] = (byte)(value >>> 56);
        memory[index + 1] = (byte)(value >>> 48);
        memory[index + 2] = (byte)(value >>> 40);
        memory[index + 3] = (byte)(value >>> 32);
        memory[index + 4] = (byte)(value >>> 24);
        memory[index + 5] = (byte)(value >>> 16);
        memory[index + 6] = (byte)(value >>> 8);
        memory[index + 7] = (byte)value;
    }

    static void setLongLE(byte[] memory, int index, long value) {
        memory[index] = (byte)value;
        memory[index + 1] = (byte)(value >>> 8);
        memory[index + 2] = (byte)(value >>> 16);
        memory[index + 3] = (byte)(value >>> 24);
        memory[index + 4] = (byte)(value >>> 32);
        memory[index + 5] = (byte)(value >>> 40);
        memory[index + 6] = (byte)(value >>> 48);
        memory[index + 7] = (byte)(value >>> 56);
    }

    private HeapByteBufUtil() {
    }
}

