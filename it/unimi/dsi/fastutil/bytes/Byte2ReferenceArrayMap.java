/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
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

public class Byte2ReferenceArrayMap<V>
extends AbstractByte2ReferenceMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] key;
    private transient Object[] value;
    private int size;

    public Byte2ReferenceArrayMap(byte[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Byte2ReferenceArrayMap() {
        this.key = ByteArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Byte2ReferenceArrayMap(int capacity) {
        this.key = new byte[capacity];
        this.value = new Object[capacity];
    }

    public Byte2ReferenceArrayMap(Byte2ReferenceMap<V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Byte2ReferenceArrayMap(Map<? extends Byte, ? extends V> m) {
        this(m.size());
        this.putAll(m);
    }

    public Byte2ReferenceArrayMap(byte[] key, Object[] value, int size) {
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

    public Byte2ReferenceMap.FastEntrySet<V> byte2ReferenceEntrySet() {
        return new EntrySet();
    }

    private int findKey(byte k) {
        byte[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public V get(byte k) {
        byte[] key = this.key;
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
    public boolean containsKey(byte k) {
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
    public V put(byte k, V v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
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
    public V remove(byte k) {
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
    public ByteSet keySet() {
        return new AbstractByteSet(){

            @Override
            public boolean contains(byte k) {
                return Byte2ReferenceArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(byte k) {
                int oldPos = Byte2ReferenceArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Byte2ReferenceArrayMap.this.size - oldPos - 1;
                System.arraycopy(Byte2ReferenceArrayMap.this.key, oldPos + 1, Byte2ReferenceArrayMap.this.key, oldPos, tail);
                System.arraycopy(Byte2ReferenceArrayMap.this.value, oldPos + 1, Byte2ReferenceArrayMap.this.value, oldPos, tail);
                Byte2ReferenceArrayMap.this.size--;
                return true;
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Byte2ReferenceArrayMap.this.size;
                    }

                    @Override
                    public byte nextByte() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Byte2ReferenceArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Byte2ReferenceArrayMap.this.size - this.pos;
                        System.arraycopy(Byte2ReferenceArrayMap.this.key, this.pos, Byte2ReferenceArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Byte2ReferenceArrayMap.this.value, this.pos, Byte2ReferenceArrayMap.this.value, this.pos - 1, tail);
                        Byte2ReferenceArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Byte2ReferenceArrayMap.this.size;
            }

            @Override
            public void clear() {
                Byte2ReferenceArrayMap.this.clear();
            }

        };
    }

    @Override
    public ReferenceCollection<V> values() {
        return new AbstractReferenceCollection<V>(){

            @Override
            public boolean contains(Object v) {
                return Byte2ReferenceArrayMap.this.containsValue(v);
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Byte2ReferenceArrayMap.this.size;
                    }

                    @Override
                    public V next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return (V)Byte2ReferenceArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Byte2ReferenceArrayMap.this.size - this.pos;
                        System.arraycopy(Byte2ReferenceArrayMap.this.key, this.pos, Byte2ReferenceArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Byte2ReferenceArrayMap.this.value, this.pos, Byte2ReferenceArrayMap.this.value, this.pos - 1, tail);
                        Byte2ReferenceArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Byte2ReferenceArrayMap.this.size;
            }

            @Override
            public void clear() {
                Byte2ReferenceArrayMap.this.clear();
            }

        };
    }

    public Byte2ReferenceArrayMap<V> clone() {
        Byte2ReferenceArrayMap c;
        try {
            c = (Byte2ReferenceArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (byte[])this.key.clone();
        c.value = (Object[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeByte(this.key[i]);
            s.writeObject(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new byte[this.size];
        this.value = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readByte();
            this.value[i] = s.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Byte2ReferenceMap.Entry<V>>
    implements Byte2ReferenceMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Byte2ReferenceMap.Entry<V>> iterator() {
            return new ObjectIterator<Byte2ReferenceMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ReferenceArrayMap.this.size;
                }

                @Override
                public Byte2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractByte2ReferenceMap.BasicEntry<Object>(Byte2ReferenceArrayMap.this.key[this.curr], Byte2ReferenceArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ReferenceArrayMap.this.key, this.next + 1, Byte2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ReferenceArrayMap.this.value, this.next + 1, Byte2ReferenceArrayMap.this.value, this.next, tail);
                    Byte2ReferenceArrayMap.access$200((Byte2ReferenceArrayMap)Byte2ReferenceArrayMap.this)[Byte2ReferenceArrayMap.access$000((Byte2ReferenceArrayMap)Byte2ReferenceArrayMap.this)] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Byte2ReferenceMap.Entry<V>> fastIterator() {
            return new ObjectIterator<Byte2ReferenceMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractByte2ReferenceMap.BasicEntry<V> entry = new AbstractByte2ReferenceMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ReferenceArrayMap.this.size;
                }

                @Override
                public Byte2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Byte2ReferenceArrayMap.this.key[this.curr];
                    this.entry.value = Byte2ReferenceArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ReferenceArrayMap.this.key, this.next + 1, Byte2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ReferenceArrayMap.this.value, this.next + 1, Byte2ReferenceArrayMap.this.value, this.next, tail);
                    Byte2ReferenceArrayMap.access$200((Byte2ReferenceArrayMap)Byte2ReferenceArrayMap.this)[Byte2ReferenceArrayMap.access$000((Byte2ReferenceArrayMap)Byte2ReferenceArrayMap.this)] = null;
                }
            };
        }

        @Override
        public int size() {
            return Byte2ReferenceArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            return Byte2ReferenceArrayMap.this.containsKey(k) && Byte2ReferenceArrayMap.this.get(k) == e.getValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            Object v = e.getValue();
            int oldPos = Byte2ReferenceArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Byte2ReferenceArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Byte2ReferenceArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2ReferenceArrayMap.this.key, oldPos + 1, Byte2ReferenceArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2ReferenceArrayMap.this.value, oldPos + 1, Byte2ReferenceArrayMap.this.value, oldPos, tail);
            Byte2ReferenceArrayMap.this.size--;
            Byte2ReferenceArrayMap.access$200((Byte2ReferenceArrayMap)Byte2ReferenceArrayMap.this)[Byte2ReferenceArrayMap.access$000((Byte2ReferenceArrayMap)Byte2ReferenceArrayMap.this)] = null;
            return true;
        }

    }

}

