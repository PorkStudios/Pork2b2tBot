/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.chars.AbstractChar2BooleanMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2BooleanMap;
import it.unimi.dsi.fastutil.chars.CharArrays;
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

public class Char2BooleanArrayMap
extends AbstractChar2BooleanMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] key;
    private transient boolean[] value;
    private int size;

    public Char2BooleanArrayMap(char[] key, boolean[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Char2BooleanArrayMap() {
        this.key = CharArrays.EMPTY_ARRAY;
        this.value = BooleanArrays.EMPTY_ARRAY;
    }

    public Char2BooleanArrayMap(int capacity) {
        this.key = new char[capacity];
        this.value = new boolean[capacity];
    }

    public Char2BooleanArrayMap(Char2BooleanMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2BooleanArrayMap(Map<? extends Character, ? extends Boolean> m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2BooleanArrayMap(char[] key, boolean[] value, int size) {
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

    public Char2BooleanMap.FastEntrySet char2BooleanEntrySet() {
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
    public boolean get(char k) {
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
    public boolean containsValue(boolean v) {
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
    public boolean put(char k, boolean v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            boolean oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            boolean[] newValue = new boolean[this.size == 0 ? 2 : this.size * 2];
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
    public boolean remove(char k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[oldPos];
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
                return Char2BooleanArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(char k) {
                int oldPos = Char2BooleanArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Char2BooleanArrayMap.this.size - oldPos - 1;
                System.arraycopy(Char2BooleanArrayMap.this.key, oldPos + 1, Char2BooleanArrayMap.this.key, oldPos, tail);
                System.arraycopy(Char2BooleanArrayMap.this.value, oldPos + 1, Char2BooleanArrayMap.this.value, oldPos, tail);
                Char2BooleanArrayMap.this.size--;
                return true;
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2BooleanArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2BooleanArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Char2BooleanArrayMap.this.key, this.pos, Char2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2BooleanArrayMap.this.value, this.pos, Char2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Char2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2BooleanArrayMap.this.clear();
            }

        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean v) {
                return Char2BooleanArrayMap.this.containsValue(v);
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2BooleanArrayMap.this.size;
                    }

                    @Override
                    public boolean nextBoolean() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2BooleanArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Char2BooleanArrayMap.this.key, this.pos, Char2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2BooleanArrayMap.this.value, this.pos, Char2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Char2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2BooleanArrayMap.this.clear();
            }

        };
    }

    public Char2BooleanArrayMap clone() {
        Char2BooleanArrayMap c;
        try {
            c = (Char2BooleanArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
        c.value = (boolean[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.key[i]);
            s.writeBoolean(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new char[this.size];
        this.value = new boolean[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readChar();
            this.value[i] = s.readBoolean();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Char2BooleanMap.Entry>
    implements Char2BooleanMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Char2BooleanMap.Entry> iterator() {
            return new ObjectIterator<Char2BooleanMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Char2BooleanArrayMap.this.size;
                }

                @Override
                public Char2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractChar2BooleanMap.BasicEntry(Char2BooleanArrayMap.this.key[this.curr], Char2BooleanArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2BooleanArrayMap.this.key, this.next + 1, Char2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2BooleanArrayMap.this.value, this.next + 1, Char2BooleanArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Char2BooleanMap.Entry> fastIterator() {
            return new ObjectIterator<Char2BooleanMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractChar2BooleanMap.BasicEntry entry = new AbstractChar2BooleanMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Char2BooleanArrayMap.this.size;
                }

                @Override
                public Char2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Char2BooleanArrayMap.this.key[this.curr];
                    this.entry.value = Char2BooleanArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2BooleanArrayMap.this.key, this.next + 1, Char2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2BooleanArrayMap.this.value, this.next + 1, Char2BooleanArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Char2BooleanArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            return Char2BooleanArrayMap.this.containsKey(k) && Char2BooleanArrayMap.this.get(k) == ((Boolean)e.getValue()).booleanValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            boolean v = (Boolean)e.getValue();
            int oldPos = Char2BooleanArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Char2BooleanArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Char2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2BooleanArrayMap.this.key, oldPos + 1, Char2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2BooleanArrayMap.this.value, oldPos + 1, Char2BooleanArrayMap.this.value, oldPos, tail);
            Char2BooleanArrayMap.this.size--;
            return true;
        }

    }

}

