/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@GwtIncompatible
final class SerializingExecutor
implements Executor {
    private static final Logger log = Logger.getLogger(SerializingExecutor.class.getName());
    private final Executor executor;
    @GuardedBy(value="queue")
    private final Deque<Runnable> queue = new ArrayDeque<Runnable>();
    @GuardedBy(value="queue")
    private boolean isWorkerRunning = false;
    @GuardedBy(value="queue")
    private int suspensions = 0;
    private final QueueWorker worker = new QueueWorker();

    public SerializingExecutor(Executor executor) {
        this.executor = Preconditions.checkNotNull(executor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Runnable task) {
        Deque<Runnable> deque = this.queue;
        synchronized (deque) {
            this.queue.addLast(task);
            if (this.isWorkerRunning || this.suspensions > 0) {
                return;
            }
            this.isWorkerRunning = true;
        }
        this.startQueueWorker();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeFirst(Runnable task) {
        Deque<Runnable> deque = this.queue;
        synchronized (deque) {
            this.queue.addFirst(task);
            if (this.isWorkerRunning || this.suspensions > 0) {
                return;
            }
            this.isWorkerRunning = true;
        }
        this.startQueueWorker();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void suspend() {
        Deque<Runnable> deque = this.queue;
        synchronized (deque) {
            ++this.suspensions;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resume() {
        Deque<Runnable> deque = this.queue;
        synchronized (deque) {
            Preconditions.checkState(this.suspensions > 0);
            --this.suspensions;
            if (this.isWorkerRunning || this.suspensions > 0 || this.queue.isEmpty()) {
                return;
            }
            this.isWorkerRunning = true;
        }
        this.startQueueWorker();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startQueueWorker() {
        boolean executionRejected = true;
        try {
            this.executor.execute(this.worker);
            executionRejected = false;
        }
        finally {
            if (executionRejected) {
                Deque<Runnable> deque = this.queue;
                synchronized (deque) {
                    this.isWorkerRunning = false;
                }
            }
        }
    }

    private final class QueueWorker
    implements Runnable {
        private QueueWorker() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                this.workOnQueue();
            }
            catch (Error e) {
                Deque deque = SerializingExecutor.this.queue;
                synchronized (deque) {
                    SerializingExecutor.this.isWorkerRunning = false;
                }
                throw e;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void workOnQueue() {
            do {
                Runnable task = null;
                Deque deque = SerializingExecutor.this.queue;
                synchronized (deque) {
                    if (SerializingExecutor.this.suspensions == 0) {
                        task = (Runnable)SerializingExecutor.this.queue.pollFirst();
                    }
                    if (task == null) {
                        SerializingExecutor.this.isWorkerRunning = false;
                        return;
                    }
                }
                try {
                    task.run();
                    continue;
                }
                catch (RuntimeException e) {
                    log.log(Level.SEVERE, "Exception while executing runnable " + task, e);
                    continue;
                }
                break;
            } while (true);
        }
    }

}

