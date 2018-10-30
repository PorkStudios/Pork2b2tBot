/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Snappy;
import java.util.List;

public class SnappyFrameDecoder
extends ByteToMessageDecoder {
    private static final int SNAPPY_IDENTIFIER_LEN = 6;
    private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
    private final Snappy snappy = new Snappy();
    private final boolean validateChecksums;
    private boolean started;
    private boolean corrupted;

    public SnappyFrameDecoder() {
        this(false);
    }

    public SnappyFrameDecoder(boolean validateChecksums) {
        this.validateChecksums = validateChecksums;
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
        if (this.corrupted) {
            in.skipBytes(in.readableBytes());
            return;
        }
        try {
            idx = in.readerIndex();
            inSize = in.readableBytes();
            if (inSize < 4) {
                return;
            }
            chunkTypeVal = in.getUnsignedByte(idx);
            chunkType = SnappyFrameDecoder.mapChunkType((byte)chunkTypeVal);
            chunkLength = in.getUnsignedMediumLE(idx + 1);
            switch (.$SwitchMap$io$netty$handler$codec$compression$SnappyFrameDecoder$ChunkType[chunkType.ordinal()]) {
                case 1: {
                    if (chunkLength != 6) {
                        throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength);
                    }
                    if (inSize < 10) {
                        return;
                    }
                    in.skipBytes(4);
                    offset = in.readerIndex();
                    in.skipBytes(6);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)115);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)78);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)97);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)80);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)112);
                    SnappyFrameDecoder.checkByte(in.getByte(offset), (byte)89);
                    this.started = true;
                    return;
                }
                case 2: {
                    if (!this.started) {
                        throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4 + chunkLength);
                    return;
                }
                case 3: {
                    throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(chunkTypeVal));
                }
                case 4: {
                    if (!this.started) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (chunkLength > 65540) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    if (this.validateChecksums) {
                        checksum = in.readIntLE();
                        Snappy.validateChecksum(checksum, in, in.readerIndex(), chunkLength - 4);
                    } else {
                        in.skipBytes(4);
                    }
                    out.add(in.readRetainedSlice(chunkLength - 4));
                    return;
                }
                case 5: {
                    if (!this.started) {
                        throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    checksum = in.readIntLE();
                    uncompressed = ctx.alloc().buffer();
                    try {
                        if (!this.validateChecksums) ** GOTO lbl73
                        oldWriterIndex = in.writerIndex();
                        try {
                            in.writerIndex(in.readerIndex() + chunkLength - 4);
                            this.snappy.decode(in, uncompressed);
                        }
                        finally {
                            in.writerIndex(oldWriterIndex);
                        }
                        Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
                        ** GOTO lbl74
lbl73: // 1 sources:
                        this.snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
lbl74: // 2 sources:
                        out.add(uncompressed);
                        uncompressed = null;
                    }
                    finally {
                        if (uncompressed != null) {
                            uncompressed.release();
                        }
                    }
                    this.snappy.reset();
                }
            }
            return;
        }
        catch (Exception e) {
            this.corrupted = true;
            throw e;
        }
    }

    private static void checkByte(byte actual, byte expect) {
        if (actual != expect) {
            throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
        }
    }

    private static ChunkType mapChunkType(byte type) {
        if (type == 0) {
            return ChunkType.COMPRESSED_DATA;
        }
        if (type == 1) {
            return ChunkType.UNCOMPRESSED_DATA;
        }
        if (type == -1) {
            return ChunkType.STREAM_IDENTIFIER;
        }
        if ((type & 128) == 128) {
            return ChunkType.RESERVED_SKIPPABLE;
        }
        return ChunkType.RESERVED_UNSKIPPABLE;
    }

    private static enum ChunkType {
        STREAM_IDENTIFIER,
        COMPRESSED_DATA,
        UNCOMPRESSED_DATA,
        RESERVED_UNSKIPPABLE,
        RESERVED_SKIPPABLE;
        

        private ChunkType() {
        }
    }

}

