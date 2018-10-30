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

public class CloseWebSocketFrame
extends WebSocketFrame {
    public CloseWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public CloseWebSocketFrame(int statusCode, String reasonText) {
        this(true, 0, statusCode, reasonText);
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv) {
        this(finalFragment, rsv, Unpooled.buffer(0));
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv, int statusCode, String reasonText) {
        super(finalFragment, rsv, CloseWebSocketFrame.newBinaryData(statusCode, reasonText));
    }

    private static ByteBuf newBinaryData(int statusCode, String reasonText) {
        if (reasonText == null) {
            reasonText = "";
        }
        ByteBuf binaryData = Unpooled.buffer(2 + reasonText.length());
        binaryData.writeShort(statusCode);
        if (!reasonText.isEmpty()) {
            binaryData.writeCharSequence(reasonText, CharsetUtil.UTF_8);
        }
        binaryData.readerIndex(0);
        return binaryData;
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }

    public int statusCode() {
        ByteBuf binaryData = this.content();
        if (binaryData == null || binaryData.capacity() == 0) {
            return -1;
        }
        binaryData.readerIndex(0);
        short statusCode = binaryData.readShort();
        binaryData.readerIndex(0);
        return statusCode;
    }

    public String reasonText() {
        ByteBuf binaryData = this.content();
        if (binaryData == null || binaryData.capacity() <= 2) {
            return "";
        }
        binaryData.readerIndex(2);
        String reasonText = binaryData.toString(CharsetUtil.UTF_8);
        binaryData.readerIndex(0);
        return reasonText;
    }

    @Override
    public CloseWebSocketFrame copy() {
        return (CloseWebSocketFrame)super.copy();
    }

    @Override
    public CloseWebSocketFrame duplicate() {
        return (CloseWebSocketFrame)super.duplicate();
    }

    @Override
    public CloseWebSocketFrame retainedDuplicate() {
        return (CloseWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public CloseWebSocketFrame replace(ByteBuf content) {
        return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
    }

    @Override
    public CloseWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public CloseWebSocketFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public CloseWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public CloseWebSocketFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

