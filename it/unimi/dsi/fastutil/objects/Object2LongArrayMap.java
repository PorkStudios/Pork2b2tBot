/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2LongMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
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

public class Object2LongArrayMap<K>
extends AbstractObject2LongMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient long[] value;
    private int size;

    public Object2LongArrayMap(Object[] key, long[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Object2LongArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = LongArrays.EMPTY_ARRAY;
    }

    public Object2LongArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new long[capacity];
    }

    public Object2LongArrayMap(Object2LongMap<K> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2LongArrayMap(Map<? extends K, ? extends Long> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2LongArrayMap(Object[] key, long[] value, int size) {
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

    public Object2LongMap.FastEntrySet<K> object2LongEntrySet() {
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
    public long getLong(Object k) {
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
    public long put(K k, long v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            long oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
    public long removeLong(Object k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        long oldValue = this.value[oldPos];
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
                return Object2LongArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(Object k) {
                int oldPos = Object2LongArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Object2LongArrayMap.this.size - oldPos - 1;
                System.arraycopy(Object2LongArrayMap.this.key, oldPos + 1, Object2LongArrayMap.this.key, oldPos, tail);
                System.arraycopy(Object2LongArrayMap.this.value, oldPos + 1, Object2LongArrayMap.this.value, oldPos, tail);
                Object2LongArrayMap.this.size--;
                return true;
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2LongArrayMap.this.size;
                    }

                    @Override
                    public K next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (K)Object2LongArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Object2LongArrayMap.this.key, this.pos, Object2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2LongArrayMap.this.value, this.pos, Object2LongArrayMap.this.value, this.pos - 1, tail);
                        Object2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2LongArrayMap.this.clear();
            }

        };
    }

    @Override
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long v) {
                return Object2LongArrayMap.this.containsValue(v);
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2LongArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Object2LongArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Object2LongArrayMap.this.key, this.pos, Object2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2LongArrayMap.this.value, this.pos, Object2LongArrayMap.this.value, this.pos - 1, tail);
                        Object2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2LongArrayMap.this.clear();
            }

        };
    }

    public Object2LongArrayMap<K> clone() {
        Object2LongArrayMap c;
        try {
            c = (Object2LongArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (Object[])this.key.clone();
        c.value = (long[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.key[i]);
            s.writeLong(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new long[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readLong();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Object2LongMap.Entry<K>>
    implements Object2LongMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Object2LongMap.Entry<K>> iterator() {
            return new ObjectIterator<Object2LongMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Object2LongArrayMap.this.size;
                }

                @Override
                public Object2LongMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractObject2LongMap.BasicEntry<Object>(Object2LongArrayMap.this.key[this.curr], Object2LongArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2LongArrayMap.this.key, this.next + 1, Object2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2LongArrayMap.this.value, this.next + 1, Object2LongArrayMap.this.value, this.next, tail);
                    Object2LongArrayMap.access$100((Object2LongArrayMap)Object2LongArrayMap.this)[Object2LongArrayMap.access$000((Object2LongArrayMap)Object2LongArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Object2LongMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Object2LongMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractObject2LongMap.BasicEntry<K> entry = new AbstractObject2LongMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Object2LongArrayMap.this.size;
                }

                @Override
                public Object2LongMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Object2LongArrayMap.this.key[this.curr];
                    this.entry.value = Object2LongArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2LongArrayMap.this.key, this.next + 1, Object2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2LongArrayMap.this.value, this.next + 1, Object2LongArrayMap.this.value, this.next, tail);
                    Object2LongArrayMap.access$100((Object2LongArrayMap)Object2LongArrayMap.this)[Object2LongArrayMap.access$000((Object2LongArrayMap)Object2LongArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Object2LongArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            Object k = e.getKey();
            return Object2LongArrayMap.this.containsKey(k) && Object2LongArrayMap.this.getLong(k) == ((Long)e.getValue()).longValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            Object k = e.getKey();
            long v = (Long)e.getValue();
            int oldPos = Object2LongArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Object2LongArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Object2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2LongArrayMap.this.key, oldPos + 1, Object2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2LongArrayMap.this.value, oldPos + 1, Object2LongArrayMap.this.value, oldPos, tail);
            Object2LongArrayMap.this.size--;
            Object2LongArrayMap.access$100((Object2LongArrayMap)Object2LongArrayMap.this)[Object2LongArrayMap.access$000((Object2LongArrayMap)Object2LongArrayMap.this)] = null;
            return true;
        }

    }

}

