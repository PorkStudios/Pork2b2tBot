/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.chars.CharComparator;
import java.util.Comparator;

public interface CharPriorityQueue
extends PriorityQueue<Character> {
    @Override
    public void enqueue(char var1);

    public char dequeueChar();

    public char firstChar();

    default public char lastChar() {
        throw new UnsupportedOperationException();
    }

    public CharComparator comparator();

    @Deprecated
    @Override
    default public void enqueue(Character x) {
        this.enqueue(x.charValue());
    }

    @Deprecated
    @Override
    default public Character dequeue() {
        return Character.valueOf(this.dequeueChar());
    }

    @Deprecated
    @Override
    default public Character first() {
        return Character.valueOf(this.firstChar());
    }

    @Deprecated
    @Override
    default public Character last() {
        return Character.valueOf(this.lastChar());
    }
}

