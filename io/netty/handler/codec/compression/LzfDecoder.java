/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  com.ning.compress.BufferRecycler
 *  com.ning.compress.lzf.ChunkDecoder
 *  com.ning.compress.lzf.util.ChunkDecoderFactory
 */
package io.netty.handler.codec.compression;

import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkDecoder;
import com.ning.compress.lzf.util.ChunkDecoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.DecompressionException;
import java.util.List;

public class LzfDecoder
extends ByteToMessageDecoder {
    private State currentState = State.INIT_BLOCK;
    private static final short MAGIC_NUMBER = 23126;
    private ChunkDecoder decoder;
    private BufferRecycler recycler;
    private int chunkLength;
    private int originalLength;
    private boolean isCompressed;

    public LzfDecoder() {
        this(false);
    }

    public LzfDecoder(boolean safeInstance) {
        this.decoder = safeInstance ? ChunkDecoderFactory.safeInstance() : ChunkDecoderFactory.optimalInstance();
        this.recycler = BufferRecycler.instance();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            switch (.$SwitchMap$io$netty$handler$codec$compression$LzfDecoder$State[this.currentState.ordinal()]) {
                case 1: {
                    if (in.readableBytes() < 5) {
                        return;
                    }
                    magic = in.readUnsignedShort();
                    if (magic != 23126) {
                        throw new DecompressionException("unexpected block identifier");
                    }
                    type = in.readByte();
                    switch (type) {
                        case 0: {
                            this.isCompressed = false;
                            this.currentState = State.DECOMPRESS_DATA;
                            ** break;
                        }
                        case 1: {
                            this.isCompressed = true;
                            this.currentState = State.INIT_ORIGINAL_LENGTH;
                            ** break;
                        }
                    }
                    throw new DecompressionException(String.format("unknown type of chunk: %d (expected: %d or %d)", new Object[]{type, 0, 1}));
lbl20: // 2 sources:
                    this.chunkLength = in.readUnsignedShort();
                    if (type != 1) {
                        return;
                    }
                }
                case 2: {
                    if (in.readableBytes() < 2) {
                        return;
                    }
                    this.originalLength = in.readUnsignedShort();
                    this.currentState = State.DECOMPRESS_DATA;
                }
                case 3: {
                    chunkLength = this.chunkLength;
                    if (in.readableBytes() < chunkLength) {
                        return;
                    }
                    originalLength = this.originalLength;
                    if (!this.isCompressed) ** GOTO lbl60
                    idx = in.readerIndex();
                    if (in.hasArray()) {
                        inputArray = in.array();
                        inPos = in.arrayOffset() + idx;
                    } else {
                        inputArray = this.recycler.allocInputBuffer(chunkLength);
                        in.getBytes(idx, inputArray, 0, chunkLength);
                        inPos = 0;
                    }
                    uncompressed = ctx.alloc().heapBuffer(originalLength, originalLength);
                    outputArray = uncompressed.array();
                    outPos = uncompressed.arrayOffset() + uncompressed.writerIndex();
                    success = false;
                    try {
                        this.decoder.decodeChunk(inputArray, inPos, outputArray, outPos, outPos + originalLength);
                        uncompressed.writerIndex(uncompressed.writerIndex() + originalLength);
                        out.add(uncompressed);
                        in.skipBytes(chunkLength);
                        success = true;
                    }
                    finally {
                        if (!success) {
                            uncompressed.release();
                        }
                    }
                    if (!in.hasArray()) {
                        this.recycler.releaseInputBuffer(inputArray);
                    }
                    ** GOTO lbl62
lbl60: // 1 sources:
                    if (chunkLength > 0) {
                        out.add(in.readRetainedSlice(chunkLength));
                    }
lbl62: // 4 sources:
                    this.currentState = State.INIT_BLOCK;
                    return;
                }
                case 4: {
                    in.skipBytes(in.readableBytes());
                    return;
                }
            }
            throw new IllegalStateException();
        }
        catch (Exception e) {
            this.currentState = State.CORRUPTED;
            this.decoder = null;
            this.recycler = null;
            throw e;
        }
    }

    private static enum State {
        INIT_BLOCK,
        INIT_ORIGINAL_LENGTH,
        DECOMPRESS_DATA,
        CORRUPTED;
        

        private State() {
        }
    }

}

