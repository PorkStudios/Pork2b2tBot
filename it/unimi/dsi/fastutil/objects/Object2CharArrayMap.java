/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2CharMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
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

public class Object2CharArrayMap<K>
extends AbstractObject2CharMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient char[] value;
    private int size;

    public Object2CharArrayMap(Object[] key, char[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Object2CharArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = CharArrays.EMPTY_ARRAY;
    }

    public Object2CharArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new char[capacity];
    }

    public Object2CharArrayMap(Object2CharMap<K> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2CharArrayMap(Map<? extends K, ? extends Character> m) {
        this(m.size());
        this.putAll(m);
    }

    public Object2CharArrayMap(Object[] key, char[] value, int size) {
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

    public Object2CharMap.FastEntrySet<K> object2CharEntrySet() {
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
    public char getChar(Object k) {
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
    public boolean containsValue(char v) {
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
    public char put(K k, char v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            char oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
            char[] newValue = new char[this.size == 0 ? 2 : this.size * 2];
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
    public char removeChar(Object k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        char oldValue = this.value[oldPos];
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
                return Object2CharArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(Object k) {
                int oldPos = Object2CharArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Object2CharArrayMap.this.size - oldPos - 1;
                System.arraycopy(Object2CharArrayMap.this.key, oldPos + 1, Object2CharArrayMap.this.key, oldPos, tail);
                System.arraycopy(Object2CharArrayMap.this.value, oldPos + 1, Object2CharArrayMap.this.value, oldPos, tail);
                Object2CharArrayMap.this.size--;
                return true;
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2CharArrayMap.this.size;
                    }

                    @Override
                    public K next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (K)Object2CharArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2CharArrayMap.this.size - this.pos;
                        System.arraycopy(Object2CharArrayMap.this.key, this.pos, Object2CharArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2CharArrayMap.this.value, this.pos, Object2CharArrayMap.this.value, this.pos - 1, tail);
                        Object2CharArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2CharArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2CharArrayMap.this.clear();
            }

        };
    }

    @Override
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char v) {
                return Object2CharArrayMap.this.containsValue(v);
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Object2CharArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Object2CharArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Object2CharArrayMap.this.size - this.pos;
                        System.arraycopy(Object2CharArrayMap.this.key, this.pos, Object2CharArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Object2CharArrayMap.this.value, this.pos, Object2CharArrayMap.this.value, this.pos - 1, tail);
                        Object2CharArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Object2CharArrayMap.this.size;
            }

            @Override
            public void clear() {
                Object2CharArrayMap.this.clear();
            }

        };
    }

    public Object2CharArrayMap<K> clone() {
        Object2CharArrayMap c;
        try {
            c = (Object2CharArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (Object[])this.key.clone();
        c.value = (char[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.key[i]);
            s.writeChar(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new char[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readChar();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Object2CharMap.Entry<K>>
    implements Object2CharMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Object2CharMap.Entry<K>> iterator() {
            return new ObjectIterator<Object2CharMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Object2CharArrayMap.this.size;
                }

                @Override
                public Object2CharMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractObject2CharMap.BasicEntry<Object>(Object2CharArrayMap.this.key[this.curr], Object2CharArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2CharArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2CharArrayMap.this.key, this.next + 1, Object2CharArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2CharArrayMap.this.value, this.next + 1, Object2CharArrayMap.this.value, this.next, tail);
                    Object2CharArrayMap.access$100((Object2CharArrayMap)Object2CharArrayMap.this)[Object2CharArrayMap.access$000((Object2CharArrayMap)Object2CharArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Object2CharMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Object2CharMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractObject2CharMap.BasicEntry<K> entry = new AbstractObject2CharMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Object2CharArrayMap.this.size;
                }

                @Override
                public Object2CharMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Object2CharArrayMap.this.key[this.curr];
                    this.entry.value = Object2CharArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2CharArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2CharArrayMap.this.key, this.next + 1, Object2CharArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2CharArrayMap.this.value, this.next + 1, Object2CharArrayMap.this.value, this.next, tail);
                    Object2CharArrayMap.access$100((Object2CharArrayMap)Object2CharArrayMap.this)[Object2CharArrayMap.access$000((Object2CharArrayMap)Object2CharArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Object2CharArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            Object k = e.getKey();
            return Object2CharArrayMap.this.containsKey(k) && Object2CharArrayMap.this.getChar(k) == ((Character)e.getValue()).charValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            Object k = e.getKey();
            char v = ((Character)e.getValue()).charValue();
            int oldPos = Object2CharArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Object2CharArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Object2CharArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2CharArrayMap.this.key, oldPos + 1, Object2CharArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2CharArrayMap.this.value, oldPos + 1, Object2CharArrayMap.this.value, oldPos, tail);
            Object2CharArrayMap.this.size--;
            Object2CharArrayMap.access$100((Object2CharArrayMap)Object2CharArrayMap.this)[Object2CharArrayMap.access$000((Object2CharArrayMap)Object2CharArrayMap.this)] = null;
            return true;
        }

    }

}

