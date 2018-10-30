/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCounted;

public class PongWebSocketFrame
extends WebSocketFrame {
    public PongWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public PongWebSocketFrame(ByteBuf binaryData) {
        super(binaryData);
    }

    public PongWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }

    @Override
    public PongWebSocketFrame copy() {
        return (PongWebSocketFrame)super.copy();
    }

    @Override
    public PongWebSocketFrame duplicate() {
        return (PongWebSocketFrame)super.duplicate();
    }

    @Override
    public PongWebSocketFrame retainedDuplicate() {
        return (PongWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public PongWebSocketFrame replace(ByteBuf content) {
        return new PongWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
    }

    @Override
    public PongWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public PongWebSocketFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public PongWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public PongWebSocketFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

