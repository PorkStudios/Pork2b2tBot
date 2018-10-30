/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2FloatMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
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

public class Long2FloatArrayMap
extends AbstractLong2FloatMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient float[] value;
    private int size;

    public Long2FloatArrayMap(long[] key, float[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Long2FloatArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = FloatArrays.EMPTY_ARRAY;
    }

    public Long2FloatArrayMap(int capacity) {
        this.key = new long[capacity];
        this.value = new float[capacity];
    }

    public Long2FloatArrayMap(Long2FloatMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2FloatArrayMap(Map<? extends Long, ? extends Float> m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2FloatArrayMap(long[] key, float[] value, int size) {
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

    public Long2FloatMap.FastEntrySet long2FloatEntrySet() {
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
    public float get(long k) {
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
    public boolean containsValue(float v) {
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(this.value[i]) != Float.floatToIntBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public float put(long k, float v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            float oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
            float[] newValue = new float[this.size == 0 ? 2 : this.size * 2];
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
    public float remove(long k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        float oldValue = this.value[oldPos];
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
                return Long2FloatArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(long k) {
                int oldPos = Long2FloatArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Long2FloatArrayMap.this.size - oldPos - 1;
                System.arraycopy(Long2FloatArrayMap.this.key, oldPos + 1, Long2FloatArrayMap.this.key, oldPos, tail);
                System.arraycopy(Long2FloatArrayMap.this.value, oldPos + 1, Long2FloatArrayMap.this.value, oldPos, tail);
                Long2FloatArrayMap.this.size--;
                return true;
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2FloatArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2FloatArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Long2FloatArrayMap.this.key, this.pos, Long2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2FloatArrayMap.this.value, this.pos, Long2FloatArrayMap.this.value, this.pos - 1, tail);
                        Long2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2FloatArrayMap.this.clear();
            }

        };
    }

    @Override
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float v) {
                return Long2FloatArrayMap.this.containsValue(v);
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2FloatArrayMap.this.size;
                    }

                    @Override
                    public float nextFloat() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2FloatArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Long2FloatArrayMap.this.key, this.pos, Long2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2FloatArrayMap.this.value, this.pos, Long2FloatArrayMap.this.value, this.pos - 1, tail);
                        Long2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2FloatArrayMap.this.clear();
            }

        };
    }

    public Long2FloatArrayMap clone() {
        Long2FloatArrayMap c;
        try {
            c = (Long2FloatArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.value = (float[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.key[i]);
            s.writeFloat(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new float[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readLong();
            this.value[i] = s.readFloat();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Long2FloatMap.Entry>
    implements Long2FloatMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Long2FloatMap.Entry> iterator() {
            return new ObjectIterator<Long2FloatMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Long2FloatArrayMap.this.size;
                }

                @Override
                public Long2FloatMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractLong2FloatMap.BasicEntry(Long2FloatArrayMap.this.key[this.curr], Long2FloatArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2FloatArrayMap.this.key, this.next + 1, Long2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2FloatArrayMap.this.value, this.next + 1, Long2FloatArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Long2FloatMap.Entry> fastIterator() {
            return new ObjectIterator<Long2FloatMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractLong2FloatMap.BasicEntry entry = new AbstractLong2FloatMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Long2FloatArrayMap.this.size;
                }

                @Override
                public Long2FloatMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Long2FloatArrayMap.this.key[this.curr];
                    this.entry.value = Long2FloatArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2FloatArrayMap.this.key, this.next + 1, Long2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2FloatArrayMap.this.value, this.next + 1, Long2FloatArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Long2FloatArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            long k = (Long)e.getKey();
            return Long2FloatArrayMap.this.containsKey(k) && Float.floatToIntBits(Long2FloatArrayMap.this.get(k)) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
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
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            long k = (Long)e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            int oldPos = Long2FloatArrayMap.this.findKey(k);
            if (oldPos == -1 || Float.floatToIntBits(v) != Float.floatToIntBits(Long2FloatArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Long2FloatArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2FloatArrayMap.this.key, oldPos + 1, Long2FloatArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2FloatArrayMap.this.value, oldPos + 1, Long2FloatArrayMap.this.value, oldPos, tail);
            Long2FloatArrayMap.this.size--;
            return true;
        }

    }

}

