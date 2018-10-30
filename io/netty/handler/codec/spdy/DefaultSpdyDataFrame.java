/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyDataFrame
extends DefaultSpdyStreamFrame
implements SpdyDataFrame {
    private final ByteBuf data;

    public DefaultSpdyDataFrame(int streamId) {
        this(streamId, Unpooled.buffer(0));
    }

    public DefaultSpdyDataFrame(int streamId, ByteBuf data) {
        super(streamId);
        if (data == null) {
            throw new NullPointerException("data");
        }
        this.data = DefaultSpdyDataFrame.validate(data);
    }

    private static ByteBuf validate(ByteBuf data) {
        if (data.readableBytes() > 16777215) {
            throw new IllegalArgumentException("data payload cannot exceed 16777215 bytes");
        }
        return data;
    }

    @Override
    public SpdyDataFrame setStreamId(int streamId) {
        super.setStreamId(streamId);
        return this;
    }

    @Override
    public SpdyDataFrame setLast(boolean last) {
        super.setLast(last);
        return this;
    }

    @Override
    public ByteBuf content() {
        if (this.data.refCnt() <= 0) {
            throw new IllegalReferenceCountException(this.data.refCnt());
        }
        return this.data;
    }

    @Override
    public SpdyDataFrame copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public SpdyDataFrame duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public SpdyDataFrame retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public SpdyDataFrame replace(ByteBuf content) {
        DefaultSpdyDataFrame frame = new DefaultSpdyDataFrame(this.streamId(), content);
        frame.setLast(this.isLast());
        return frame;
    }

    @Override
    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public SpdyDataFrame retain() {
        this.data.retain();
        return this;
    }

    @Override
    public SpdyDataFrame retain(int increment) {
        this.data.retain(increment);
        return this;
    }

    @Override
    public SpdyDataFrame touch() {
        this.data.touch();
        return this;
    }

    @Override
    public SpdyDataFrame touch(Object hint) {
        this.data.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.data.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.data.release(decrement);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append("(last: ").append(this.isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(this.streamId()).append(StringUtil.NEWLINE).append("--> Size = ");
        if (this.refCnt() == 0) {
            buf.append("(freed)");
        } else {
            buf.append(this.content().readableBytes());
        }
        return buf.toString();
    }
}

