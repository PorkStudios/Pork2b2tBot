/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2DoubleMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2DoubleMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharHash;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
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
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public class Char2DoubleOpenCustomHashMap
extends AbstractChar2DoubleMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient char[] key;
    protected transient double[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected CharHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Char2DoubleMap.FastEntrySet entries;
    protected transient CharSet keys;
    protected transient DoubleCollection values;

    public Char2DoubleOpenCustomHashMap(int expected, float f, CharHash.Strategy strategy) {
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
        this.key = new char[this.n + 1];
        this.value = new double[this.n + 1];
    }

    public Char2DoubleOpenCustomHashMap(int expected, CharHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Char2DoubleOpenCustomHashMap(CharHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Char2DoubleOpenCustomHashMap(Map<? extends Character, ? extends Double> m, float f, CharHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Char2DoubleOpenCustomHashMap(Map<? extends Character, ? extends Double> m, CharHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Char2DoubleOpenCustomHashMap(Char2DoubleMap m, float f, CharHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Char2DoubleOpenCustomHashMap(Char2DoubleMap m, CharHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Char2DoubleOpenCustomHashMap(char[] k, double[] v, float f, CharHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Char2DoubleOpenCustomHashMap(char[] k, double[] v, CharHash.Strategy strategy) {
        this(k, v, 0.75f, strategy);
    }

    public CharHash.Strategy strategy() {
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

    private double removeEntry(int pos) {
        double oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private double removeNullEntry() {
        this.containsNullKey = false;
        double oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Double> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(char k) {
        if (this.strategy.equals(k, '\u0000')) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return - pos + 1;
        }
        if (this.strategy.equals(k, curr)) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return - pos + 1;
        } while (!this.strategy.equals(k, curr));
        return pos;
    }

    private void insert(int pos, char k, double v) {
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
    public double put(char k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private double addToValue(int pos, double incr) {
        double oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }

    public double addTo(char k, double incr) {
        int pos;
        if (this.strategy.equals(k, '\u0000')) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            char curr = key[pos];
            if (curr != '\u0000') {
                if (this.strategy.equals(curr, k)) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') {
                    if (!this.strategy.equals(curr, k)) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = this.defRetValue + incr;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }

    protected final void shiftKeys(int pos) {
        char[] key = this.key;
        do {
            char curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == '\u0000') {
                    key[last] = '\u0000';
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
    public double remove(char k) {
        if (this.strategy.equals(k, '\u0000')) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public double get(char k) {
        if (this.strategy.equals(k, '\u0000')) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(char k) {
        if (this.strategy.equals(k, '\u0000')) {
            return this.containsNullKey;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return true;
    }

    @Override
    public boolean containsValue(double v) {
        double[] value = this.value;
        char[] key = this.key;
        if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == '\u0000' || Double.doubleToLongBits(value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public double getOrDefault(char k, double defaultValue) {
        if (this.strategy.equals(k, '\u0000')) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return defaultValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return defaultValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public double putIfAbsent(char k, double v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(char k, double v) {
        if (this.strategy.equals(k, '\u0000')) {
            if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return false;
        }
        if (this.strategy.equals(k, curr) && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return false;
        } while (!this.strategy.equals(k, curr) || Double.doubleToLongBits(v) != Double.doubleToLongBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(char k, double oldValue, double v) {
        int pos = this.find(k);
        if (pos < 0 || Double.doubleToLongBits(oldValue) != Double.doubleToLongBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public double replace(char k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public double computeIfAbsent(char k, IntToDoubleFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        double newValue = mappingFunction.applyAsDouble(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public double computeIfAbsentNullable(char k, IntFunction<? extends Double> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        Double newValue = mappingFunction.apply(k);
        if (newValue == null) {
            return this.defRetValue;
        }
        double v = newValue;
        this.insert(- pos - 1, k, v);
        return v;
    }

    @Override
    public double computeIfPresent(char k, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Double newValue = remappingFunction.apply(Character.valueOf(k), (Double)this.value[pos]);
        if (newValue == null) {
            if (this.strategy.equals(k, '\u0000')) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public double compute(char k, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Double newValue = remappingFunction.apply(Character.valueOf(k), pos >= 0 ? Double.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, '\u0000')) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        double newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public double merge(char k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Double newValue = remappingFunction.apply((Double)this.value[pos], (Double)v);
        if (newValue == null) {
            if (this.strategy.equals(k, '\u0000')) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, '\u0000');
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Char2DoubleMap.FastEntrySet char2DoubleEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public CharSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public DoubleCollection values() {
        if (this.values == null) {
            this.values = new AbstractDoubleCollection(){

                @Override
                public DoubleIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Char2DoubleOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(double v) {
                    return Char2DoubleOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Char2DoubleOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Char2DoubleOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Char2DoubleOpenCustomHashMap.this.value[Char2DoubleOpenCustomHashMap.this.n]);
                    }
                    int pos = Char2DoubleOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Char2DoubleOpenCustomHashMap.this.key[pos] == '\u0000') continue;
                        consumer.accept(Char2DoubleOpenCustomHashMap.this.value[pos]);
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
        char[] key = this.key;
        double[] value = this.value;
        int mask = newN - 1;
        char[] newKey = new char[newN + 1];
        double[] newValue = new double[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == '\u0000') {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
            if (newKey[pos] != '\u0000') {
                while (newKey[pos = pos + 1 & mask] != '\u0000') {
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

    public Char2DoubleOpenCustomHashMap clone() {
        Char2DoubleOpenCustomHashMap c;
        try {
            c = (Char2DoubleOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (char[])this.key.clone();
        c.value = (double[])this.value.clone();
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
            while (this.key[i] == '\u0000') {
                ++i;
            }
            t = this.strategy.hashCode(this.key[i]);
            h += (t ^= HashCommon.double2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.double2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        char[] key = this.key;
        double[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeChar(key[e]);
            s.writeDouble(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new char[this.n + 1];
        char[] key = this.key;
        this.value = new double[this.n + 1];
        double[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            char k = s.readChar();
            double v = s.readDouble();
            if (this.strategy.equals(k, '\u0000')) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
                while (key[pos] != '\u0000') {
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
    implements DoubleIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Char2DoubleOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractCharSet {
        private KeySet() {
        }

        @Override
        public CharIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(IntConsumer consumer) {
            if (Char2DoubleOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Char2DoubleOpenCustomHashMap.this.key[Char2DoubleOpenCustomHashMap.this.n]);
            }
            int pos = Char2DoubleOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                char k = Char2DoubleOpenCustomHashMap.this.key[pos];
                if (k == '\u0000') continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Char2DoubleOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(char k) {
            return Char2DoubleOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(char k) {
            int oldSize = Char2DoubleOpenCustomHashMap.this.size;
            Char2DoubleOpenCustomHashMap.this.remove(k);
            return Char2DoubleOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Char2DoubleOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements CharIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public char nextChar() {
            return Char2DoubleOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Char2DoubleMap.Entry>
    implements Char2DoubleMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Char2DoubleMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Char2DoubleMap.Entry> fastIterator() {
            return new FastEntryIterator();
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
            double v = (Double)e.getValue();
            if (Char2DoubleOpenCustomHashMap.this.strategy.equals(k, '\u0000')) {
                return Char2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Char2DoubleOpenCustomHashMap.this.value[Char2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(v);
            }
            char[] key = Char2DoubleOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Char2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Char2DoubleOpenCustomHashMap.this.mask;
            char curr = key[pos];
            if (curr == '\u0000') {
                return false;
            }
            if (Char2DoubleOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Double.doubleToLongBits(Char2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Char2DoubleOpenCustomHashMap.this.mask]) != '\u0000') continue;
                return false;
            } while (!Char2DoubleOpenCustomHashMap.this.strategy.equals(k, curr));
            return Double.doubleToLongBits(Char2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
            if (Char2DoubleOpenCustomHashMap.this.strategy.equals(k, '\u0000')) {
                if (Char2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Char2DoubleOpenCustomHashMap.this.value[Char2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(v)) {
                    Char2DoubleOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            char[] key = Char2DoubleOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Char2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Char2DoubleOpenCustomHashMap.this.mask;
            char curr = key[pos];
            if (curr == '\u0000') {
                return false;
            }
            if (Char2DoubleOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Double.doubleToLongBits(Char2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                    Char2DoubleOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Char2DoubleOpenCustomHashMap.this.mask]) != '\u0000') continue;
                return false;
            } while (!Char2DoubleOpenCustomHashMap.this.strategy.equals(curr, k) || Double.doubleToLongBits(Char2DoubleOpenCustomHashMap.this.value[pos]) != Double.doubleToLongBits(v));
            Char2DoubleOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Char2DoubleOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Char2DoubleOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Char2DoubleMap.Entry> consumer) {
            if (Char2DoubleOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractChar2DoubleMap.BasicEntry(Char2DoubleOpenCustomHashMap.this.key[Char2DoubleOpenCustomHashMap.this.n], Char2DoubleOpenCustomHashMap.this.value[Char2DoubleOpenCustomHashMap.this.n]));
            }
            int pos = Char2DoubleOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Char2DoubleOpenCustomHashMap.this.key[pos] == '\u0000') continue;
                consumer.accept(new AbstractChar2DoubleMap.BasicEntry(Char2DoubleOpenCustomHashMap.this.key[pos], Char2DoubleOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Char2DoubleMap.Entry> consumer) {
            AbstractChar2DoubleMap.BasicEntry entry = new AbstractChar2DoubleMap.BasicEntry();
            if (Char2DoubleOpenCustomHashMap.this.containsNullKey) {
                entry.key = Char2DoubleOpenCustomHashMap.this.key[Char2DoubleOpenCustomHashMap.this.n];
                entry.value = Char2DoubleOpenCustomHashMap.this.value[Char2DoubleOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Char2DoubleOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Char2DoubleOpenCustomHashMap.this.key[pos] == '\u0000') continue;
                entry.key = Char2DoubleOpenCustomHashMap.this.key[pos];
                entry.value = Char2DoubleOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Char2DoubleMap.Entry> {
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
    implements ObjectIterator<Char2DoubleMap.Entry> {
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
        CharArrayList wrapped;

        private MapIterator() {
            this.pos = Char2DoubleOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Char2DoubleOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Char2DoubleOpenCustomHashMap.this.containsNullKey;
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
                this.last = Char2DoubleOpenCustomHashMap.this.n;
                return this.last;
            }
            char[] key = Char2DoubleOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                char k = this.wrapped.getChar(- this.pos - 1);
                int p = HashCommon.mix(Char2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Char2DoubleOpenCustomHashMap.this.mask;
                while (!Char2DoubleOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Char2DoubleOpenCustomHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == '\u0000');
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            char[] key = Char2DoubleOpenCustomHashMap.this.key;
            do {
                char curr;
                int last = pos;
                pos = last + 1 & Char2DoubleOpenCustomHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == '\u0000') {
                        key[last] = '\u0000';
                        return;
                    }
                    int slot = HashCommon.mix(Char2DoubleOpenCustomHashMap.this.strategy.hashCode(curr)) & Char2DoubleOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Char2DoubleOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new CharArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Char2DoubleOpenCustomHashMap.this.value[last] = Char2DoubleOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Char2DoubleOpenCustomHashMap.this.n) {
                Char2DoubleOpenCustomHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Char2DoubleOpenCustomHashMap.this.remove(this.wrapped.getChar(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Char2DoubleOpenCustomHashMap.this.size;
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
    implements Char2DoubleMap.Entry,
    Map.Entry<Character, Double> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public char getCharKey() {
            return Char2DoubleOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public double getDoubleValue() {
            return Char2DoubleOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public double setValue(double v) {
            double oldValue = Char2DoubleOpenCustomHashMap.this.value[this.index];
            Char2DoubleOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Character getKey() {
            return Character.valueOf(Char2DoubleOpenCustomHashMap.this.key[this.index]);
        }

        @Deprecated
        @Override
        public Double getValue() {
            return Char2DoubleOpenCustomHashMap.this.value[this.index];
        }

        @Deprecated
        @Override
        public Double setValue(Double v) {
            return this.setValue((double)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Char2DoubleOpenCustomHashMap.this.strategy.equals(Char2DoubleOpenCustomHashMap.this.key[this.index], ((Character)e.getKey()).charValue()) && Double.doubleToLongBits(Char2DoubleOpenCustomHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return Char2DoubleOpenCustomHashMap.this.strategy.hashCode(Char2DoubleOpenCustomHashMap.this.key[this.index]) ^ HashCommon.double2int(Char2DoubleOpenCustomHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Char2DoubleOpenCustomHashMap.this.key[this.index] + "=>" + Char2DoubleOpenCustomHashMap.this.value[this.index];
        }
    }

}

