/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public final class NonStickyEventExecutorGroup
implements EventExecutorGroup {
    private final EventExecutorGroup group;
    private final int maxTaskExecutePerRun;

    public NonStickyEventExecutorGroup(EventExecutorGroup group) {
        this(group, 1024);
    }

    public NonStickyEventExecutorGroup(EventExecutorGroup group, int maxTaskExecutePerRun) {
        this.group = NonStickyEventExecutorGroup.verify(group);
        this.maxTaskExecutePerRun = ObjectUtil.checkPositive(maxTaskExecutePerRun, "maxTaskExecutePerRun");
    }

    private static EventExecutorGroup verify(EventExecutorGroup group) {
        for (EventExecutor executor : ObjectUtil.checkNotNull(group, "group")) {
            if (!(executor instanceof OrderedEventExecutor)) continue;
            throw new IllegalArgumentException("EventExecutorGroup " + group + " contains OrderedEventExecutors: " + executor);
        }
        return group;
    }

    private NonStickyOrderedEventExecutor newExecutor(EventExecutor executor) {
        return new NonStickyOrderedEventExecutor(executor, this.maxTaskExecutePerRun);
    }

    @Override
    public boolean isShuttingDown() {
        return this.group.isShuttingDown();
    }

    @Override
    public Future<?> shutdownGracefully() {
        return this.group.shutdownGracefully();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return this.group.shutdownGracefully(quietPeriod, timeout, unit);
    }

    @Override
    public Future<?> terminationFuture() {
        return this.group.terminationFuture();
    }

    @Override
    public void shutdown() {
        this.group.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.group.shutdownNow();
    }

    @Override
    public EventExecutor next() {
        return this.newExecutor(this.group.next());
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        final Iterator<EventExecutor> itr = this.group.iterator();
        return new Iterator<EventExecutor>(){

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public EventExecutor next() {
                return NonStickyEventExecutorGroup.this.newExecutor((EventExecutor)itr.next());
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.group.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.group.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.group.submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.group.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.group.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.group.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.group.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override
    public boolean isShutdown() {
        return this.group.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.group.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.group.awaitTermination(timeout, unit);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.group.invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.group.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.group.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.group.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.group.execute(command);
    }

    private static final class NonStickyOrderedEventExecutor
    extends AbstractEventExecutor
    implements Runnable,
    OrderedEventExecutor {
        private final EventExecutor executor;
        private final Queue<Runnable> tasks = PlatformDependent.newMpscQueue();
        private static final int NONE = 0;
        private static final int SUBMITTED = 1;
        private static final int RUNNING = 2;
        private final AtomicInteger state = new AtomicInteger();
        private final int maxTaskExecutePerRun;

        NonStickyOrderedEventExecutor(EventExecutor executor, int maxTaskExecutePerRun) {
            super(executor);
            this.executor = executor;
            this.maxTaskExecutePerRun = maxTaskExecutePerRun;
        }

        @Override
        public void run() {
            if (!this.state.compareAndSet(1, 2)) {
                return;
            }
            block7 : do {
                int i = 0;
                try {
                    do {
                        Runnable task;
                        if (i >= this.maxTaskExecutePerRun || (task = this.tasks.poll()) == null) continue block7;
                        NonStickyOrderedEventExecutor.safeExecute(task);
                        ++i;
                    } while (true);
                }
                finally {
                    if (i == this.maxTaskExecutePerRun) {
                        try {
                            this.state.set(1);
                            this.executor.execute(this);
                            return;
                        }
                        catch (Throwable ignore) {
                            this.state.set(2);
                        }
                    } else {
                        this.state.set(0);
                        return;
                    }
                    continue;
                }
                break;
            } while (true);
        }

        @Override
        public boolean inEventLoop(Thread thread) {
            return false;
        }

        @Override
        public boolean inEventLoop() {
            return false;
        }

        @Override
        public boolean isShuttingDown() {
            return this.executor.isShutdown();
        }

        @Override
        public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
            return this.executor.shutdownGracefully(quietPeriod, timeout, unit);
        }

        @Override
        public Future<?> terminationFuture() {
            return this.executor.terminationFuture();
        }

        @Override
        public void shutdown() {
            this.executor.shutdown();
        }

        @Override
        public boolean isShutdown() {
            return this.executor.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return this.executor.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return this.executor.awaitTermination(timeout, unit);
        }

        @Override
        public void execute(Runnable command) {
            if (!this.tasks.offer(command)) {
                throw new RejectedExecutionException();
            }
            if (this.state.compareAndSet(0, 1)) {
                try {
                    this.executor.execute(this);
                }
                catch (Throwable e) {
                    this.tasks.remove(command);
                    PlatformDependent.throwException(e);
                }
            }
        }
    }

}

