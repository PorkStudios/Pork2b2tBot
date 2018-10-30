/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.kqueue.AbstractKQueueChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventArray;
import io.netty.channel.kqueue.Native;
import io.netty.channel.kqueue.NativeLongArray;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class KQueueEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(KQueueEventLoop.class);
    private static final AtomicIntegerFieldUpdater<KQueueEventLoop> WAKEN_UP_UPDATER = AtomicIntegerFieldUpdater.newUpdater(KQueueEventLoop.class, "wakenUp");
    private static final int KQUEUE_WAKE_UP_IDENT = 0;
    private final NativeLongArray jniChannelPointers;
    private final boolean allowGrowing;
    private final FileDescriptor kqueueFd;
    private final KQueueEventArray changeList;
    private final KQueueEventArray eventList;
    private final SelectStrategy selectStrategy;
    private final IovArray iovArray = new IovArray();
    private final IntSupplier selectNowSupplier = new IntSupplier(){

        @Override
        public int get() throws Exception {
            return KQueueEventLoop.this.kqueueWaitNow();
        }
    };
    private final Callable<Integer> pendingTasksCallable = new Callable<Integer>(){

        @Override
        public Integer call() throws Exception {
            return KQueueEventLoop.super.pendingTasks();
        }
    };
    private volatile int wakenUp;
    private volatile int ioRatio = 50;

    KQueueEventLoop(EventLoopGroup parent, Executor executor, int maxEvents, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
        this.kqueueFd = Native.newKQueue();
        if (maxEvents == 0) {
            this.allowGrowing = true;
            maxEvents = 4096;
        } else {
            this.allowGrowing = false;
        }
        this.changeList = new KQueueEventArray(maxEvents);
        this.eventList = new KQueueEventArray(maxEvents);
        this.jniChannelPointers = new NativeLongArray(4096);
        int result = Native.keventAddUserEvent(this.kqueueFd.intValue(), 0);
        if (result < 0) {
            this.cleanup();
            throw new IllegalStateException("kevent failed to add user event with errno: " + (- result));
        }
    }

    void evSet(AbstractKQueueChannel ch, short filter, short flags, int fflags) {
        this.changeList.evSet(ch, filter, flags, fflags);
    }

    void remove(AbstractKQueueChannel ch) throws IOException {
        assert (this.inEventLoop());
        if (ch.jniSelfPtr == 0L) {
            return;
        }
        this.jniChannelPointers.add(ch.jniSelfPtr);
        ch.jniSelfPtr = 0L;
    }

    IovArray cleanArray() {
        this.iovArray.clear();
        return this.iovArray;
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
            this.wakeup();
        }
    }

    private void wakeup() {
        Native.keventTriggerUserEvent(this.kqueueFd.intValue(), 0);
    }

    private int kqueueWait(boolean oldWakeup) throws IOException {
        if (oldWakeup && this.hasTasks()) {
            return this.kqueueWaitNow();
        }
        long totalDelay = this.delayNanos(System.nanoTime());
        int delaySeconds = (int)Math.min(totalDelay / 1000000000L, Integer.MAX_VALUE);
        return this.kqueueWait(delaySeconds, (int)Math.min(totalDelay - (long)delaySeconds * 1000000000L, Integer.MAX_VALUE));
    }

    private int kqueueWaitNow() throws IOException {
        return this.kqueueWait(0, 0);
    }

    private int kqueueWait(int timeoutSec, int timeoutNs) throws IOException {
        this.deleteJniChannelPointers();
        int numEvents = Native.keventWait(this.kqueueFd.intValue(), this.changeList, this.eventList, timeoutSec, timeoutNs);
        this.changeList.clear();
        return numEvents;
    }

    private void deleteJniChannelPointers() {
        if (!this.jniChannelPointers.isEmpty()) {
            KQueueEventArray.deleteGlobalRefs(this.jniChannelPointers.memoryAddress(), this.jniChannelPointers.memoryAddressEnd());
            this.jniChannelPointers.clear();
        }
    }

    private void processReady(int ready) {
        for (int i = 0; i < ready; ++i) {
            short filter = this.eventList.filter(i);
            short flags = this.eventList.flags(i);
            if (filter == Native.EVFILT_USER || (flags & Native.EV_ERROR) != 0) {
                assert (filter != Native.EVFILT_USER || filter == Native.EVFILT_USER && this.eventList.fd(i) == 0);
                continue;
            }
            AbstractKQueueChannel channel = this.eventList.channel(i);
            if (channel == null) {
                logger.warn("events[{}]=[{}, {}] had no channel!", i, this.eventList.fd(i), filter);
                continue;
            }
            AbstractKQueueChannel.AbstractKQueueUnsafe unsafe = (AbstractKQueueChannel.AbstractKQueueUnsafe)channel.unsafe();
            if (filter == Native.EVFILT_WRITE) {
                unsafe.writeReady();
            } else if (filter == Native.EVFILT_READ) {
                unsafe.readReady(this.eventList.data(i));
            } else if (filter == Native.EVFILT_SOCK && (this.eventList.fflags(i) & Native.NOTE_RDHUP) != 0) {
                unsafe.readEOF();
            }
            if ((flags & Native.EV_EOF) == 0) continue;
            unsafe.readEOF();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    protected void run() {
        do {
            try {
                block15 : do {
                    strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                    switch (strategy) {
                        case -2: {
                            continue block15;
                        }
                        case -1: {
                            strategy = this.kqueueWait(KQueueEventLoop.WAKEN_UP_UPDATER.getAndSet(this, 0) == 1);
                            if (this.wakenUp != 1) break block15;
                            this.wakeup();
                        }
                    }
                    break;
                } while (true);
                ioRatio = this.ioRatio;
                if (ioRatio == 100) {
                    try {
                        if (strategy <= 0) ** GOTO lbl30
                        this.processReady(strategy);
                    }
                    finally {
                        this.runAllTasks();
                    }
                } else {
                    ioStartTime = System.nanoTime();
                    try {
                        if (strategy > 0) {
                            this.processReady(strategy);
                        }
                    }
                    finally {
                        ioTime = System.nanoTime() - ioStartTime;
                        this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                    }
                }
                if (this.allowGrowing && strategy == this.eventList.capacity()) {
                    this.eventList.realloc(false);
                }
            }
            catch (Throwable t) {
                KQueueEventLoop.handleLoopException(t);
            }
            try {
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
                return;
            }
            catch (Throwable t) {
                KQueueEventLoop.handleLoopException(t);
                continue;
            }
            break;
        } while (true);
    }

    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(maxPendingTasks);
    }

    @Override
    public int pendingTasks() {
        return this.inEventLoop() ? super.pendingTasks() : ((Integer)this.submit(this.pendingTasksCallable).syncUninterruptibly().getNow()).intValue();
    }

    public int getIoRatio() {
        return this.ioRatio;
    }

    public void setIoRatio(int ioRatio) {
        if (ioRatio <= 0 || ioRatio > 100) {
            throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
        }
        this.ioRatio = ioRatio;
    }

    @Override
    protected void cleanup() {
        try {
            try {
                this.kqueueFd.close();
            }
            catch (IOException e) {
                logger.warn("Failed to close the kqueue fd.", e);
            }
        }
        finally {
            this.deleteJniChannelPointers();
            this.jniChannelPointers.free();
            this.changeList.free();
            this.eventList.free();
        }
    }

    private void closeAll() {
        try {
            this.kqueueWaitNow();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static void handleLoopException(Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    static {
        KQueue.ensureAvailability();
    }

}

