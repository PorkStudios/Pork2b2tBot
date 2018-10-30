/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.Utf8Validator;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class Utf8FrameValidator
extends ChannelInboundHandlerAdapter {
    private int fragmentedFramesCount;
    private Utf8Validator utf8Validator;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame)msg;
            if (((WebSocketFrame)msg).isFinalFragment()) {
                if (!(frame instanceof PingWebSocketFrame)) {
                    this.fragmentedFramesCount = 0;
                    if (frame instanceof TextWebSocketFrame || this.utf8Validator != null && this.utf8Validator.isChecking()) {
                        this.checkUTF8String(ctx, frame.content());
                        this.utf8Validator.finish();
                    }
                }
            } else {
                if (this.fragmentedFramesCount == 0) {
                    if (frame instanceof TextWebSocketFrame) {
                        this.checkUTF8String(ctx, frame.content());
                    }
                } else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                    this.checkUTF8String(ctx, frame.content());
                }
                ++this.fragmentedFramesCount;
            }
        }
        super.channelRead(ctx, msg);
    }

    private void checkUTF8String(ChannelHandlerContext ctx, ByteBuf buffer) {
        block3 : {
            try {
                if (this.utf8Validator == null) {
                    this.utf8Validator = new Utf8Validator();
                }
                this.utf8Validator.check(buffer);
            }
            catch (CorruptedFrameException ex) {
                if (!ctx.channel().isActive()) break block3;
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}

