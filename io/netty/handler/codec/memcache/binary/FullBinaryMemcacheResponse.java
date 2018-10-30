/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.FullMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponse;

public interface FullBinaryMemcacheResponse
extends BinaryMemcacheResponse,
FullMemcacheMessage {
    @Override
    public FullBinaryMemcacheResponse copy();

    @Override
    public FullBinaryMemcacheResponse duplicate();

    @Override
    public FullBinaryMemcacheResponse retainedDuplicate();

    @Override
    public FullBinaryMemcacheResponse replace(ByteBuf var1);

    @Override
    public FullBinaryMemcacheResponse retain(int var1);

    @Override
    public FullBinaryMemcacheResponse retain();

    @Override
    public FullBinaryMemcacheResponse touch();

    @Override
    public FullBinaryMemcacheResponse touch(Object var1);
}

