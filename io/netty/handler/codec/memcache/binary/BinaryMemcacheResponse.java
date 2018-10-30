/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;

public interface BinaryMemcacheResponse
extends BinaryMemcacheMessage {
    public short status();

    public BinaryMemcacheResponse setStatus(short var1);

    @Override
    public BinaryMemcacheResponse retain();

    @Override
    public BinaryMemcacheResponse retain(int var1);

    @Override
    public BinaryMemcacheResponse touch();

    @Override
    public BinaryMemcacheResponse touch(Object var1);
}

