/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ThreadDeathWatcher {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadDeathWatcher.class);
    static final ThreadFactory threadFactory;
    private static final Queue<Entry> pendingEntries;
    private static final Watcher watcher;
    private static final AtomicBoolean started;
    private static volatile Thread watcherThread;

    public static void watch(Thread thread, Runnable task) {
        if (thread == null) {
            throw new NullPointerException("thread");
        }
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (!thread.isAlive()) {
            throw new IllegalArgumentException("thread must be alive.");
        }
        ThreadDeathWatcher.schedule(thread, task, true);
    }

    public static void unwatch(Thread thread, Runnable task) {
        if (thread == null) {
            throw new NullPointerException("thread");
        }
        if (task == null) {
            throw new NullPointerException("task");
        }
        ThreadDeathWatcher.schedule(thread, task, false);
    }

    private static void schedule(Thread thread, Runnable task, boolean isWatch) {
        pendingEntries.add(new Entry(thread, task, isWatch));
        if (started.compareAndSet(false, true)) {
            Thread watcherThread = threadFactory.newThread(watcher);
            watcherThread.setContextClassLoader(null);
            watcherThread.start();
            ThreadDeathWatcher.watcherThread = watcherThread;
        }
    }

    public static boolean awaitInactivity(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        Thread watcherThread = ThreadDeathWatcher.watcherThread;
        if (watcherThread != null) {
            watcherThread.join(unit.toMillis(timeout));
            return !watcherThread.isAlive();
        }
        return true;
    }

    private ThreadDeathWatcher() {
    }

    static /* synthetic */ AtomicBoolean access$200() {
        return started;
    }

    static {
        pendingEntries = new ConcurrentLinkedQueue<Entry>();
        watcher = new Watcher();
        started = new AtomicBoolean();
        String poolName = "threadDeathWatcher";
        String serviceThreadPrefix = SystemPropertyUtil.get("io.netty.serviceThreadPrefix");
        if (!StringUtil.isNullOrEmpty(serviceThreadPrefix)) {
            poolName = serviceThreadPrefix + poolName;
        }
        threadFactory = new DefaultThreadFactory(poolName, true, 1, null);
    }

    private static final class Entry {
        final Thread thread;
        final Runnable task;
        final boolean isWatch;

        Entry(Thread thread, Runnable task, boolean isWatch) {
            this.thread = thread;
            this.task = task;
            this.isWatch = isWatch;
        }

        public int hashCode() {
            return this.thread.hashCode() ^ this.task.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry that = (Entry)obj;
            return this.thread == that.thread && this.task == that.task;
        }
    }

    private static final class Watcher
    implements Runnable {
        private final List<Entry> watchees = new ArrayList<Entry>();

        private Watcher() {
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
            do lbl-1000: // 3 sources:
            {
                this.fetchWatchees();
                this.notifyWatchees();
                this.fetchWatchees();
                this.notifyWatchees();
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException var1_1) {
                    // empty catch block
                }
                if (!this.watchees.isEmpty() || !ThreadDeathWatcher.access$100().isEmpty()) ** GOTO lbl-1000
                stopped = ThreadDeathWatcher.access$200().compareAndSet(true, false);
                if (!Watcher.$assertionsDisabled && !stopped) {
                    throw new AssertionError();
                }
                if (!ThreadDeathWatcher.access$100().isEmpty()) continue;
                return;
            } while (ThreadDeathWatcher.access$200().compareAndSet(false, true));
        }

        private void fetchWatchees() {
            Entry e;
            while ((e = (Entry)pendingEntries.poll()) != null) {
                if (e.isWatch) {
                    this.watchees.add(e);
                    continue;
                }
                this.watchees.remove(e);
            }
        }

        private void notifyWatchees() {
            List<Entry> watchees = this.watchees;
            int i = 0;
            while (i < watchees.size()) {
                Entry e = watchees.get(i);
                if (!e.thread.isAlive()) {
                    watchees.remove(i);
                    try {
                        e.task.run();
                    }
                    catch (Throwable t) {
                        logger.warn("Thread death watcher task raised an exception:", t);
                    }
                    continue;
                }
                ++i;
            }
        }
    }

}

