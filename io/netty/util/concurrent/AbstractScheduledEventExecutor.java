/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledEventExecutor
extends AbstractEventExecutor {
    private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>(){

        @Override
        public int compare(ScheduledFutureTask<?> o1, ScheduledFutureTask<?> o2) {
            return o1.compareTo(o2);
        }
    };
    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;

    protected AbstractScheduledEventExecutor() {
    }

    protected AbstractScheduledEventExecutor(EventExecutorGroup parent) {
        super(parent);
    }

    protected static long nanoTime() {
        return ScheduledFutureTask.nanoTime();
    }

    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
        if (this.scheduledTaskQueue == null) {
            this.scheduledTaskQueue = new DefaultPriorityQueue(SCHEDULED_FUTURE_TASK_COMPARATOR, 11);
        }
        return this.scheduledTaskQueue;
    }

    private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> queue) {
        return queue == null || queue.isEmpty();
    }

    protected void cancelScheduledTasks() {
        ScheduledFutureTask[] scheduledTasks;
        assert (this.inEventLoop());
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (AbstractScheduledEventExecutor.isNullOrEmpty(scheduledTaskQueue)) {
            return;
        }
        for (ScheduledFutureTask task : scheduledTasks = scheduledTaskQueue.toArray(new ScheduledFutureTask[scheduledTaskQueue.size()])) {
            task.cancelWithoutRemove(false);
        }
        scheduledTaskQueue.clearIgnoringIndexes();
    }

    protected final Runnable pollScheduledTask() {
        return this.pollScheduledTask(AbstractScheduledEventExecutor.nanoTime());
    }

    protected final Runnable pollScheduledTask(long nanoTime) {
        ScheduledFutureTask<?> scheduledTask;
        assert (this.inEventLoop());
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        ScheduledFutureTask<?> scheduledFutureTask = scheduledTask = scheduledTaskQueue == null ? null : scheduledTaskQueue.peek();
        if (scheduledTask == null) {
            return null;
        }
        if (scheduledTask.deadlineNanos() <= nanoTime) {
            scheduledTaskQueue.remove();
            return scheduledTask;
        }
        return null;
    }

    protected final long nextScheduledTaskNano() {
        ScheduledFutureTask<?> scheduledTask;
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        ScheduledFutureTask<?> scheduledFutureTask = scheduledTask = scheduledTaskQueue == null ? null : scheduledTaskQueue.peek();
        if (scheduledTask == null) {
            return -1L;
        }
        return Math.max(0L, scheduledTask.deadlineNanos() - AbstractScheduledEventExecutor.nanoTime());
    }

    final ScheduledFutureTask<?> peekScheduledTask() {
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (scheduledTaskQueue == null) {
            return null;
        }
        return scheduledTaskQueue.peek();
    }

    protected final boolean hasScheduledTasks() {
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        ScheduledFutureTask<?> scheduledTask = scheduledTaskQueue == null ? null : scheduledTaskQueue.peek();
        return scheduledTask != null && scheduledTask.deadlineNanos() <= AbstractScheduledEventExecutor.nanoTime();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (delay < 0L) {
            delay = 0L;
        }
        return this.schedule(new ScheduledFutureTask<Object>(this, command, null, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(callable, "callable");
        ObjectUtil.checkNotNull(unit, "unit");
        if (delay < 0L) {
            delay = 0L;
        }
        return this.schedule(new ScheduledFutureTask<V>(this, callable, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (period <= 0L) {
            throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", period));
        }
        return this.schedule(new ScheduledFutureTask<Object>(this, Executors.callable(command, null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period)));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (delay <= 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", delay));
        }
        return this.schedule(new ScheduledFutureTask<Object>(this, Executors.callable(command, null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), - unit.toNanos(delay)));
    }

    <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> task) {
        if (this.inEventLoop()) {
            this.scheduledTaskQueue().add(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    AbstractScheduledEventExecutor.this.scheduledTaskQueue().add(task);
                }
            });
        }
        return task;
    }

    final void removeScheduled(final ScheduledFutureTask<?> task) {
        if (this.inEventLoop()) {
            this.scheduledTaskQueue().removeTyped(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    AbstractScheduledEventExecutor.this.removeScheduled(task);
                }
            });
        }
    }

}

