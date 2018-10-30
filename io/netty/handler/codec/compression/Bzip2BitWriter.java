/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;

final class Bzip2BitWriter {
    private long bitBuffer;
    private int bitCount;

    Bzip2BitWriter() {
    }

    void writeBits(ByteBuf out, int count, long value) {
        if (count < 0 || count > 32) {
            throw new IllegalArgumentException("count: " + count + " (expected: 0-32)");
        }
        int bitCount = this.bitCount;
        long bitBuffer = this.bitBuffer | value << 64 - count >>> bitCount;
        if ((bitCount += count) >= 32) {
            out.writeInt((int)(bitBuffer >>> 32));
            bitBuffer <<= 32;
            bitCount -= 32;
        }
        this.bitBuffer = bitBuffer;
        this.bitCount = bitCount;
    }

    void writeBoolean(ByteBuf out, boolean value) {
        int bitCount = this.bitCount + 1;
        long bitBuffer = this.bitBuffer | (value ? 1L << 64 - bitCount : 0L);
        if (bitCount == 32) {
            out.writeInt((int)(bitBuffer >>> 32));
            bitBuffer = 0L;
            bitCount = 0;
        }
        this.bitBuffer = bitBuffer;
        this.bitCount = bitCount;
    }

    void writeUnary(ByteBuf out, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value: " + value + " (expected 0 or more)");
        }
        while (value-- > 0) {
            this.writeBoolean(out, true);
        }
        this.writeBoolean(out, false);
    }

    void writeInt(ByteBuf out, int value) {
        this.writeBits(out, 32, value);
    }

    void flush(ByteBuf out) {
        int bitCount = this.bitCount;
        if (bitCount > 0) {
            long bitBuffer = this.bitBuffer;
            int shiftToRight = 64 - bitCount;
            if (bitCount <= 8) {
                out.writeByte((int)(bitBuffer >>> shiftToRight << 8 - bitCount));
            } else if (bitCount <= 16) {
                out.writeShort((int)(bitBuffer >>> shiftToRight << 16 - bitCount));
            } else if (bitCount <= 24) {
                out.writeMedium((int)(bitBuffer >>> shiftToRight << 24 - bitCount));
            } else {
                out.writeInt((int)(bitBuffer >>> shiftToRight << 32 - bitCount));
            }
        }
    }
}

