/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockDecoder;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;

public class SpdyHeaderBlockRawDecoder
extends SpdyHeaderBlockDecoder {
    private static final int LENGTH_FIELD_SIZE = 4;
    private final int maxHeaderSize;
    private State state;
    private ByteBuf cumulation;
    private int headerSize;
    private int numHeaders;
    private int length;
    private String name;

    public SpdyHeaderBlockRawDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.maxHeaderSize = maxHeaderSize;
        this.state = State.READ_NUM_HEADERS;
    }

    private static int readLengthField(ByteBuf buffer) {
        int length = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex());
        buffer.skipBytes(4);
        return length;
    }

    @Override
    void decode(ByteBufAllocator alloc, ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
        if (headerBlock == null) {
            throw new NullPointerException("headerBlock");
        }
        if (frame == null) {
            throw new NullPointerException("frame");
        }
        if (this.cumulation == null) {
            this.decodeHeaderBlock(headerBlock, frame);
            if (headerBlock.isReadable()) {
                this.cumulation = alloc.buffer(headerBlock.readableBytes());
                this.cumulation.writeBytes(headerBlock);
            }
        } else {
            this.cumulation.writeBytes(headerBlock);
            this.decodeHeaderBlock(this.cumulation, frame);
            if (this.cumulation.isReadable()) {
                this.cumulation.discardReadBytes();
            } else {
                this.releaseBuffer();
            }
        }
    }

    protected void decodeHeaderBlock(ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
        block13 : while (headerBlock.isReadable()) {
            switch (this.state) {
                int skipLength;
                case READ_NUM_HEADERS: {
                    if (headerBlock.readableBytes() < 4) {
                        return;
                    }
                    this.numHeaders = SpdyHeaderBlockRawDecoder.readLengthField(headerBlock);
                    if (this.numHeaders < 0) {
                        this.state = State.ERROR;
                        frame.setInvalid();
                        continue block13;
                    }
                    if (this.numHeaders == 0) {
                        this.state = State.END_HEADER_BLOCK;
                        continue block13;
                    }
                    this.state = State.READ_NAME_LENGTH;
                    continue block13;
                }
                case READ_NAME_LENGTH: {
                    if (headerBlock.readableBytes() < 4) {
                        return;
                    }
                    this.length = SpdyHeaderBlockRawDecoder.readLengthField(headerBlock);
                    if (this.length <= 0) {
                        this.state = State.ERROR;
                        frame.setInvalid();
                        continue block13;
                    }
                    if (this.length > this.maxHeaderSize || this.headerSize > this.maxHeaderSize - this.length) {
                        this.headerSize = this.maxHeaderSize + 1;
                        this.state = State.SKIP_NAME;
                        frame.setTruncated();
                        continue block13;
                    }
                    this.headerSize += this.length;
                    this.state = State.READ_NAME;
                    continue block13;
                }
                case READ_NAME: {
                    if (headerBlock.readableBytes() < this.length) {
                        return;
                    }
                    byte[] nameBytes = new byte[this.length];
                    headerBlock.readBytes(nameBytes);
                    this.name = new String(nameBytes, "UTF-8");
                    if (frame.headers().contains(this.name)) {
                        this.state = State.ERROR;
                        frame.setInvalid();
                        continue block13;
                    }
                    this.state = State.READ_VALUE_LENGTH;
                    continue block13;
                }
                case SKIP_NAME: {
                    skipLength = Math.min(headerBlock.readableBytes(), this.length);
                    headerBlock.skipBytes(skipLength);
                    this.length -= skipLength;
                    if (this.length != 0) continue block13;
                    this.state = State.READ_VALUE_LENGTH;
                    continue block13;
                }
                case READ_VALUE_LENGTH: {
                    if (headerBlock.readableBytes() < 4) {
                        return;
                    }
                    this.length = SpdyHeaderBlockRawDecoder.readLengthField(headerBlock);
                    if (this.length < 0) {
                        this.state = State.ERROR;
                        frame.setInvalid();
                        continue block13;
                    }
                    if (this.length == 0) {
                        if (!frame.isTruncated()) {
                            frame.headers().add(this.name, "");
                        }
                        this.name = null;
                        if (--this.numHeaders == 0) {
                            this.state = State.END_HEADER_BLOCK;
                            continue block13;
                        }
                        this.state = State.READ_NAME_LENGTH;
                        continue block13;
                    }
                    if (this.length > this.maxHeaderSize || this.headerSize > this.maxHeaderSize - this.length) {
                        this.headerSize = this.maxHeaderSize + 1;
                        this.name = null;
                        this.state = State.SKIP_VALUE;
                        frame.setTruncated();
                        continue block13;
                    }
                    this.headerSize += this.length;
                    this.state = State.READ_VALUE;
                    continue block13;
                }
                case READ_VALUE: {
                    if (headerBlock.readableBytes() < this.length) {
                        return;
                    }
                    byte[] valueBytes = new byte[this.length];
                    headerBlock.readBytes(valueBytes);
                    int index = 0;
                    int offset = 0;
                    if (valueBytes[0] == 0) {
                        this.state = State.ERROR;
                        frame.setInvalid();
                        continue block13;
                    }
                    while (index < this.length) {
                        while (index < valueBytes.length && valueBytes[index] != 0) {
                            ++index;
                        }
                        if (index < valueBytes.length && (index + 1 == valueBytes.length || valueBytes[index + 1] == 0)) {
                            this.state = State.ERROR;
                            frame.setInvalid();
                            break;
                        }
                        String value = new String(valueBytes, offset, index - offset, "UTF-8");
                        try {
                            frame.headers().add(this.name, value);
                        }
                        catch (IllegalArgumentException e) {
                            this.state = State.ERROR;
                            frame.setInvalid();
                            break;
                        }
                        offset = ++index;
                    }
                    this.name = null;
                    if (this.state == State.ERROR) continue block13;
                    if (--this.numHeaders == 0) {
                        this.state = State.END_HEADER_BLOCK;
                        continue block13;
                    }
                    this.state = State.READ_NAME_LENGTH;
                    continue block13;
                }
                case SKIP_VALUE: {
                    skipLength = Math.min(headerBlock.readableBytes(), this.length);
                    headerBlock.skipBytes(skipLength);
                    this.length -= skipLength;
                    if (this.length != 0) continue block13;
                    if (--this.numHeaders == 0) {
                        this.state = State.END_HEADER_BLOCK;
                        continue block13;
                    }
                    this.state = State.READ_NAME_LENGTH;
                    continue block13;
                }
                case END_HEADER_BLOCK: {
                    this.state = State.ERROR;
                    frame.setInvalid();
                    continue block13;
                }
                case ERROR: {
                    headerBlock.skipBytes(headerBlock.readableBytes());
                    return;
                }
            }
            throw new Error("Shouldn't reach here.");
        }
    }

    @Override
    void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
        if (this.state != State.END_HEADER_BLOCK) {
            frame.setInvalid();
        }
        this.releaseBuffer();
        this.headerSize = 0;
        this.name = null;
        this.state = State.READ_NUM_HEADERS;
    }

    @Override
    void end() {
        this.releaseBuffer();
    }

    private void releaseBuffer() {
        if (this.cumulation != null) {
            this.cumulation.release();
            this.cumulation = null;
        }
    }

    private static enum State {
        READ_NUM_HEADERS,
        READ_NAME_LENGTH,
        READ_NAME,
        SKIP_NAME,
        READ_VALUE_LENGTH,
        READ_VALUE,
        SKIP_VALUE,
        END_HEADER_BLOCK,
        ERROR;
        

        private State() {
        }
    }

}

