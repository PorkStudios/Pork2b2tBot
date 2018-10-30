/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2LongMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Reference2LongArrayMap<K>
extends AbstractReference2LongMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient long[] value;
    private int size;

    public Reference2LongArrayMap(Object[] key, long[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Reference2LongArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = LongArrays.EMPTY_ARRAY;
    }

    public Reference2LongArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new long[capacity];
    }

    public Reference2LongArrayMap(Reference2LongMap<K> m) {
        this(m.size());
        this.putAll(m);
    }

    public Reference2LongArrayMap(Map<? extends K, ? extends Long> m) {
        this(m.size());
        this.putAll(m);
    }

    public Reference2LongArrayMap(Object[] key, long[] value, int size) {
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

    public Reference2LongMap.FastEntrySet<K> reference2LongEntrySet() {
        return new EntrySet();
    }

    private int findKey(Object k) {
        Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public long getLong(Object k) {
        Object[] key = this.key;
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
    public ReferenceSet<K> keySet() {
        return new AbstractReferenceSet<K>(){

            @Override
            public boolean contains(Object k) {
                return Reference2LongArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(Object k) {
                int oldPos = Reference2LongArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Reference2LongArrayMap.this.size - oldPos - 1;
                System.arraycopy(Reference2LongArrayMap.this.key, oldPos + 1, Reference2LongArrayMap.this.key, oldPos, tail);
                System.arraycopy(Reference2LongArrayMap.this.value, oldPos + 1, Reference2LongArrayMap.this.value, oldPos, tail);
                Reference2LongArrayMap.this.size--;
                return true;
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Reference2LongArrayMap.this.size;
                    }

                    @Override
                    public K next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (K)Reference2LongArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Reference2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Reference2LongArrayMap.this.key, this.pos, Reference2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Reference2LongArrayMap.this.value, this.pos, Reference2LongArrayMap.this.value, this.pos - 1, tail);
                        Reference2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Reference2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Reference2LongArrayMap.this.clear();
            }

        };
    }

    @Override
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long v) {
                return Reference2LongArrayMap.this.containsValue(v);
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Reference2LongArrayMap.this.size;
                    }

                    @Override
                    public long nextLong() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Reference2LongArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Reference2LongArrayMap.this.size - this.pos;
                        System.arraycopy(Reference2LongArrayMap.this.key, this.pos, Reference2LongArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Reference2LongArrayMap.this.value, this.pos, Reference2LongArrayMap.this.value, this.pos - 1, tail);
                        Reference2LongArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Reference2LongArrayMap.this.size;
            }

            @Override
            public void clear() {
                Reference2LongArrayMap.this.clear();
            }

        };
    }

    public Reference2LongArrayMap<K> clone() {
        Reference2LongArrayMap c;
        try {
            c = (Reference2LongArrayMap)Object.super.clone();
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
    extends AbstractObjectSet<Reference2LongMap.Entry<K>>
    implements Reference2LongMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Reference2LongMap.Entry<K>> iterator() {
            return new ObjectIterator<Reference2LongMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Reference2LongArrayMap.this.size;
                }

                @Override
                public Reference2LongMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractReference2LongMap.BasicEntry<Object>(Reference2LongArrayMap.this.key[this.curr], Reference2LongArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Reference2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Reference2LongArrayMap.this.key, this.next + 1, Reference2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Reference2LongArrayMap.this.value, this.next + 1, Reference2LongArrayMap.this.value, this.next, tail);
                    Reference2LongArrayMap.access$100((Reference2LongArrayMap)Reference2LongArrayMap.this)[Reference2LongArrayMap.access$000((Reference2LongArrayMap)Reference2LongArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Reference2LongMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Reference2LongMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractReference2LongMap.BasicEntry<K> entry = new AbstractReference2LongMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Reference2LongArrayMap.this.size;
                }

                @Override
                public Reference2LongMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Reference2LongArrayMap.this.key[this.curr];
                    this.entry.value = Reference2LongArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Reference2LongArrayMap.this.size-- - this.next--;
                    System.arraycopy(Reference2LongArrayMap.this.key, this.next + 1, Reference2LongArrayMap.this.key, this.next, tail);
                    System.arraycopy(Reference2LongArrayMap.this.value, this.next + 1, Reference2LongArrayMap.this.value, this.next, tail);
                    Reference2LongArrayMap.access$100((Reference2LongArrayMap)Reference2LongArrayMap.this)[Reference2LongArrayMap.access$000((Reference2LongArrayMap)Reference2LongArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Reference2LongArrayMap.this.size;
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
            return Reference2LongArrayMap.this.containsKey(k) && Reference2LongArrayMap.this.getLong(k) == ((Long)e.getValue()).longValue();
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
            int oldPos = Reference2LongArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Reference2LongArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Reference2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2LongArrayMap.this.key, oldPos + 1, Reference2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2LongArrayMap.this.value, oldPos + 1, Reference2LongArrayMap.this.value, oldPos, tail);
            Reference2LongArrayMap.this.size--;
            Reference2LongArrayMap.access$100((Reference2LongArrayMap)Reference2LongArrayMap.this)[Reference2LongArrayMap.access$000((Reference2LongArrayMap)Reference2LongArrayMap.this)] = null;
            return true;
        }

    }

}

