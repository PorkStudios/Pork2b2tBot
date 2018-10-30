/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2FloatMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2FloatMap;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
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

public class Byte2FloatArrayMap
extends AbstractByte2FloatMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] key;
    private transient float[] value;
    private int size;

    public Byte2FloatArrayMap(byte[] key, float[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Byte2FloatArrayMap() {
        this.key = ByteArrays.EMPTY_ARRAY;
        this.value = FloatArrays.EMPTY_ARRAY;
    }

    public Byte2FloatArrayMap(int capacity) {
        this.key = new byte[capacity];
        this.value = new float[capacity];
    }

    public Byte2FloatArrayMap(Byte2FloatMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Byte2FloatArrayMap(Map<? extends Byte, ? extends Float> m) {
        this(m.size());
        this.putAll(m);
    }

    public Byte2FloatArrayMap(byte[] key, float[] value, int size) {
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

    public Byte2FloatMap.FastEntrySet byte2FloatEntrySet() {
        return new EntrySet();
    }

    private int findKey(byte k) {
        byte[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public float get(byte k) {
        byte[] key = this.key;
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
    public boolean containsKey(byte k) {
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
    public float put(byte k, float v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            float oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
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
    public float remove(byte k) {
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
    public ByteSet keySet() {
        return new AbstractByteSet(){

            @Override
            public boolean contains(byte k) {
                return Byte2FloatArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(byte k) {
                int oldPos = Byte2FloatArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Byte2FloatArrayMap.this.size - oldPos - 1;
                System.arraycopy(Byte2FloatArrayMap.this.key, oldPos + 1, Byte2FloatArrayMap.this.key, oldPos, tail);
                System.arraycopy(Byte2FloatArrayMap.this.value, oldPos + 1, Byte2FloatArrayMap.this.value, oldPos, tail);
                Byte2FloatArrayMap.this.size--;
                return true;
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Byte2FloatArrayMap.this.size;
                    }

                    @Override
                    public byte nextByte() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Byte2FloatArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Byte2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Byte2FloatArrayMap.this.key, this.pos, Byte2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Byte2FloatArrayMap.this.value, this.pos, Byte2FloatArrayMap.this.value, this.pos - 1, tail);
                        Byte2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Byte2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Byte2FloatArrayMap.this.clear();
            }

        };
    }

    @Override
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float v) {
                return Byte2FloatArrayMap.this.containsValue(v);
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Byte2FloatArrayMap.this.size;
                    }

                    @Override
                    public float nextFloat() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Byte2FloatArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Byte2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Byte2FloatArrayMap.this.key, this.pos, Byte2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Byte2FloatArrayMap.this.value, this.pos, Byte2FloatArrayMap.this.value, this.pos - 1, tail);
                        Byte2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Byte2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Byte2FloatArrayMap.this.clear();
            }

        };
    }

    public Byte2FloatArrayMap clone() {
        Byte2FloatArrayMap c;
        try {
            c = (Byte2FloatArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (byte[])this.key.clone();
        c.value = (float[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeByte(this.key[i]);
            s.writeFloat(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new byte[this.size];
        this.value = new float[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readByte();
            this.value[i] = s.readFloat();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Byte2FloatMap.Entry>
    implements Byte2FloatMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Byte2FloatMap.Entry> iterator() {
            return new ObjectIterator<Byte2FloatMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Byte2FloatArrayMap.this.size;
                }

                @Override
                public Byte2FloatMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractByte2FloatMap.BasicEntry(Byte2FloatArrayMap.this.key[this.curr], Byte2FloatArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2FloatArrayMap.this.key, this.next + 1, Byte2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2FloatArrayMap.this.value, this.next + 1, Byte2FloatArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Byte2FloatMap.Entry> fastIterator() {
            return new ObjectIterator<Byte2FloatMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractByte2FloatMap.BasicEntry entry = new AbstractByte2FloatMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Byte2FloatArrayMap.this.size;
                }

                @Override
                public Byte2FloatMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Byte2FloatArrayMap.this.key[this.curr];
                    this.entry.value = Byte2FloatArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2FloatArrayMap.this.key, this.next + 1, Byte2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2FloatArrayMap.this.value, this.next + 1, Byte2FloatArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Byte2FloatArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            return Byte2FloatArrayMap.this.containsKey(k) && Float.floatToIntBits(Byte2FloatArrayMap.this.get(k)) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            int oldPos = Byte2FloatArrayMap.this.findKey(k);
            if (oldPos == -1 || Float.floatToIntBits(v) != Float.floatToIntBits(Byte2FloatArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Byte2FloatArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2FloatArrayMap.this.key, oldPos + 1, Byte2FloatArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2FloatArrayMap.this.value, oldPos + 1, Byte2FloatArrayMap.this.value, oldPos, tail);
            Byte2FloatArrayMap.this.size--;
            return true;
        }

    }

}

