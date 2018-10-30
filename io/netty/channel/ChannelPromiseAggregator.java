/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseAggregator;

@Deprecated
public final class ChannelPromiseAggregator
extends PromiseAggregator<Void, ChannelFuture>
implements ChannelFutureListener {
    public ChannelPromiseAggregator(ChannelPromise aggregatePromise) {
        super(aggregatePromise);
    }
}

