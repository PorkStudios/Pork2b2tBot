/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheDecoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequest;
import io.netty.handler.codec.memcache.binary.DefaultBinaryMemcacheRequest;

public class BinaryMemcacheRequestDecoder
extends AbstractBinaryMemcacheDecoder<BinaryMemcacheRequest> {
    public BinaryMemcacheRequestDecoder() {
        this(8192);
    }

    public BinaryMemcacheRequestDecoder(int chunkSize) {
        super(chunkSize);
    }

    @Override
    protected BinaryMemcacheRequest decodeHeader(ByteBuf in) {
        DefaultBinaryMemcacheRequest header = new DefaultBinaryMemcacheRequest();
        header.setMagic(in.readByte());
        header.setOpcode(in.readByte());
        header.setKeyLength(in.readShort());
        header.setExtrasLength(in.readByte());
        header.setDataType(in.readByte());
        header.setReserved(in.readShort());
        header.setTotalBodyLength(in.readInt());
        header.setOpaque(in.readInt());
        header.setCas(in.readLong());
        return header;
    }

    @Override
    protected BinaryMemcacheRequest buildInvalidMessage() {
        return new DefaultBinaryMemcacheRequest(Unpooled.EMPTY_BUFFER, Unpooled.EMPTY_BUFFER);
    }
}

