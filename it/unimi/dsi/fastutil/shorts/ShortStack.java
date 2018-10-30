/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Stack;

public interface ShortStack
extends Stack<Short> {
    @Override
    public void push(short var1);

    public short popShort();

    public short topShort();

    public short peekShort(int var1);

    @Deprecated
    @Override
    default public void push(Short o) {
        this.push((short)o);
    }

    @Deprecated
    @Override
    default public Short pop() {
        return this.popShort();
    }

    @Deprecated
    @Override
    default public Short top() {
        return this.topShort();
    }

    @Deprecated
    @Override
    default public Short peek(int i) {
        return this.peekShort(i);
    }
}

