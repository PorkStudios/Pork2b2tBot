/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2CharMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2CharMap;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleHash;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.IntConsumer;

public class Double2CharOpenCustomHashMap
extends AbstractDouble2CharMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient double[] key;
    protected transient char[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected DoubleHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Double2CharMap.FastEntrySet entries;
    protected transient DoubleSet keys;
    protected transient CharCollection values;

    public Double2CharOpenCustomHashMap(int expected, float f, DoubleHash.Strategy strategy) {
        this.strategy = strategy;
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = new double[this.n + 1];
        this.value = new char[this.n + 1];
    }

    public Double2CharOpenCustomHashMap(int expected, DoubleHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Double2CharOpenCustomHashMap(DoubleHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Double2CharOpenCustomHashMap(Map<? extends Double, ? extends Character> m, float f, DoubleHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Double2CharOpenCustomHashMap(Map<? extends Double, ? extends Character> m, DoubleHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Double2CharOpenCustomHashMap(Double2CharMap m, float f, DoubleHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Double2CharOpenCustomHashMap(Double2CharMap m, DoubleHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Double2CharOpenCustomHashMap(double[] k, char[] v, float f, DoubleHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Double2CharOpenCustomHashMap(double[] k, char[] v, DoubleHash.Strategy strategy) {
        this(k, v, 0.75f, strategy);
    }

    public DoubleHash.Strategy strategy() {
        return this.strategy;
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    private void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private char removeEntry(int pos) {
        char oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private char removeNullEntry() {
        this.containsNullKey = false;
        char oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Character> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(double k) {
        if (this.strategy.equals(k, 0.0)) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return - pos + 1;
        }
        if (this.strategy.equals(k, curr)) {
            return pos;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return - pos + 1;
        } while (!this.strategy.equals(k, curr));
        return pos;
    }

    private void insert(int pos, double k, char v) {
        if (pos == this.n) {
            this.containsNullKey = true;
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
    }

    @Override
    public char put(double k, char v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        char oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    protected final void shiftKeys(int pos) {
        double[] key = this.key;
        do {
            double curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                    key[last] = 0.0;
                    return;
                }
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public char remove(double k) {
        if (this.strategy.equals(k, 0.0)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public char get(double k) {
        if (this.strategy.equals(k, 0.0)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(double k) {
        if (this.strategy.equals(k, 0.0)) {
            return this.containsNullKey;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return true;
    }

    @Override
    public boolean containsValue(char v) {
        char[] value = this.value;
        double[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) == 0L || value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public char getOrDefault(double k, char defaultValue) {
        if (this.strategy.equals(k, 0.0)) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return defaultValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return defaultValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public char putIfAbsent(double k, char v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(double k, char v) {
        if (this.strategy.equals(k, 0.0)) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (!this.strategy.equals(k, curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(double k, char oldValue, char v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public char replace(double k, char v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        char oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public char computeIfAbsent(double k, DoubleToIntFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        char newValue = SafeMath.safeIntToChar(mappingFunction.applyAsInt(k));
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public char computeIfAbsentNullable(double k, DoubleFunction<? extends Character> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        Character newValue = mappingFunction.apply(k);
        if (newValue == null) {
            return this.defRetValue;
        }
        char v = newValue.charValue();
        this.insert(- pos - 1, k, v);
        return v;
    }

    @Override
    public char computeIfPresent(double k, BiFunction<? super Double, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Character newValue = remappingFunction.apply((Double)k, Character.valueOf(this.value[pos]));
        if (newValue == null) {
            if (this.strategy.equals(k, 0.0)) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue.charValue();
        return this.value[pos];
    }

    @Override
    public char compute(double k, BiFunction<? super Double, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Character newValue = remappingFunction.apply((Double)k, pos >= 0 ? Character.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, 0.0)) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        char newVal = newValue.charValue();
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public char merge(double k, char v, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Character newValue = remappingFunction.apply(Character.valueOf(this.value[pos]), Character.valueOf(v));
        if (newValue == null) {
            if (this.strategy.equals(k, 0.0)) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue.charValue();
        return this.value[pos];
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, 0.0);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Double2CharMap.FastEntrySet double2CharEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public DoubleSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public CharCollection values() {
        if (this.values == null) {
            this.values = new AbstractCharCollection(){

                @Override
                public CharIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Double2CharOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(char v) {
                    return Double2CharOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Double2CharOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(IntConsumer consumer) {
                    if (Double2CharOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Double2CharOpenCustomHashMap.this.value[Double2CharOpenCustomHashMap.this.n]);
                    }
                    int pos = Double2CharOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Double.doubleToLongBits(Double2CharOpenCustomHashMap.this.key[pos]) == 0L) continue;
                        consumer.accept(Double2CharOpenCustomHashMap.this.value[pos]);
                    }
                }
            };
        }
        return this.values;
    }

    public boolean trim() {
        int l = HashCommon.arraySize(this.size, this.f);
        if (l >= this.n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    public boolean trim(int n) {
        int l = HashCommon.nextPowerOfTwo((int)Math.ceil((float)n / this.f));
        if (l >= n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    protected void rehash(int newN) {
        double[] key = this.key;
        char[] value = this.value;
        int mask = newN - 1;
        double[] newKey = new double[newN + 1];
        char[] newValue = new char[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (Double.doubleToLongBits(key[--i]) == 0L) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
            if (Double.doubleToLongBits(newKey[pos]) != 0L) {
                while (Double.doubleToLongBits(newKey[pos = pos + 1 & mask]) != 0L) {
                }
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
        }
        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Double2CharOpenCustomHashMap clone() {
        Double2CharOpenCustomHashMap c;
        try {
            c = (Double2CharOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (double[])this.key.clone();
        c.value = (char[])this.value.clone();
        c.strategy = this.strategy;
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (Double.doubleToLongBits(this.key[i]) == 0L) {
                ++i;
            }
            t = this.strategy.hashCode(this.key[i]);
            h += (t ^= this.value[i]);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n];
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        double[] key = this.key;
        char[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeDouble(key[e]);
            s.writeChar(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new double[this.n + 1];
        double[] key = this.key;
        this.value = new char[this.n + 1];
        char[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            double k = s.readDouble();
            char v = s.readChar();
            if (this.strategy.equals(k, 0.0)) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
                while (Double.doubleToLongBits(key[pos]) != 0L) {
                    pos = pos + 1 & this.mask;
                }
            }
            key[pos] = k;
            value[pos] = v;
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends MapIterator
    implements CharIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public char nextChar() {
            return Double2CharOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractDoubleSet {
        private KeySet() {
        }

        @Override
        public DoubleIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(DoubleConsumer consumer) {
            if (Double2CharOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Double2CharOpenCustomHashMap.this.key[Double2CharOpenCustomHashMap.this.n]);
            }
            int pos = Double2CharOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                double k = Double2CharOpenCustomHashMap.this.key[pos];
                if (Double.doubleToLongBits(k) == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Double2CharOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(double k) {
            return Double2CharOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(double k) {
            int oldSize = Double2CharOpenCustomHashMap.this.size;
            Double2CharOpenCustomHashMap.this.remove(k);
            return Double2CharOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Double2CharOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements DoubleIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Double2CharOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Double2CharMap.Entry>
    implements Double2CharMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Double2CharMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Double2CharMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            double k = (Double)e.getKey();
            char v = ((Character)e.getValue()).charValue();
            if (Double2CharOpenCustomHashMap.this.strategy.equals(k, 0.0)) {
                return Double2CharOpenCustomHashMap.this.containsNullKey && Double2CharOpenCustomHashMap.this.value[Double2CharOpenCustomHashMap.this.n] == v;
            }
            double[] key = Double2CharOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Double2CharOpenCustomHashMap.this.strategy.hashCode(k)) & Double2CharOpenCustomHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double2CharOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Double2CharOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2CharOpenCustomHashMap.this.mask]) != 0L) continue;
                return false;
            } while (!Double2CharOpenCustomHashMap.this.strategy.equals(k, curr));
            return Double2CharOpenCustomHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            double k = (Double)e.getKey();
            char v = ((Character)e.getValue()).charValue();
            if (Double2CharOpenCustomHashMap.this.strategy.equals(k, 0.0)) {
                if (Double2CharOpenCustomHashMap.this.containsNullKey && Double2CharOpenCustomHashMap.this.value[Double2CharOpenCustomHashMap.this.n] == v) {
                    Double2CharOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            double[] key = Double2CharOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Double2CharOpenCustomHashMap.this.strategy.hashCode(k)) & Double2CharOpenCustomHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double2CharOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Double2CharOpenCustomHashMap.this.value[pos] == v) {
                    Double2CharOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2CharOpenCustomHashMap.this.mask]) != 0L) continue;
                return false;
            } while (!Double2CharOpenCustomHashMap.this.strategy.equals(curr, k) || Double2CharOpenCustomHashMap.this.value[pos] != v);
            Double2CharOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Double2CharOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Double2CharOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Double2CharMap.Entry> consumer) {
            if (Double2CharOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractDouble2CharMap.BasicEntry(Double2CharOpenCustomHashMap.this.key[Double2CharOpenCustomHashMap.this.n], Double2CharOpenCustomHashMap.this.value[Double2CharOpenCustomHashMap.this.n]));
            }
            int pos = Double2CharOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2CharOpenCustomHashMap.this.key[pos]) == 0L) continue;
                consumer.accept(new AbstractDouble2CharMap.BasicEntry(Double2CharOpenCustomHashMap.this.key[pos], Double2CharOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Double2CharMap.Entry> consumer) {
            AbstractDouble2CharMap.BasicEntry entry = new AbstractDouble2CharMap.BasicEntry();
            if (Double2CharOpenCustomHashMap.this.containsNullKey) {
                entry.key = Double2CharOpenCustomHashMap.this.key[Double2CharOpenCustomHashMap.this.n];
                entry.value = Double2CharOpenCustomHashMap.this.value[Double2CharOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Double2CharOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2CharOpenCustomHashMap.this.key[pos]) == 0L) continue;
                entry.key = Double2CharOpenCustomHashMap.this.key[pos];
                entry.value = Double2CharOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Double2CharMap.Entry> {
        private final MapEntry entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends MapIterator
    implements ObjectIterator<Double2CharMap.Entry> {
        private MapEntry entry;

        private EntryIterator() {
            super();
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        DoubleArrayList wrapped;

        private MapIterator() {
            this.pos = Double2CharOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Double2CharOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Double2CharOpenCustomHashMap.this.containsNullKey;
        }

        public boolean hasNext() {
            return this.c != 0;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                this.last = Double2CharOpenCustomHashMap.this.n;
                return this.last;
            }
            double[] key = Double2CharOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                double k = this.wrapped.getDouble(- this.pos - 1);
                int p = HashCommon.mix(Double2CharOpenCustomHashMap.this.strategy.hashCode(k)) & Double2CharOpenCustomHashMap.this.mask;
                while (!Double2CharOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Double2CharOpenCustomHashMap.this.mask;
                }
                return p;
            } while (Double.doubleToLongBits(key[this.pos]) == 0L);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            double[] key = Double2CharOpenCustomHashMap.this.key;
            do {
                double curr;
                int last = pos;
                pos = last + 1 & Double2CharOpenCustomHashMap.this.mask;
                do {
                    if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                        key[last] = 0.0;
                        return;
                    }
                    int slot = HashCommon.mix(Double2CharOpenCustomHashMap.this.strategy.hashCode(curr)) & Double2CharOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Double2CharOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Double2CharOpenCustomHashMap.this.value[last] = Double2CharOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Double2CharOpenCustomHashMap.this.n) {
                Double2CharOpenCustomHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Double2CharOpenCustomHashMap.this.remove(this.wrapped.getDouble(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Double2CharOpenCustomHashMap.this.size;
            this.last = -1;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }
    }

    final class MapEntry
    implements Double2CharMap.Entry,
    Map.Entry<Double, Character> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public double getDoubleKey() {
            return Double2CharOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public char getCharValue() {
            return Double2CharOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public char setValue(char v) {
            char oldValue = Double2CharOpenCustomHashMap.this.value[this.index];
            Double2CharOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Double getKey() {
            return Double2CharOpenCustomHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Character getValue() {
            return Character.valueOf(Double2CharOpenCustomHashMap.this.value[this.index]);
        }

        @Deprecated
        @Override
        public Character setValue(Character v) {
            return Character.valueOf(this.setValue(v.charValue()));
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Double2CharOpenCustomHashMap.this.strategy.equals(Double2CharOpenCustomHashMap.this.key[this.index], (Double)e.getKey()) && Double2CharOpenCustomHashMap.this.value[this.index] == ((Character)e.getValue()).charValue();
        }

        @Override
        public int hashCode() {
            return Double2CharOpenCustomHashMap.this.strategy.hashCode(Double2CharOpenCustomHashMap.this.key[this.index]) ^ Double2CharOpenCustomHashMap.this.value[this.index];
        }

        public String toString() {
            return "" + Double2CharOpenCustomHashMap.this.key[this.index] + "=>" + Double2CharOpenCustomHashMap.this.value[this.index];
        }
    }

}

