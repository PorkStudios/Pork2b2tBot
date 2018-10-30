/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableListIterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;

@GwtCompatible(serializable=true, emulated=true)
class RegularImmutableList<E>
extends ImmutableList<E> {
    static final ImmutableList<Object> EMPTY = new RegularImmutableList<Object>(new Object[0]);
    private final transient Object[] array;

    RegularImmutableList(Object[] array) {
        this.array = array;
    }

    @Override
    public int size() {
        return this.array.length;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    int copyIntoArray(Object[] dst, int dstOff) {
        System.arraycopy(this.array, 0, dst, dstOff, this.array.length);
        return dstOff + this.array.length;
    }

    @Override
    public E get(int index) {
        return (E)this.array[index];
    }

    @Override
    public UnmodifiableListIterator<E> listIterator(int index) {
        return Iterators.forArray(this.array, 0, this.array.length, index);
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this.array, 1296);
    }
}

