/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2FloatMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2FloatMap;
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

public class Short2FloatArrayMap
extends AbstractShort2FloatMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient short[] key;
    private transient float[] value;
    private int size;

    public Short2FloatArrayMap(short[] key, float[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Short2FloatArrayMap() {
        this.key = ShortArrays.EMPTY_ARRAY;
        this.value = FloatArrays.EMPTY_ARRAY;
    }

    public Short2FloatArrayMap(int capacity) {
        this.key = new short[capacity];
        this.value = new float[capacity];
    }

    public Short2FloatArrayMap(Short2FloatMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2FloatArrayMap(Map<? extends Short, ? extends Float> m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2FloatArrayMap(short[] key, float[] value, int size) {
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

    public Short2FloatMap.FastEntrySet short2FloatEntrySet() {
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
    public float get(short k) {
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
    public float put(short k, float v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            float oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            short[] newKey = new short[this.size == 0 ? 2 : this.size * 2];
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
    public float remove(short k) {
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
    public ShortSet keySet() {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short k) {
                return Short2FloatArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(short k) {
                int oldPos = Short2FloatArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Short2FloatArrayMap.this.size - oldPos - 1;
                System.arraycopy(Short2FloatArrayMap.this.key, oldPos + 1, Short2FloatArrayMap.this.key, oldPos, tail);
                System.arraycopy(Short2FloatArrayMap.this.value, oldPos + 1, Short2FloatArrayMap.this.value, oldPos, tail);
                Short2FloatArrayMap.this.size--;
                return true;
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2FloatArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Short2FloatArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Short2FloatArrayMap.this.key, this.pos, Short2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2FloatArrayMap.this.value, this.pos, Short2FloatArrayMap.this.value, this.pos - 1, tail);
                        Short2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2FloatArrayMap.this.clear();
            }

        };
    }

    @Override
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float v) {
                return Short2FloatArrayMap.this.containsValue(v);
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2FloatArrayMap.this.size;
                    }

                    @Override
                    public float nextFloat() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Short2FloatArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Short2FloatArrayMap.this.key, this.pos, Short2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2FloatArrayMap.this.value, this.pos, Short2FloatArrayMap.this.value, this.pos - 1, tail);
                        Short2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2FloatArrayMap.this.clear();
            }

        };
    }

    public Short2FloatArrayMap clone() {
        Short2FloatArrayMap c;
        try {
            c = (Short2FloatArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (short[])this.key.clone();
        c.value = (float[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeShort(this.key[i]);
            s.writeFloat(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new short[this.size];
        this.value = new float[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readShort();
            this.value[i] = s.readFloat();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Short2FloatMap.Entry>
    implements Short2FloatMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Short2FloatMap.Entry> iterator() {
            return new ObjectIterator<Short2FloatMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Short2FloatArrayMap.this.size;
                }

                @Override
                public Short2FloatMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractShort2FloatMap.BasicEntry(Short2FloatArrayMap.this.key[this.curr], Short2FloatArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2FloatArrayMap.this.key, this.next + 1, Short2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2FloatArrayMap.this.value, this.next + 1, Short2FloatArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Short2FloatMap.Entry> fastIterator() {
            return new ObjectIterator<Short2FloatMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractShort2FloatMap.BasicEntry entry = new AbstractShort2FloatMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Short2FloatArrayMap.this.size;
                }

                @Override
                public Short2FloatMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Short2FloatArrayMap.this.key[this.curr];
                    this.entry.value = Short2FloatArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2FloatArrayMap.this.key, this.next + 1, Short2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2FloatArrayMap.this.value, this.next + 1, Short2FloatArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Short2FloatArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            short k = (Short)e.getKey();
            return Short2FloatArrayMap.this.containsKey(k) && Float.floatToIntBits(Short2FloatArrayMap.this.get(k)) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
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
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            short k = (Short)e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            int oldPos = Short2FloatArrayMap.this.findKey(k);
            if (oldPos == -1 || Float.floatToIntBits(v) != Float.floatToIntBits(Short2FloatArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Short2FloatArrayMap.this.size - oldPos - 1;
            System.arraycopy(Short2FloatArrayMap.this.key, oldPos + 1, Short2FloatArrayMap.this.key, oldPos, tail);
            System.arraycopy(Short2FloatArrayMap.this.value, oldPos + 1, Short2FloatArrayMap.this.value, oldPos, tail);
            Short2FloatArrayMap.this.size--;
            return true;
        }

    }

}

