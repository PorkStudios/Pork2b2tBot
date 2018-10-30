/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2DoubleMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2DoubleMap;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
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

public class Char2DoubleArrayMap
extends AbstractChar2DoubleMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] key;
    private transient double[] value;
    private int size;

    public Char2DoubleArrayMap(char[] key, double[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Char2DoubleArrayMap() {
        this.key = CharArrays.EMPTY_ARRAY;
        this.value = DoubleArrays.EMPTY_ARRAY;
    }

    public Char2DoubleArrayMap(int capacity) {
        this.key = new char[capacity];
        this.value = new double[capacity];
    }

    public Char2DoubleArrayMap(Char2DoubleMap m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2DoubleArrayMap(Map<? extends Character, ? extends Double> m) {
        this(m.size());
        this.putAll(m);
    }

    public Char2DoubleArrayMap(char[] key, double[] value, int size) {
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

    public Char2DoubleMap.FastEntrySet char2DoubleEntrySet() {
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
    public double get(char k) {
        char[] key = this.key;
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
    public boolean containsKey(char k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(double v) {
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(this.value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public double put(char k, double v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            double oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            double[] newValue = new double[this.size == 0 ? 2 : this.size * 2];
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
    public double remove(char k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        double oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public CharSet keySet() {
        return new AbstractCharSet(){

            @Override
            public boolean contains(char k) {
                return Char2DoubleArrayMap.this.findKey(k) != -1;
            }

            @Override
            public boolean remove(char k) {
                int oldPos = Char2DoubleArrayMap.this.findKey(k);
                if (oldPos == -1) {
                    return false;
                }
                int tail = Char2DoubleArrayMap.this.size - oldPos - 1;
                System.arraycopy(Char2DoubleArrayMap.this.key, oldPos + 1, Char2DoubleArrayMap.this.key, oldPos, tail);
                System.arraycopy(Char2DoubleArrayMap.this.value, oldPos + 1, Char2DoubleArrayMap.this.value, oldPos, tail);
                Char2DoubleArrayMap.this.size--;
                return true;
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2DoubleArrayMap.this.size;
                    }

                    @Override
                    public char nextChar() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2DoubleArrayMap.this.key[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2DoubleArrayMap.this.size - this.pos;
                        System.arraycopy(Char2DoubleArrayMap.this.key, this.pos, Char2DoubleArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2DoubleArrayMap.this.value, this.pos, Char2DoubleArrayMap.this.value, this.pos - 1, tail);
                        Char2DoubleArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2DoubleArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2DoubleArrayMap.this.clear();
            }

        };
    }

    @Override
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double v) {
                return Char2DoubleArrayMap.this.containsValue(v);
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return this.pos < Char2DoubleArrayMap.this.size;
                    }

                    @Override
                    public double nextDouble() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Char2DoubleArrayMap.this.value[this.pos++];
                    }

                    @Override
                    public void remove() {
                        if (this.pos == 0) {
                            throw new IllegalStateException();
                        }
                        int tail = Char2DoubleArrayMap.this.size - this.pos;
                        System.arraycopy(Char2DoubleArrayMap.this.key, this.pos, Char2DoubleArrayMap.this.key, this.pos - 1, tail);
                        System.arraycopy(Char2DoubleArrayMap.this.value, this.pos, Char2DoubleArrayMap.this.value, this.pos - 1, tail);
                        Char2DoubleArrayMap.this.size--;
                    }
                };
            }

            @Override
            public int size() {
                return Char2DoubleArrayMap.this.size;
            }

            @Override
            public void clear() {
                Char2DoubleArrayMap.this.clear();
            }

        };
    }

    public Char2DoubleArrayMap clone() {
        Char2DoubleArrayMap c;
        try {
            c = (Char2DoubleArrayMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
        c.value = (double[])this.value.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.key[i]);
            s.writeDouble(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new char[this.size];
        this.value = new double[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readChar();
            this.value[i] = s.readDouble();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Char2DoubleMap.Entry>
    implements Char2DoubleMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Char2DoubleMap.Entry> iterator() {
            return new ObjectIterator<Char2DoubleMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Char2DoubleArrayMap.this.size;
                }

                @Override
                public Char2DoubleMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractChar2DoubleMap.BasicEntry(Char2DoubleArrayMap.this.key[this.curr], Char2DoubleArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2DoubleArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2DoubleArrayMap.this.key, this.next + 1, Char2DoubleArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2DoubleArrayMap.this.value, this.next + 1, Char2DoubleArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Char2DoubleMap.Entry> fastIterator() {
            return new ObjectIterator<Char2DoubleMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractChar2DoubleMap.BasicEntry entry = new AbstractChar2DoubleMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Char2DoubleArrayMap.this.size;
                }

                @Override
                public Char2DoubleMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Char2DoubleArrayMap.this.key[this.curr];
                    this.entry.value = Char2DoubleArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2DoubleArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2DoubleArrayMap.this.key, this.next + 1, Char2DoubleArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2DoubleArrayMap.this.value, this.next + 1, Char2DoubleArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Char2DoubleArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            return Char2DoubleArrayMap.this.containsKey(k) && Double.doubleToLongBits(Char2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            char k = ((Character)e.getKey()).charValue();
            double v = (Double)e.getValue();
            int oldPos = Char2DoubleArrayMap.this.findKey(k);
            if (oldPos == -1 || Double.doubleToLongBits(v) != Double.doubleToLongBits(Char2DoubleArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Char2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2DoubleArrayMap.this.key, oldPos + 1, Char2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2DoubleArrayMap.this.value, oldPos + 1, Char2DoubleArrayMap.this.value, oldPos, tail);
            Char2DoubleArrayMap.this.size--;
            return true;
        }

    }

}

