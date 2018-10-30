/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Iterator;
import java.util.Set;

public interface CharSet
extends CharCollection,
Set<Character> {
    @Override
    public CharIterator iterator();

    public boolean remove(char var1);

    @Deprecated
    @Override
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
}

