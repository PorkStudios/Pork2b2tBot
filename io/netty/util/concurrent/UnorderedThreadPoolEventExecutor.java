/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseTask;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.SucceededFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class UnorderedThreadPoolEventExecutor
extends ScheduledThreadPoolExecutor
implements EventExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnorderedThreadPoolEventExecutor.class);
    private final Promise<?> terminationFuture = GlobalEventExecutor.INSTANCE.newPromise();
    private final Set<EventExecutor> executorSet = Collections.singleton(this);

    public UnorderedThreadPoolEventExecutor(int corePoolSize) {
        this(corePoolSize, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class));
    }

    public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public UnorderedThreadPoolEventExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        this(corePoolSize, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class), handler);
    }

    public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    @Override
    public EventExecutor next() {
        return this;
    }

    @Override
    public EventExecutorGroup parent() {
        return this;
    }

    @Override
    public boolean inEventLoop() {
        return false;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return false;
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new DefaultPromise(this);
    }

    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new DefaultProgressivePromise(this);
    }

    @Override
    public <V> Future<V> newSucceededFuture(V result) {
        return new SucceededFuture<V>(this, result);
    }

    @Override
    public <V> Future<V> newFailedFuture(Throwable cause) {
        return new FailedFuture(this, cause);
    }

    @Override
    public boolean isShuttingDown() {
        return this.isShutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks = super.shutdownNow();
        this.terminationFuture.trySuccess(null);
        return tasks;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        this.terminationFuture.trySuccess(null);
    }

    @Override
    public Future<?> shutdownGracefully() {
        return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        this.shutdown();
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return this.executorSet.iterator();
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        return runnable instanceof NonNotifyRunnable ? task : new RunnableScheduledFutureTask((EventExecutor)this, runnable, task);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        return new RunnableScheduledFutureTask<V>((EventExecutor)this, callable, task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return (ScheduledFuture)super.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return (ScheduledFuture)super.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return (ScheduledFuture)super.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return (ScheduledFuture)super.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return (Future)super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return (Future)super.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return (Future)super.submit(task);
    }

    @Override
    public void execute(Runnable command) {
        super.schedule(new NonNotifyRunnable(command), 0L, TimeUnit.NANOSECONDS);
    }

    private static final class NonNotifyRunnable
    implements Runnable {
        private final Runnable task;

        NonNotifyRunnable(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            this.task.run();
        }
    }

    private static final class RunnableScheduledFutureTask<V>
    extends PromiseTask<V>
    implements RunnableScheduledFuture<V>,
    ScheduledFuture<V> {
        private final RunnableScheduledFuture<V> future;

        RunnableScheduledFutureTask(EventExecutor executor, Runnable runnable, RunnableScheduledFuture<V> future) {
            super(executor, runnable, null);
            this.future = future;
        }

        RunnableScheduledFutureTask(EventExecutor executor, Callable<V> callable, RunnableScheduledFuture<V> future) {
            super(executor, callable);
            this.future = future;
        }

        @Override
        public void run() {
            block5 : {
                if (!this.isPeriodic()) {
                    super.run();
                } else if (!this.isDone()) {
                    try {
                        this.task.call();
                    }
                    catch (Throwable cause) {
                        if (this.tryFailureInternal(cause)) break block5;
                        logger.warn("Failure during execution of task", cause);
                    }
                }
            }
        }

        @Override
        public boolean isPeriodic() {
            return this.future.isPeriodic();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return this.future.getDelay(unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return this.future.compareTo(o);
        }
    }

}

