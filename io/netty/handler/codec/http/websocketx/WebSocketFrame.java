/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public abstract class WebSocketFrame
extends DefaultByteBufHolder {
    private final boolean finalFragment;
    private final int rsv;

    protected WebSocketFrame(ByteBuf binaryData) {
        this(true, 0, binaryData);
    }

    protected WebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(binaryData);
        this.finalFragment = finalFragment;
        this.rsv = rsv;
    }

    public boolean isFinalFragment() {
        return this.finalFragment;
    }

    public int rsv() {
        return this.rsv;
    }

    @Override
    public WebSocketFrame copy() {
        return (WebSocketFrame)super.copy();
    }

    @Override
    public WebSocketFrame duplicate() {
        return (WebSocketFrame)super.duplicate();
    }

    @Override
    public WebSocketFrame retainedDuplicate() {
        return (WebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public abstract WebSocketFrame replace(ByteBuf var1);

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(data: " + this.contentToString() + ')';
    }

    @Override
    public WebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public WebSocketFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public WebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public WebSocketFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

