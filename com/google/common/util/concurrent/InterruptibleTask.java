/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.atomic.AtomicReference;

@GwtCompatible(emulated=true)
abstract class InterruptibleTask
extends AtomicReference<Thread>
implements Runnable {
    private volatile boolean doneInterrupting;

    InterruptibleTask() {
    }

    @Override
    public final void run() {
        if (!this.compareAndSet(null, Thread.currentThread())) {
            return;
        }
        try {
            this.runInterruptibly();
        }
        finally {
            if (this.wasInterrupted()) {
                while (!this.doneInterrupting) {
                    Thread.yield();
                }
            }
        }
    }

    abstract void runInterruptibly();

    abstract boolean wasInterrupted();

    final void interruptTask() {
        Thread currentRunner = (Thread)this.get();
        if (currentRunner != null) {
            currentRunner.interrupt();
        }
        this.doneInterrupting = true;
    }

    @Override
    public abstract String toString();
}

