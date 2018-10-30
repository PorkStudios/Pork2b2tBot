/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateEncoder;
import java.util.List;

class PerMessageDeflateEncoder
extends DeflateEncoder {
    private boolean compressing;

    public PerMessageDeflateEncoder(int compressionLevel, int windowSize, boolean noContext) {
        super(compressionLevel, windowSize, noContext);
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) && (((WebSocketFrame)msg).rsv() & 4) == 0 || msg instanceof ContinuationWebSocketFrame && this.compressing;
    }

    @Override
    protected int rsv(WebSocketFrame msg) {
        return msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame ? msg.rsv() | 4 : msg.rsv();
    }

    @Override
    protected boolean removeFrameTail(WebSocketFrame msg) {
        return msg.isFinalFragment();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
        if (msg.isFinalFragment()) {
            this.compressing = false;
        } else if (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) {
            this.compressing = true;
        }
    }
}

