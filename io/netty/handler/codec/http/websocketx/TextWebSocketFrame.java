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

public class TextWebSocketFrame
extends WebSocketFrame {
    public TextWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public TextWebSocketFrame(String text) {
        super(TextWebSocketFrame.fromText(text));
    }

    public TextWebSocketFrame(ByteBuf binaryData) {
        super(binaryData);
    }

    public TextWebSocketFrame(boolean finalFragment, int rsv, String text) {
        super(finalFragment, rsv, TextWebSocketFrame.fromText(text));
    }

    private static ByteBuf fromText(String text) {
        if (text == null || text.isEmpty()) {
            return Unpooled.EMPTY_BUFFER;
        }
        return Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
    }

    public TextWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }

    public String text() {
        return this.content().toString(CharsetUtil.UTF_8);
    }

    @Override
    public TextWebSocketFrame copy() {
        return (TextWebSocketFrame)super.copy();
    }

    @Override
    public TextWebSocketFrame duplicate() {
        return (TextWebSocketFrame)super.duplicate();
    }

    @Override
    public TextWebSocketFrame retainedDuplicate() {
        return (TextWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public TextWebSocketFrame replace(ByteBuf content) {
        return new TextWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
    }

    @Override
    public TextWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public TextWebSocketFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public TextWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public TextWebSocketFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

