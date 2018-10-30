/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2IntMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2IntMap;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Short2IntArrayMap
extends AbstractShort2IntMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient short[] key;
    private transient int[] value;
    private int size;

    public Short2IntArrayMap(short[] key, int[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Short2IntArrayMap() {
        this.key = ShortArrays.EMPTY_ARRAY;
        this.value = IntArrays.EMPTY_ARRAY;
    }

    public Short2IntArrayMap(int capacity) {
        this.key = new short[capacity];
        this.value = new int[capacity];
    }

    public Short2IntArrayMap(Short2IntMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2IntArrayMap(Map<? extends Short, ? extends Integer> m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2IntArrayMap(short[] key, int[] value, int size) {
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

    public Short2IntMap.FastEntrySet short2IntEntrySet() {
        return new EntrySet();
    }

    private int findKey(short k) {
        short[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int get(short k) {
        short[] key = this.key;
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
    public boolean containsKey(short k) {
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
    public int put(short k, int v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            int oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            short[] newKey = new short[this.size == 0 ? 2 : this.size * 2];
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
    public int remove(short k) {
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
    public ShortSet keySet() {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short k) {
                return Short2IntArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(short k) {
                int oldPos = Short2IntArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Short2IntArrayMap.this.size - oldPos - 1;
                System.arraycopy(Short2IntArrayMap.this.key, oldPos + 1, Short2IntArrayMap.this.key, oldPos, tail);
                System.arraycopy(Short2IntArrayMap.this.value, oldPos + 1, Short2IntArrayMap.this.value, oldPos, tail);
                Short2IntArrayMap.this.size--;
                return true;
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2IntArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Short2IntArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2IntArrayMap.this.size - this.pos;
                        System.arraycopy(Short2IntArrayMap.this.key, this.pos, Short2IntArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2IntArrayMap.this.value, this.pos, Short2IntArrayMap.this.value, this.pos - 1, tail);
                        Short2IntArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2IntArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2IntArrayMap.this.clear();
            }

        };
    }

    @Override
    public IntCollection values() {
        return new AbstractIntCollection(){

            @Override
            public boolean contains(int v) {
                return Short2IntArrayMap.this.containsValue(v);
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2IntArrayMap.this.size;
                    }

                    @Override
                    public int nextInt() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Short2IntArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2IntArrayMap.this.size - this.pos;
                        System.arraycopy(Short2IntArrayMap.this.key, this.pos, Short2IntArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2IntArrayMap.this.value, this.pos, Short2IntArrayMap.this.value, this.pos - 1, tail);
                        Short2IntArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2IntArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2IntArrayMap.this.clear();
            }

        };
    }

    public Short2IntArrayMap clone() {
        Short2IntArrayMap c;
        try {
            c = (Short2IntArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (short[])this.key.clone();
        c.value = (int[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeShort(this.key[i]);
            s.writeInt(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new short[this.size];
        this.value = new int[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readShort();
            this.value[i] = s.readInt();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Short2IntMap.Entry>
    implements Short2IntMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Short2IntMap.Entry> iterator() {
            return new ObjectIterator<Short2IntMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Short2IntArrayMap.this.size;
                }

                @Override
                public Short2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractShort2IntMap.BasicEntry(Short2IntArrayMap.this.key[this.curr], Short2IntArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2IntArrayMap.this.key, this.next + 1, Short2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2IntArrayMap.this.value, this.next + 1, Short2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Short2IntMap.Entry> fastIterator() {
            return new ObjectIterator<Short2IntMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractShort2IntMap.BasicEntry entry = new AbstractShort2IntMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Short2IntArrayMap.this.size;
                }

                @Override
                public Short2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Short2IntArrayMap.this.key[this.curr];
                    this.entry.value = Short2IntArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2IntArrayMap.this.key, this.next + 1, Short2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2IntArrayMap.this.value, this.next + 1, Short2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Short2IntArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            short k = (Short)e.getKey();
            return Short2IntArrayMap.this.containsKey(k) && Short2IntArrayMap.this.get(k) == ((Integer)e.getValue()).intValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            short k = (Short)e.getKey();
            int v = (Integer)e.getValue();
            int oldPos = Short2IntArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Short2IntArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Short2IntArrayMap.this.size - oldPos - 1;
            System.arraycopy(Short2IntArrayMap.this.key, oldPos + 1, Short2IntArrayMap.this.key, oldPos, tail);
            System.arraycopy(Short2IntArrayMap.this.value, oldPos + 1, Short2IntArrayMap.this.value, oldPos, tail);
            Short2IntArrayMap.this.size--;
            return true;
        }

    }

}

