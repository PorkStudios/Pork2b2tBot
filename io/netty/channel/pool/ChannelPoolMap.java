/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.pool;

import io.netty.channel.pool.ChannelPool;

public interface ChannelPoolMap<K, P extends ChannelPool> {
    public P get(K var1);

    public boolean contains(K var1);
}

