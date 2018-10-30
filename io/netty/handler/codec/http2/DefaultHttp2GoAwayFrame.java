/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp2GoAwayFrame
extends DefaultByteBufHolder
implements Http2GoAwayFrame {
    private final long errorCode;
    private final int lastStreamId;
    private int extraStreamIds;

    public DefaultHttp2GoAwayFrame(Http2Error error) {
        this(error.code());
    }

    public DefaultHttp2GoAwayFrame(long errorCode) {
        this(errorCode, Unpooled.EMPTY_BUFFER);
    }

    public DefaultHttp2GoAwayFrame(Http2Error error, ByteBuf content) {
        this(error.code(), content);
    }

    public DefaultHttp2GoAwayFrame(long errorCode, ByteBuf content) {
        this(-1, errorCode, content);
    }

    DefaultHttp2GoAwayFrame(int lastStreamId, long errorCode, ByteBuf content) {
        super(content);
        this.errorCode = errorCode;
        this.lastStreamId = lastStreamId;
    }

    @Override
    public String name() {
        return "GOAWAY";
    }

    @Override
    public long errorCode() {
        return this.errorCode;
    }

    @Override
    public int extraStreamIds() {
        return this.extraStreamIds;
    }

    @Override
    public Http2GoAwayFrame setExtraStreamIds(int extraStreamIds) {
        if (extraStreamIds < 0) {
            throw new IllegalArgumentException("extraStreamIds must be non-negative");
        }
        this.extraStreamIds = extraStreamIds;
        return this;
    }

    @Override
    public int lastStreamId() {
        return this.lastStreamId;
    }

    @Override
    public Http2GoAwayFrame copy() {
        return new DefaultHttp2GoAwayFrame(this.lastStreamId, this.errorCode, this.content().copy());
    }

    @Override
    public Http2GoAwayFrame duplicate() {
        return (Http2GoAwayFrame)super.duplicate();
    }

    @Override
    public Http2GoAwayFrame retainedDuplicate() {
        return (Http2GoAwayFrame)super.retainedDuplicate();
    }

    @Override
    public Http2GoAwayFrame replace(ByteBuf content) {
        return new DefaultHttp2GoAwayFrame(this.errorCode, content).setExtraStreamIds(this.extraStreamIds);
    }

    @Override
    public Http2GoAwayFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public Http2GoAwayFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public Http2GoAwayFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public Http2GoAwayFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttp2GoAwayFrame)) {
            return false;
        }
        DefaultHttp2GoAwayFrame other = (DefaultHttp2GoAwayFrame)o;
        return this.errorCode == other.errorCode && this.extraStreamIds == other.extraStreamIds && super.equals(other);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + (int)(this.errorCode ^ this.errorCode >>> 32);
        hash = hash * 31 + this.extraStreamIds;
        return hash;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(errorCode=" + this.errorCode + ", content=" + this.content() + ", extraStreamIds=" + this.extraStreamIds + ", lastStreamId=" + this.lastStreamId + ')';
    }
}

