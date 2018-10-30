/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2BooleanMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
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

public class Int2BooleanArrayMap
extends AbstractInt2BooleanMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient int[] key;
    private transient boolean[] value;
    private int size;

    public Int2BooleanArrayMap(int[] key, boolean[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Int2BooleanArrayMap() {
        this.key = IntArrays.EMPTY_ARRAY;
        this.value = BooleanArrays.EMPTY_ARRAY;
    }

    public Int2BooleanArrayMap(int capacity) {
        this.key = new int[capacity];
        this.value = new boolean[capacity];
    }

    public Int2BooleanArrayMap(Int2BooleanMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Int2BooleanArrayMap(Map<? extends Integer, ? extends Boolean> m) {
        this(m.size());
        this.putAll(m);
    }

    public Int2BooleanArrayMap(int[] key, boolean[] value, int size) {
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

    public Int2BooleanMap.FastEntrySet int2BooleanEntrySet() {
        return new EntrySet();
    }

    private int findKey(int k) {
        int[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public boolean get(int k) {
        int[] key = this.key;
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
    public boolean containsKey(int k) {
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
    public boolean put(int k, boolean v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            boolean oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            int[] newKey = new int[this.size == 0 ? 2 : this.size * 2];
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
    public boolean remove(int k) {
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
    public IntSet keySet() {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int k) {
                return Int2BooleanArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(int k) {
                int oldPos = Int2BooleanArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Int2BooleanArrayMap.this.size - oldPos - 1;
                System.arraycopy(Int2BooleanArrayMap.this.key, oldPos + 1, Int2BooleanArrayMap.this.key, oldPos, tail);
                System.arraycopy(Int2BooleanArrayMap.this.value, oldPos + 1, Int2BooleanArrayMap.this.value, oldPos, tail);
                Int2BooleanArrayMap.this.size--;
                return true;
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Int2BooleanArrayMap.this.size;
                    }

                    @Override
                    public int nextInt() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Int2BooleanArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Int2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Int2BooleanArrayMap.this.key, this.pos, Int2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Int2BooleanArrayMap.this.value, this.pos, Int2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Int2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Int2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Int2BooleanArrayMap.this.clear();
            }

        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean v) {
                return Int2BooleanArrayMap.this.containsValue(v);
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Int2BooleanArrayMap.this.size;
                    }

                    @Override
                    public boolean nextBoolean() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Int2BooleanArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Int2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Int2BooleanArrayMap.this.key, this.pos, Int2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Int2BooleanArrayMap.this.value, this.pos, Int2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Int2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Int2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Int2BooleanArrayMap.this.clear();
            }

        };
    }

    public Int2BooleanArrayMap clone() {
        Int2BooleanArrayMap c;
        try {
            c = (Int2BooleanArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (int[])this.key.clone();
        c.value = (boolean[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeInt(this.key[i]);
            s.writeBoolean(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new int[this.size];
        this.value = new boolean[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readInt();
            this.value[i] = s.readBoolean();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Int2BooleanMap.Entry>
    implements Int2BooleanMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Int2BooleanMap.Entry> iterator() {
            return new ObjectIterator<Int2BooleanMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Int2BooleanArrayMap.this.size;
                }

                @Override
                public Int2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractInt2BooleanMap.BasicEntry(Int2BooleanArrayMap.this.key[this.curr], Int2BooleanArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Int2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Int2BooleanArrayMap.this.key, this.next + 1, Int2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Int2BooleanArrayMap.this.value, this.next + 1, Int2BooleanArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Int2BooleanMap.Entry> fastIterator() {
            return new ObjectIterator<Int2BooleanMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractInt2BooleanMap.BasicEntry entry = new AbstractInt2BooleanMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Int2BooleanArrayMap.this.size;
                }

                @Override
                public Int2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Int2BooleanArrayMap.this.key[this.curr];
                    this.entry.value = Int2BooleanArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Int2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Int2BooleanArrayMap.this.key, this.next + 1, Int2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Int2BooleanArrayMap.this.value, this.next + 1, Int2BooleanArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Int2BooleanArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            int k = (Integer)e.getKey();
            return Int2BooleanArrayMap.this.containsKey(k) && Int2BooleanArrayMap.this.get(k) == ((Boolean)e.getValue()).booleanValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            int k = (Integer)e.getKey();
            boolean v = (Boolean)e.getValue();
            int oldPos = Int2BooleanArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Int2BooleanArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Int2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Int2BooleanArrayMap.this.key, oldPos + 1, Int2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Int2BooleanArrayMap.this.value, oldPos + 1, Int2BooleanArrayMap.this.value, oldPos, tail);
            Int2BooleanArrayMap.this.size--;
            return true;
        }

    }

}

