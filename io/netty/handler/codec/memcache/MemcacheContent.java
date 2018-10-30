/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.memcache.MemcacheObject;

public interface MemcacheContent
extends MemcacheObject,
ByteBufHolder {
    @Override
    public MemcacheContent copy();

    @Override
    public MemcacheContent duplicate();

    @Override
    public MemcacheContent retainedDuplicate();

    @Override
    public MemcacheContent replace(ByteBuf var1);

    @Override
    public MemcacheContent retain();

    @Override
    public MemcacheContent retain(int var1);

    @Override
    public MemcacheContent touch();

    @Override
    public MemcacheContent touch(Object var1);
}

