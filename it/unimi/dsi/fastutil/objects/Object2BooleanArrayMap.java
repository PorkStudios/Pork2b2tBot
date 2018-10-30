/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
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

public class Object2BooleanArrayMap<K>
extends AbstractObject2BooleanMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient boolean[] value;
    private int size;

    public Object2BooleanArrayMap(Object[] key, boolean[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Object2BooleanArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = BooleanArrays.EMPTY_ARRAY;
    }

    public Object2BooleanArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new boolean[capacity];
    }

    public Object2BooleanArrayMap(Object2BooleanMap<K> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2BooleanArrayMap(Map<? extends K, ? extends Boolean> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2BooleanArrayMap(Object[] key, boolean[] value, int size) {
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

    public Object2BooleanMap.FastEntrySet<K> object2BooleanEntrySet() {
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
    public boolean getBoolean(Object k) {
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
    public boolean put(K k, boolean v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            boolean oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
    public boolean removeBoolean(Object k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[oldPos];
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
                return Object2BooleanArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(Object k) {
                int oldPos = Object2BooleanArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Object2BooleanArrayMap.this.size - oldPos - 1;
                System.arraycopy(Object2BooleanArrayMap.this.key, oldPos + 1, Object2BooleanArrayMap.this.key, oldPos, tail);
                System.arraycopy(Object2BooleanArrayMap.this.value, oldPos + 1, Object2BooleanArrayMap.this.value, oldPos, tail);
                Object2BooleanArrayMap.this.size--;
                return true;
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2BooleanArrayMap.this.size;
                    }

                    @Override
                    public K next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (K)Object2BooleanArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Object2BooleanArrayMap.this.key, this.pos, Object2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2BooleanArrayMap.this.value, this.pos, Object2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Object2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2BooleanArrayMap.this.clear();
            }

        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean v) {
                return Object2BooleanArrayMap.this.containsValue(v);
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2BooleanArrayMap.this.size;
                    }

                    @Override
                    public boolean nextBoolean() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Object2BooleanArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Object2BooleanArrayMap.this.key, this.pos, Object2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2BooleanArrayMap.this.value, this.pos, Object2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Object2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2BooleanArrayMap.this.clear();
            }

        };
    }

    public Object2BooleanArrayMap<K> clone() {
        Object2BooleanArrayMap c;
        try {
            c = (Object2BooleanArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (Object[])this.key.clone();
        c.value = (boolean[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.key[i]);
            s.writeBoolean(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new boolean[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readBoolean();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Object2BooleanMap.Entry<K>>
    implements Object2BooleanMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Object2BooleanMap.Entry<K>> iterator() {
            return new ObjectIterator<Object2BooleanMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Object2BooleanArrayMap.this.size;
                }

                @Override
                public Object2BooleanMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractObject2BooleanMap.BasicEntry<Object>(Object2BooleanArrayMap.this.key[this.curr], Object2BooleanArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2BooleanArrayMap.this.key, this.next + 1, Object2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2BooleanArrayMap.this.value, this.next + 1, Object2BooleanArrayMap.this.value, this.next, tail);
                    Object2BooleanArrayMap.access$100((Object2BooleanArrayMap)Object2BooleanArrayMap.this)[Object2BooleanArrayMap.access$000((Object2BooleanArrayMap)Object2BooleanArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Object2BooleanMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Object2BooleanMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractObject2BooleanMap.BasicEntry<K> entry = new AbstractObject2BooleanMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Object2BooleanArrayMap.this.size;
                }

                @Override
                public Object2BooleanMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Object2BooleanArrayMap.this.key[this.curr];
                    this.entry.value = Object2BooleanArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2BooleanArrayMap.this.key, this.next + 1, Object2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2BooleanArrayMap.this.value, this.next + 1, Object2BooleanArrayMap.this.value, this.next, tail);
                    Object2BooleanArrayMap.access$100((Object2BooleanArrayMap)Object2BooleanArrayMap.this)[Object2BooleanArrayMap.access$000((Object2BooleanArrayMap)Object2BooleanArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Object2BooleanArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            return Object2BooleanArrayMap.this.containsKey(k) && Object2BooleanArrayMap.this.getBoolean(k) == ((Boolean)e.getValue()).booleanValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            boolean v = (Boolean)e.getValue();
            int oldPos = Object2BooleanArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Object2BooleanArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Object2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2BooleanArrayMap.this.key, oldPos + 1, Object2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2BooleanArrayMap.this.value, oldPos + 1, Object2BooleanArrayMap.this.value, oldPos, tail);
            Object2BooleanArrayMap.this.size--;
            Object2BooleanArrayMap.access$100((Object2BooleanArrayMap)Object2BooleanArrayMap.this)[Object2BooleanArrayMap.access$000((Object2BooleanArrayMap)Object2BooleanArrayMap.this)] = null;
            return true;
        }

    }

}

