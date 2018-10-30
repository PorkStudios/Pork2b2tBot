/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.udt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.ReferenceCounted;

@Deprecated
public final class UdtMessage
extends DefaultByteBufHolder {
    public UdtMessage(ByteBuf data) {
        super(data);
    }

    @Override
    public UdtMessage copy() {
        return (UdtMessage)super.copy();
    }

    @Override
    public UdtMessage duplicate() {
        return (UdtMessage)super.duplicate();
    }

    @Override
    public UdtMessage retainedDuplicate() {
        return (UdtMessage)super.retainedDuplicate();
    }

    @Override
    public UdtMessage replace(ByteBuf content) {
        return new UdtMessage(content);
    }

    @Override
    public UdtMessage retain() {
        super.retain();
        return this;
    }

    @Override
    public UdtMessage retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public UdtMessage touch() {
        super.touch();
        return this;
    }

    @Override
    public UdtMessage touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

