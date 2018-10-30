/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
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
import java.util.Objects;
import java.util.Set;

public class Object2FloatArrayMap<K>
extends AbstractObject2FloatMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient float[] value;
    private int size;

    public Object2FloatArrayMap(Object[] key, float[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Object2FloatArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = FloatArrays.EMPTY_ARRAY;
    }

    public Object2FloatArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new float[capacity];
    }

    public Object2FloatArrayMap(Object2FloatMap<K> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2FloatArrayMap(Map<? extends K, ? extends Float> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2FloatArrayMap(Object[] key, float[] value, int size) {
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

    public Object2FloatMap.FastEntrySet<K> object2FloatEntrySet() {
        return new EntrySet();
    }

    private int findKey(Object k) {
        Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (!Objects.equals(key[i], k)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public float getFloat(Object k) {
        Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (!Objects.equals(key[i], k)) continue;
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
        int i = this.size;
        while (i-- != 0) {
            this.key[i] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object k) {
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
    public float put(K k, float v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            float oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
    public float removeFloat(Object k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        float oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.key[this.size] = null;
        return oldValue;
    }

    @Override
    public ObjectSet<K> keySet() {
        return new AbstractObjectSet<K>(){

            @Override
            public boolean contains(Object k) {
                return Object2FloatArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(Object k) {
                int oldPos = Object2FloatArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Object2FloatArrayMap.this.size - oldPos - 1;
                System.arraycopy(Object2FloatArrayMap.this.key, oldPos + 1, Object2FloatArrayMap.this.key, oldPos, tail);
                System.arraycopy(Object2FloatArrayMap.this.value, oldPos + 1, Object2FloatArrayMap.this.value, oldPos, tail);
                Object2FloatArrayMap.this.size--;
                return true;
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2FloatArrayMap.this.size;
                    }

                    @Override
                    public K next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (K)Object2FloatArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Object2FloatArrayMap.this.key, this.pos, Object2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2FloatArrayMap.this.value, this.pos, Object2FloatArrayMap.this.value, this.pos - 1, tail);
                        Object2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2FloatArrayMap.this.clear();
            }

        };
    }

    @Override
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float v) {
                return Object2FloatArrayMap.this.containsValue(v);
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2FloatArrayMap.this.size;
                    }

                    @Override
                    public float nextFloat() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Object2FloatArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2FloatArrayMap.this.size - this.pos;
                        System.arraycopy(Object2FloatArrayMap.this.key, this.pos, Object2FloatArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2FloatArrayMap.this.value, this.pos, Object2FloatArrayMap.this.value, this.pos - 1, tail);
                        Object2FloatArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2FloatArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2FloatArrayMap.this.clear();
            }

        };
    }

    public Object2FloatArrayMap<K> clone() {
        Object2FloatArrayMap c;
        try {
            c = (Object2FloatArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (Object[])this.key.clone();
        c.value = (float[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.key[i]);
            s.writeFloat(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new float[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readFloat();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Object2FloatMap.Entry<K>>
    implements Object2FloatMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Object2FloatMap.Entry<K>> iterator() {
            return new ObjectIterator<Object2FloatMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Object2FloatArrayMap.this.size;
                }

                @Override
                public Object2FloatMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractObject2FloatMap.BasicEntry<Object>(Object2FloatArrayMap.this.key[this.curr], Object2FloatArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2FloatArrayMap.this.key, this.next + 1, Object2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2FloatArrayMap.this.value, this.next + 1, Object2FloatArrayMap.this.value, this.next, tail);
                    Object2FloatArrayMap.access$100((Object2FloatArrayMap)Object2FloatArrayMap.this)[Object2FloatArrayMap.access$000((Object2FloatArrayMap)Object2FloatArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Object2FloatMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Object2FloatMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractObject2FloatMap.BasicEntry<K> entry = new AbstractObject2FloatMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Object2FloatArrayMap.this.size;
                }

                @Override
                public Object2FloatMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Object2FloatArrayMap.this.key[this.curr];
                    this.entry.value = Object2FloatArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2FloatArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2FloatArrayMap.this.key, this.next + 1, Object2FloatArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2FloatArrayMap.this.value, this.next + 1, Object2FloatArrayMap.this.value, this.next, tail);
                    Object2FloatArrayMap.access$100((Object2FloatArrayMap)Object2FloatArrayMap.this)[Object2FloatArrayMap.access$000((Object2FloatArrayMap)Object2FloatArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Object2FloatArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            Object k = e.getKey();
            return Object2FloatArrayMap.this.containsKey(k) && Float.floatToIntBits(Object2FloatArrayMap.this.getFloat(k)) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            Object k = e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            int oldPos = Object2FloatArrayMap.this.findKey(k);
            if (oldPos == -1 || Float.floatToIntBits(v) != Float.floatToIntBits(Object2FloatArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Object2FloatArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2FloatArrayMap.this.key, oldPos + 1, Object2FloatArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2FloatArrayMap.this.value, oldPos + 1, Object2FloatArrayMap.this.value, oldPos, tail);
            Object2FloatArrayMap.this.size--;
            Object2FloatArrayMap.access$100((Object2FloatArrayMap)Object2FloatArrayMap.this)[Object2FloatArrayMap.access$000((Object2FloatArrayMap)Object2FloatArrayMap.this)] = null;
            return true;
        }

    }

}

