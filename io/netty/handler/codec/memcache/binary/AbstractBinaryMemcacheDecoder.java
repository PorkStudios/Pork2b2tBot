/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.memcache.AbstractMemcacheObjectDecoder;
import io.netty.handler.codec.memcache.DefaultLastMemcacheContent;
import io.netty.handler.codec.memcache.DefaultMemcacheContent;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import java.util.List;

public abstract class AbstractBinaryMemcacheDecoder<M extends BinaryMemcacheMessage>
extends AbstractMemcacheObjectDecoder {
    public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
    private final int chunkSize;
    private M currentMessage;
    private int alreadyReadChunkSize;
    private State state = State.READ_HEADER;

    protected AbstractBinaryMemcacheDecoder() {
        this(8192);
    }

    protected AbstractBinaryMemcacheDecoder(int chunkSize) {
        if (chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize must be a positive integer: " + chunkSize);
        }
        this.chunkSize = chunkSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (this.state) {
            case READ_HEADER: {
                try {
                    if (in.readableBytes() < 24) {
                        return;
                    }
                    this.resetDecoder();
                    this.currentMessage = this.decodeHeader(in);
                    this.state = State.READ_EXTRAS;
                }
                catch (Exception e) {
                    this.resetDecoder();
                    out.add(this.invalidMessage(e));
                    return;
                }
            }
            case READ_EXTRAS: {
                try {
                    byte extrasLength = this.currentMessage.extrasLength();
                    if (extrasLength > 0) {
                        if (in.readableBytes() < extrasLength) {
                            return;
                        }
                        this.currentMessage.setExtras(in.readRetainedSlice(extrasLength));
                    }
                    this.state = State.READ_KEY;
                }
                catch (Exception e) {
                    this.resetDecoder();
                    out.add(this.invalidMessage(e));
                    return;
                }
            }
            case READ_KEY: {
                try {
                    short keyLength = this.currentMessage.keyLength();
                    if (keyLength > 0) {
                        if (in.readableBytes() < keyLength) {
                            return;
                        }
                        this.currentMessage.setKey(in.readRetainedSlice(keyLength));
                    }
                    out.add(this.currentMessage.retain());
                    this.state = State.READ_CONTENT;
                }
                catch (Exception e) {
                    this.resetDecoder();
                    out.add(this.invalidMessage(e));
                    return;
                }
            }
            case READ_CONTENT: {
                try {
                    int valueLength = this.currentMessage.totalBodyLength() - this.currentMessage.keyLength() - this.currentMessage.extrasLength();
                    int toRead = in.readableBytes();
                    if (valueLength > 0) {
                        int remainingLength;
                        if (toRead == 0) {
                            return;
                        }
                        if (toRead > this.chunkSize) {
                            toRead = this.chunkSize;
                        }
                        if (toRead > (remainingLength = valueLength - this.alreadyReadChunkSize)) {
                            toRead = remainingLength;
                        }
                        ByteBuf chunkBuffer = in.readRetainedSlice(toRead);
                        DefaultMemcacheContent chunk = (this.alreadyReadChunkSize += toRead) >= valueLength ? new DefaultLastMemcacheContent(chunkBuffer) : new DefaultMemcacheContent(chunkBuffer);
                        out.add(chunk);
                        if (this.alreadyReadChunkSize < valueLength) {
                            return;
                        }
                    } else {
                        out.add(LastMemcacheContent.EMPTY_LAST_CONTENT);
                    }
                    this.resetDecoder();
                    this.state = State.READ_HEADER;
                    return;
                }
                catch (Exception e) {
                    this.resetDecoder();
                    out.add(this.invalidChunk(e));
                    return;
                }
            }
            case BAD_MESSAGE: {
                in.skipBytes(this.actualReadableBytes());
                return;
            }
        }
        throw new Error("Unknown state reached: " + (Object)((Object)this.state));
    }

    private M invalidMessage(Exception cause) {
        this.state = State.BAD_MESSAGE;
        M message = this.buildInvalidMessage();
        message.setDecoderResult(DecoderResult.failure(cause));
        return message;
    }

    private MemcacheContent invalidChunk(Exception cause) {
        this.state = State.BAD_MESSAGE;
        DefaultLastMemcacheContent chunk = new DefaultLastMemcacheContent(Unpooled.EMPTY_BUFFER);
        chunk.setDecoderResult(DecoderResult.failure(cause));
        return chunk;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.resetDecoder();
    }

    protected void resetDecoder() {
        if (this.currentMessage != null) {
            this.currentMessage.release();
            this.currentMessage = null;
        }
        this.alreadyReadChunkSize = 0;
    }

    protected abstract M decodeHeader(ByteBuf var1);

    protected abstract M buildInvalidMessage();

    static enum State {
        READ_HEADER,
        READ_EXTRAS,
        READ_KEY,
        READ_CONTENT,
        BAD_MESSAGE;
        

        private State() {
        }
    }

}

