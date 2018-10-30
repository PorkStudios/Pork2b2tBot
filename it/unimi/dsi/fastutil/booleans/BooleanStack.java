/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Stack;

public interface BooleanStack
extends Stack<Boolean> {
    @Override
    public void push(boolean var1);

    public boolean popBoolean();

    public boolean topBoolean();

    public boolean peekBoolean(int var1);

    @Deprecated
    @Override
    default public void push(Boolean o) {
        this.push((boolean)o);
    }

    @Deprecated
    @Override
    default public Boolean pop() {
        return this.popBoolean();
    }

    @Deprecated
    @Override
    default public Boolean top() {
        return this.topBoolean();
    }

    @Deprecated
    @Override
    default public Boolean peek(int i) {
        return this.peekBoolean(i);
    }
}

