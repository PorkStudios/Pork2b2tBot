/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ReferenceMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2ReferenceMap;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Char2ReferenceArrayMap<V>
extends AbstractChar2ReferenceMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] key;
    private transient Object[] value;
    private int size;

    public Char2ReferenceArrayMap(char[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Char2ReferenceArrayMap() {
        this.key = CharArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Char2ReferenceArrayMap(int capacity) {
        this.key = new char[capacity];
        this.value = new Object[capacity];
    }

    public Char2ReferenceArrayMap(Char2ReferenceMap<V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2ReferenceArrayMap(Map<? extends Character, ? extends V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2ReferenceArrayMap(char[] key, Object[] value, int size) {
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

    public Char2ReferenceMap.FastEntrySet<V> char2ReferenceEntrySet() {
        return new EntrySet();
    }

    private int findKey(char k) {
        char[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public V get(char k) {
        char[] key = this.key;
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
    public boolean containsKey(char k) {
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
    public V put(char k, V v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
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
    public V remove(char k) {
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
    public CharSet keySet() {
        return new AbstractCharSet(){

            @Override
            public boolean contains(char k) {
                return Char2ReferenceArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(char k) {
                int oldPos = Char2ReferenceArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Char2ReferenceArrayMap.this.size - oldPos - 1;
                System.arraycopy(Char2ReferenceArrayMap.this.key, oldPos + 1, Char2ReferenceArrayMap.this.key, oldPos, tail);
                System.arraycopy(Char2ReferenceArrayMap.this.value, oldPos + 1, Char2ReferenceArrayMap.this.value, oldPos, tail);
                Char2ReferenceArrayMap.this.size--;
                return true;
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2ReferenceArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2ReferenceArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2ReferenceArrayMap.this.size - this.pos;
                        System.arraycopy(Char2ReferenceArrayMap.this.key, this.pos, Char2ReferenceArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2ReferenceArrayMap.this.value, this.pos, Char2ReferenceArrayMap.this.value, this.pos - 1, tail);
                        Char2ReferenceArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2ReferenceArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2ReferenceArrayMap.this.clear();
            }

        };
    }

    @Override
    public ReferenceCollection<V> values() {
        return new AbstractReferenceCollection<V>(){

            @Override
            public boolean contains(Object v) {
                return Char2ReferenceArrayMap.this.containsValue(v);
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2ReferenceArrayMap.this.size;
                    }

                    @Override
                    public V next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (V)Char2ReferenceArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2ReferenceArrayMap.this.size - this.pos;
                        System.arraycopy(Char2ReferenceArrayMap.this.key, this.pos, Char2ReferenceArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2ReferenceArrayMap.this.value, this.pos, Char2ReferenceArrayMap.this.value, this.pos - 1, tail);
                        Char2ReferenceArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2ReferenceArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2ReferenceArrayMap.this.clear();
            }

        };
    }

    public Char2ReferenceArrayMap<V> clone() {
        Char2ReferenceArrayMap c;
        try {
            c = (Char2ReferenceArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
        c.value = (Object[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.key[i]);
            s.writeObject(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new char[this.size];
        this.value = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readChar();
            this.value[i] = s.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Char2ReferenceMap.Entry<V>>
    implements Char2ReferenceMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Char2ReferenceMap.Entry<V>> iterator() {
            return new ObjectIterator<Char2ReferenceMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Char2ReferenceArrayMap.this.size;
                }

                @Override
                public Char2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractChar2ReferenceMap.BasicEntry<Object>(Char2ReferenceArrayMap.this.key[this.curr], Char2ReferenceArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2ReferenceArrayMap.this.key, this.next + 1, Char2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2ReferenceArrayMap.this.value, this.next + 1, Char2ReferenceArrayMap.this.value, this.next, tail);
                    Char2ReferenceArrayMap.access$200((Char2ReferenceArrayMap)Char2ReferenceArrayMap.this)[Char2ReferenceArrayMap.access$000((Char2ReferenceArrayMap)Char2ReferenceArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Char2ReferenceMap.Entry<V>> fastIterator() {
            return new ObjectIterator<Char2ReferenceMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractChar2ReferenceMap.BasicEntry<V> entry = new AbstractChar2ReferenceMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Char2ReferenceArrayMap.this.size;
                }

                @Override
                public Char2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Char2ReferenceArrayMap.this.key[this.curr];
                    this.entry.value = Char2ReferenceArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2ReferenceArrayMap.this.key, this.next + 1, Char2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2ReferenceArrayMap.this.value, this.next + 1, Char2ReferenceArrayMap.this.value, this.next, tail);
                    Char2ReferenceArrayMap.access$200((Char2ReferenceArrayMap)Char2ReferenceArrayMap.this)[Char2ReferenceArrayMap.access$000((Char2ReferenceArrayMap)Char2ReferenceArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Char2ReferenceArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            return Char2ReferenceArrayMap.this.containsKey(k) && Char2ReferenceArrayMap.this.get(k) == e.getValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            Object v = e.getValue();
            int oldPos = Char2ReferenceArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Char2ReferenceArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Char2ReferenceArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2ReferenceArrayMap.this.key, oldPos + 1, Char2ReferenceArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2ReferenceArrayMap.this.value, oldPos + 1, Char2ReferenceArrayMap.this.value, oldPos, tail);
            Char2ReferenceArrayMap.this.size--;
            Char2ReferenceArrayMap.access$200((Char2ReferenceArrayMap)Char2ReferenceArrayMap.this)[Char2ReferenceArrayMap.access$000((Char2ReferenceArrayMap)Char2ReferenceArrayMap.this)] = null;
            return true;
        }

    }

}

