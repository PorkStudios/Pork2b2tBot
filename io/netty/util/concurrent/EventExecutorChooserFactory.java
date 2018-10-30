/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;

public interface EventExecutorChooserFactory {
    public EventExecutorChooser newChooser(EventExecutor[] var1);

    public static interface EventExecutorChooser {
        public EventExecutor next();
    }

}

