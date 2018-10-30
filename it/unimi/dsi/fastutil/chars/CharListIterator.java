/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import java.util.ListIterator;

public interface CharListIterator
extends CharBidirectionalIterator,
ListIterator<Character> {
    @Override
    default public void set(char k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(char k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Character k) {
        this.set(k.charValue());
    }

    @Deprecated
    @Override
    default public void add(Character k) {
        this.add(k.charValue());
    }

    @Deprecated
    @Override
    default public Character next() {
        return CharBidirectionalIterator.super.next();
    }

    @Deprecated
    @Override
    default public Character previous() {
        return CharBidirectionalIterator.super.previous();
    }
}

