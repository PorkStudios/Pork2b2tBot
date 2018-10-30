/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2ObjectMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
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

public class Long2ObjectArrayMap<V>
extends AbstractLong2ObjectMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient Object[] value;
    private int size;

    public Long2ObjectArrayMap(long[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Long2ObjectArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Long2ObjectArrayMap(int capacity) {
        this.key = new long[capacity];
        this.value = new Object[capacity];
    }

    public Long2ObjectArrayMap(Long2ObjectMap<V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2ObjectArrayMap(Map<? extends Long, ? extends V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2ObjectArrayMap(long[] key, Object[] value, int size) {
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

    public Long2ObjectMap.FastEntrySet<V> long2ObjectEntrySet() {
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
    public V get(long k) {
        long[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return (V)this.value[i];
        }
        return (V)this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        int i = this.size;
        while (i-- != 0) {
            this.value[i] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean containsKey(long k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(Object v) {
        int i = this.size;
        while (i-- != 0) {
            if (!Objects.equals(this.value[i], v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public V put(long k, V v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
            Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];
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
        return (V)this.defRetValue;
    }

    @Override
    public V remove(long k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return (V)this.defRetValue;
        }
        Object oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.value[this.size] = null;
        return (V)oldValue;
    }

    @Override
    public LongSet keySet() {
        return new AbstractLongSet(){

            @Override
            public boolean contains(long k) {
                return Long2ObjectArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(long k) {
                int oldPos = Long2ObjectArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Long2ObjectArrayMap.this.size - oldPos - 1;
                System.arraycopy(Long2ObjectArrayMap.this.key, oldPos + 1, Long2ObjectArrayMap.this.key, oldPos, tail);
                System.arraycopy(Long2ObjectArrayMap.this.value, oldPos + 1, Long2ObjectArrayMap.this.value, oldPos, tail);
                Long2ObjectArrayMap.this.size--;
                return true;
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2ObjectArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2ObjectArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2ObjectArrayMap.this.size - this.pos;
                        System.arraycopy(Long2ObjectArrayMap.this.key, this.pos, Long2ObjectArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2ObjectArrayMap.this.value, this.pos, Long2ObjectArrayMap.this.value, this.pos - 1, tail);
                        Long2ObjectArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2ObjectArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2ObjectArrayMap.this.clear();
            }

        };
    }

    @Override
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>(){

            @Override
            public boolean contains(Object v) {
                return Long2ObjectArrayMap.this.containsValue(v);
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2ObjectArrayMap.this.size;
                    }

                    @Override
                    public V next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (V)Long2ObjectArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2ObjectArrayMap.this.size - this.pos;
                        System.arraycopy(Long2ObjectArrayMap.this.key, this.pos, Long2ObjectArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2ObjectArrayMap.this.value, this.pos, Long2ObjectArrayMap.this.value, this.pos - 1, tail);
                        Long2ObjectArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2ObjectArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2ObjectArrayMap.this.clear();
            }

        };
    }

    public Long2ObjectArrayMap<V> clone() {
        Long2ObjectArrayMap c;
        try {
            c = (Long2ObjectArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.value = (Object[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.key[i]);
            s.writeObject(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readLong();
            this.value[i] = s.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Long2ObjectMap.Entry<V>>
    implements Long2ObjectMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
            return new ObjectIterator<Long2ObjectMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Long2ObjectArrayMap.this.size;
                }

                @Override
                public Long2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractLong2ObjectMap.BasicEntry<Object>(Long2ObjectArrayMap.this.key[this.curr], Long2ObjectArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ObjectArrayMap.this.key, this.next + 1, Long2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ObjectArrayMap.this.value, this.next + 1, Long2ObjectArrayMap.this.value, this.next, tail);
                    Long2ObjectArrayMap.access$200((Long2ObjectArrayMap)Long2ObjectArrayMap.this)[Long2ObjectArrayMap.access$000((Long2ObjectArrayMap)Long2ObjectArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Long2ObjectMap.Entry<V>> fastIterator() {
            return new ObjectIterator<Long2ObjectMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractLong2ObjectMap.BasicEntry<V> entry = new AbstractLong2ObjectMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Long2ObjectArrayMap.this.size;
                }

                @Override
                public Long2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Long2ObjectArrayMap.this.key[this.curr];
                    this.entry.value = Long2ObjectArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ObjectArrayMap.this.key, this.next + 1, Long2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ObjectArrayMap.this.value, this.next + 1, Long2ObjectArrayMap.this.value, this.next, tail);
                    Long2ObjectArrayMap.access$200((Long2ObjectArrayMap)Long2ObjectArrayMap.this)[Long2ObjectArrayMap.access$000((Long2ObjectArrayMap)Long2ObjectArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Long2ObjectArrayMap.this.size;
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
            long k = (Long)e.getKey();
            return Long2ObjectArrayMap.this.containsKey(k) && Objects.equals(Long2ObjectArrayMap.this.get(k), e.getValue());
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
            long k = (Long)e.getKey();
            Object v = e.getValue();
            int oldPos = Long2ObjectArrayMap.this.findKey(k);
            if (oldPos == -1 || !Objects.equals(v, Long2ObjectArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Long2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2ObjectArrayMap.this.key, oldPos + 1, Long2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2ObjectArrayMap.this.value, oldPos + 1, Long2ObjectArrayMap.this.value, oldPos, tail);
            Long2ObjectArrayMap.this.size--;
            Long2ObjectArrayMap.access$200((Long2ObjectArrayMap)Long2ObjectArrayMap.this)[Long2ObjectArrayMap.access$000((Long2ObjectArrayMap)Long2ObjectArrayMap.this)] = null;
            return true;
        }

    }

}

