/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ConcurrentMap;

@ChannelHandler.Sharable
public abstract class ChannelInitializer<C extends Channel>
extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
    private final ConcurrentMap<ChannelHandlerContext, Boolean> initMap = PlatformDependent.newConcurrentHashMap();

    protected abstract void initChannel(C var1) throws Exception;

    @Override
    public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (this.initChannel(ctx)) {
            ctx.pipeline().fireChannelRegistered();
        } else {
            ctx.fireChannelRegistered();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), cause);
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isRegistered()) {
            this.initChannel(ctx);
        }
    }

    private boolean initChannel(ChannelHandlerContext ctx) throws Exception {
        if (this.initMap.putIfAbsent(ctx, Boolean.TRUE) == null) {
            try {
                this.initChannel(ctx.channel());
            }
            catch (Throwable cause) {
                this.exceptionCaught(ctx, cause);
            }
            finally {
                this.remove(ctx);
            }
            return true;
        }
        return false;
    }

    private void remove(ChannelHandlerContext ctx) {
        try {
            ChannelPipeline pipeline = ctx.pipeline();
            if (pipeline.context(this) != null) {
                pipeline.remove(this);
            }
        }
        finally {
            this.initMap.remove(ctx);
        }
    }
}

