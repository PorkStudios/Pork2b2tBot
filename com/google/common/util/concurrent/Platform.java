/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class Platform {
    static boolean isInstanceOfThrowableClass(@Nullable Throwable t, Class<? extends Throwable> expectedClass) {
        return expectedClass.isInstance(t);
    }

    private Platform() {
    }
}

