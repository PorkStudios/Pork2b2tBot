/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ConcurrentSet;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

@ChannelHandler.Sharable
public class UniqueIpFilter
extends AbstractRemoteAddressFilter<InetSocketAddress> {
    private final Set<InetAddress> connected = new ConcurrentSet();

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        final InetAddress remoteIp = remoteAddress.getAddress();
        if (this.connected.contains(remoteIp)) {
            return false;
        }
        this.connected.add(remoteIp);
        ctx.channel().closeFuture().addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                UniqueIpFilter.this.connected.remove(remoteIp);
            }
        });
        return true;
    }

}

