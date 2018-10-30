/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.MemcacheMessage;

public interface BinaryMemcacheMessage
extends MemcacheMessage {
    public byte magic();

    public BinaryMemcacheMessage setMagic(byte var1);

    public byte opcode();

    public BinaryMemcacheMessage setOpcode(byte var1);

    public short keyLength();

    public byte extrasLength();

    public byte dataType();

    public BinaryMemcacheMessage setDataType(byte var1);

    public int totalBodyLength();

    public BinaryMemcacheMessage setTotalBodyLength(int var1);

    public int opaque();

    public BinaryMemcacheMessage setOpaque(int var1);

    public long cas();

    public BinaryMemcacheMessage setCas(long var1);

    public ByteBuf key();

    public BinaryMemcacheMessage setKey(ByteBuf var1);

    public ByteBuf extras();

    public BinaryMemcacheMessage setExtras(ByteBuf var1);

    @Override
    public BinaryMemcacheMessage retain();

    @Override
    public BinaryMemcacheMessage retain(int var1);

    @Override
    public BinaryMemcacheMessage touch();

    @Override
    public BinaryMemcacheMessage touch(Object var1);
}

