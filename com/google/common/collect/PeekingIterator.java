/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;

@GwtCompatible
public interface PeekingIterator<E>
extends Iterator<E> {
    public E peek();

    @CanIgnoreReturnValue
    @Override
    public E next();

    @Override
    public void remove();
}

