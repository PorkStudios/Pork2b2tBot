/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventExecutorGroup
extends MultithreadEventExecutorGroup {
    public DefaultEventExecutorGroup(int nThreads) {
        this(nThreads, null);
    }

    public DefaultEventExecutorGroup(int nThreads, ThreadFactory threadFactory) {
        this(nThreads, threadFactory, SingleThreadEventExecutor.DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
    }

    public DefaultEventExecutorGroup(int nThreads, ThreadFactory threadFactory, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        super(nThreads, threadFactory, maxPendingTasks, rejectedHandler);
    }

    @Override
    protected /* varargs */ EventExecutor newChild(Executor executor, Object ... args) throws Exception {
        return new DefaultEventExecutor((EventExecutorGroup)this, executor, (int)((Integer)args[0]), (RejectedExecutionHandler)args[1]);
    }
}

