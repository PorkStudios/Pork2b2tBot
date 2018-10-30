/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Stack;

public interface IntStack
extends Stack<Integer> {
    @Override
    public void push(int var1);

    public int popInt();

    public int topInt();

    public int peekInt(int var1);

    @Deprecated
    @Override
    default public void push(Integer o) {
        this.push((int)o);
    }

    @Deprecated
    @Override
    default public Integer pop() {
        return this.popInt();
    }

    @Deprecated
    @Override
    default public Integer top() {
        return this.topInt();
    }

    @Deprecated
    @Override
    default public Integer peek(int i) {
        return this.peekInt(i);
    }
}

