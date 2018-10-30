/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2BooleanMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
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

public class Long2BooleanArrayMap
extends AbstractLong2BooleanMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient boolean[] value;
    private int size;

    public Long2BooleanArrayMap(long[] key, boolean[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Long2BooleanArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = BooleanArrays.EMPTY_ARRAY;
    }

    public Long2BooleanArrayMap(int capacity) {
        this.key = new long[capacity];
        this.value = new boolean[capacity];
    }

    public Long2BooleanArrayMap(Long2BooleanMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2BooleanArrayMap(Map<? extends Long, ? extends Boolean> m) {
        this(m.size());
        this.putAll(m);
    }

    public Long2BooleanArrayMap(long[] key, boolean[] value, int size) {
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

    public Long2BooleanMap.FastEntrySet long2BooleanEntrySet() {
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
    public boolean get(long k) {
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
    public boolean containsValue(boolean v) {
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
    public boolean put(long k, boolean v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            boolean oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
            boolean[] newValue = new boolean[this.size == 0 ? 2 : this.size * 2];
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
    public boolean remove(long k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[oldPos];
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
                return Long2BooleanArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(long k) {
                int oldPos = Long2BooleanArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Long2BooleanArrayMap.this.size - oldPos - 1;
                System.arraycopy(Long2BooleanArrayMap.this.key, oldPos + 1, Long2BooleanArrayMap.this.key, oldPos, tail);
                System.arraycopy(Long2BooleanArrayMap.this.value, oldPos + 1, Long2BooleanArrayMap.this.value, oldPos, tail);
                Long2BooleanArrayMap.this.size--;
                return true;
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2BooleanArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2BooleanArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Long2BooleanArrayMap.this.key, this.pos, Long2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2BooleanArrayMap.this.value, this.pos, Long2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Long2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2BooleanArrayMap.this.clear();
            }

        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean v) {
                return Long2BooleanArrayMap.this.containsValue(v);
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Long2BooleanArrayMap.this.size;
                    }

                    @Override
                    public boolean nextBoolean() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Long2BooleanArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Long2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Long2BooleanArrayMap.this.key, this.pos, Long2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Long2BooleanArrayMap.this.value, this.pos, Long2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Long2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Long2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Long2BooleanArrayMap.this.clear();
            }

        };
    }

    public Long2BooleanArrayMap clone() {
        Long2BooleanArrayMap c;
        try {
            c = (Long2BooleanArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.value = (boolean[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.key[i]);
            s.writeBoolean(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new boolean[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readLong();
            this.value[i] = s.readBoolean();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Long2BooleanMap.Entry>
    implements Long2BooleanMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Long2BooleanMap.Entry> iterator() {
            return new ObjectIterator<Long2BooleanMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Long2BooleanArrayMap.this.size;
                }

                @Override
                public Long2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractLong2BooleanMap.BasicEntry(Long2BooleanArrayMap.this.key[this.curr], Long2BooleanArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2BooleanArrayMap.this.key, this.next + 1, Long2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2BooleanArrayMap.this.value, this.next + 1, Long2BooleanArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Long2BooleanMap.Entry> fastIterator() {
            return new ObjectIterator<Long2BooleanMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractLong2BooleanMap.BasicEntry entry = new AbstractLong2BooleanMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Long2BooleanArrayMap.this.size;
                }

                @Override
                public Long2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Long2BooleanArrayMap.this.key[this.curr];
                    this.entry.value = Long2BooleanArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2BooleanArrayMap.this.key, this.next + 1, Long2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2BooleanArrayMap.this.value, this.next + 1, Long2BooleanArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Long2BooleanArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            long k = (Long)e.getKey();
            return Long2BooleanArrayMap.this.containsKey(k) && Long2BooleanArrayMap.this.get(k) == ((Boolean)e.getValue()).booleanValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            long k = (Long)e.getKey();
            boolean v = (Boolean)e.getValue();
            int oldPos = Long2BooleanArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Long2BooleanArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Long2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2BooleanArrayMap.this.key, oldPos + 1, Long2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2BooleanArrayMap.this.value, oldPos + 1, Long2BooleanArrayMap.this.value, oldPos, tail);
            Long2BooleanArrayMap.this.size--;
            return true;
        }

    }

}

