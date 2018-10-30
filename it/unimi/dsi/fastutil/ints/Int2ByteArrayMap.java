/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2ByteMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
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

public class Int2ByteArrayMap
extends AbstractInt2ByteMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient int[] key;
    private transient byte[] value;
    private int size;

    public Int2ByteArrayMap(int[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Int2ByteArrayMap() {
        this.key = IntArrays.EMPTY_ARRAY;
        this.value = ByteArrays.EMPTY_ARRAY;
    }

    public Int2ByteArrayMap(int capacity) {
        this.key = new int[capacity];
        this.value = new byte[capacity];
    }

    public Int2ByteArrayMap(Int2ByteMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Int2ByteArrayMap(Map<? extends Integer, ? extends Byte> m) {
        this(m.size());
        this.putAll(m);
    }

    public Int2ByteArrayMap(int[] key, byte[] value, int size) {
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

    public Int2ByteMap.FastEntrySet int2ByteEntrySet() {
        return new EntrySet();
    }

    private int findKey(int k) {
        int[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public byte get(int k) {
        int[] key = this.key;
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
    public boolean containsKey(int k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(byte v) {
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
    public byte put(int k, byte v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            byte oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            int[] newKey = new int[this.size == 0 ? 2 : this.size * 2];
            byte[] newValue = new byte[this.size == 0 ? 2 : this.size * 2];
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
    public byte remove(int k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        byte oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public IntSet keySet() {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int k) {
                return Int2ByteArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(int k) {
                int oldPos = Int2ByteArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Int2ByteArrayMap.this.size - oldPos - 1;
                System.arraycopy(Int2ByteArrayMap.this.key, oldPos + 1, Int2ByteArrayMap.this.key, oldPos, tail);
                System.arraycopy(Int2ByteArrayMap.this.value, oldPos + 1, Int2ByteArrayMap.this.value, oldPos, tail);
                Int2ByteArrayMap.this.size--;
                return true;
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Int2ByteArrayMap.this.size;
                    }

                    @Override
                    public int nextInt() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Int2ByteArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Int2ByteArrayMap.this.size - this.pos;
                        System.arraycopy(Int2ByteArrayMap.this.key, this.pos, Int2ByteArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Int2ByteArrayMap.this.value, this.pos, Int2ByteArrayMap.this.value, this.pos - 1, tail);
                        Int2ByteArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Int2ByteArrayMap.this.size;
            }

            @Override
            public void clear() {
                Int2ByteArrayMap.this.clear();
            }

        };
    }

    @Override
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte v) {
                return Int2ByteArrayMap.this.containsValue(v);
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Int2ByteArrayMap.this.size;
                    }

                    @Override
                    public byte nextByte() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Int2ByteArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Int2ByteArrayMap.this.size - this.pos;
                        System.arraycopy(Int2ByteArrayMap.this.key, this.pos, Int2ByteArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Int2ByteArrayMap.this.value, this.pos, Int2ByteArrayMap.this.value, this.pos - 1, tail);
                        Int2ByteArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Int2ByteArrayMap.this.size;
            }

            @Override
            public void clear() {
                Int2ByteArrayMap.this.clear();
            }

        };
    }

    public Int2ByteArrayMap clone() {
        Int2ByteArrayMap c;
        try {
            c = (Int2ByteArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (int[])this.key.clone();
        c.value = (byte[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeInt(this.key[i]);
            s.writeByte(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new int[this.size];
        this.value = new byte[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readInt();
            this.value[i] = s.readByte();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Int2ByteMap.Entry>
    implements Int2ByteMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Int2ByteMap.Entry> iterator() {
            return new ObjectIterator<Int2ByteMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Int2ByteArrayMap.this.size;
                }

                @Override
                public Int2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractInt2ByteMap.BasicEntry(Int2ByteArrayMap.this.key[this.curr], Int2ByteArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Int2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Int2ByteArrayMap.this.key, this.next + 1, Int2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Int2ByteArrayMap.this.value, this.next + 1, Int2ByteArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Int2ByteMap.Entry> fastIterator() {
            return new ObjectIterator<Int2ByteMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractInt2ByteMap.BasicEntry entry = new AbstractInt2ByteMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Int2ByteArrayMap.this.size;
                }

                @Override
                public Int2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Int2ByteArrayMap.this.key[this.curr];
                    this.entry.value = Int2ByteArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Int2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Int2ByteArrayMap.this.key, this.next + 1, Int2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Int2ByteArrayMap.this.value, this.next + 1, Int2ByteArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Int2ByteArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            int k = (Integer)e.getKey();
            return Int2ByteArrayMap.this.containsKey(k) && Int2ByteArrayMap.this.get(k) == ((Byte)e.getValue()).byteValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            int k = (Integer)e.getKey();
            byte v = (Byte)e.getValue();
            int oldPos = Int2ByteArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Int2ByteArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Int2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Int2ByteArrayMap.this.key, oldPos + 1, Int2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Int2ByteArrayMap.this.value, oldPos + 1, Int2ByteArrayMap.this.value, oldPos, tail);
            Int2ByteArrayMap.this.size--;
            return true;
        }

    }

}

