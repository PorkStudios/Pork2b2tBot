/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
@GwtIncompatible
public interface Interner<E> {
    @CanIgnoreReturnValue
    public E intern(E var1);
}

