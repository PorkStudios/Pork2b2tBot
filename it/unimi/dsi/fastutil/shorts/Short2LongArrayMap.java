/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2LongMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2LongMap;
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

public class Short2LongArrayMap
extends AbstractShort2LongMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient short[] key;
    private transient long[] value;
    private int size;

    public Short2LongArrayMap(short[] key, long[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Short2LongArrayMap() {
        this.key = ShortArrays.EMPTY_ARRAY;
        this.value = LongArrays.EMPTY_ARRAY;
    }

    public Short2LongArrayMap(int capacity) {
        this.key = new short[capacity];
        this.value = new long[capacity];
    }

    public Short2LongArrayMap(Short2LongMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2LongArrayMap(Map<? extends Short, ? extends Long> m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2LongArrayMap(short[] key, long[] value, int size) {
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

    public Short2LongMap.FastEntrySet short2LongEntrySet() {
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
    public long get(short k) {
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
    public boolean containsValue(long v) {
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
    public long put(short k, long v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            long oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            short[] newKey = new short[this.size == 0 ? 2 : this.size * 2];
            long[] newValue = new long[this.size == 0 ? 2 : this.size * 2];
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
    public long remove(short k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        long oldValue = this.value[oldPos];
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
                return Short2LongArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(short k) {
                int oldPos = Short2LongArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Short2LongArrayMap.this.size - oldPos - 1;
                System.arraycopy(Short2LongArrayMap.this.key, oldPos + 1, Short2LongArrayMap.this.key, oldPos, tail);
                System.arraycopy(Short2LongArrayMap.this.value, oldPos + 1, Short2LongArrayMap.this.value, oldPos, tail);
                Short2LongArrayMap.this.size--;
                return true;
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2LongArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Short2LongArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Short2LongArrayMap.this.key, this.pos, Short2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2LongArrayMap.this.value, this.pos, Short2LongArrayMap.this.value, this.pos - 1, tail);
                        Short2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2LongArrayMap.this.clear();
            }

        };
    }

    @Override
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long v) {
                return Short2LongArrayMap.this.containsValue(v);
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2LongArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Short2LongArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Short2LongArrayMap.this.key, this.pos, Short2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2LongArrayMap.this.value, this.pos, Short2LongArrayMap.this.value, this.pos - 1, tail);
                        Short2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2LongArrayMap.this.clear();
            }

        };
    }

    public Short2LongArrayMap clone() {
        Short2LongArrayMap c;
        try {
            c = (Short2LongArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (short[])this.key.clone();
        c.value = (long[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeShort(this.key[i]);
            s.writeLong(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new short[this.size];
        this.value = new long[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readShort();
            this.value[i] = s.readLong();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Short2LongMap.Entry>
    implements Short2LongMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Short2LongMap.Entry> iterator() {
            return new ObjectIterator<Short2LongMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Short2LongArrayMap.this.size;
                }

                @Override
                public Short2LongMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractShort2LongMap.BasicEntry(Short2LongArrayMap.this.key[this.curr], Short2LongArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2LongArrayMap.this.key, this.next + 1, Short2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2LongArrayMap.this.value, this.next + 1, Short2LongArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Short2LongMap.Entry> fastIterator() {
            return new ObjectIterator<Short2LongMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractShort2LongMap.BasicEntry entry = new AbstractShort2LongMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Short2LongArrayMap.this.size;
                }

                @Override
                public Short2LongMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Short2LongArrayMap.this.key[this.curr];
                    this.entry.value = Short2LongArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2LongArrayMap.this.key, this.next + 1, Short2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2LongArrayMap.this.value, this.next + 1, Short2LongArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Short2LongArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            short k = (Short)e.getKey();
            return Short2LongArrayMap.this.containsKey(k) && Short2LongArrayMap.this.get(k) == ((Long)e.getValue()).longValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            short k = (Short)e.getKey();
            long v = (Long)e.getValue();
            int oldPos = Short2LongArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Short2LongArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Short2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Short2LongArrayMap.this.key, oldPos + 1, Short2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Short2LongArrayMap.this.value, oldPos + 1, Short2LongArrayMap.this.value, oldPos, tail);
            Short2LongArrayMap.this.size--;
            return true;
        }

    }

}

