/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker00;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker07;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker08;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;

public class WebSocketServerHandshakerFactory {
    private final String webSocketURL;
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadLength;
    private final boolean allowMaskMismatch;

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions) {
        this(webSocketURL, subprotocols, allowExtensions, 65536);
    }

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
        this(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength, false);
    }

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        this.webSocketURL = webSocketURL;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadLength = maxFramePayloadLength;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    public WebSocketServerHandshaker newHandshaker(HttpRequest req) {
        String version = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
        if (version != null) {
            if (version.equals(WebSocketVersion.V13.toHttpHeaderValue())) {
                return new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch);
            }
            if (version.equals(WebSocketVersion.V08.toHttpHeaderValue())) {
                return new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch);
            }
            if (version.equals(WebSocketVersion.V07.toHttpHeaderValue())) {
                return new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch);
            }
            return null;
        }
        return new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.maxFramePayloadLength);
    }

    @Deprecated
    public static void sendUnsupportedWebSocketVersionResponse(Channel channel) {
        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
    }

    public static ChannelFuture sendUnsupportedVersionResponse(Channel channel) {
        return WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel, channel.newPromise());
    }

    public static ChannelFuture sendUnsupportedVersionResponse(Channel channel, ChannelPromise promise) {
        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED);
        res.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, (Object)WebSocketVersion.V13.toHttpHeaderValue());
        HttpUtil.setContentLength(res, 0L);
        return channel.writeAndFlush(res, promise);
    }
}

