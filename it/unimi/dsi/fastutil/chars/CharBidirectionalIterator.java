/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface CharBidirectionalIterator
extends CharIterator,
ObjectBidirectionalIterator<Character> {
    public char previousChar();

    @Deprecated
    @Override
    default public Character previous() {
        return Character.valueOf(this.previousChar());
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousChar();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return CharIterator.super.skip(n);
    }
}

