/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2ShortMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
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

public class Long2ShortArrayMap
extends AbstractLong2ShortMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient short[] value;
    private int size;

    public Long2ShortArrayMap(long[] key, short[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Long2ShortArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = ShortArrays.EMPTY_ARRAY;
    }

    public Long2ShortArrayMap(int capacity) {
        this.key = new long[capacity];
        this.value = new short[capacity];
    }

    public Long2ShortArrayMap(Long2ShortMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2ShortArrayMap(Map<? extends Long, ? extends Short> m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2ShortArrayMap(long[] key, short[] value, int size) {
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

    public Long2ShortMap.FastEntrySet long2ShortEntrySet() {
        return new EntrySet();
    }

    private int findKey(long k) {
        long[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public short get(long k) {
        long[] key = this.key;
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
    public boolean containsKey(long k) {
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
    public short put(long k, short v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            short oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
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
    public short remove(long k) {
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
    public LongSet keySet() {
        return new AbstractLongSet(){

            @Override
            public boolean contains(long k) {
                return Long2ShortArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(long k) {
                int oldPos = Long2ShortArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Long2ShortArrayMap.this.size - oldPos - 1;
                System.arraycopy(Long2ShortArrayMap.this.key, oldPos + 1, Long2ShortArrayMap.this.key, oldPos, tail);
                System.arraycopy(Long2ShortArrayMap.this.value, oldPos + 1, Long2ShortArrayMap.this.value, oldPos, tail);
                Long2ShortArrayMap.this.size--;
                return true;
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2ShortArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2ShortArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Long2ShortArrayMap.this.key, this.pos, Long2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2ShortArrayMap.this.value, this.pos, Long2ShortArrayMap.this.value, this.pos - 1, tail);
                        Long2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2ShortArrayMap.this.clear();
            }

        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short v) {
                return Long2ShortArrayMap.this.containsValue(v);
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2ShortArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2ShortArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Long2ShortArrayMap.this.key, this.pos, Long2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2ShortArrayMap.this.value, this.pos, Long2ShortArrayMap.this.value, this.pos - 1, tail);
                        Long2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2ShortArrayMap.this.clear();
            }

        };
    }

    public Long2ShortArrayMap clone() {
        Long2ShortArrayMap c;
        try {
            c = (Long2ShortArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.value = (short[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.key[i]);
            s.writeShort(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readLong();
            this.value[i] = s.readShort();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Long2ShortMap.Entry>
    implements Long2ShortMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Long2ShortMap.Entry> iterator() {
            return new ObjectIterator<Long2ShortMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Long2ShortArrayMap.this.size;
                }

                @Override
                public Long2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractLong2ShortMap.BasicEntry(Long2ShortArrayMap.this.key[this.curr], Long2ShortArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ShortArrayMap.this.key, this.next + 1, Long2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ShortArrayMap.this.value, this.next + 1, Long2ShortArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Long2ShortMap.Entry> fastIterator() {
            return new ObjectIterator<Long2ShortMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractLong2ShortMap.BasicEntry entry = new AbstractLong2ShortMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Long2ShortArrayMap.this.size;
                }

                @Override
                public Long2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Long2ShortArrayMap.this.key[this.curr];
                    this.entry.value = Long2ShortArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ShortArrayMap.this.key, this.next + 1, Long2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ShortArrayMap.this.value, this.next + 1, Long2ShortArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Long2ShortArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            long k = (Long)e.getKey();
            return Long2ShortArrayMap.this.containsKey(k) && Long2ShortArrayMap.this.get(k) == ((Short)e.getValue()).shortValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            long k = (Long)e.getKey();
            short v = (Short)e.getValue();
            int oldPos = Long2ShortArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Long2ShortArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Long2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2ShortArrayMap.this.key, oldPos + 1, Long2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2ShortArrayMap.this.value, oldPos + 1, Long2ShortArrayMap.this.value, oldPos, tail);
            Long2ShortArrayMap.this.size--;
            return true;
        }

    }

}

