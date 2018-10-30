/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocket00FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import io.netty.util.internal.logging.InternalLogger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketServerHandshaker00
extends WebSocketServerHandshaker {
    private static final Pattern BEGINNING_DIGIT = Pattern.compile("[^0-9]");
    private static final Pattern BEGINNING_SPACE = Pattern.compile("[^ ]");

    public WebSocketServerHandshaker00(String webSocketURL, String subprotocols, int maxFramePayloadLength) {
        super(WebSocketVersion.V00, webSocketURL, subprotocols, maxFramePayloadLength);
    }

    @Override
    protected FullHttpResponse newHandshakeResponse(FullHttpRequest req, HttpHeaders headers) {
        if (!req.headers().containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true) || !HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(req.headers().get(HttpHeaderNames.UPGRADE))) {
            throw new WebSocketHandshakeException("not a WebSocket handshake request: missing upgrade");
        }
        boolean isHixie76 = req.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_KEY1) && req.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_KEY2);
        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, isHixie76 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"));
        if (headers != null) {
            res.headers().add(headers);
        }
        res.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET);
        res.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
        if (isHixie76) {
            res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, (Object)req.headers().get(HttpHeaderNames.ORIGIN));
            res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_LOCATION, (Object)this.uri());
            String subprotocols = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
            if (subprotocols != null) {
                String selectedSubprotocol = this.selectSubprotocol(subprotocols);
                if (selectedSubprotocol == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Requested subprotocol(s) not supported: {}", (Object)subprotocols);
                    }
                } else {
                    res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)selectedSubprotocol);
                }
            }
            String key1 = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY1);
            String key2 = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY2);
            int a = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(key1).replaceAll("")) / (long)BEGINNING_SPACE.matcher(key1).replaceAll("").length());
            int b = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(key2).replaceAll("")) / (long)BEGINNING_SPACE.matcher(key2).replaceAll("").length());
            long c = req.content().readLong();
            ByteBuf input = Unpooled.buffer(16);
            input.writeInt(a);
            input.writeInt(b);
            input.writeLong(c);
            res.content().writeBytes(WebSocketUtil.md5(input.array()));
        } else {
            res.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_ORIGIN, (Object)req.headers().get(HttpHeaderNames.ORIGIN));
            res.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_LOCATION, (Object)this.uri());
            String protocol = req.headers().get(HttpHeaderNames.WEBSOCKET_PROTOCOL);
            if (protocol != null) {
                res.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_PROTOCOL, (Object)this.selectSubprotocol(protocol));
            }
        }
        return res;
    }

    @Override
    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
        return channel.writeAndFlush(frame, promise);
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }
}

