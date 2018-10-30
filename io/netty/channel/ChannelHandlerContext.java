/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundInvoker;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;

public interface ChannelHandlerContext
extends AttributeMap,
ChannelInboundInvoker,
ChannelOutboundInvoker {
    public Channel channel();

    public EventExecutor executor();

    public String name();

    public ChannelHandler handler();

    public boolean isRemoved();

    @Override
    public ChannelHandlerContext fireChannelRegistered();

    @Override
    public ChannelHandlerContext fireChannelUnregistered();

    @Override
    public ChannelHandlerContext fireChannelActive();

    @Override
    public ChannelHandlerContext fireChannelInactive();

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable var1);

    @Override
    public ChannelHandlerContext fireUserEventTriggered(Object var1);

    @Override
    public ChannelHandlerContext fireChannelRead(Object var1);

    @Override
    public ChannelHandlerContext fireChannelReadComplete();

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged();

    @Override
    public ChannelHandlerContext read();

    @Override
    public ChannelHandlerContext flush();

    public ChannelPipeline pipeline();

    public ByteBufAllocator alloc();

    @Deprecated
    @Override
    public <T> Attribute<T> attr(AttributeKey<T> var1);

    @Deprecated
    @Override
    public <T> boolean hasAttr(AttributeKey<T> var1);
}

