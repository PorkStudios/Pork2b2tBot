/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

public abstract class AbstractEventLoop
extends AbstractEventExecutor
implements EventLoop {
    protected AbstractEventLoop() {
    }

    protected AbstractEventLoop(EventLoopGroup parent) {
        super(parent);
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup)super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop)super.next();
    }
}

