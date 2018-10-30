/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.spdy.SpdyHeaderBlockEncoder;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import java.util.List;
import java.util.Set;

public class SpdyHeaderBlockRawEncoder
extends SpdyHeaderBlockEncoder {
    private final int version;

    public SpdyHeaderBlockRawEncoder(SpdyVersion version) {
        if (version == null) {
            throw new NullPointerException("version");
        }
        this.version = version.getVersion();
    }

    private static void setLengthField(ByteBuf buffer, int writerIndex, int length) {
        buffer.setInt(writerIndex, length);
    }

    private static void writeLengthField(ByteBuf buffer, int length) {
        buffer.writeInt(length);
    }

    @Override
    public ByteBuf encode(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
        Set names = frame.headers().names();
        int numHeaders = names.size();
        if (numHeaders == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        if (numHeaders > 65535) {
            throw new IllegalArgumentException("header block contains too many headers");
        }
        ByteBuf headerBlock = alloc.heapBuffer();
        SpdyHeaderBlockRawEncoder.writeLengthField(headerBlock, numHeaders);
        for (CharSequence name : names) {
            SpdyHeaderBlockRawEncoder.writeLengthField(headerBlock, name.length());
            ByteBufUtil.writeAscii(headerBlock, name);
            int savedIndex = headerBlock.writerIndex();
            int valueLength = 0;
            SpdyHeaderBlockRawEncoder.writeLengthField(headerBlock, valueLength);
            for (CharSequence value : frame.headers().getAll(name)) {
                int length = value.length();
                if (length <= 0) continue;
                ByteBufUtil.writeAscii(headerBlock, value);
                headerBlock.writeByte(0);
                valueLength += length + 1;
            }
            if (valueLength != 0) {
                --valueLength;
            }
            if (valueLength > 65535) {
                throw new IllegalArgumentException("header exceeds allowable length: " + name);
            }
            if (valueLength <= 0) continue;
            SpdyHeaderBlockRawEncoder.setLengthField(headerBlock, savedIndex, valueLength);
            headerBlock.writerIndex(headerBlock.writerIndex() - 1);
        }
        return headerBlock;
    }

    @Override
    void end() {
    }
}

