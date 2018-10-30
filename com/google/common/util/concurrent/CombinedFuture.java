/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.util.concurrent.AggregateFuture;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.InterruptibleTask;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nullable;

@GwtCompatible
final class CombinedFuture<V>
extends AggregateFuture<Object, V> {
    CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, AsyncCallable<V> callable) {
        this.init((AggregateFuture.RunningState)((Object)new CombinedFutureRunningState(this, futures, allMustSucceed, (CombinedFutureInterruptibleTask)((Object)new AsyncCallableInterruptibleTask(callable, listenerExecutor)))));
    }

    CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, Callable<V> callable) {
        this.init((AggregateFuture.RunningState)((Object)new CombinedFutureRunningState(this, futures, allMustSucceed, (CombinedFutureInterruptibleTask)((Object)new CallableInterruptibleTask(callable, listenerExecutor)))));
    }

    private final class CallableInterruptibleTask
    extends CombinedFuture<V> {
        private final Callable<V> callable;

        public CallableInterruptibleTask(Callable<V> callable, Executor listenerExecutor) {
            super(listenerExecutor);
            this.callable = Preconditions.checkNotNull(callable);
        }

        void setValue() throws Exception {
            CombinedFuture.this.set(this.callable.call());
        }

        @Override
        public String toString() {
            return this.callable.toString();
        }
    }

    private final class AsyncCallableInterruptibleTask
    extends CombinedFuture<V> {
        private final AsyncCallable<V> callable;

        public AsyncCallableInterruptibleTask(AsyncCallable<V> callable, Executor listenerExecutor) {
            super(listenerExecutor);
            this.callable = Preconditions.checkNotNull(callable);
        }

        void setValue() throws Exception {
            CombinedFuture.this.setFuture(this.callable.call());
        }

        @Override
        public String toString() {
            return this.callable.toString();
        }
    }

    private abstract class CombinedFutureInterruptibleTask
    extends InterruptibleTask {
        private final Executor listenerExecutor;
        volatile boolean thrownByExecute = true;

        public CombinedFutureInterruptibleTask(Executor listenerExecutor) {
            this.listenerExecutor = Preconditions.checkNotNull(listenerExecutor);
        }

        @Override
        final void runInterruptibly() {
            this.thrownByExecute = false;
            if (!CombinedFuture.this.isDone()) {
                try {
                    this.setValue();
                }
                catch (ExecutionException e) {
                    CombinedFuture.this.setException(e.getCause());
                }
                catch (CancellationException e) {
                    CombinedFuture.this.cancel(false);
                }
                catch (Throwable e) {
                    CombinedFuture.this.setException(e);
                }
            }
        }

        @Override
        final boolean wasInterrupted() {
            return CombinedFuture.this.wasInterrupted();
        }

        final void execute() {
            block2 : {
                try {
                    this.listenerExecutor.execute(this);
                }
                catch (RejectedExecutionException e) {
                    if (!this.thrownByExecute) break block2;
                    CombinedFuture.this.setException(e);
                }
            }
        }

        abstract void setValue() throws Exception;
    }

    private final class CombinedFutureRunningState
    extends AggregateFuture<Object, V> {
        private CombinedFuture<V> task;

        CombinedFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends Object>> futures, boolean allMustSucceed, CombinedFuture<V> task) {
            super(futures, allMustSucceed, false);
            this.task = task;
        }

        void collectOneValue(boolean allMustSucceed, int index, @Nullable Object returnValue) {
        }

        void handleAllCompleted() {
            CombinedFuture<V> localTask = this.task;
            if (localTask != null) {
                localTask.execute();
            } else {
                Preconditions.checkState(this$0.isDone());
            }
        }

        void releaseResourcesAfterFailure() {
            AggregateFuture.RunningState.super.releaseResourcesAfterFailure();
            this.task = null;
        }

        @Override
        void interruptTask() {
            CombinedFuture<V> localTask = this.task;
            if (localTask != null) {
                localTask.interruptTask();
            }
        }
    }

}

