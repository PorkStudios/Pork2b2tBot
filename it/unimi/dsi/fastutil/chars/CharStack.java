/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Stack;

public interface CharStack
extends Stack<Character> {
    @Override
    public void push(char var1);

    public char popChar();

    public char topChar();

    public char peekChar(int var1);

    @Deprecated
    @Override
    default public void push(Character o) {
        this.push(o.charValue());
    }

    @Deprecated
    @Override
    default public Character pop() {
        return Character.valueOf(this.popChar());
    }

    @Deprecated
    @Override
    default public Character top() {
        return Character.valueOf(this.topChar());
    }

    @Deprecated
    @Override
    default public Character peek(int i) {
        return Character.valueOf(this.peekChar(i));
    }
}

