/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;

public interface ChannelProgressiveFuture
extends ChannelFuture,
ProgressiveFuture<Void> {
    @Override
    public ChannelProgressiveFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public /* varargs */ ChannelProgressiveFuture addListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelProgressiveFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public /* varargs */ ChannelProgressiveFuture removeListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelProgressiveFuture sync() throws InterruptedException;

    @Override
    public ChannelProgressiveFuture syncUninterruptibly();

    @Override
    public ChannelProgressiveFuture await() throws InterruptedException;

    @Override
    public ChannelProgressiveFuture awaitUninterruptibly();
}

