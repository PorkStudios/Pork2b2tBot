/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2FrameStreamVisitor;
import io.netty.util.internal.StringUtil;

public abstract class Http2ChannelDuplexHandler
extends ChannelDuplexHandler {
    private volatile Http2FrameCodec frameCodec;

    @Override
    public final void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.frameCodec = Http2ChannelDuplexHandler.requireHttp2FrameCodec(ctx);
        this.handlerAdded0(ctx);
    }

    protected void handlerAdded0(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            this.handlerRemoved0(ctx);
        }
        finally {
            this.frameCodec = null;
        }
    }

    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    }

    public final Http2FrameStream newStream() {
        Http2FrameCodec codec = this.frameCodec;
        if (codec == null) {
            throw new IllegalStateException(StringUtil.simpleClassName(Http2FrameCodec.class) + " not found. Has the handler been added to a pipeline?");
        }
        return codec.newStream();
    }

    protected final void forEachActiveStream(Http2FrameStreamVisitor streamVisitor) throws Http2Exception {
        this.frameCodec.forEachActiveStream(streamVisitor);
    }

    private static Http2FrameCodec requireHttp2FrameCodec(ChannelHandlerContext ctx) {
        ChannelHandlerContext frameCodecCtx = ctx.pipeline().context(Http2FrameCodec.class);
        if (frameCodecCtx == null) {
            throw new IllegalArgumentException(Http2FrameCodec.class.getSimpleName() + " was not found in the channel pipeline.");
        }
        return (Http2FrameCodec)frameCodecCtx.handler();
    }
}

