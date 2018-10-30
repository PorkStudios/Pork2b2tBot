/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Stack;

public interface FloatStack
extends Stack<Float> {
    @Override
    public void push(float var1);

    public float popFloat();

    public float topFloat();

    public float peekFloat(int var1);

    @Deprecated
    @Override
    default public void push(Float o) {
        this.push(o.floatValue());
    }

    @Deprecated
    @Override
    default public Float pop() {
        return Float.valueOf(this.popFloat());
    }

    @Deprecated
    @Override
    default public Float top() {
        return Float.valueOf(this.topFloat());
    }

    @Deprecated
    @Override
    default public Float peek(int i) {
        return Float.valueOf(this.peekFloat(i));
    }
}

