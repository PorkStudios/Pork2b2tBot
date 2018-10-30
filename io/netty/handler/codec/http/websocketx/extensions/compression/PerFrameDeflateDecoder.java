/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder;

class PerFrameDeflateDecoder
extends DeflateDecoder {
    public PerFrameDeflateDecoder(boolean noContext) {
        super(noContext);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame || msg instanceof ContinuationWebSocketFrame) && (((WebSocketFrame)msg).rsv() & 4) > 0;
    }

    @Override
    protected int newRsv(WebSocketFrame msg) {
        return msg.rsv() ^ 4;
    }

    @Override
    protected boolean appendFrameTail(WebSocketFrame msg) {
        return true;
    }
}

