/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
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

public class Reference2BooleanArrayMap<K>
extends AbstractReference2BooleanMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient boolean[] value;
    private int size;

    public Reference2BooleanArrayMap(Object[] key, boolean[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Reference2BooleanArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = BooleanArrays.EMPTY_ARRAY;
    }

    public Reference2BooleanArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new boolean[capacity];
    }

    public Reference2BooleanArrayMap(Reference2BooleanMap<K> m) {
        this(m.size());
        this.putAll(m);
    }

    public Reference2BooleanArrayMap(Map<? extends K, ? extends Boolean> m) {
        this(m.size());
        this.putAll(m);
    }

    public Reference2BooleanArrayMap(Object[] key, boolean[] value, int size) {
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

    public Reference2BooleanMap.FastEntrySet<K> reference2BooleanEntrySet() {
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
    public boolean getBoolean(Object k) {
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
    public ReferenceSet<K> keySet() {
        return new AbstractReferenceSet<K>(){

            @Override
            public boolean contains(Object k) {
                return Reference2BooleanArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(Object k) {
                int oldPos = Reference2BooleanArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Reference2BooleanArrayMap.this.size - oldPos - 1;
                System.arraycopy(Reference2BooleanArrayMap.this.key, oldPos + 1, Reference2BooleanArrayMap.this.key, oldPos, tail);
                System.arraycopy(Reference2BooleanArrayMap.this.value, oldPos + 1, Reference2BooleanArrayMap.this.value, oldPos, tail);
                Reference2BooleanArrayMap.this.size--;
                return true;
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Reference2BooleanArrayMap.this.size;
                    }

                    @Override
                    public K next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (K)Reference2BooleanArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Reference2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Reference2BooleanArrayMap.this.key, this.pos, Reference2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Reference2BooleanArrayMap.this.value, this.pos, Reference2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Reference2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Reference2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Reference2BooleanArrayMap.this.clear();
            }

        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean v) {
                return Reference2BooleanArrayMap.this.containsValue(v);
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Reference2BooleanArrayMap.this.size;
                    }

                    @Override
                    public boolean nextBoolean() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Reference2BooleanArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Reference2BooleanArrayMap.this.size - this.pos;
                        System.arraycopy(Reference2BooleanArrayMap.this.key, this.pos, Reference2BooleanArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Reference2BooleanArrayMap.this.value, this.pos, Reference2BooleanArrayMap.this.value, this.pos - 1, tail);
                        Reference2BooleanArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Reference2BooleanArrayMap.this.size;
            }

            @Override
            public void clear() {
                Reference2BooleanArrayMap.this.clear();
            }

        };
    }

    public Reference2BooleanArrayMap<K> clone() {
        Reference2BooleanArrayMap c;
        try {
            c = (Reference2BooleanArrayMap)Object.super.clone();
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
    extends AbstractObjectSet<Reference2BooleanMap.Entry<K>>
    implements Reference2BooleanMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Reference2BooleanMap.Entry<K>> iterator() {
            return new ObjectIterator<Reference2BooleanMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Reference2BooleanArrayMap.this.size;
                }

                @Override
                public Reference2BooleanMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractReference2BooleanMap.BasicEntry<Object>(Reference2BooleanArrayMap.this.key[this.curr], Reference2BooleanArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Reference2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Reference2BooleanArrayMap.this.key, this.next + 1, Reference2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Reference2BooleanArrayMap.this.value, this.next + 1, Reference2BooleanArrayMap.this.value, this.next, tail);
                    Reference2BooleanArrayMap.access$100((Reference2BooleanArrayMap)Reference2BooleanArrayMap.this)[Reference2BooleanArrayMap.access$000((Reference2BooleanArrayMap)Reference2BooleanArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Reference2BooleanMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Reference2BooleanMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractReference2BooleanMap.BasicEntry<K> entry = new AbstractReference2BooleanMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Reference2BooleanArrayMap.this.size;
                }

                @Override
                public Reference2BooleanMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Reference2BooleanArrayMap.this.key[this.curr];
                    this.entry.value = Reference2BooleanArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Reference2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Reference2BooleanArrayMap.this.key, this.next + 1, Reference2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Reference2BooleanArrayMap.this.value, this.next + 1, Reference2BooleanArrayMap.this.value, this.next, tail);
                    Reference2BooleanArrayMap.access$100((Reference2BooleanArrayMap)Reference2BooleanArrayMap.this)[Reference2BooleanArrayMap.access$000((Reference2BooleanArrayMap)Reference2BooleanArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Reference2BooleanArrayMap.this.size;
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
            return Reference2BooleanArrayMap.this.containsKey(k) && Reference2BooleanArrayMap.this.getBoolean(k) == ((Boolean)e.getValue()).booleanValue();
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
            int oldPos = Reference2BooleanArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Reference2BooleanArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Reference2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2BooleanArrayMap.this.key, oldPos + 1, Reference2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2BooleanArrayMap.this.value, oldPos + 1, Reference2BooleanArrayMap.this.value, oldPos, tail);
            Reference2BooleanArrayMap.this.size--;
            Reference2BooleanArrayMap.access$100((Reference2BooleanArrayMap)Reference2BooleanArrayMap.this)[Reference2BooleanArrayMap.access$000((Reference2BooleanArrayMap)Reference2BooleanArrayMap.this)] = null;
            return true;
        }

    }

}

