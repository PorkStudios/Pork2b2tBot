/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheEncoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponse;

public class BinaryMemcacheResponseEncoder
extends AbstractBinaryMemcacheEncoder<BinaryMemcacheResponse> {
    @Override
    protected void encodeHeader(ByteBuf buf, BinaryMemcacheResponse msg) {
        buf.writeByte(msg.magic());
        buf.writeByte(msg.opcode());
        buf.writeShort(msg.keyLength());
        buf.writeByte(msg.extrasLength());
        buf.writeByte(msg.dataType());
        buf.writeShort(msg.status());
        buf.writeInt(msg.totalBodyLength());
        buf.writeInt(msg.opaque());
        buf.writeLong(msg.cas());
    }
}

