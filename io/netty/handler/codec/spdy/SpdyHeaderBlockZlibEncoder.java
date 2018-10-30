/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import java.util.zip.Deflater;

class SpdyHeaderBlockZlibEncoder
extends SpdyHeaderBlockRawEncoder {
    private final Deflater compressor;
    private boolean finished;

    SpdyHeaderBlockZlibEncoder(SpdyVersion spdyVersion, int compressionLevel) {
        super(spdyVersion);
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        this.compressor = new Deflater(compressionLevel);
        this.compressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
    }

    private int setInput(ByteBuf decompressed) {
        int len = decompressed.readableBytes();
        if (decompressed.hasArray()) {
            this.compressor.setInput(decompressed.array(), decompressed.arrayOffset() + decompressed.readerIndex(), len);
        } else {
            byte[] in = new byte[len];
            decompressed.getBytes(decompressed.readerIndex(), in);
            this.compressor.setInput(in, 0, in.length);
        }
        return len;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ByteBuf encode(ByteBufAllocator alloc, int len) {
        ByteBuf compressed = alloc.heapBuffer(len);
        boolean release = true;
        try {
            while (this.compressInto(compressed)) {
                compressed.ensureWritable(compressed.capacity() << 1);
            }
            release = false;
            ByteBuf byteBuf = compressed;
            return byteBuf;
        }
        finally {
            if (release) {
                compressed.release();
            }
        }
    }

    private boolean compressInto(ByteBuf compressed) {
        byte[] out = compressed.array();
        int off = compressed.arrayOffset() + compressed.writerIndex();
        int toWrite = compressed.writableBytes();
        int numBytes = this.compressor.deflate(out, off, toWrite, 2);
        compressed.writerIndex(compressed.writerIndex() + numBytes);
        return numBytes == toWrite;
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
            int len = this.setInput(decompressed);
            ByteBuf byteBuf = this.encode(alloc, len);
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
        this.compressor.end();
        super.end();
    }
}

