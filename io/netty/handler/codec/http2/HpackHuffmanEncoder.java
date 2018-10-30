/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.HpackUtil;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

final class HpackHuffmanEncoder {
    private final int[] codes;
    private final byte[] lengths;
    private final EncodedLengthProcessor encodedLengthProcessor = new EncodedLengthProcessor();
    private final EncodeProcessor encodeProcessor = new EncodeProcessor();

    HpackHuffmanEncoder() {
        this(HpackUtil.HUFFMAN_CODES, HpackUtil.HUFFMAN_CODE_LENGTHS);
    }

    private HpackHuffmanEncoder(int[] codes, byte[] lengths) {
        this.codes = codes;
        this.lengths = lengths;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void encode(ByteBuf out, CharSequence data) {
        ObjectUtil.checkNotNull(out, "out");
        if (data instanceof AsciiString) {
            AsciiString string = (AsciiString)data;
            try {
                this.encodeProcessor.out = out;
                string.forEachByte(this.encodeProcessor);
            }
            catch (Exception e) {
                PlatformDependent.throwException(e);
            }
            finally {
                this.encodeProcessor.end();
            }
        } else {
            this.encodeSlowPath(out, data);
        }
    }

    private void encodeSlowPath(ByteBuf out, CharSequence data) {
        long current = 0L;
        int n = 0;
        for (int i = 0; i < data.length(); ++i) {
            int b = data.charAt(i) & 255;
            int code = this.codes[b];
            byte nbits = this.lengths[b];
            current <<= nbits;
            current |= (long)code;
            n += nbits;
            while (n >= 8) {
                out.writeByte((int)(current >> (n -= 8)));
            }
        }
        if (n > 0) {
            current <<= 8 - n;
            out.writeByte((int)(current |= (long)(255 >>> n)));
        }
    }

    int getEncodedLength(CharSequence data) {
        if (data instanceof AsciiString) {
            AsciiString string = (AsciiString)data;
            try {
                this.encodedLengthProcessor.reset();
                string.forEachByte(this.encodedLengthProcessor);
                return this.encodedLengthProcessor.length();
            }
            catch (Exception e) {
                PlatformDependent.throwException(e);
                return -1;
            }
        }
        return this.getEncodedLengthSlowPath(data);
    }

    private int getEncodedLengthSlowPath(CharSequence data) {
        long len = 0L;
        for (int i = 0; i < data.length(); ++i) {
            len += (long)this.lengths[data.charAt(i) & 255];
        }
        return (int)(len + 7L >> 3);
    }

    private final class EncodedLengthProcessor
    implements ByteProcessor {
        private long len;

        private EncodedLengthProcessor() {
        }

        @Override
        public boolean process(byte value) {
            this.len += (long)HpackHuffmanEncoder.this.lengths[value & 255];
            return true;
        }

        void reset() {
            this.len = 0L;
        }

        int length() {
            return (int)(this.len + 7L >> 3);
        }
    }

    private final class EncodeProcessor
    implements ByteProcessor {
        ByteBuf out;
        private long current;
        private int n;

        private EncodeProcessor() {
        }

        @Override
        public boolean process(byte value) {
            int b = value & 255;
            byte nbits = HpackHuffmanEncoder.this.lengths[b];
            this.current <<= nbits;
            this.current |= (long)HpackHuffmanEncoder.this.codes[b];
            this.n += nbits;
            while (this.n >= 8) {
                this.n -= 8;
                this.out.writeByte((int)(this.current >> this.n));
            }
            return true;
        }

        void end() {
            try {
                if (this.n > 0) {
                    this.current <<= 8 - this.n;
                    this.current |= (long)(255 >>> this.n);
                    this.out.writeByte((int)this.current);
                }
            }
            finally {
                this.out = null;
                this.current = 0L;
                this.n = 0;
            }
        }
    }

}

