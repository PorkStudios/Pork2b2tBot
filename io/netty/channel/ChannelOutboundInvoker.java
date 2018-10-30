/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

public interface ChannelOutboundInvoker {
    public ChannelFuture bind(SocketAddress var1);

    public ChannelFuture connect(SocketAddress var1);

    public ChannelFuture connect(SocketAddress var1, SocketAddress var2);

    public ChannelFuture disconnect();

    public ChannelFuture close();

    public ChannelFuture deregister();

    public ChannelFuture bind(SocketAddress var1, ChannelPromise var2);

    public ChannelFuture connect(SocketAddress var1, ChannelPromise var2);

    public ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

    public ChannelFuture disconnect(ChannelPromise var1);

    public ChannelFuture close(ChannelPromise var1);

    public ChannelFuture deregister(ChannelPromise var1);

    public ChannelOutboundInvoker read();

    public ChannelFuture write(Object var1);

    public ChannelFuture write(Object var1, ChannelPromise var2);

    public ChannelOutboundInvoker flush();

    public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

    public ChannelFuture writeAndFlush(Object var1);

    public ChannelPromise newPromise();

    public ChannelProgressivePromise newProgressivePromise();

    public ChannelFuture newSucceededFuture();

    public ChannelFuture newFailedFuture(Throwable var1);

    public ChannelPromise voidPromise();
}

