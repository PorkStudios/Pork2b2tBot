/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoopGroup
extends MultithreadEventLoopGroup {
    public DefaultEventLoopGroup() {
        this(0);
    }

    public DefaultEventLoopGroup(int nThreads) {
        this(nThreads, (ThreadFactory)null);
    }

    public DefaultEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        super(nThreads, threadFactory, new Object[0]);
    }

    public DefaultEventLoopGroup(int nThreads, Executor executor) {
        super(nThreads, executor, new Object[0]);
    }

    @Override
    protected /* varargs */ EventLoop newChild(Executor executor, Object ... args) throws Exception {
        return new DefaultEventLoop((EventLoopGroup)this, executor);
    }
}

