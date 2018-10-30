/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.pool;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.io.Closeable;

public interface ChannelPool
extends Closeable {
    public Future<Channel> acquire();

    public Future<Channel> acquire(Promise<Channel> var1);

    public Future<Void> release(Channel var1);

    public Future<Void> release(Channel var1, Promise<Void> var2);

    @Override
    public void close();
}

