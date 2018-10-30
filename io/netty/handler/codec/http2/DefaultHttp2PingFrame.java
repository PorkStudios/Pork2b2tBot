/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.http2.Http2PingFrame;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultHttp2PingFrame
extends DefaultByteBufHolder
implements Http2PingFrame {
    private final boolean ack;

    public DefaultHttp2PingFrame(ByteBuf content) {
        this(content, false);
    }

    DefaultHttp2PingFrame(ByteBuf content, boolean ack) {
        super(DefaultHttp2PingFrame.mustBeEightBytes(content));
        this.ack = ack;
    }

    @Override
    public boolean ack() {
        return this.ack;
    }

    @Override
    public String name() {
        return "PING";
    }

    @Override
    public DefaultHttp2PingFrame copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public DefaultHttp2PingFrame duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public DefaultHttp2PingFrame retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public DefaultHttp2PingFrame replace(ByteBuf content) {
        return new DefaultHttp2PingFrame(content, this.ack);
    }

    @Override
    public DefaultHttp2PingFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public DefaultHttp2PingFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public DefaultHttp2PingFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public DefaultHttp2PingFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Http2PingFrame)) {
            return false;
        }
        Http2PingFrame other = (Http2PingFrame)o;
        return super.equals(o) && this.ack == other.ack();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + (this.ack ? 1 : 0);
        return hash;
    }

    private static ByteBuf mustBeEightBytes(ByteBuf content) {
        if (content.readableBytes() != 8) {
            throw new IllegalArgumentException("PING frames require 8 bytes of content. Was " + content.readableBytes() + " bytes.");
        }
        return content;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(content=" + this.contentToString() + ", ack=" + this.ack + ')';
    }
}

