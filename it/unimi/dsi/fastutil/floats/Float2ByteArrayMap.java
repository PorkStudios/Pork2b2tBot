/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ByteMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ByteMap;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
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

public class Float2ByteArrayMap
extends AbstractFloat2ByteMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient float[] key;
    private transient byte[] value;
    private int size;

    public Float2ByteArrayMap(float[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Float2ByteArrayMap() {
        this.key = FloatArrays.EMPTY_ARRAY;
        this.value = ByteArrays.EMPTY_ARRAY;
    }

    public Float2ByteArrayMap(int capacity) {
        this.key = new float[capacity];
        this.value = new byte[capacity];
    }

    public Float2ByteArrayMap(Float2ByteMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Float2ByteArrayMap(Map<? extends Float, ? extends Byte> m) {
        this(m.size());
        this.putAll(m);
    }

    public Float2ByteArrayMap(float[] key, byte[] value, int size) {
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

    public Float2ByteMap.FastEntrySet float2ByteEntrySet() {
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
    public byte get(float k) {
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
    public boolean containsValue(byte v) {
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
    public byte put(float k, byte v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            byte oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
            byte[] newValue = new byte[this.size == 0 ? 2 : this.size * 2];
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
    public byte remove(float k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        byte oldValue = this.value[oldPos];
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
                return Float2ByteArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(float k) {
                int oldPos = Float2ByteArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Float2ByteArrayMap.this.size - oldPos - 1;
                System.arraycopy(Float2ByteArrayMap.this.key, oldPos + 1, Float2ByteArrayMap.this.key, oldPos, tail);
                System.arraycopy(Float2ByteArrayMap.this.value, oldPos + 1, Float2ByteArrayMap.this.value, oldPos, tail);
                Float2ByteArrayMap.this.size--;
                return true;
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Float2ByteArrayMap.this.size;
                    }

                    @Override
                    public float nextFloat() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Float2ByteArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Float2ByteArrayMap.this.size - this.pos;
                        System.arraycopy(Float2ByteArrayMap.this.key, this.pos, Float2ByteArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Float2ByteArrayMap.this.value, this.pos, Float2ByteArrayMap.this.value, this.pos - 1, tail);
                        Float2ByteArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Float2ByteArrayMap.this.size;
            }

            @Override
            public void clear() {
                Float2ByteArrayMap.this.clear();
            }

        };
    }

    @Override
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte v) {
                return Float2ByteArrayMap.this.containsValue(v);
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Float2ByteArrayMap.this.size;
                    }

                    @Override
                    public byte nextByte() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Float2ByteArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Float2ByteArrayMap.this.size - this.pos;
                        System.arraycopy(Float2ByteArrayMap.this.key, this.pos, Float2ByteArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Float2ByteArrayMap.this.value, this.pos, Float2ByteArrayMap.this.value, this.pos - 1, tail);
                        Float2ByteArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Float2ByteArrayMap.this.size;
            }

            @Override
            public void clear() {
                Float2ByteArrayMap.this.clear();
            }

        };
    }

    public Float2ByteArrayMap clone() {
        Float2ByteArrayMap c;
        try {
            c = (Float2ByteArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (float[])this.key.clone();
        c.value = (byte[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeFloat(this.key[i]);
            s.writeByte(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new float[this.size];
        this.value = new byte[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readFloat();
            this.value[i] = s.readByte();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Float2ByteMap.Entry>
    implements Float2ByteMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Float2ByteMap.Entry> iterator() {
            return new ObjectIterator<Float2ByteMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Float2ByteArrayMap.this.size;
                }

                @Override
                public Float2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractFloat2ByteMap.BasicEntry(Float2ByteArrayMap.this.key[this.curr], Float2ByteArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2ByteArrayMap.this.key, this.next + 1, Float2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2ByteArrayMap.this.value, this.next + 1, Float2ByteArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Float2ByteMap.Entry> fastIterator() {
            return new ObjectIterator<Float2ByteMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractFloat2ByteMap.BasicEntry entry = new AbstractFloat2ByteMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Float2ByteArrayMap.this.size;
                }

                @Override
                public Float2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Float2ByteArrayMap.this.key[this.curr];
                    this.entry.value = Float2ByteArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2ByteArrayMap.this.key, this.next + 1, Float2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2ByteArrayMap.this.value, this.next + 1, Float2ByteArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Float2ByteArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            return Float2ByteArrayMap.this.containsKey(k) && Float2ByteArrayMap.this.get(k) == ((Byte)e.getValue()).byteValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            byte v = (Byte)e.getValue();
            int oldPos = Float2ByteArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Float2ByteArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Float2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2ByteArrayMap.this.key, oldPos + 1, Float2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2ByteArrayMap.this.value, oldPos + 1, Float2ByteArrayMap.this.value, oldPos, tail);
            Float2ByteArrayMap.this.size--;
            return true;
        }

    }

}

