/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

class WebSocketServerProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadSize;
    private final boolean allowMaskMismatch;
    private final boolean checkStartsWith;

    WebSocketServerProtocolHandshakeHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, false);
    }

    WebSocketServerProtocolHandshakeHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith) {
        this.websocketPath = websocketPath;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadSize = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
        this.checkStartsWith = checkStartsWith;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final FullHttpRequest req = (FullHttpRequest)msg;
        if (this.isNotWebSocketPath(req)) {
            ctx.fireChannelRead(msg);
            return;
        }
        try {
            if (req.method() != HttpMethod.GET) {
                WebSocketServerProtocolHandshakeHandler.sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
                return;
            }
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WebSocketServerProtocolHandshakeHandler.getWebSocketLocation(ctx.pipeline(), req, this.websocketPath), this.subprotocols, this.allowExtensions, this.maxFramePayloadSize, this.allowMaskMismatch);
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            ctx.fireUserEventTriggered((Object)WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                            ctx.fireUserEventTriggered(new WebSocketServerProtocolHandler.HandshakeComplete(req.uri(), req.headers(), handshaker.selectedSubprotocol()));
                        }
                    }
                });
                WebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handshaker);
                ctx.pipeline().replace(this, "WS403Responder", WebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
            }
        }
        finally {
            req.release();
        }
    }

    private boolean isNotWebSocketPath(FullHttpRequest req) {
        return this.checkStartsWith ? !req.uri().startsWith(this.websocketPath) : !req.uri().equals(this.websocketPath);
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }
        String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }

}

