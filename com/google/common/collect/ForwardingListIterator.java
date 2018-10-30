/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.ListIterator;

@GwtCompatible
public abstract class ForwardingListIterator<E>
extends ForwardingIterator<E>
implements ListIterator<E> {
    protected ForwardingListIterator() {
    }

    @Override
    protected abstract ListIterator<E> delegate();

    @Override
    public void add(E element) {
        this.delegate().add(element);
    }

    @Override
    public boolean hasPrevious() {
        return this.delegate().hasPrevious();
    }

    @Override
    public int nextIndex() {
        return this.delegate().nextIndex();
    }

    @CanIgnoreReturnValue
    @Override
    public E previous() {
        return this.delegate().previous();
    }

    @Override
    public int previousIndex() {
        return this.delegate().previousIndex();
    }

    @Override
    public void set(E element) {
        this.delegate().set(element);
    }
}

