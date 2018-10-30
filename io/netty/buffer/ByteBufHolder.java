/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;

public interface ByteBufHolder
extends ReferenceCounted {
    public ByteBuf content();

    public ByteBufHolder copy();

    public ByteBufHolder duplicate();

    public ByteBufHolder retainedDuplicate();

    public ByteBufHolder replace(ByteBuf var1);

    @Override
    public ByteBufHolder retain();

    @Override
    public ByteBufHolder retain(int var1);

    @Override
    public ByteBufHolder touch();

    @Override
    public ByteBufHolder touch(Object var1);
}

