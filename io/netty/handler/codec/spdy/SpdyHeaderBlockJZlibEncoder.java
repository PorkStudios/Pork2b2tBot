/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  com.jcraft.jzlib.Deflater
 *  com.jcraft.jzlib.JZlib
 *  com.jcraft.jzlib.JZlib$WrapperType
 */
package io.netty.handler.codec.spdy;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.ReferenceCounted;

class SpdyHeaderBlockJZlibEncoder
extends SpdyHeaderBlockRawEncoder {
    private final Deflater z = new Deflater();
    private boolean finished;

    SpdyHeaderBlockJZlibEncoder(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
        super(version);
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        }
        if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        }
        int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
        if (resultCode != 0) {
            throw new CompressionException("failed to initialize an SPDY header block deflater: " + resultCode);
        }
        resultCode = this.z.deflateSetDictionary(SpdyCodecUtil.SPDY_DICT, SpdyCodecUtil.SPDY_DICT.length);
        if (resultCode != 0) {
            throw new CompressionException("failed to set the SPDY dictionary: " + resultCode);
        }
    }

    private void setInput(ByteBuf decompressed) {
        byte[] in;
        int offset;
        int len = decompressed.readableBytes();
        if (decompressed.hasArray()) {
            in = decompressed.array();
            offset = decompressed.arrayOffset() + decompressed.readerIndex();
        } else {
            in = new byte[len];
            decompressed.getBytes(decompressed.readerIndex(), in);
            offset = 0;
        }
        this.z.next_in = in;
        this.z.next_in_index = offset;
        this.z.avail_in = len;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ByteBuf encode(ByteBufAllocator alloc) {
        boolean release = true;
        ReferenceCounted out = null;
        try {
            int resultCode;
            int oldNextOutIndex;
            int oldNextInIndex = this.z.next_in_index;
            oldNextOutIndex = this.z.next_out_index;
            int maxOutputLength = (int)Math.ceil((double)this.z.next_in.length * 1.001) + 12;
            out = alloc.heapBuffer(maxOutputLength);
            this.z.next_out = out.array();
            this.z.next_out_index = out.arrayOffset() + out.writerIndex();
            this.z.avail_out = maxOutputLength;
            try {
                resultCode = this.z.deflate(2);
            }
            finally {
                out.skipBytes(this.z.next_in_index - oldNextInIndex);
            }
            if (resultCode != 0) {
                throw new CompressionException("compression failure: " + resultCode);
            }
            int outputLength = this.z.next_out_index - oldNextOutIndex;
            if (outputLength > 0) {
                out.writerIndex(out.writerIndex() + outputLength);
            }
            release = false;
            ReferenceCounted referenceCounted = out;
            return referenceCounted;
        }
        finally {
            this.z.next_in = null;
            this.z.next_out = null;
            if (release && out != null) {
                out.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf encode(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
        if (frame == null) {
            throw new IllegalArgumentException("frame");
        }
        if (this.finished) {
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBuf decompressed = super.encode(alloc, frame);
        try {
            if (!decompressed.isReadable()) {
                ByteBuf byteBuf = Unpooled.EMPTY_BUFFER;
                return byteBuf;
            }
            this.setInput(decompressed);
            ByteBuf byteBuf = this.encode(alloc);
            return byteBuf;
        }
        finally {
            decompressed.release();
        }
    }

    @Override
    public void end() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        this.z.deflateEnd();
        this.z.next_in = null;
        this.z.next_out = null;
    }
}

