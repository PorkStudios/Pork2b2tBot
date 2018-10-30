/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceMap;
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

public class Short2ReferenceArrayMap<V>
extends AbstractShort2ReferenceMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient short[] key;
    private transient Object[] value;
    private int size;

    public Short2ReferenceArrayMap(short[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Short2ReferenceArrayMap() {
        this.key = ShortArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Short2ReferenceArrayMap(int capacity) {
        this.key = new short[capacity];
        this.value = new Object[capacity];
    }

    public Short2ReferenceArrayMap(Short2ReferenceMap<V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2ReferenceArrayMap(Map<? extends Short, ? extends V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Short2ReferenceArrayMap(short[] key, Object[] value, int size) {
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

    public Short2ReferenceMap.FastEntrySet<V> short2ReferenceEntrySet() {
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
    public V get(short k) {
        short[] key = this.key;
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
    public boolean containsKey(short k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(Object v) {
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
    public V put(short k, V v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            short[] newKey = new short[this.size == 0 ? 2 : this.size * 2];
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
    public V remove(short k) {
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
    public ShortSet keySet() {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short k) {
                return Short2ReferenceArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(short k) {
                int oldPos = Short2ReferenceArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Short2ReferenceArrayMap.this.size - oldPos - 1;
                System.arraycopy(Short2ReferenceArrayMap.this.key, oldPos + 1, Short2ReferenceArrayMap.this.key, oldPos, tail);
                System.arraycopy(Short2ReferenceArrayMap.this.value, oldPos + 1, Short2ReferenceArrayMap.this.value, oldPos, tail);
                Short2ReferenceArrayMap.this.size--;
                return true;
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2ReferenceArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Short2ReferenceArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2ReferenceArrayMap.this.size - this.pos;
                        System.arraycopy(Short2ReferenceArrayMap.this.key, this.pos, Short2ReferenceArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2ReferenceArrayMap.this.value, this.pos, Short2ReferenceArrayMap.this.value, this.pos - 1, tail);
                        Short2ReferenceArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2ReferenceArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2ReferenceArrayMap.this.clear();
            }

        };
    }

    @Override
    public ReferenceCollection<V> values() {
        return new AbstractReferenceCollection<V>(){

            @Override
            public boolean contains(Object v) {
                return Short2ReferenceArrayMap.this.containsValue(v);
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Short2ReferenceArrayMap.this.size;
                    }

                    @Override
                    public V next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (V)Short2ReferenceArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Short2ReferenceArrayMap.this.size - this.pos;
                        System.arraycopy(Short2ReferenceArrayMap.this.key, this.pos, Short2ReferenceArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Short2ReferenceArrayMap.this.value, this.pos, Short2ReferenceArrayMap.this.value, this.pos - 1, tail);
                        Short2ReferenceArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Short2ReferenceArrayMap.this.size;
            }

            @Override
            public void clear() {
                Short2ReferenceArrayMap.this.clear();
            }

        };
    }

    public Short2ReferenceArrayMap<V> clone() {
        Short2ReferenceArrayMap c;
        try {
            c = (Short2ReferenceArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (short[])this.key.clone();
        c.value = (Object[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeShort(this.key[i]);
            s.writeObject(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new short[this.size];
        this.value = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readShort();
            this.value[i] = s.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Short2ReferenceMap.Entry<V>>
    implements Short2ReferenceMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Short2ReferenceMap.Entry<V>> iterator() {
            return new ObjectIterator<Short2ReferenceMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Short2ReferenceArrayMap.this.size;
                }

                @Override
                public Short2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractShort2ReferenceMap.BasicEntry<Object>(Short2ReferenceArrayMap.this.key[this.curr], Short2ReferenceArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2ReferenceArrayMap.this.key, this.next + 1, Short2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2ReferenceArrayMap.this.value, this.next + 1, Short2ReferenceArrayMap.this.value, this.next, tail);
                    Short2ReferenceArrayMap.access$200((Short2ReferenceArrayMap)Short2ReferenceArrayMap.this)[Short2ReferenceArrayMap.access$000((Short2ReferenceArrayMap)Short2ReferenceArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Short2ReferenceMap.Entry<V>> fastIterator() {
            return new ObjectIterator<Short2ReferenceMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractShort2ReferenceMap.BasicEntry<V> entry = new AbstractShort2ReferenceMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Short2ReferenceArrayMap.this.size;
                }

                @Override
                public Short2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Short2ReferenceArrayMap.this.key[this.curr];
                    this.entry.value = Short2ReferenceArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Short2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Short2ReferenceArrayMap.this.key, this.next + 1, Short2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Short2ReferenceArrayMap.this.value, this.next + 1, Short2ReferenceArrayMap.this.value, this.next, tail);
                    Short2ReferenceArrayMap.access$200((Short2ReferenceArrayMap)Short2ReferenceArrayMap.this)[Short2ReferenceArrayMap.access$000((Short2ReferenceArrayMap)Short2ReferenceArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Short2ReferenceArrayMap.this.size;
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
            short k = (Short)e.getKey();
            return Short2ReferenceArrayMap.this.containsKey(k) && Short2ReferenceArrayMap.this.get(k) == e.getValue();
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
            short k = (Short)e.getKey();
            Object v = e.getValue();
            int oldPos = Short2ReferenceArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Short2ReferenceArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Short2ReferenceArrayMap.this.size - oldPos - 1;
            System.arraycopy(Short2ReferenceArrayMap.this.key, oldPos + 1, Short2ReferenceArrayMap.this.key, oldPos, tail);
            System.arraycopy(Short2ReferenceArrayMap.this.value, oldPos + 1, Short2ReferenceArrayMap.this.value, oldPos, tail);
            Short2ReferenceArrayMap.this.size--;
            Short2ReferenceArrayMap.access$200((Short2ReferenceArrayMap)Short2ReferenceArrayMap.this)[Short2ReferenceArrayMap.access$000((Short2ReferenceArrayMap)Short2ReferenceArrayMap.this)] = null;
            return true;
        }

    }

}

