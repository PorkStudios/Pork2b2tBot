/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Stack;

public interface LongStack
extends Stack<Long> {
    @Override
    public void push(long var1);

    public long popLong();

    public long topLong();

    public long peekLong(int var1);

    @Deprecated
    @Override
    default public void push(Long o) {
        this.push((long)o);
    }

    @Deprecated
    @Override
    default public Long pop() {
        return this.popLong();
    }

    @Deprecated
    @Override
    default public Long top() {
        return this.topLong();
    }

    @Deprecated
    @Override
    default public Long peek(int i) {
        return this.peekLong(i);
    }
}

