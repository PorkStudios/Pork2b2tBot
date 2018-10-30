/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import java.util.Set;

public interface ChannelGroup
extends Set<Channel>,
Comparable<ChannelGroup> {
    public String name();

    public Channel find(ChannelId var1);

    public ChannelGroupFuture write(Object var1);

    public ChannelGroupFuture write(Object var1, ChannelMatcher var2);

    public ChannelGroupFuture write(Object var1, ChannelMatcher var2, boolean var3);

    public ChannelGroup flush();

    public ChannelGroup flush(ChannelMatcher var1);

    public ChannelGroupFuture writeAndFlush(Object var1);

    @Deprecated
    public ChannelGroupFuture flushAndWrite(Object var1);

    public ChannelGroupFuture writeAndFlush(Object var1, ChannelMatcher var2);

    public ChannelGroupFuture writeAndFlush(Object var1, ChannelMatcher var2, boolean var3);

    @Deprecated
    public ChannelGroupFuture flushAndWrite(Object var1, ChannelMatcher var2);

    public ChannelGroupFuture disconnect();

    public ChannelGroupFuture disconnect(ChannelMatcher var1);

    public ChannelGroupFuture close();

    public ChannelGroupFuture close(ChannelMatcher var1);

    @Deprecated
    public ChannelGroupFuture deregister();

    @Deprecated
    public ChannelGroupFuture deregister(ChannelMatcher var1);

    public ChannelGroupFuture newCloseFuture();

    public ChannelGroupFuture newCloseFuture(ChannelMatcher var1);
}

