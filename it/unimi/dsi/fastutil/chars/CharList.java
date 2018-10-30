/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface CharList
extends List<Character>,
Comparable<List<? extends Character>>,
CharCollection {
    @Override
    public CharListIterator iterator();

    public CharListIterator listIterator();

    public CharListIterator listIterator(int var1);

    public CharList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, char[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, char[] var2);

    public void addElements(int var1, char[] var2, int var3, int var4);

    @Override
    public boolean add(char var1);

    @Override
    public void add(int var1, char var2);

    @Deprecated
    @Override
    public void add(int var1, Character var2);

    public boolean addAll(int var1, CharCollection var2);

    public boolean addAll(int var1, CharList var2);

    public boolean addAll(CharList var1);

    @Override
    public char set(int var1, char var2);

    public char getChar(int var1);

    public int indexOf(char var1);

    public int lastIndexOf(char var1);

    @Deprecated
    @Override
    public Character get(int var1);

    @Deprecated
    @Override
    public int indexOf(Object var1);

    @Deprecated
    @Override
    public int lastIndexOf(Object var1);

    @Deprecated
    @Override
    public boolean add(Character var1);

    public char removeChar(int var1);

    @Deprecated
    @Override
    public Character remove(int var1);

    @Deprecated
    @Override
    public Character set(int var1, Character var2);
}

