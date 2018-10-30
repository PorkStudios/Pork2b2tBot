/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
public final class SettableFuture<V>
extends AbstractFuture.TrustedFuture<V> {
    public static <V> SettableFuture<V> create() {
        return new SettableFuture<V>();
    }

    @CanIgnoreReturnValue
    @Override
    public boolean set(@Nullable V value) {
        return super.set(value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean setException(Throwable throwable) {
        return super.setException(throwable);
    }

    @Beta
    @CanIgnoreReturnValue
    @Override
    public boolean setFuture(ListenableFuture<? extends V> future) {
        return super.setFuture(future);
    }

    private SettableFuture() {
    }
}

