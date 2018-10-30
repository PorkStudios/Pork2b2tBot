/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.stomp.LastStompContentSubframe;
import io.netty.handler.codec.stomp.StompHeadersSubframe;

public interface StompFrame
extends StompHeadersSubframe,
LastStompContentSubframe {
    @Override
    public StompFrame copy();

    @Override
    public StompFrame duplicate();

    @Override
    public StompFrame retainedDuplicate();

    @Override
    public StompFrame replace(ByteBuf var1);

    @Override
    public StompFrame retain();

    @Override
    public StompFrame retain(int var1);

    @Override
    public StompFrame touch();

    @Override
    public StompFrame touch(Object var1);
}

