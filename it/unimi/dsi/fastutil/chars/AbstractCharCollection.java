/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractCharCollection
extends AbstractCollection<Character>
implements CharCollection {
    protected AbstractCharCollection() {
    }

    @Override
    public abstract CharIterator iterator();

    @Override
    public boolean add(char k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(char k) {
        CharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextChar()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(char k) {
        CharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextChar()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Deprecated
    @Override
    public boolean add(Character o) {
        return this.add(o.charValue());
    }

    @Deprecated
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains(((Character)o).charValue());
    }

    @Deprecated
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        return this.rem(((Character)o).charValue());
    }

    @Override
    public char[] toArray(char[] a) {
        if (a == null || a.length < this.size()) {
            a = new char[this.size()];
        }
        CharIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public char[] toCharArray() {
        return this.toCharArray(null);
    }

    @Deprecated
    @Override
    public char[] toCharArray(char[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(CharCollection c) {
        boolean retVal = false;
        CharIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextChar())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(CharCollection c) {
        CharIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextChar())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(CharCollection c) {
        boolean retVal = false;
        CharIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextChar())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(CharCollection c) {
        boolean retVal = false;
        CharIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextChar())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        CharIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            char k = i.nextChar();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

