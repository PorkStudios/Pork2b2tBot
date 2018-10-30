/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class EpollEventLoopGroup
extends MultithreadEventLoopGroup {
    public EpollEventLoopGroup() {
        this(0);
    }

    public EpollEventLoopGroup(int nThreads) {
        this(nThreads, (ThreadFactory)null);
    }

    public EpollEventLoopGroup(int nThreads, SelectStrategyFactory selectStrategyFactory) {
        this(nThreads, (ThreadFactory)null, selectStrategyFactory);
    }

    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        this(nThreads, threadFactory, 0);
    }

    public EpollEventLoopGroup(int nThreads, Executor executor) {
        this(nThreads, executor, DefaultSelectStrategyFactory.INSTANCE);
    }

    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectStrategyFactory selectStrategyFactory) {
        this(nThreads, threadFactory, 0, selectStrategyFactory);
    }

    @Deprecated
    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce) {
        this(nThreads, threadFactory, maxEventsAtOnce, DefaultSelectStrategyFactory.INSTANCE);
    }

    @Deprecated
    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce, SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, threadFactory, maxEventsAtOnce, selectStrategyFactory, RejectedExecutionHandlers.reject());
        Epoll.ensureAvailability();
    }

    public EpollEventLoopGroup(int nThreads, Executor executor, SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, executor, 0, selectStrategyFactory, RejectedExecutionHandlers.reject());
        Epoll.ensureAvailability();
    }

    public EpollEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, executor, chooserFactory, 0, selectStrategyFactory, RejectedExecutionHandlers.reject());
        Epoll.ensureAvailability();
    }

    public EpollEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectStrategyFactory selectStrategyFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super(nThreads, executor, chooserFactory, 0, selectStrategyFactory, rejectedExecutionHandler);
        Epoll.ensureAvailability();
    }

    public void setIoRatio(int ioRatio) {
        for (EventExecutor e : this) {
            ((EpollEventLoop)e).setIoRatio(ioRatio);
        }
    }

    @Override
    protected /* varargs */ EventLoop newChild(Executor executor, Object ... args) throws Exception {
        return new EpollEventLoop(this, executor, (Integer)args[0], ((SelectStrategyFactory)args[1]).newSelectStrategy(), (RejectedExecutionHandler)args[2]);
    }
}

