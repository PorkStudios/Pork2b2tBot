/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2IntMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
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

public class Char2IntArrayMap
extends AbstractChar2IntMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] key;
    private transient int[] value;
    private int size;

    public Char2IntArrayMap(char[] key, int[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Char2IntArrayMap() {
        this.key = CharArrays.EMPTY_ARRAY;
        this.value = IntArrays.EMPTY_ARRAY;
    }

    public Char2IntArrayMap(int capacity) {
        this.key = new char[capacity];
        this.value = new int[capacity];
    }

    public Char2IntArrayMap(Char2IntMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2IntArrayMap(Map<? extends Character, ? extends Integer> m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2IntArrayMap(char[] key, int[] value, int size) {
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

    public Char2IntMap.FastEntrySet char2IntEntrySet() {
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
    public int get(char k) {
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
    public boolean containsValue(int v) {
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
    public int put(char k, int v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            int oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            int[] newValue = new int[this.size == 0 ? 2 : this.size * 2];
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
    public int remove(char k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        int oldValue = this.value[oldPos];
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
                return Char2IntArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(char k) {
                int oldPos = Char2IntArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Char2IntArrayMap.this.size - oldPos - 1;
                System.arraycopy(Char2IntArrayMap.this.key, oldPos + 1, Char2IntArrayMap.this.key, oldPos, tail);
                System.arraycopy(Char2IntArrayMap.this.value, oldPos + 1, Char2IntArrayMap.this.value, oldPos, tail);
                Char2IntArrayMap.this.size--;
                return true;
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2IntArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2IntArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2IntArrayMap.this.size - this.pos;
                        System.arraycopy(Char2IntArrayMap.this.key, this.pos, Char2IntArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2IntArrayMap.this.value, this.pos, Char2IntArrayMap.this.value, this.pos - 1, tail);
                        Char2IntArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2IntArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2IntArrayMap.this.clear();
            }

        };
    }

    @Override
    public IntCollection values() {
        return new AbstractIntCollection(){

            @Override
            public boolean contains(int v) {
                return Char2IntArrayMap.this.containsValue(v);
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2IntArrayMap.this.size;
                    }

                    @Override
                    public int nextInt() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2IntArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2IntArrayMap.this.size - this.pos;
                        System.arraycopy(Char2IntArrayMap.this.key, this.pos, Char2IntArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2IntArrayMap.this.value, this.pos, Char2IntArrayMap.this.value, this.pos - 1, tail);
                        Char2IntArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2IntArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2IntArrayMap.this.clear();
            }

        };
    }

    public Char2IntArrayMap clone() {
        Char2IntArrayMap c;
        try {
            c = (Char2IntArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
        c.value = (int[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.key[i]);
            s.writeInt(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new char[this.size];
        this.value = new int[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readChar();
            this.value[i] = s.readInt();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Char2IntMap.Entry>
    implements Char2IntMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Char2IntMap.Entry> iterator() {
            return new ObjectIterator<Char2IntMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Char2IntArrayMap.this.size;
                }

                @Override
                public Char2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractChar2IntMap.BasicEntry(Char2IntArrayMap.this.key[this.curr], Char2IntArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2IntArrayMap.this.key, this.next + 1, Char2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2IntArrayMap.this.value, this.next + 1, Char2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Char2IntMap.Entry> fastIterator() {
            return new ObjectIterator<Char2IntMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractChar2IntMap.BasicEntry entry = new AbstractChar2IntMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Char2IntArrayMap.this.size;
                }

                @Override
                public Char2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Char2IntArrayMap.this.key[this.curr];
                    this.entry.value = Char2IntArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2IntArrayMap.this.key, this.next + 1, Char2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2IntArrayMap.this.value, this.next + 1, Char2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Char2IntArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            return Char2IntArrayMap.this.containsKey(k) && Char2IntArrayMap.this.get(k) == ((Integer)e.getValue()).intValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            int v = (Integer)e.getValue();
            int oldPos = Char2IntArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Char2IntArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Char2IntArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2IntArrayMap.this.key, oldPos + 1, Char2IntArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2IntArrayMap.this.value, oldPos + 1, Char2IntArrayMap.this.value, oldPos, tail);
            Char2IntArrayMap.this.size--;
            return true;
        }

    }

}

