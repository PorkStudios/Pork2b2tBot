/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;

public abstract class AbstractRemoteAddressFilter<T extends SocketAddress>
extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.handleNewChannel(ctx);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.handleNewChannel(ctx)) {
            throw new IllegalStateException("cannot determine to accept or reject a channel: " + ctx.channel());
        }
        ctx.fireChannelActive();
    }

    private boolean handleNewChannel(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress == null) {
            return false;
        }
        ctx.pipeline().remove(this);
        if (this.accept(ctx, remoteAddress)) {
            this.channelAccepted(ctx, remoteAddress);
        } else {
            ChannelFuture rejectedFuture = this.channelRejected(ctx, remoteAddress);
            if (rejectedFuture != null) {
                rejectedFuture.addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.close();
            }
        }
        return true;
    }

    protected abstract boolean accept(ChannelHandlerContext var1, T var2) throws Exception;

    protected void channelAccepted(ChannelHandlerContext ctx, T remoteAddress) {
    }

    protected ChannelFuture channelRejected(ChannelHandlerContext ctx, T remoteAddress) {
        return null;
    }
}

