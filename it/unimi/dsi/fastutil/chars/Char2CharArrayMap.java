/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2CharMap;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Char2CharArrayMap
extends AbstractChar2CharMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] key;
    private transient char[] value;
    private int size;

    public Char2CharArrayMap(char[] key, char[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Char2CharArrayMap() {
        this.key = CharArrays.EMPTY_ARRAY;
        this.value = CharArrays.EMPTY_ARRAY;
    }

    public Char2CharArrayMap(int capacity) {
        this.key = new char[capacity];
        this.value = new char[capacity];
    }

    public Char2CharArrayMap(Char2CharMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2CharArrayMap(Map<? extends Character, ? extends Character> m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2CharArrayMap(char[] key, char[] value, int size) {
        this.key = key;
        this.value = value;
        this.size = size;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
        if (size > key.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
        }
    }

    public Char2CharMap.FastEntrySet char2CharEntrySet() {
        return new EntrySet();
    }

    private int findKey(char k) {
        char[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public char get(char k) {
        char[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return this.value[i];
        }
        return this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean containsKey(char k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(char v) {
        int i = this.size;
        while (i-- != 0) {
            if (this.value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public char put(char k, char v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            char oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            char[] newValue = new char[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;
            while (i-- != 0) {
                newKey[i] = this.key[i];
                newValue[i] = this.value[i];
            }
            this.key = newKey;
            this.value = newValue;
        }
        this.key[this.size] = k;
        this.value[this.size] = v;
        ++this.size;
        return this.defRetValue;
    }

    @Override
    public char remove(char k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        char oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public CharSet keySet() {
        return new AbstractCharSet(){

            @Override
            public boolean contains(char k) {
                return Char2CharArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(char k) {
                int oldPos = Char2CharArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Char2CharArrayMap.this.size - oldPos - 1;
                System.arraycopy(Char2CharArrayMap.this.key, oldPos + 1, Char2CharArrayMap.this.key, oldPos, tail);
                System.arraycopy(Char2CharArrayMap.this.value, oldPos + 1, Char2CharArrayMap.this.value, oldPos, tail);
                Char2CharArrayMap.this.size--;
                return true;
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2CharArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2CharArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2CharArrayMap.this.size - this.pos;
                        System.arraycopy(Char2CharArrayMap.this.key, this.pos, Char2CharArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2CharArrayMap.this.value, this.pos, Char2CharArrayMap.this.value, this.pos - 1, tail);
                        Char2CharArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2CharArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2CharArrayMap.this.clear();
            }

        };
    }

    @Override
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char v) {
                return Char2CharArrayMap.this.containsValue(v);
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2CharArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2CharArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2CharArrayMap.this.size - this.pos;
                        System.arraycopy(Char2CharArrayMap.this.key, this.pos, Char2CharArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2CharArrayMap.this.value, this.pos, Char2CharArrayMap.this.value, this.pos - 1, tail);
                        Char2CharArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2CharArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2CharArrayMap.this.clear();
            }

        };
    }

    public Char2CharArrayMap clone() {
        Char2CharArrayMap c;
        try {
            c = (Char2CharArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
        c.value = (char[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.key[i]);
            s.writeChar(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new char[this.size];
        this.value = new char[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readChar();
            this.value[i] = s.readChar();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Char2CharMap.Entry>
    implements Char2CharMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Char2CharMap.Entry> iterator() {
            return new ObjectIterator<Char2CharMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Char2CharArrayMap.this.size;
                }

                @Override
                public Char2CharMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractChar2CharMap.BasicEntry(Char2CharArrayMap.this.key[this.curr], Char2CharArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2CharArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2CharArrayMap.this.key, this.next + 1, Char2CharArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2CharArrayMap.this.value, this.next + 1, Char2CharArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Char2CharMap.Entry> fastIterator() {
            return new ObjectIterator<Char2CharMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractChar2CharMap.BasicEntry entry = new AbstractChar2CharMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Char2CharArrayMap.this.size;
                }

                @Override
                public Char2CharMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Char2CharArrayMap.this.key[this.curr];
                    this.entry.value = Char2CharArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2CharArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2CharArrayMap.this.key, this.next + 1, Char2CharArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2CharArrayMap.this.value, this.next + 1, Char2CharArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Char2CharArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            return Char2CharArrayMap.this.containsKey(k) && Char2CharArrayMap.this.get(k) == ((Character)e.getValue()).charValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            char v = ((Character)e.getValue()).charValue();
            int oldPos = Char2CharArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Char2CharArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Char2CharArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2CharArrayMap.this.key, oldPos + 1, Char2CharArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2CharArrayMap.this.value, oldPos + 1, Char2CharArrayMap.this.value, oldPos, tail);
            Char2CharArrayMap.this.size--;
            return true;
        }

    }

}

