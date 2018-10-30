/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FixedChannelPool
extends SimpleChannelPool {
    private static final IllegalStateException FULL_EXCEPTION = ThrowableUtil.unknownStackTrace(new IllegalStateException("Too many outstanding acquire operations"), FixedChannelPool.class, "acquire0(...)");
    private static final TimeoutException TIMEOUT_EXCEPTION = ThrowableUtil.unknownStackTrace(new TimeoutException("Acquire operation took longer then configured maximum time"), FixedChannelPool.class, "<init>(...)");
    static final IllegalStateException POOL_CLOSED_ON_RELEASE_EXCEPTION = ThrowableUtil.unknownStackTrace(new IllegalStateException("FixedChannelPooled was closed"), FixedChannelPool.class, "release(...)");
    static final IllegalStateException POOL_CLOSED_ON_ACQUIRE_EXCEPTION = ThrowableUtil.unknownStackTrace(new IllegalStateException("FixedChannelPooled was closed"), FixedChannelPool.class, "acquire0(...)");
    private final EventExecutor executor;
    private final long acquireTimeoutNanos;
    private final Runnable timeoutTask;
    private final Queue<AcquireTask> pendingAcquireQueue = new ArrayDeque<AcquireTask>();
    private final int maxConnections;
    private final int maxPendingAcquires;
    private int acquiredChannelCount;
    private int pendingAcquireCount;
    private boolean closed;

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections) {
        this(bootstrap, handler, maxConnections, Integer.MAX_VALUE);
    }

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections, int maxPendingAcquires) {
        this(bootstrap, handler, ChannelHealthChecker.ACTIVE, null, -1L, maxConnections, maxPendingAcquires);
    }

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires) {
        this(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, true);
    }

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck) {
        this(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, releaseHealthCheck, true);
    }

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck, boolean lastRecentUsed) {
        super(bootstrap, handler, healthCheck, releaseHealthCheck, lastRecentUsed);
        if (maxConnections < 1) {
            throw new IllegalArgumentException("maxConnections: " + maxConnections + " (expected: >= 1)");
        }
        if (maxPendingAcquires < 1) {
            throw new IllegalArgumentException("maxPendingAcquires: " + maxPendingAcquires + " (expected: >= 1)");
        }
        if (action == null && acquireTimeoutMillis == -1L) {
            this.timeoutTask = null;
            this.acquireTimeoutNanos = -1L;
        } else {
            if (action == null && acquireTimeoutMillis != -1L) {
                throw new NullPointerException("action");
            }
            if (action != null && acquireTimeoutMillis < 0L) {
                throw new IllegalArgumentException("acquireTimeoutMillis: " + acquireTimeoutMillis + " (expected: >= 0)");
            }
            this.acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(acquireTimeoutMillis);
            switch (action) {
                case FAIL: {
                    this.timeoutTask = new TimeoutTask(){

                        @Override
                        public void onTimeout(AcquireTask task) {
                            task.promise.setFailure(TIMEOUT_EXCEPTION);
                        }
                    };
                    break;
                }
                case NEW: {
                    this.timeoutTask = new TimeoutTask(){

                        @Override
                        public void onTimeout(AcquireTask task) {
                            task.acquired();
                            FixedChannelPool.super.acquire(task.promise);
                        }
                    };
                    break;
                }
                default: {
                    throw new Error();
                }
            }
        }
        this.executor = bootstrap.config().group().next();
        this.maxConnections = maxConnections;
        this.maxPendingAcquires = maxPendingAcquires;
    }

    @Override
    public Future<Channel> acquire(final Promise<Channel> promise) {
        try {
            if (this.executor.inEventLoop()) {
                this.acquire0(promise);
            } else {
                this.executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        FixedChannelPool.this.acquire0(promise);
                    }
                });
            }
        }
        catch (Throwable cause) {
            promise.setFailure(cause);
        }
        return promise;
    }

    private void acquire0(Promise<Channel> promise) {
        assert (this.executor.inEventLoop());
        if (this.closed) {
            promise.setFailure(POOL_CLOSED_ON_ACQUIRE_EXCEPTION);
            return;
        }
        if (this.acquiredChannelCount < this.maxConnections) {
            assert (this.acquiredChannelCount >= 0);
            Promise<Channel> p = this.executor.newPromise();
            AcquireListener l = new AcquireListener(promise);
            l.acquired();
            p.addListener(l);
            super.acquire(p);
        } else {
            if (this.pendingAcquireCount >= this.maxPendingAcquires) {
                promise.setFailure(FULL_EXCEPTION);
            } else {
                AcquireTask task = new AcquireTask(promise);
                if (this.pendingAcquireQueue.offer(task)) {
                    ++this.pendingAcquireCount;
                    if (this.timeoutTask != null) {
                        task.timeoutFuture = this.executor.schedule(this.timeoutTask, this.acquireTimeoutNanos, TimeUnit.NANOSECONDS);
                    }
                } else {
                    promise.setFailure(FULL_EXCEPTION);
                }
            }
            assert (this.pendingAcquireCount > 0);
        }
    }

    @Override
    public Future<Void> release(final Channel channel, final Promise<Void> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        Promise p = this.executor.newPromise();
        super.release(channel, p.addListener(new FutureListener<Void>(){

            @Override
            public void operationComplete(Future<Void> future) throws Exception {
                assert (FixedChannelPool.this.executor.inEventLoop());
                if (FixedChannelPool.this.closed) {
                    channel.close();
                    promise.setFailure(FixedChannelPool.POOL_CLOSED_ON_RELEASE_EXCEPTION);
                    return;
                }
                if (future.isSuccess()) {
                    FixedChannelPool.this.decrementAndRunTaskQueue();
                    promise.setSuccess(null);
                } else {
                    Throwable cause = future.cause();
                    if (!(cause instanceof IllegalArgumentException)) {
                        FixedChannelPool.this.decrementAndRunTaskQueue();
                    }
                    promise.setFailure(future.cause());
                }
            }
        }));
        return promise;
    }

    private void decrementAndRunTaskQueue() {
        --this.acquiredChannelCount;
        assert (this.acquiredChannelCount >= 0);
        this.runTaskQueue();
    }

    private void runTaskQueue() {
        AcquireTask task;
        while (this.acquiredChannelCount < this.maxConnections && (task = this.pendingAcquireQueue.poll()) != null) {
            ScheduledFuture<?> timeoutFuture = task.timeoutFuture;
            if (timeoutFuture != null) {
                timeoutFuture.cancel(false);
            }
            --this.pendingAcquireCount;
            task.acquired();
            super.acquire(task.promise);
        }
        assert (this.pendingAcquireCount >= 0);
        assert (this.acquiredChannelCount >= 0);
    }

    @Override
    public void close() {
        this.executor.execute(new Runnable(){

            @Override
            public void run() {
                if (!FixedChannelPool.this.closed) {
                    AcquireTask task;
                    FixedChannelPool.this.closed = true;
                    while ((task = (AcquireTask)FixedChannelPool.this.pendingAcquireQueue.poll()) != null) {
                        ScheduledFuture<?> f = task.timeoutFuture;
                        if (f != null) {
                            f.cancel(false);
                        }
                        task.promise.setFailure(new ClosedChannelException());
                    }
                    FixedChannelPool.this.acquiredChannelCount = 0;
                    FixedChannelPool.this.pendingAcquireCount = 0;
                    FixedChannelPool.super.close();
                }
            }
        });
    }

    private class AcquireListener
    implements FutureListener<Channel> {
        private final Promise<Channel> originalPromise;
        protected boolean acquired;

        AcquireListener(Promise<Channel> originalPromise) {
            this.originalPromise = originalPromise;
        }

        @Override
        public void operationComplete(Future<Channel> future) throws Exception {
            assert (FixedChannelPool.this.executor.inEventLoop());
            if (FixedChannelPool.this.closed) {
                if (future.isSuccess()) {
                    future.getNow().close();
                }
                this.originalPromise.setFailure(FixedChannelPool.POOL_CLOSED_ON_ACQUIRE_EXCEPTION);
                return;
            }
            if (future.isSuccess()) {
                this.originalPromise.setSuccess(future.getNow());
            } else {
                if (this.acquired) {
                    FixedChannelPool.this.decrementAndRunTaskQueue();
                } else {
                    FixedChannelPool.this.runTaskQueue();
                }
                this.originalPromise.setFailure(future.cause());
            }
        }

        public void acquired() {
            if (this.acquired) {
                return;
            }
            FixedChannelPool.this.acquiredChannelCount++;
            this.acquired = true;
        }
    }

    private abstract class TimeoutTask
    implements Runnable {
        private TimeoutTask() {
        }

        @Override
        public final void run() {
            AcquireTask task;
            assert (FixedChannelPool.this.executor.inEventLoop());
            long nanoTime = System.nanoTime();
            while ((task = (AcquireTask)FixedChannelPool.this.pendingAcquireQueue.peek()) != null && nanoTime - task.expireNanoTime >= 0L) {
                FixedChannelPool.this.pendingAcquireQueue.remove();
                --FixedChannelPool.this.pendingAcquireCount;
                this.onTimeout(task);
            }
        }

        public abstract void onTimeout(AcquireTask var1);
    }

    private final class AcquireTask
    extends AcquireListener {
        final Promise<Channel> promise;
        final long expireNanoTime;
        ScheduledFuture<?> timeoutFuture;

        public AcquireTask(Promise<Channel> promise) {
            super(promise);
            this.expireNanoTime = System.nanoTime() + FixedChannelPool.this.acquireTimeoutNanos;
            this.promise = FixedChannelPool.this.executor.newPromise().addListener(this);
        }
    }

    public static enum AcquireTimeoutAction {
        NEW,
        FAIL;
        

        private AcquireTimeoutAction() {
        }
    }

}

