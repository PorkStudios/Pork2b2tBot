/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder;
import java.util.List;

class PerMessageDeflateDecoder
extends DeflateDecoder {
    private boolean compressing;

    public PerMessageDeflateDecoder(boolean noContext) {
        super(noContext);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) && (((WebSocketFrame)msg).rsv() & 4) > 0 || msg instanceof ContinuationWebSocketFrame && this.compressing;
    }

    @Override
    protected int newRsv(WebSocketFrame msg) {
        return (msg.rsv() & 4) > 0 ? msg.rsv() ^ 4 : msg.rsv();
    }

    @Override
    protected boolean appendFrameTail(WebSocketFrame msg) {
        return msg.isFinalFragment();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        super.decode(ctx, msg, out);
        if (msg.isFinalFragment()) {
            this.compressing = false;
        } else if (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) {
            this.compressing = true;
        }
    }
}

