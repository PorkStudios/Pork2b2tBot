/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCounted;
import java.nio.charset.Charset;

public class ContinuationWebSocketFrame
extends WebSocketFrame {
    public ContinuationWebSocketFrame() {
        this(Unpooled.buffer(0));
    }

    public ContinuationWebSocketFrame(ByteBuf binaryData) {
        super(binaryData);
    }

    public ContinuationWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }

    public ContinuationWebSocketFrame(boolean finalFragment, int rsv, String text) {
        this(finalFragment, rsv, ContinuationWebSocketFrame.fromText(text));
    }

    public String text() {
        return this.content().toString(CharsetUtil.UTF_8);
    }

    private static ByteBuf fromText(String text) {
        if (text == null || text.isEmpty()) {
            return Unpooled.EMPTY_BUFFER;
        }
        return Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
    }

    @Override
    public ContinuationWebSocketFrame copy() {
        return (ContinuationWebSocketFrame)super.copy();
    }

    @Override
    public ContinuationWebSocketFrame duplicate() {
        return (ContinuationWebSocketFrame)super.duplicate();
    }

    @Override
    public ContinuationWebSocketFrame retainedDuplicate() {
        return (ContinuationWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public ContinuationWebSocketFrame replace(ByteBuf content) {
        return new ContinuationWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
    }

    @Override
    public ContinuationWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public ContinuationWebSocketFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public ContinuationWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public ContinuationWebSocketFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

