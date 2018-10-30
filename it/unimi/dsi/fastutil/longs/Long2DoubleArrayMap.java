/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2DoubleMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
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

public class Long2DoubleArrayMap
extends AbstractLong2DoubleMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient double[] value;
    private int size;

    public Long2DoubleArrayMap(long[] key, double[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Long2DoubleArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = DoubleArrays.EMPTY_ARRAY;
    }

    public Long2DoubleArrayMap(int capacity) {
        this.key = new long[capacity];
        this.value = new double[capacity];
    }

    public Long2DoubleArrayMap(Long2DoubleMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2DoubleArrayMap(Map<? extends Long, ? extends Double> m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2DoubleArrayMap(long[] key, double[] value, int size) {
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

    public Long2DoubleMap.FastEntrySet long2DoubleEntrySet() {
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
    public double get(long k) {
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
    public boolean containsValue(double v) {
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(this.value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public double put(long k, double v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            double oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
            double[] newValue = new double[this.size == 0 ? 2 : this.size * 2];
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
    public double remove(long k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        double oldValue = this.value[oldPos];
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
                return Long2DoubleArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(long k) {
                int oldPos = Long2DoubleArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Long2DoubleArrayMap.this.size - oldPos - 1;
                System.arraycopy(Long2DoubleArrayMap.this.key, oldPos + 1, Long2DoubleArrayMap.this.key, oldPos, tail);
                System.arraycopy(Long2DoubleArrayMap.this.value, oldPos + 1, Long2DoubleArrayMap.this.value, oldPos, tail);
                Long2DoubleArrayMap.this.size--;
                return true;
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2DoubleArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2DoubleArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2DoubleArrayMap.this.size - this.pos;
                        System.arraycopy(Long2DoubleArrayMap.this.key, this.pos, Long2DoubleArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2DoubleArrayMap.this.value, this.pos, Long2DoubleArrayMap.this.value, this.pos - 1, tail);
                        Long2DoubleArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2DoubleArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2DoubleArrayMap.this.clear();
            }

        };
    }

    @Override
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double v) {
                return Long2DoubleArrayMap.this.containsValue(v);
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2DoubleArrayMap.this.size;
                    }

                    @Override
                    public double nextDouble() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2DoubleArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2DoubleArrayMap.this.size - this.pos;
                        System.arraycopy(Long2DoubleArrayMap.this.key, this.pos, Long2DoubleArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2DoubleArrayMap.this.value, this.pos, Long2DoubleArrayMap.this.value, this.pos - 1, tail);
                        Long2DoubleArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2DoubleArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2DoubleArrayMap.this.clear();
            }

        };
    }

    public Long2DoubleArrayMap clone() {
        Long2DoubleArrayMap c;
        try {
            c = (Long2DoubleArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.value = (double[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.key[i]);
            s.writeDouble(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new double[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readLong();
            this.value[i] = s.readDouble();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Long2DoubleMap.Entry>
    implements Long2DoubleMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Long2DoubleMap.Entry> iterator() {
            return new ObjectIterator<Long2DoubleMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Long2DoubleArrayMap.this.size;
                }

                @Override
                public Long2DoubleMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractLong2DoubleMap.BasicEntry(Long2DoubleArrayMap.this.key[this.curr], Long2DoubleArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2DoubleArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2DoubleArrayMap.this.key, this.next + 1, Long2DoubleArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2DoubleArrayMap.this.value, this.next + 1, Long2DoubleArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Long2DoubleMap.Entry> fastIterator() {
            return new ObjectIterator<Long2DoubleMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractLong2DoubleMap.BasicEntry entry = new AbstractLong2DoubleMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Long2DoubleArrayMap.this.size;
                }

                @Override
                public Long2DoubleMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Long2DoubleArrayMap.this.key[this.curr];
                    this.entry.value = Long2DoubleArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2DoubleArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2DoubleArrayMap.this.key, this.next + 1, Long2DoubleArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2DoubleArrayMap.this.value, this.next + 1, Long2DoubleArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Long2DoubleArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            long k = (Long)e.getKey();
            return Long2DoubleArrayMap.this.containsKey(k) && Double.doubleToLongBits(Long2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            long k = (Long)e.getKey();
            double v = (Double)e.getValue();
            int oldPos = Long2DoubleArrayMap.this.findKey(k);
            if (oldPos == -1 || Double.doubleToLongBits(v) != Double.doubleToLongBits(Long2DoubleArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Long2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2DoubleArrayMap.this.key, oldPos + 1, Long2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2DoubleArrayMap.this.value, oldPos + 1, Long2DoubleArrayMap.this.value, oldPos, tail);
            Long2DoubleArrayMap.this.size--;
            return true;
        }

    }

}

