/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public final class ImmediateEventExecutor
extends AbstractEventExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ImmediateEventExecutor.class);
    public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
    private static final FastThreadLocal<Queue<Runnable>> DELAYED_RUNNABLES = new FastThreadLocal<Queue<Runnable>>(){

        @Override
        protected Queue<Runnable> initialValue() throws Exception {
            return new ArrayDeque<Runnable>();
        }
    };
    private static final FastThreadLocal<Boolean> RUNNING = new FastThreadLocal<Boolean>(){

        @Override
        protected Boolean initialValue() throws Exception {
            return false;
        }
    };
    private final Future<?> terminationFuture = new FailedFuture(GlobalEventExecutor.INSTANCE, new UnsupportedOperationException());

    private ImmediateEventExecutor() {
    }

    @Override
    public boolean inEventLoop() {
        return true;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return true;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        if (!RUNNING.get().booleanValue()) {
            RUNNING.set(true);
            try {
                command.run();
            }
            catch (Throwable cause) {
                logger.info("Throwable caught while executing Runnable {}", (Object)command, (Object)cause);
            }
            finally {
                Runnable runnable;
                Queue<Runnable> delayedRunnables = DELAYED_RUNNABLES.get();
                while ((runnable = delayedRunnables.poll()) != null) {
                    try {
                        runnable.run();
                    }
                    catch (Throwable cause) {
                        logger.info("Throwable caught while executing Runnable {}", (Object)runnable, (Object)cause);
                    }
                }
                RUNNING.set(false);
            }
        } else {
            DELAYED_RUNNABLES.get().add(command);
        }
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new ImmediatePromise(this);
    }

    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new ImmediateProgressivePromise(this);
    }

    static class ImmediateProgressivePromise<V>
    extends DefaultProgressivePromise<V> {
        ImmediateProgressivePromise(EventExecutor executor) {
            super(executor);
        }

        @Override
        protected void checkDeadLock() {
        }
    }

    static class ImmediatePromise<V>
    extends DefaultPromise<V> {
        ImmediatePromise(EventExecutor executor) {
            super(executor);
        }

        @Override
        protected void checkDeadLock() {
        }
    }

}

