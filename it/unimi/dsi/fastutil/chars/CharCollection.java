/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharIterable;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Collection;
import java.util.Iterator;

public interface CharCollection
extends Collection<Character>,
CharIterable {
    @Override
    public CharIterator iterator();

    @Override
    public boolean add(char var1);

    public boolean contains(char var1);

    public boolean rem(char var1);

    @Deprecated
    @Override
    public boolean add(Character var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);

    public char[] toCharArray();

    @Deprecated
    public char[] toCharArray(char[] var1);

    public char[] toArray(char[] var1);

    public boolean addAll(CharCollection var1);

    public boolean containsAll(CharCollection var1);

    public boolean removeAll(CharCollection var1);

    public boolean retainAll(CharCollection var1);
}

