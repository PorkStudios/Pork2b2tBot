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
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

class WebSocketClientProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private final WebSocketClientHandshaker handshaker;

    WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.handshaker.handshake(ctx.channel()).addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    ctx.fireExceptionCaught(future.cause());
                } else {
                    ctx.fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        FullHttpResponse response = (FullHttpResponse)msg;
        try {
            if (!this.handshaker.isHandshakeComplete()) {
                this.handshaker.finishHandshake(ctx.channel(), response);
                ctx.fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
                ctx.pipeline().remove(this);
                return;
            }
            throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
        }
        finally {
            response.release();
        }
    }

}

