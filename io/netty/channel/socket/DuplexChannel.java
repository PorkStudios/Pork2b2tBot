/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

public interface DuplexChannel
extends Channel {
    public boolean isInputShutdown();

    public ChannelFuture shutdownInput();

    public ChannelFuture shutdownInput(ChannelPromise var1);

    public boolean isOutputShutdown();

    public ChannelFuture shutdownOutput();

    public ChannelFuture shutdownOutput(ChannelPromise var1);

    public boolean isShutdown();

    public ChannelFuture shutdown();

    public ChannelFuture shutdown(ChannelPromise var1);
}

