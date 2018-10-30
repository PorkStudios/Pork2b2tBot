/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.InterruptibleTask;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import javax.annotation.Nullable;

@GwtCompatible
class TrustedListenableFutureTask<V>
extends AbstractFuture.TrustedFuture<V>
implements RunnableFuture<V> {
    private InterruptibleTask task;

    static <V> TrustedListenableFutureTask<V> create(AsyncCallable<V> callable) {
        return new TrustedListenableFutureTask<V>(callable);
    }

    static <V> TrustedListenableFutureTask<V> create(Callable<V> callable) {
        return new TrustedListenableFutureTask<V>(callable);
    }

    static <V> TrustedListenableFutureTask<V> create(Runnable runnable, @Nullable V result) {
        return new TrustedListenableFutureTask<V>(Executors.callable(runnable, result));
    }

    TrustedListenableFutureTask(Callable<V> callable) {
        this.task = new TrustedFutureInterruptibleTask(callable);
    }

    TrustedListenableFutureTask(AsyncCallable<V> callable) {
        this.task = new TrustedFutureInterruptibleAsyncTask(callable);
    }

    @Override
    public void run() {
        InterruptibleTask localTask = this.task;
        if (localTask != null) {
            localTask.run();
        }
    }

    @Override
    protected void afterDone() {
        InterruptibleTask localTask;
        super.afterDone();
        if (this.wasInterrupted() && (localTask = this.task) != null) {
            localTask.interruptTask();
        }
        this.task = null;
    }

    @Override
    protected String pendingToString() {
        InterruptibleTask localTask = this.task;
        if (localTask != null) {
            return "task=[" + localTask + "]";
        }
        return null;
    }

    private final class TrustedFutureInterruptibleAsyncTask
    extends InterruptibleTask {
        private final AsyncCallable<V> callable;

        TrustedFutureInterruptibleAsyncTask(AsyncCallable<V> callable) {
            this.callable = Preconditions.checkNotNull(callable);
        }

        @Override
        void runInterruptibly() {
            if (!TrustedListenableFutureTask.this.isDone()) {
                try {
                    ListenableFuture<V> result = this.callable.call();
                    Preconditions.checkNotNull(result, "AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)?");
                    TrustedListenableFutureTask.this.setFuture(result);
                }
                catch (Throwable t) {
                    TrustedListenableFutureTask.this.setException(t);
                }
            }
        }

        @Override
        boolean wasInterrupted() {
            return TrustedListenableFutureTask.this.wasInterrupted();
        }

        @Override
        public String toString() {
            return this.callable.toString();
        }
    }

    private final class TrustedFutureInterruptibleTask
    extends InterruptibleTask {
        private final Callable<V> callable;

        TrustedFutureInterruptibleTask(Callable<V> callable) {
            this.callable = Preconditions.checkNotNull(callable);
        }

        @Override
        void runInterruptibly() {
            if (!TrustedListenableFutureTask.this.isDone()) {
                try {
                    TrustedListenableFutureTask.this.set(this.callable.call());
                }
                catch (Throwable t) {
                    TrustedListenableFutureTask.this.setException(t);
                }
            }
        }

        @Override
        boolean wasInterrupted() {
            return TrustedListenableFutureTask.this.wasInterrupted();
        }

        @Override
        public String toString() {
            return this.callable.toString();
        }
    }

}

