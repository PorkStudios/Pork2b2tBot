/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioTask;
import io.netty.channel.nio.SelectedSelectionKeySet;
import io.netty.channel.nio.SelectedSelectionKeySetSelector;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger;
    private static final int CLEANUP_INTERVAL = 256;
    private static final boolean DISABLE_KEYSET_OPTIMIZATION;
    private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
    private final IntSupplier selectNowSupplier = new IntSupplier(){

        @Override
        public int get() throws Exception {
            return NioEventLoop.this.selectNow();
        }
    };
    private final Callable<Integer> pendingTasksCallable = new Callable<Integer>(){

        @Override
        public Integer call() throws Exception {
            return NioEventLoop.super.pendingTasks();
        }
    };
    private Selector selector;
    private Selector unwrappedSelector;
    private SelectedSelectionKeySet selectedKeys;
    private final SelectorProvider provider;
    private final AtomicBoolean wakenUp = new AtomicBoolean();
    private final SelectStrategy selectStrategy;
    private volatile int ioRatio = 50;
    private int cancelledKeys;
    private boolean needsToSelectAgain;

    NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventLoopGroup)parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
        if (selectorProvider == null) {
            throw new NullPointerException("selectorProvider");
        }
        if (strategy == null) {
            throw new NullPointerException("selectStrategy");
        }
        this.provider = selectorProvider;
        SelectorTuple selectorTuple = this.openSelector();
        this.selector = selectorTuple.selector;
        this.unwrappedSelector = selectorTuple.unwrappedSelector;
        this.selectStrategy = strategy;
    }

    private SelectorTuple openSelector() {
        AbstractSelector unwrappedSelector;
        try {
            unwrappedSelector = this.provider.openSelector();
        }
        catch (IOException e) {
            throw new ChannelException("failed to open a new selector", e);
        }
        if (DISABLE_KEYSET_OPTIMIZATION) {
            return new SelectorTuple(unwrappedSelector);
        }
        final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
        Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
                }
                catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (!(maybeSelectorImplClass instanceof Class) || !((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
            if (maybeSelectorImplClass instanceof Throwable) {
                Throwable t = (Throwable)maybeSelectorImplClass;
                logger.trace("failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)t);
            }
            return new SelectorTuple(unwrappedSelector);
        }
        final Class selectorImplClass = (Class)maybeSelectorImplClass;
        Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
                    Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
                    Throwable cause = ReflectionUtil.trySetAccessible(selectedKeysField);
                    if (cause != null) {
                        return cause;
                    }
                    cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField);
                    if (cause != null) {
                        return cause;
                    }
                    selectedKeysField.set(unwrappedSelector, selectedKeySet);
                    publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
                    return null;
                }
                catch (NoSuchFieldException e) {
                    return e;
                }
                catch (IllegalAccessException e) {
                    return e;
                }
            }
        });
        if (maybeException instanceof Exception) {
            this.selectedKeys = null;
            Exception e = (Exception)maybeException;
            logger.trace("failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)e);
            return new SelectorTuple(unwrappedSelector);
        }
        this.selectedKeys = selectedKeySet;
        logger.trace("instrumented a special java.util.Set into: {}", (Object)unwrappedSelector);
        return new SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
    }

    public SelectorProvider selectorProvider() {
        return this.provider;
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

    public void register(SelectableChannel ch, int interestOps, NioTask<?> task) {
        if (ch == null) {
            throw new NullPointerException("ch");
        }
        if (interestOps == 0) {
            throw new IllegalArgumentException("interestOps must be non-zero.");
        }
        if ((interestOps & ~ ch.validOps()) != 0) {
            throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')');
        }
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
        }
        try {
            ch.register(this.selector, interestOps, task);
        }
        catch (Exception e) {
            throw new EventLoopException("failed to register a channel", e);
        }
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

    public void rebuildSelector() {
        if (!this.inEventLoop()) {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    NioEventLoop.this.rebuildSelector0();
                }
            });
            return;
        }
        this.rebuildSelector0();
    }

    private void rebuildSelector0() {
        int nChannels;
        block10 : {
            Selector oldSelector = this.selector;
            if (oldSelector == null) {
                return;
            }
            try {
                SelectorTuple newSelectorTuple = this.openSelector();
            }
            catch (Exception e) {
                logger.warn("Failed to create a new Selector.", e);
                return;
            }
            nChannels = 0;
            for (SelectionKey key : oldSelector.keys()) {
                Object a = key.attachment();
                try {
                    if (!key.isValid() || key.channel().keyFor(newSelectorTuple.unwrappedSelector) != null) continue;
                    int interestOps = key.interestOps();
                    key.cancel();
                    SelectionKey newKey = key.channel().register(newSelectorTuple.unwrappedSelector, interestOps, a);
                    if (a instanceof AbstractNioChannel) {
                        ((AbstractNioChannel)a).selectionKey = newKey;
                    }
                    ++nChannels;
                }
                catch (Exception e) {
                    logger.warn("Failed to re-register a Channel to the new Selector.", e);
                    if (a instanceof AbstractNioChannel) {
                        AbstractNioChannel ch = (AbstractNioChannel)a;
                        ch.unsafe().close(ch.unsafe().voidPromise());
                        continue;
                    }
                    NioTask task = (NioTask)a;
                    NioEventLoop.invokeChannelUnregistered(task, key, e);
                }
            }
            this.selector = newSelectorTuple.selector;
            this.unwrappedSelector = newSelectorTuple.unwrappedSelector;
            try {
                oldSelector.close();
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block10;
                logger.warn("Failed to close the old Selector.", t);
            }
        }
        logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void run() {
        do {
            block17 : {
                try {
                    block15 : do {
                        switch (this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks())) {
                            case -2: {
                                continue block15;
                            }
                            case -1: {
                                this.select(this.wakenUp.getAndSet(false));
                                if (!this.wakenUp.get()) break block15;
                                this.selector.wakeup();
                            }
                        }
                        break;
                    } while (true);
                    this.cancelledKeys = 0;
                    this.needsToSelectAgain = false;
                    int ioRatio = this.ioRatio;
                    if (ioRatio == 100) {
                        try {
                            this.processSelectedKeys();
                            break block17;
                        }
                        finally {
                            this.runAllTasks();
                        }
                    }
                    long ioStartTime = System.nanoTime();
                    try {
                        this.processSelectedKeys();
                    }
                    finally {
                        long ioTime = System.nanoTime() - ioStartTime;
                        this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                    }
                }
                catch (Throwable t) {
                    NioEventLoop.handleLoopException(t);
                }
            }
            try {
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
                return;
            }
            catch (Throwable t) {
                NioEventLoop.handleLoopException(t);
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

    private void processSelectedKeys() {
        if (this.selectedKeys != null) {
            this.processSelectedKeysOptimized();
        } else {
            this.processSelectedKeysPlain(this.selector.selectedKeys());
        }
    }

    @Override
    protected void cleanup() {
        try {
            this.selector.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a selector.", e);
        }
    }

    void cancel(SelectionKey key) {
        key.cancel();
        ++this.cancelledKeys;
        if (this.cancelledKeys >= 256) {
            this.cancelledKeys = 0;
            this.needsToSelectAgain = true;
        }
    }

    @Override
    protected Runnable pollTask() {
        Runnable task = super.pollTask();
        if (this.needsToSelectAgain) {
            this.selectAgain();
        }
        return task;
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        do {
            SelectionKey k = i.next();
            Object a = k.attachment();
            i.remove();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey(k, (AbstractNioChannel)a);
            } else {
                NioTask task = (NioTask)a;
                NioEventLoop.processSelectedKey(k, task);
            }
            if (!i.hasNext()) break;
            if (!this.needsToSelectAgain) continue;
            this.selectAgain();
            selectedKeys = this.selector.selectedKeys();
            if (selectedKeys.isEmpty()) break;
            i = selectedKeys.iterator();
        } while (true);
    }

    private void processSelectedKeysOptimized() {
        for (int i = 0; i < this.selectedKeys.size; ++i) {
            SelectionKey k = this.selectedKeys.keys[i];
            this.selectedKeys.keys[i] = null;
            Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey(k, (AbstractNioChannel)a);
            } else {
                NioTask task = (NioTask)a;
                NioEventLoop.processSelectedKey(k, task);
            }
            if (!this.needsToSelectAgain) continue;
            this.selectedKeys.reset(i + 1);
            this.selectAgain();
            i = -1;
        }
    }

    private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
        AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
        if (!k.isValid()) {
            NioEventLoop eventLoop;
            try {
                eventLoop = ch.eventLoop();
            }
            catch (Throwable ignored) {
                return;
            }
            if (eventLoop != this || eventLoop == null) {
                return;
            }
            unsafe.close(unsafe.voidPromise());
            return;
        }
        try {
            int readyOps = k.readyOps();
            if ((readyOps & 8) != 0) {
                int ops = k.interestOps();
                k.interestOps(ops &= -9);
                unsafe.finishConnect();
            }
            if ((readyOps & 4) != 0) {
                ch.unsafe().forceFlush();
            }
            if ((readyOps & 17) != 0 || readyOps == 0) {
                unsafe.read();
            }
        }
        catch (CancelledKeyException ignored) {
            unsafe.close(unsafe.voidPromise());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processSelectedKey(SelectionKey k, NioTask<SelectableChannel> task) {
        int state = 0;
        try {
            task.channelReady(k.channel(), k);
            state = 1;
        }
        catch (Exception e) {
            k.cancel();
            NioEventLoop.invokeChannelUnregistered(task, k, e);
            state = 2;
        }
        finally {
            switch (state) {
                case 0: {
                    k.cancel();
                    NioEventLoop.invokeChannelUnregistered(task, k, null);
                    break;
                }
                case 1: {
                    if (k.isValid()) break;
                    NioEventLoop.invokeChannelUnregistered(task, k, null);
                }
            }
        }
    }

    private void closeAll() {
        this.selectAgain();
        Set<SelectionKey> keys = this.selector.keys();
        ArrayList<AbstractNioChannel> channels = new ArrayList<AbstractNioChannel>(keys.size());
        for (SelectionKey k : keys) {
            Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                channels.add((AbstractNioChannel)a);
                continue;
            }
            k.cancel();
            NioTask task = (NioTask)a;
            NioEventLoop.invokeChannelUnregistered(task, k, null);
        }
        for (AbstractNioChannel ch : channels) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }

    private static void invokeChannelUnregistered(NioTask<SelectableChannel> task, SelectionKey k, Throwable cause) {
        try {
            task.channelUnregistered(k.channel(), cause);
        }
        catch (Exception e) {
            logger.warn("Unexpected exception while running NioTask.channelUnregistered()", e);
        }
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && this.wakenUp.compareAndSet(false, true)) {
            this.selector.wakeup();
        }
    }

    Selector unwrappedSelector() {
        return this.unwrappedSelector;
    }

    int selectNow() throws IOException {
        try {
            int n = this.selector.selectNow();
            return n;
        }
        finally {
            if (this.wakenUp.get()) {
                this.selector.wakeup();
            }
        }
    }

    private void select(boolean oldWakenUp) throws IOException {
        block11 : {
            Selector selector = this.selector;
            try {
                int selectCnt = 0;
                long currentTimeNanos = System.nanoTime();
                long selectDeadLineNanos = currentTimeNanos + this.delayNanos(currentTimeNanos);
                do {
                    long timeoutMillis;
                    if ((timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L) <= 0L) {
                        if (selectCnt != 0) break;
                        selector.selectNow();
                        selectCnt = 1;
                        break;
                    }
                    if (this.hasTasks() && this.wakenUp.compareAndSet(false, true)) {
                        selector.selectNow();
                        selectCnt = 1;
                        break;
                    }
                    int selectedKeys = selector.select(timeoutMillis);
                    ++selectCnt;
                    if (selectedKeys != 0 || oldWakenUp || this.wakenUp.get() || this.hasTasks() || this.hasScheduledTasks()) break;
                    if (Thread.interrupted()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
                        }
                        selectCnt = 1;
                        break;
                    }
                    long time = System.nanoTime();
                    if (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
                        selectCnt = 1;
                    } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                        logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", (Object)selectCnt, (Object)selector);
                        this.rebuildSelector();
                        selector = this.selector;
                        selector.selectNow();
                        selectCnt = 1;
                        break;
                    }
                    currentTimeNanos = time;
                } while (true);
                if (selectCnt > 3 && logger.isDebugEnabled()) {
                    logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", (Object)(selectCnt - 1), (Object)selector);
                }
            }
            catch (CancelledKeyException e) {
                if (!logger.isDebugEnabled()) break block11;
                logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", (Object)selector, (Object)e);
            }
        }
    }

    private void selectAgain() {
        this.needsToSelectAgain = false;
        try {
            this.selector.selectNow();
        }
        catch (Throwable t) {
            logger.warn("Failed to update SelectionKeys.", t);
        }
    }

    static {
        int selectorAutoRebuildThreshold;
        logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
        DISABLE_KEYSET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
        String key = "sun.nio.ch.bugLevel";
        String buglevel = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
        if (buglevel == null) {
            try {
                AccessController.doPrivileged(new PrivilegedAction<Void>(){

                    @Override
                    public Void run() {
                        System.setProperty("sun.nio.ch.bugLevel", "");
                        return null;
                    }
                });
            }
            catch (SecurityException e) {
                logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", e);
            }
        }
        if ((selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512)) < 3) {
            selectorAutoRebuildThreshold = 0;
        }
        SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object)DISABLE_KEYSET_OPTIMIZATION);
            logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)SELECTOR_AUTO_REBUILD_THRESHOLD);
        }
    }

    private static final class SelectorTuple {
        final Selector unwrappedSelector;
        final Selector selector;

        SelectorTuple(Selector unwrappedSelector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = unwrappedSelector;
        }

        SelectorTuple(Selector unwrappedSelector, Selector selector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = selector;
        }
    }

}

