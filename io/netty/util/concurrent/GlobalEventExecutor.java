/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GlobalEventExecutor
extends AbstractScheduledEventExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalEventExecutor.class);
    private static final long SCHEDULE_QUIET_PERIOD_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
    public static final GlobalEventExecutor INSTANCE = new GlobalEventExecutor();
    final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
    final ScheduledFutureTask<Void> quietPeriodTask = new ScheduledFutureTask<Object>((AbstractScheduledEventExecutor)this, Executors.callable(new Runnable(){

        @Override
        public void run() {
        }
    }, null), ScheduledFutureTask.deadlineNanos(SCHEDULE_QUIET_PERIOD_INTERVAL), - SCHEDULE_QUIET_PERIOD_INTERVAL);
    final ThreadFactory threadFactory = new DefaultThreadFactory(DefaultThreadFactory.toPoolName(this.getClass()), false, 5, null);
    private final TaskRunner taskRunner = new TaskRunner();
    private final AtomicBoolean started = new AtomicBoolean();
    volatile Thread thread;
    private final Future<?> terminationFuture = new FailedFuture(this, new UnsupportedOperationException());

    private GlobalEventExecutor() {
        this.scheduledTaskQueue().add(this.quietPeriodTask);
    }

    Runnable takeTask() {
        Runnable task;
        BlockingQueue<Runnable> taskQueue = this.taskQueue;
        do {
            ScheduledFutureTask<?> scheduledTask;
            if ((scheduledTask = this.peekScheduledTask()) == null) {
                Runnable task2 = null;
                try {
                    task2 = taskQueue.take();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return task2;
            }
            long delayNanos = scheduledTask.delayNanos();
            if (delayNanos > 0L) {
                try {
                    task = taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e) {
                    return null;
                }
            } else {
                task = taskQueue.poll();
            }
            if (task != null) continue;
            this.fetchFromScheduledTaskQueue();
            task = taskQueue.poll();
        } while (task == null);
        return task;
    }

    private void fetchFromScheduledTaskQueue() {
        long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        Runnable scheduledTask = this.pollScheduledTask(nanoTime);
        while (scheduledTask != null) {
            this.taskQueue.add(scheduledTask);
            scheduledTask = this.pollScheduledTask(nanoTime);
        }
    }

    public int pendingTasks() {
        return this.taskQueue.size();
    }

    private void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        this.taskQueue.add(task);
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Deprecated
    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    public boolean awaitInactivity(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        Thread thread = this.thread;
        if (thread == null) {
            throw new IllegalStateException("thread was not started");
        }
        thread.join(unit.toMillis(timeout));
        return !thread.isAlive();
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        this.addTask(task);
        if (!this.inEventLoop()) {
            this.startThread();
        }
    }

    private void startThread() {
        if (this.started.compareAndSet(false, true)) {
            Thread t = this.threadFactory.newThread(this.taskRunner);
            t.setContextClassLoader(null);
            this.thread = t;
            t.start();
        }
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }

    static /* synthetic */ AtomicBoolean access$100(GlobalEventExecutor x0) {
        return x0.started;
    }

    final class TaskRunner
    implements Runnable {
        TaskRunner() {
        }

        /*
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Lifted jumps to return sites
         */
        @Override
        public void run() {
            do lbl-1000: // 4 sources:
            {
                block4 : {
                    if ((task = GlobalEventExecutor.this.takeTask()) == null) break block4;
                    try {
                        task.run();
                    }
                    catch (Throwable t) {
                        GlobalEventExecutor.access$000().warn("Unexpected exception from the global event executor: ", t);
                    }
                    if (task != GlobalEventExecutor.this.quietPeriodTask) ** GOTO lbl-1000
                }
                scheduledTaskQueue = GlobalEventExecutor.this.scheduledTaskQueue;
                if (!GlobalEventExecutor.this.taskQueue.isEmpty() || scheduledTaskQueue != null && scheduledTaskQueue.size() != 1) ** GOTO lbl-1000
                stopped = GlobalEventExecutor.access$100(GlobalEventExecutor.this).compareAndSet(true, false);
                if (!TaskRunner.$assertionsDisabled && !stopped) {
                    throw new AssertionError();
                }
                if (!GlobalEventExecutor.this.taskQueue.isEmpty()) continue;
                if (scheduledTaskQueue == null) return;
                if (scheduledTaskQueue.size() != 1) continue;
                return;
            } while (GlobalEventExecutor.access$100(GlobalEventExecutor.this).compareAndSet(false, true));
        }
    }

}

