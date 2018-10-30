/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultEventExecutorChooserFactory
implements EventExecutorChooserFactory {
    public static final DefaultEventExecutorChooserFactory INSTANCE = new DefaultEventExecutorChooserFactory();

    private DefaultEventExecutorChooserFactory() {
    }

    @Override
    public EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] executors) {
        if (DefaultEventExecutorChooserFactory.isPowerOfTwo(executors.length)) {
            return new PowerOfTwoEventExecutorChooser(executors);
        }
        return new GenericEventExecutorChooser(executors);
    }

    private static boolean isPowerOfTwo(int val) {
        return (val & - val) == val;
    }

    private static final class GenericEventExecutorChooser
    implements EventExecutorChooserFactory.EventExecutorChooser {
        private final AtomicInteger idx = new AtomicInteger();
        private final EventExecutor[] executors;

        GenericEventExecutorChooser(EventExecutor[] executors) {
            this.executors = executors;
        }

        @Override
        public EventExecutor next() {
            return this.executors[Math.abs(this.idx.getAndIncrement() % this.executors.length)];
        }
    }

    private static final class PowerOfTwoEventExecutorChooser
    implements EventExecutorChooserFactory.EventExecutorChooser {
        private final AtomicInteger idx = new AtomicInteger();
        private final EventExecutor[] executors;

        PowerOfTwoEventExecutorChooser(EventExecutor[] executors) {
            this.executors = executors;
        }

        @Override
        public EventExecutor next() {
            return this.executors[this.idx.getAndIncrement() & this.executors.length - 1];
        }
    }

}

