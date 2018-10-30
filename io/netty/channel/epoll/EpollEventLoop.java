/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventArray;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class EpollEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
    private static final AtomicIntegerFieldUpdater<EpollEventLoop> WAKEN_UP_UPDATER = AtomicIntegerFieldUpdater.newUpdater(EpollEventLoop.class, "wakenUp");
    private final FileDescriptor epollFd;
    private final FileDescriptor eventFd;
    private final FileDescriptor timerFd;
    private final IntObjectMap<AbstractEpollChannel> channels;
    private final boolean allowGrowing;
    private final EpollEventArray events;
    private final IovArray iovArray;
    private final SelectStrategy selectStrategy;
    private final IntSupplier selectNowSupplier;
    private final Callable<Integer> pendingTasksCallable;
    private volatile int wakenUp;
    private volatile int ioRatio;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    EpollEventLoop(EventLoopGroup parent, Executor executor, int maxEvents, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
        this.channels = new IntObjectHashMap<AbstractEpollChannel>(4096);
        this.iovArray = new IovArray();
        this.selectNowSupplier = new IntSupplier(){

            @Override
            public int get() throws Exception {
                return EpollEventLoop.this.epollWaitNow();
            }
        };
        this.pendingTasksCallable = new Callable<Integer>(){

            @Override
            public Integer call() throws Exception {
                return EpollEventLoop.super.pendingTasks();
            }
        };
        this.ioRatio = 50;
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
        if (maxEvents == 0) {
            this.allowGrowing = true;
            this.events = new EpollEventArray(4096);
        } else {
            this.allowGrowing = false;
            this.events = new EpollEventArray(maxEvents);
        }
        boolean success = false;
        FileDescriptor epollFd = null;
        FileDescriptor eventFd = null;
        FileDescriptor timerFd = null;
        try {
            this.epollFd = epollFd = Native.newEpollCreate();
            this.eventFd = eventFd = Native.newEventFd();
            try {
                Native.epollCtlAdd(epollFd.intValue(), eventFd.intValue(), Native.EPOLLIN);
            }
            catch (IOException e) {
                throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", e);
            }
            this.timerFd = timerFd = Native.newTimerFd();
            try {
                Native.epollCtlAdd(epollFd.intValue(), timerFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            }
            catch (IOException e) {
                throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", e);
            }
            success = true;
        }
        finally {
            if (!success) {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (Exception e) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (Exception e) {}
                }
                if (timerFd != null) {
                    try {
                        timerFd.close();
                    }
                    catch (Exception e) {}
                }
            }
        }
    }

    IovArray cleanArray() {
        this.iovArray.clear();
        return this.iovArray;
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
            Native.eventFdWrite(this.eventFd.intValue(), 1L);
        }
    }

    void add(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        int fd = ch.socket.intValue();
        Native.epollCtlAdd(this.epollFd.intValue(), fd, ch.flags);
        this.channels.put(fd, ch);
    }

    void modify(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        Native.epollCtlMod(this.epollFd.intValue(), ch.socket.intValue(), ch.flags);
    }

    void remove(AbstractEpollChannel ch) throws IOException {
        int fd;
        assert (this.inEventLoop());
        if (ch.isOpen() && this.channels.remove(fd = ch.socket.intValue()) != null) {
            Native.epollCtlDel(this.epollFd.intValue(), ch.fd().intValue());
        }
    }

    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(maxPendingTasks);
    }

    @Override
    public int pendingTasks() {
        if (this.inEventLoop()) {
            return super.pendingTasks();
        }
        return (Integer)this.submit(this.pendingTasksCallable).syncUninterruptibly().getNow();
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

    private int epollWait(boolean oldWakeup) throws IOException {
        if (oldWakeup && this.hasTasks()) {
            return this.epollWaitNow();
        }
        long totalDelay = this.delayNanos(System.nanoTime());
        int delaySeconds = (int)Math.min(totalDelay / 1000000000L, Integer.MAX_VALUE);
        return Native.epollWait(this.epollFd, this.events, this.timerFd, delaySeconds, (int)Math.min(totalDelay - (long)delaySeconds * 1000000000L, Integer.MAX_VALUE));
    }

    private int epollWaitNow() throws IOException {
        return Native.epollWait(this.epollFd, this.events, this.timerFd, 0, 0);
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
                            strategy = this.epollWait(EpollEventLoop.WAKEN_UP_UPDATER.getAndSet(this, 0) == 1);
                            if (this.wakenUp != 1) break block15;
                            Native.eventFdWrite(this.eventFd.intValue(), 1L);
                        }
                    }
                    break;
                } while (true);
                ioRatio = this.ioRatio;
                if (ioRatio == 100) {
                    try {
                        if (strategy <= 0) ** GOTO lbl30
                        this.processReady(this.events, strategy);
                    }
                    finally {
                        this.runAllTasks();
                    }
                } else {
                    ioStartTime = System.nanoTime();
                    try {
                        if (strategy > 0) {
                            this.processReady(this.events, strategy);
                        }
                    }
                    finally {
                        ioTime = System.nanoTime() - ioStartTime;
                        this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                    }
                }
                if (this.allowGrowing && strategy == this.events.length()) {
                    this.events.increase();
                }
            }
            catch (Throwable t) {
                EpollEventLoop.handleLoopException(t);
            }
            try {
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
                return;
            }
            catch (Throwable t) {
                EpollEventLoop.handleLoopException(t);
                continue;
            }
            break;
        } while (true);
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

    private void closeAll() {
        try {
            this.epollWaitNow();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        ArrayList<AbstractEpollChannel> array = new ArrayList<AbstractEpollChannel>(this.channels.size());
        for (AbstractEpollChannel channel : this.channels.values()) {
            array.add(channel);
        }
        for (AbstractEpollChannel ch : array) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }

    private void processReady(EpollEventArray events, int ready) {
        for (int i = 0; i < ready; ++i) {
            int fd = events.fd(i);
            if (fd == this.eventFd.intValue()) {
                Native.eventFdRead(fd);
                continue;
            }
            if (fd == this.timerFd.intValue()) {
                Native.timerFdRead(fd);
                continue;
            }
            long ev = events.events(i);
            AbstractEpollChannel ch = this.channels.get(fd);
            if (ch != null) {
                AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
                if ((ev & (long)(Native.EPOLLERR | Native.EPOLLOUT)) != 0L) {
                    unsafe.epollOutReady();
                }
                if ((ev & (long)(Native.EPOLLERR | Native.EPOLLIN)) != 0L) {
                    unsafe.epollInReady();
                }
                if ((ev & (long)Native.EPOLLRDHUP) == 0L) continue;
                unsafe.epollRdHupReady();
                continue;
            }
            try {
                Native.epollCtlDel(this.epollFd.intValue(), fd);
                continue;
            }
            catch (IOException unsafe) {
                // empty catch block
            }
        }
    }

    @Override
    protected void cleanup() {
        try {
            try {
                this.epollFd.close();
            }
            catch (IOException e) {
                logger.warn("Failed to close the epoll fd.", e);
            }
            try {
                this.eventFd.close();
            }
            catch (IOException e) {
                logger.warn("Failed to close the event fd.", e);
            }
            try {
                this.timerFd.close();
            }
            catch (IOException e) {
                logger.warn("Failed to close the timer fd.", e);
            }
        }
        finally {
            this.iovArray.release();
            this.events.free();
        }
    }

    static {
        Epoll.ensureAvailability();
    }

}

