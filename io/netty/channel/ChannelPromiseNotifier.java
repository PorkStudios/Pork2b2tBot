/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;

public final class ChannelPromiseNotifier
extends PromiseNotifier<Void, ChannelFuture>
implements ChannelFutureListener {
    public /* varargs */ ChannelPromiseNotifier(ChannelPromise ... promises) {
        super(promises);
    }

    public /* varargs */ ChannelPromiseNotifier(boolean logNotifyFailure, ChannelPromise ... promises) {
        super(logNotifyFailure, promises);
    }
}

