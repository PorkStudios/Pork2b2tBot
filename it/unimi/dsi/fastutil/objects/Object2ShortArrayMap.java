/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObject2ShortMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
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
import java.util.Objects;
import java.util.Set;

public class Object2ShortArrayMap<K>
extends AbstractObject2ShortMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient short[] value;
    private int size;

    public Object2ShortArrayMap(Object[] key, short[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Object2ShortArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = ShortArrays.EMPTY_ARRAY;
    }

    public Object2ShortArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new short[capacity];
    }

    public Object2ShortArrayMap(Object2ShortMap<K> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2ShortArrayMap(Map<? extends K, ? extends Short> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2ShortArrayMap(Object[] key, short[] value, int size) {
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

    public Object2ShortMap.FastEntrySet<K> object2ShortEntrySet() {
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
    public short getShort(Object k) {
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
    public short put(K k, short v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            short oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
    public short removeShort(Object k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        short oldValue = this.value[oldPos];
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
                return Object2ShortArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(Object k) {
                int oldPos = Object2ShortArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Object2ShortArrayMap.this.size - oldPos - 1;
                System.arraycopy(Object2ShortArrayMap.this.key, oldPos + 1, Object2ShortArrayMap.this.key, oldPos, tail);
                System.arraycopy(Object2ShortArrayMap.this.value, oldPos + 1, Object2ShortArrayMap.this.value, oldPos, tail);
                Object2ShortArrayMap.this.size--;
                return true;
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2ShortArrayMap.this.size;
                    }

                    @Override
                    public K next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (K)Object2ShortArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Object2ShortArrayMap.this.key, this.pos, Object2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2ShortArrayMap.this.value, this.pos, Object2ShortArrayMap.this.value, this.pos - 1, tail);
                        Object2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2ShortArrayMap.this.clear();
            }

        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short v) {
                return Object2ShortArrayMap.this.containsValue(v);
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2ShortArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Object2ShortArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Object2ShortArrayMap.this.key, this.pos, Object2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2ShortArrayMap.this.value, this.pos, Object2ShortArrayMap.this.value, this.pos - 1, tail);
                        Object2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2ShortArrayMap.this.clear();
            }

        };
    }

    public Object2ShortArrayMap<K> clone() {
        Object2ShortArrayMap c;
        try {
            c = (Object2ShortArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (Object[])this.key.clone();
        c.value = (short[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.key[i]);
            s.writeShort(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readShort();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Object2ShortMap.Entry<K>>
    implements Object2ShortMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Object2ShortMap.Entry<K>> iterator() {
            return new ObjectIterator<Object2ShortMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Object2ShortArrayMap.this.size;
                }

                @Override
                public Object2ShortMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractObject2ShortMap.BasicEntry<Object>(Object2ShortArrayMap.this.key[this.curr], Object2ShortArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2ShortArrayMap.this.key, this.next + 1, Object2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2ShortArrayMap.this.value, this.next + 1, Object2ShortArrayMap.this.value, this.next, tail);
                    Object2ShortArrayMap.access$100((Object2ShortArrayMap)Object2ShortArrayMap.this)[Object2ShortArrayMap.access$000((Object2ShortArrayMap)Object2ShortArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Object2ShortMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Object2ShortMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractObject2ShortMap.BasicEntry<K> entry = new AbstractObject2ShortMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Object2ShortArrayMap.this.size;
                }

                @Override
                public Object2ShortMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Object2ShortArrayMap.this.key[this.curr];
                    this.entry.value = Object2ShortArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2ShortArrayMap.this.key, this.next + 1, Object2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2ShortArrayMap.this.value, this.next + 1, Object2ShortArrayMap.this.value, this.next, tail);
                    Object2ShortArrayMap.access$100((Object2ShortArrayMap)Object2ShortArrayMap.this)[Object2ShortArrayMap.access$000((Object2ShortArrayMap)Object2ShortArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Object2ShortArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            Object k = e.getKey();
            return Object2ShortArrayMap.this.containsKey(k) && Object2ShortArrayMap.this.getShort(k) == ((Short)e.getValue()).shortValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            Object k = e.getKey();
            short v = (Short)e.getValue();
            int oldPos = Object2ShortArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Object2ShortArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Object2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2ShortArrayMap.this.key, oldPos + 1, Object2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2ShortArrayMap.this.value, oldPos + 1, Object2ShortArrayMap.this.value, oldPos, tail);
            Object2ShortArrayMap.this.size--;
            Object2ShortArrayMap.access$100((Object2ShortArrayMap)Object2ShortArrayMap.this)[Object2ShortArrayMap.access$000((Object2ShortArrayMap)Object2ShortArrayMap.this)] = null;
            return true;
        }

    }

}

