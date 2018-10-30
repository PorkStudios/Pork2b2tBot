/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2IntMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2IntMap;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
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

public class Float2IntArrayMap
extends AbstractFloat2IntMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient float[] key;
    private transient int[] value;
    private int size;

    public Float2IntArrayMap(float[] key, int[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Float2IntArrayMap() {
        this.key = FloatArrays.EMPTY_ARRAY;
        this.value = IntArrays.EMPTY_ARRAY;
    }

    public Float2IntArrayMap(int capacity) {
        this.key = new float[capacity];
        this.value = new int[capacity];
    }

    public Float2IntArrayMap(Float2IntMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Float2IntArrayMap(Map<? extends Float, ? extends Integer> m) {
        this(m.size());
        this.putAll(m);
    }

    public Float2IntArrayMap(float[] key, int[] value, int size) {
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

    public Float2IntMap.FastEntrySet float2IntEntrySet() {
        return new EntrySet();
    }

    private int findKey(float k) {
        float[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != Float.floatToIntBits(k)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int get(float k) {
        float[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != Float.floatToIntBits(k)) continue;
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
    public boolean containsKey(float k) {
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
    public int put(float k, int v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            int oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
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
    public int remove(float k) {
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
    public FloatSet keySet() {
        return new AbstractFloatSet(){

            @Override
            public boolean contains(float k) {
                return Float2IntArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(float k) {
                int oldPos = Float2IntArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Float2IntArrayMap.this.size - oldPos - 1;
                System.arraycopy(Float2IntArrayMap.this.key, oldPos + 1, Float2IntArrayMap.this.key, oldPos, tail);
                System.arraycopy(Float2IntArrayMap.this.value, oldPos + 1, Float2IntArrayMap.this.value, oldPos, tail);
                Float2IntArrayMap.this.size--;
                return true;
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Float2IntArrayMap.this.size;
                    }

                    @Override
                    public float nextFloat() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Float2IntArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Float2IntArrayMap.this.size - this.pos;
                        System.arraycopy(Float2IntArrayMap.this.key, this.pos, Float2IntArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Float2IntArrayMap.this.value, this.pos, Float2IntArrayMap.this.value, this.pos - 1, tail);
                        Float2IntArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Float2IntArrayMap.this.size;
            }

            @Override
            public void clear() {
                Float2IntArrayMap.this.clear();
            }

        };
    }

    @Override
    public IntCollection values() {
        return new AbstractIntCollection(){

            @Override
            public boolean contains(int v) {
                return Float2IntArrayMap.this.containsValue(v);
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Float2IntArrayMap.this.size;
                    }

                    @Override
                    public int nextInt() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Float2IntArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Float2IntArrayMap.this.size - this.pos;
                        System.arraycopy(Float2IntArrayMap.this.key, this.pos, Float2IntArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Float2IntArrayMap.this.value, this.pos, Float2IntArrayMap.this.value, this.pos - 1, tail);
                        Float2IntArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Float2IntArrayMap.this.size;
            }

            @Override
            public void clear() {
                Float2IntArrayMap.this.clear();
            }

        };
    }

    public Float2IntArrayMap clone() {
        Float2IntArrayMap c;
        try {
            c = (Float2IntArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (float[])this.key.clone();
        c.value = (int[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeFloat(this.key[i]);
            s.writeInt(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new float[this.size];
        this.value = new int[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readFloat();
            this.value[i] = s.readInt();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Float2IntMap.Entry>
    implements Float2IntMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Float2IntMap.Entry> iterator() {
            return new ObjectIterator<Float2IntMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Float2IntArrayMap.this.size;
                }

                @Override
                public Float2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractFloat2IntMap.BasicEntry(Float2IntArrayMap.this.key[this.curr], Float2IntArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2IntArrayMap.this.key, this.next + 1, Float2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2IntArrayMap.this.value, this.next + 1, Float2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Float2IntMap.Entry> fastIterator() {
            return new ObjectIterator<Float2IntMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractFloat2IntMap.BasicEntry entry = new AbstractFloat2IntMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Float2IntArrayMap.this.size;
                }

                @Override
                public Float2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Float2IntArrayMap.this.key[this.curr];
                    this.entry.value = Float2IntArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2IntArrayMap.this.key, this.next + 1, Float2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2IntArrayMap.this.value, this.next + 1, Float2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Float2IntArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            return Float2IntArrayMap.this.containsKey(k) && Float2IntArrayMap.this.get(k) == ((Integer)e.getValue()).intValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            int v = (Integer)e.getValue();
            int oldPos = Float2IntArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Float2IntArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Float2IntArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2IntArrayMap.this.key, oldPos + 1, Float2IntArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2IntArrayMap.this.value, oldPos + 1, Float2IntArrayMap.this.value, oldPos, tail);
            Float2IntArrayMap.this.size--;
            return true;
        }

    }

}

