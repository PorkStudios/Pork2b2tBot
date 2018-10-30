/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.Channel;

public interface ChannelFactory<T extends Channel>
extends io.netty.bootstrap.ChannelFactory<T> {
    @Override
    public T newChannel();
}

