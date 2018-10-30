/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;

public class ChunkedNioFile
implements ChunkedInput<ByteBuf> {
    private final FileChannel in;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;

    public ChunkedNioFile(File in) throws IOException {
        this(new FileInputStream(in).getChannel());
    }

    public ChunkedNioFile(File in, int chunkSize) throws IOException {
        this(new FileInputStream(in).getChannel(), chunkSize);
    }

    public ChunkedNioFile(FileChannel in) throws IOException {
        this(in, 8192);
    }

    public ChunkedNioFile(FileChannel in, int chunkSize) throws IOException {
        this(in, 0L, in.size(), chunkSize);
    }

    public ChunkedNioFile(FileChannel in, long offset, long length, int chunkSize) throws IOException {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (offset < 0L) {
            throw new IllegalArgumentException("offset: " + offset + " (expected: 0 or greater)");
        }
        if (length < 0L) {
            throw new IllegalArgumentException("length: " + length + " (expected: 0 or greater)");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        }
        if (offset != 0L) {
            in.position(offset);
        }
        this.in = in;
        this.chunkSize = chunkSize;
        this.offset = this.startOffset = offset;
        this.endOffset = offset + length;
    }

    public long startOffset() {
        return this.startOffset;
    }

    public long endOffset() {
        return this.endOffset;
    }

    public long currentOffset() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return this.offset >= this.endOffset || !this.in.isOpen();
    }

    @Override
    public void close() throws Exception {
        this.in.close();
    }

    @Deprecated
    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        return this.readChunk(ctx.alloc());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
        long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        int chunkSize = (int)Math.min((long)this.chunkSize, this.endOffset - offset);
        ByteBuf buffer = allocator.buffer(chunkSize);
        boolean release = true;
        try {
            int localReadBytes22;
            int readBytes = 0;
            while ((localReadBytes22 = buffer.writeBytes(this.in, chunkSize - readBytes)) >= 0 && (readBytes += localReadBytes22) != chunkSize) {
            }
            this.offset += (long)readBytes;
            release = false;
            ByteBuf localReadBytes22 = buffer;
            return localReadBytes22;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }

    @Override
    public long length() {
        return this.endOffset - this.startOffset;
    }

    @Override
    public long progress() {
        return this.offset - this.startOffset;
    }
}

