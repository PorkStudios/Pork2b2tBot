/*
 * Decompiled with CFR 0_132.
 */
package io.netty.bootstrap;

import io.netty.channel.Channel;

@Deprecated
public interface ChannelFactory<T extends Channel> {
    public T newChannel();
}

