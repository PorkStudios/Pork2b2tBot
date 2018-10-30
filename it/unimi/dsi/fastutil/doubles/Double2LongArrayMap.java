/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDouble2LongMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2LongMap;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
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

public class Double2LongArrayMap
extends AbstractDouble2LongMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient double[] key;
    private transient long[] value;
    private int size;

    public Double2LongArrayMap(double[] key, long[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Double2LongArrayMap() {
        this.key = DoubleArrays.EMPTY_ARRAY;
        this.value = LongArrays.EMPTY_ARRAY;
    }

    public Double2LongArrayMap(int capacity) {
        this.key = new double[capacity];
        this.value = new long[capacity];
    }

    public Double2LongArrayMap(Double2LongMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Double2LongArrayMap(Map<? extends Double, ? extends Long> m) {
        this(m.size());
        this.putAll(m);
    }

    public Double2LongArrayMap(double[] key, long[] value, int size) {
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

    public Double2LongMap.FastEntrySet double2LongEntrySet() {
        return new EntrySet();
    }

    private int findKey(double k) {
        double[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) != Double.doubleToLongBits(k)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public long get(double k) {
        double[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) != Double.doubleToLongBits(k)) continue;
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
    public boolean containsKey(double k) {
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
    public long put(double k, long v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            long oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            double[] newKey = new double[this.size == 0 ? 2 : this.size * 2];
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
    public long remove(double k) {
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
    public DoubleSet keySet() {
        return new AbstractDoubleSet(){

            @Override
            public boolean contains(double k) {
                return Double2LongArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(double k) {
                int oldPos = Double2LongArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Double2LongArrayMap.this.size - oldPos - 1;
                System.arraycopy(Double2LongArrayMap.this.key, oldPos + 1, Double2LongArrayMap.this.key, oldPos, tail);
                System.arraycopy(Double2LongArrayMap.this.value, oldPos + 1, Double2LongArrayMap.this.value, oldPos, tail);
                Double2LongArrayMap.this.size--;
                return true;
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Double2LongArrayMap.this.size;
                    }

                    @Override
                    public double nextDouble() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Double2LongArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Double2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Double2LongArrayMap.this.key, this.pos, Double2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Double2LongArrayMap.this.value, this.pos, Double2LongArrayMap.this.value, this.pos - 1, tail);
                        Double2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Double2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Double2LongArrayMap.this.clear();
            }

        };
    }

    @Override
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long v) {
                return Double2LongArrayMap.this.containsValue(v);
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Double2LongArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Double2LongArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Double2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Double2LongArrayMap.this.key, this.pos, Double2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Double2LongArrayMap.this.value, this.pos, Double2LongArrayMap.this.value, this.pos - 1, tail);
                        Double2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Double2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Double2LongArrayMap.this.clear();
            }

        };
    }

    public Double2LongArrayMap clone() {
        Double2LongArrayMap c;
        try {
            c = (Double2LongArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (double[])this.key.clone();
        c.value = (long[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeDouble(this.key[i]);
            s.writeLong(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new double[this.size];
        this.value = new long[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readDouble();
            this.value[i] = s.readLong();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Double2LongMap.Entry>
    implements Double2LongMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Double2LongMap.Entry> iterator() {
            return new ObjectIterator<Double2LongMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Double2LongArrayMap.this.size;
                }

                @Override
                public Double2LongMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractDouble2LongMap.BasicEntry(Double2LongArrayMap.this.key[this.curr], Double2LongArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Double2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Double2LongArrayMap.this.key, this.next + 1, Double2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Double2LongArrayMap.this.value, this.next + 1, Double2LongArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Double2LongMap.Entry> fastIterator() {
            return new ObjectIterator<Double2LongMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractDouble2LongMap.BasicEntry entry = new AbstractDouble2LongMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Double2LongArrayMap.this.size;
                }

                @Override
                public Double2LongMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Double2LongArrayMap.this.key[this.curr];
                    this.entry.value = Double2LongArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Double2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Double2LongArrayMap.this.key, this.next + 1, Double2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Double2LongArrayMap.this.value, this.next + 1, Double2LongArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Double2LongArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            double k = (Double)e.getKey();
            return Double2LongArrayMap.this.containsKey(k) && Double2LongArrayMap.this.get(k) == ((Long)e.getValue()).longValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            double k = (Double)e.getKey();
            long v = (Long)e.getValue();
            int oldPos = Double2LongArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Double2LongArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Double2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Double2LongArrayMap.this.key, oldPos + 1, Double2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Double2LongArrayMap.this.value, oldPos + 1, Double2LongArrayMap.this.value, oldPos, tail);
            Double2LongArrayMap.this.size--;
            return true;
        }

    }

}

