/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Stack;

public interface DoubleStack
extends Stack<Double> {
    @Override
    public void push(double var1);

    public double popDouble();

    public double topDouble();

    public double peekDouble(int var1);

    @Deprecated
    @Override
    default public void push(Double o) {
        this.push((double)o);
    }

    @Deprecated
    @Override
    default public Double pop() {
        return this.popDouble();
    }

    @Deprecated
    @Override
    default public Double top() {
        return this.topDouble();
    }

    @Deprecated
    @Override
    default public Double peek(int i) {
        return this.peekDouble(i);
    }
}

