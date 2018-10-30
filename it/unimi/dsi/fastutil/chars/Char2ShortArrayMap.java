/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ShortMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2ShortMap;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Char2ShortArrayMap
extends AbstractChar2ShortMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] key;
    private transient short[] value;
    private int size;

    public Char2ShortArrayMap(char[] key, short[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Char2ShortArrayMap() {
        this.key = CharArrays.EMPTY_ARRAY;
        this.value = ShortArrays.EMPTY_ARRAY;
    }

    public Char2ShortArrayMap(int capacity) {
        this.key = new char[capacity];
        this.value = new short[capacity];
    }

    public Char2ShortArrayMap(Char2ShortMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2ShortArrayMap(Map<? extends Character, ? extends Short> m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2ShortArrayMap(char[] key, short[] value, int size) {
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

    public Char2ShortMap.FastEntrySet char2ShortEntrySet() {
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
    public short get(char k) {
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
    public boolean containsValue(short v) {
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
    public short put(char k, short v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            short oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            short[] newValue = new short[this.size == 0 ? 2 : this.size * 2];
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
    public short remove(char k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        short oldValue = this.value[oldPos];
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
                return Char2ShortArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(char k) {
                int oldPos = Char2ShortArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Char2ShortArrayMap.this.size - oldPos - 1;
                System.arraycopy(Char2ShortArrayMap.this.key, oldPos + 1, Char2ShortArrayMap.this.key, oldPos, tail);
                System.arraycopy(Char2ShortArrayMap.this.value, oldPos + 1, Char2ShortArrayMap.this.value, oldPos, tail);
                Char2ShortArrayMap.this.size--;
                return true;
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2ShortArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2ShortArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Char2ShortArrayMap.this.key, this.pos, Char2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2ShortArrayMap.this.value, this.pos, Char2ShortArrayMap.this.value, this.pos - 1, tail);
                        Char2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2ShortArrayMap.this.clear();
            }

        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short v) {
                return Char2ShortArrayMap.this.containsValue(v);
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2ShortArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2ShortArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Char2ShortArrayMap.this.key, this.pos, Char2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2ShortArrayMap.this.value, this.pos, Char2ShortArrayMap.this.value, this.pos - 1, tail);
                        Char2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2ShortArrayMap.this.clear();
            }

        };
    }

    public Char2ShortArrayMap clone() {
        Char2ShortArrayMap c;
        try {
            c = (Char2ShortArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
        c.value = (short[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.key[i]);
            s.writeShort(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new char[this.size];
        this.value = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readChar();
            this.value[i] = s.readShort();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Char2ShortMap.Entry>
    implements Char2ShortMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Char2ShortMap.Entry> iterator() {
            return new ObjectIterator<Char2ShortMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Char2ShortArrayMap.this.size;
                }

                @Override
                public Char2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractChar2ShortMap.BasicEntry(Char2ShortArrayMap.this.key[this.curr], Char2ShortArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2ShortArrayMap.this.key, this.next + 1, Char2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2ShortArrayMap.this.value, this.next + 1, Char2ShortArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Char2ShortMap.Entry> fastIterator() {
            return new ObjectIterator<Char2ShortMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractChar2ShortMap.BasicEntry entry = new AbstractChar2ShortMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Char2ShortArrayMap.this.size;
                }

                @Override
                public Char2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Char2ShortArrayMap.this.key[this.curr];
                    this.entry.value = Char2ShortArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2ShortArrayMap.this.key, this.next + 1, Char2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2ShortArrayMap.this.value, this.next + 1, Char2ShortArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Char2ShortArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            return Char2ShortArrayMap.this.containsKey(k) && Char2ShortArrayMap.this.get(k) == ((Short)e.getValue()).shortValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            short v = (Short)e.getValue();
            int oldPos = Char2ShortArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Char2ShortArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Char2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2ShortArrayMap.this.key, oldPos + 1, Char2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2ShortArrayMap.this.value, oldPos + 1, Char2ShortArrayMap.this.value, oldPos, tail);
            Char2ShortArrayMap.this.size--;
            return true;
        }

    }

}

