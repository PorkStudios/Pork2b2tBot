/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Byte2ShortArrayMap
extends AbstractByte2ShortMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] key;
    private transient short[] value;
    private int size;

    public Byte2ShortArrayMap(byte[] key, short[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Byte2ShortArrayMap() {
        this.key = ByteArrays.EMPTY_ARRAY;
        this.value = ShortArrays.EMPTY_ARRAY;
    }

    public Byte2ShortArrayMap(int capacity) {
        this.key = new byte[capacity];
        this.value = new short[capacity];
    }

    public Byte2ShortArrayMap(Byte2ShortMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Byte2ShortArrayMap(Map<? extends Byte, ? extends Short> m) {
        this(m.size());
        this.putAll(m);
    }

    public Byte2ShortArrayMap(byte[] key, short[] value, int size) {
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

    public Byte2ShortMap.FastEntrySet byte2ShortEntrySet() {
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
    public short get(byte k) {
        byte[] key = this.key;
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
    public boolean containsKey(byte k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(short v) {
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
    public short put(byte k, short v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            short oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
            short[] newValue = new short[this.size == 0 ? 2 : this.size * 2];
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
    public short remove(byte k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        short oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public ByteSet keySet() {
        return new AbstractByteSet(){

            @Override
            public boolean contains(byte k) {
                return Byte2ShortArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(byte k) {
                int oldPos = Byte2ShortArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Byte2ShortArrayMap.this.size - oldPos - 1;
                System.arraycopy(Byte2ShortArrayMap.this.key, oldPos + 1, Byte2ShortArrayMap.this.key, oldPos, tail);
                System.arraycopy(Byte2ShortArrayMap.this.value, oldPos + 1, Byte2ShortArrayMap.this.value, oldPos, tail);
                Byte2ShortArrayMap.this.size--;
                return true;
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Byte2ShortArrayMap.this.size;
                    }

                    @Override
                    public byte nextByte() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Byte2ShortArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Byte2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Byte2ShortArrayMap.this.key, this.pos, Byte2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Byte2ShortArrayMap.this.value, this.pos, Byte2ShortArrayMap.this.value, this.pos - 1, tail);
                        Byte2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Byte2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Byte2ShortArrayMap.this.clear();
            }

        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short v) {
                return Byte2ShortArrayMap.this.containsValue(v);
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Byte2ShortArrayMap.this.size;
                    }

                    @Override
                    public short nextShort() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Byte2ShortArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Byte2ShortArrayMap.this.size - this.pos;
                        System.arraycopy(Byte2ShortArrayMap.this.key, this.pos, Byte2ShortArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Byte2ShortArrayMap.this.value, this.pos, Byte2ShortArrayMap.this.value, this.pos - 1, tail);
                        Byte2ShortArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Byte2ShortArrayMap.this.size;
            }

            @Override
            public void clear() {
                Byte2ShortArrayMap.this.clear();
            }

        };
    }

    public Byte2ShortArrayMap clone() {
        Byte2ShortArrayMap c;
        try {
            c = (Byte2ShortArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (byte[])this.key.clone();
        c.value = (short[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeByte(this.key[i]);
            s.writeShort(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new byte[this.size];
        this.value = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readByte();
            this.value[i] = s.readShort();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Byte2ShortMap.Entry>
    implements Byte2ShortMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Byte2ShortMap.Entry> iterator() {
            return new ObjectIterator<Byte2ShortMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ShortArrayMap.this.size;
                }

                @Override
                public Byte2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractByte2ShortMap.BasicEntry(Byte2ShortArrayMap.this.key[this.curr], Byte2ShortArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ShortArrayMap.this.key, this.next + 1, Byte2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ShortArrayMap.this.value, this.next + 1, Byte2ShortArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Byte2ShortMap.Entry> fastIterator() {
            return new ObjectIterator<Byte2ShortMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractByte2ShortMap.BasicEntry entry = new AbstractByte2ShortMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ShortArrayMap.this.size;
                }

                @Override
                public Byte2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Byte2ShortArrayMap.this.key[this.curr];
                    this.entry.value = Byte2ShortArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ShortArrayMap.this.key, this.next + 1, Byte2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ShortArrayMap.this.value, this.next + 1, Byte2ShortArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Byte2ShortArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            return Byte2ShortArrayMap.this.containsKey(k) && Byte2ShortArrayMap.this.get(k) == ((Short)e.getValue()).shortValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            short v = (Short)e.getValue();
            int oldPos = Byte2ShortArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Byte2ShortArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Byte2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2ShortArrayMap.this.key, oldPos + 1, Byte2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2ShortArrayMap.this.value, oldPos + 1, Byte2ShortArrayMap.this.value, oldPos, tail);
            Byte2ShortArrayMap.this.size--;
            return true;
        }

    }

}

