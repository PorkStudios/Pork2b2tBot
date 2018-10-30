/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheMessage;

public interface FullMemcacheMessage
extends MemcacheMessage,
LastMemcacheContent {
    @Override
    public FullMemcacheMessage copy();

    @Override
    public FullMemcacheMessage duplicate();

    @Override
    public FullMemcacheMessage retainedDuplicate();

    @Override
    public FullMemcacheMessage replace(ByteBuf var1);

    @Override
    public FullMemcacheMessage retain(int var1);

    @Override
    public FullMemcacheMessage retain();

    @Override
    public FullMemcacheMessage touch();

    @Override
    public FullMemcacheMessage touch(Object var1);
}

