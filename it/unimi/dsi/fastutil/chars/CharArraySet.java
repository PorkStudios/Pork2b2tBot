/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharArraySet
extends AbstractCharSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] a;
    private int size;

    public CharArraySet(char[] a) {
        this.a = a;
        this.size = a.length;
    }

    public CharArraySet() {
        this.a = CharArrays.EMPTY_ARRAY;
    }

    public CharArraySet(int capacity) {
        this.a = new char[capacity];
    }

    public CharArraySet(CharCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public CharArraySet(Collection<? extends Character> c) {
        this(c.size());
        this.addAll(c);
    }

    public CharArraySet(char[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    private int findKey(char o) {
        int i = this.size;
        while (i-- != 0) {
            if (this.a[i] != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public CharIterator iterator() {
        return new CharIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < CharArraySet.this.size;
            }

            @Override
            public char nextChar() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return CharArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = CharArraySet.this.size-- - this.next--;
                System.arraycopy(CharArraySet.this.a, this.next + 1, CharArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(char k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(char k) {
        int pos = this.findKey(k);
        if (pos == -1) {
            return false;
        }
        int tail = this.size - pos - 1;
        for (int i = 0; i < tail; ++i) {
            this.a[pos + i] = this.a[pos + i + 1];
        }
        --this.size;
        return true;
    }

    @Override
    public boolean add(char k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            char[] b = new char[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;
            while (i-- != 0) {
                b[i] = this.a[i];
            }
            this.a = b;
        }
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public CharArraySet clone() {
        CharArraySet c;
        try {
            c = (CharArraySet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (char[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new char[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readChar();
        }
    }

}

