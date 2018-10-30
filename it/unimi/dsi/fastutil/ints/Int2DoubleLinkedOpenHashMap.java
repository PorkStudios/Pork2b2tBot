/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2DoubleMap;
import it.unimi.dsi.fastutil.ints.AbstractInt2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public class Int2DoubleLinkedOpenHashMap
extends AbstractInt2DoubleSortedMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient int[] key;
    protected transient double[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Int2DoubleSortedMap.FastSortedEntrySet entries;
    protected transient IntSortedSet keys;
    protected transient DoubleCollection values;

    public Int2DoubleLinkedOpenHashMap(int expected, float f) {
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
        this.key = new int[this.n + 1];
        this.value = new double[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Int2DoubleLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Int2DoubleLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Int2DoubleLinkedOpenHashMap(Map<? extends Integer, ? extends Double> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Int2DoubleLinkedOpenHashMap(Map<? extends Integer, ? extends Double> m) {
        this(m, 0.75f);
    }

    public Int2DoubleLinkedOpenHashMap(Int2DoubleMap m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Int2DoubleLinkedOpenHashMap(Int2DoubleMap m) {
        this(m, 0.75f);
    }

    public Int2DoubleLinkedOpenHashMap(int[] k, double[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Int2DoubleLinkedOpenHashMap(int[] k, double[] v) {
        this(k, v, 0.75f);
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
        this.fixPointers(pos);
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
        this.fixPointers(this.n);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Double> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(int k) {
        if (k == 0) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return - pos + 1;
        }
        if (k == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return - pos + 1;
        } while (k != curr);
        return pos;
    }

    private void insert(int pos, int k, double v) {
        if (pos == this.n) {
            this.containsNullKey = true;
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            long[] arrl = this.link;
            int n = this.last;
            arrl[n] = arrl[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
    }

    @Override
    public double put(int k, double v) {
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

    public double addTo(int k, double incr) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            int[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
            int curr = key[pos];
            if (curr != 0) {
                if (curr == k) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = this.defRetValue + incr;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            long[] arrl = this.link;
            int n = this.last;
            arrl[n] = arrl[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }

    protected final void shiftKeys(int pos) {
        int[] key = this.key;
        do {
            int curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
            this.fixPointers(pos, last);
        } while (true);
    }

    @Override
    public double remove(int k) {
        if (k == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    private double setValue(int pos, double v) {
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    public double removeFirstDouble() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.first;
        this.first = (int)this.link[pos];
        if (0 <= this.first) {
            long[] arrl = this.link;
            int n = this.first;
            arrl[n] = arrl[n] | -4294967296L;
        }
        --this.size;
        double v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }

    public double removeLastDouble() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.last;
        this.last = (int)(this.link[pos] >>> 32);
        if (0 <= this.last) {
            long[] arrl = this.link;
            int n = this.last;
            arrl[n] = arrl[n] | 0xFFFFFFFFL;
        }
        --this.size;
        double v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }

    private void moveIndexToFirst(int i) {
        if (this.size == 1 || this.first == i) {
            return;
        }
        if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            long[] arrl = this.link;
            int n = this.last;
            arrl[n] = arrl[n] | 0xFFFFFFFFL;
        } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            long[] arrl = this.link;
            int n = prev;
            arrl[n] = arrl[n] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            long[] arrl2 = this.link;
            int n2 = next;
            arrl2[n2] = arrl2[n2] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
        }
        long[] arrl = this.link;
        int n = this.first;
        arrl[n] = arrl[n] ^ (this.link[this.first] ^ ((long)i & 0xFFFFFFFFL) << 32) & -4294967296L;
        this.link[i] = -4294967296L | (long)this.first & 0xFFFFFFFFL;
        this.first = i;
    }

    private void moveIndexToLast(int i) {
        if (this.size == 1 || this.last == i) {
            return;
        }
        if (this.first == i) {
            this.first = (int)this.link[i];
            long[] arrl = this.link;
            int n = this.first;
            arrl[n] = arrl[n] | -4294967296L;
        } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            long[] arrl = this.link;
            int n = prev;
            arrl[n] = arrl[n] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            long[] arrl2 = this.link;
            int n2 = next;
            arrl2[n2] = arrl2[n2] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
        }
        long[] arrl = this.link;
        int n = this.last;
        arrl[n] = arrl[n] ^ (this.link[this.last] ^ (long)i & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        this.link[i] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
        this.last = i;
    }

    public double getAndMoveToFirst(int k) {
        if (k == 0) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public double getAndMoveToLast(int k) {
        if (k == 0) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public double putAndMoveToFirst(int k, double v) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            int[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
            int curr = key[pos];
            if (curr != 0) {
                if (curr == k) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k) continue;
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            long[] arrl = this.link;
            int n = this.first;
            arrl[n] = arrl[n] ^ (this.link[this.first] ^ ((long)pos & 0xFFFFFFFFL) << 32) & -4294967296L;
            this.link[pos] = -4294967296L | (long)this.first & 0xFFFFFFFFL;
            this.first = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }

    public double putAndMoveToLast(int k, double v) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            int[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
            int curr = key[pos];
            if (curr != 0) {
                if (curr == k) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k) continue;
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            long[] arrl = this.link;
            int n = this.last;
            arrl[n] = arrl[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }

    @Override
    public double get(int k) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(int k) {
        if (k == 0) {
            return this.containsNullKey;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public boolean containsValue(double v) {
        double[] value = this.value;
        int[] key = this.key;
        if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == 0 || Double.doubleToLongBits(value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public double getOrDefault(int k, double defaultValue) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return defaultValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return defaultValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public double putIfAbsent(int k, double v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(int k, double v) {
        if (k == 0) {
            if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr || Double.doubleToLongBits(v) != Double.doubleToLongBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(int k, double oldValue, double v) {
        int pos = this.find(k);
        if (pos < 0 || Double.doubleToLongBits(oldValue) != Double.doubleToLongBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public double replace(int k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public double computeIfAbsent(int k, IntToDoubleFunction mappingFunction) {
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
    public double computeIfAbsentNullable(int k, IntFunction<? extends Double> mappingFunction) {
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
    public double computeIfPresent(int k, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Double newValue = remappingFunction.apply((Integer)k, (Double)this.value[pos]);
        if (newValue == null) {
            if (k == 0) {
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
    public double compute(int k, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Double newValue = remappingFunction.apply((Integer)k, pos >= 0 ? Double.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == 0) {
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
    public double merge(int k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Double newValue = remappingFunction.apply((Double)this.value[pos], (Double)v);
        if (newValue == null) {
            if (k == 0) {
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
        Arrays.fill(this.key, 0);
        this.last = -1;
        this.first = -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    protected void fixPointers(int i) {
        if (this.size == 0) {
            this.last = -1;
            this.first = -1;
            return;
        }
        if (this.first == i) {
            this.first = (int)this.link[i];
            if (0 <= this.first) {
                long[] arrl = this.link;
                int n = this.first;
                arrl[n] = arrl[n] | -4294967296L;
            }
            return;
        }
        if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            if (0 <= this.last) {
                long[] arrl = this.link;
                int n = this.last;
                arrl[n] = arrl[n] | 0xFFFFFFFFL;
            }
            return;
        }
        long linki = this.link[i];
        int prev = (int)(linki >>> 32);
        int next = (int)linki;
        long[] arrl = this.link;
        int n = prev;
        arrl[n] = arrl[n] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        long[] arrl2 = this.link;
        int n2 = next;
        arrl2[n2] = arrl2[n2] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
    }

    protected void fixPointers(int s, int d) {
        if (this.size == 1) {
            this.first = this.last = d;
            this.link[d] = -1L;
            return;
        }
        if (this.first == s) {
            this.first = d;
            long[] arrl = this.link;
            int n = (int)this.link[s];
            arrl[n] = arrl[n] ^ (this.link[(int)this.link[s]] ^ ((long)d & 0xFFFFFFFFL) << 32) & -4294967296L;
            this.link[d] = this.link[s];
            return;
        }
        if (this.last == s) {
            this.last = d;
            long[] arrl = this.link;
            int n = (int)(this.link[s] >>> 32);
            arrl[n] = arrl[n] ^ (this.link[(int)(this.link[s] >>> 32)] ^ (long)d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[d] = this.link[s];
            return;
        }
        long links = this.link[s];
        int prev = (int)(links >>> 32);
        int next = (int)links;
        long[] arrl = this.link;
        int n = prev;
        arrl[n] = arrl[n] ^ (this.link[prev] ^ (long)d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        long[] arrl2 = this.link;
        int n2 = next;
        arrl2[n2] = arrl2[n2] ^ (this.link[next] ^ ((long)d & 0xFFFFFFFFL) << 32) & -4294967296L;
        this.link[d] = links;
    }

    @Override
    public int firstIntKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public int lastIntKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public Int2DoubleSortedMap tailMap(int from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2DoubleSortedMap headMap(int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2DoubleSortedMap subMap(int from, int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntComparator comparator() {
        return null;
    }

    @Override
    public Int2DoubleSortedMap.FastSortedEntrySet int2DoubleEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public IntSortedSet keySet() {
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
                    return Int2DoubleLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(double v) {
                    return Int2DoubleLinkedOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Int2DoubleLinkedOpenHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Int2DoubleLinkedOpenHashMap.this.containsNullKey) {
                        consumer.accept(Int2DoubleLinkedOpenHashMap.this.value[Int2DoubleLinkedOpenHashMap.this.n]);
                    }
                    int pos = Int2DoubleLinkedOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Int2DoubleLinkedOpenHashMap.this.key[pos] == 0) continue;
                        consumer.accept(Int2DoubleLinkedOpenHashMap.this.value[pos]);
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
        int[] key = this.key;
        double[] value = this.value;
        int mask = newN - 1;
        int[] newKey = new int[newN + 1];
        double[] newValue = new double[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (key[i] == 0) {
                pos = newN;
            } else {
                pos = HashCommon.mix(key[i]) & mask;
                while (newKey[pos] != 0) {
                    pos = pos + 1 & mask;
                }
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
            if (prev != -1) {
                long[] arrl = newLink;
                int n = newPrev;
                arrl[n] = arrl[n] ^ (newLink[newPrev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                long[] arrl2 = newLink;
                int n2 = pos;
                arrl2[n2] = arrl2[n2] ^ (newLink[pos] ^ ((long)newPrev & 0xFFFFFFFFL) << 32) & -4294967296L;
                newPrev = pos;
            } else {
                newPrev = this.first = pos;
                newLink[pos] = -1L;
            }
            int t = i;
            i = (int)link[i];
            prev = t;
        }
        this.link = newLink;
        this.last = newPrev;
        if (newPrev != -1) {
            long[] arrl = newLink;
            int n = newPrev;
            arrl[n] = arrl[n] | 0xFFFFFFFFL;
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Int2DoubleLinkedOpenHashMap clone() {
        Int2DoubleLinkedOpenHashMap c;
        try {
            c = (Int2DoubleLinkedOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (int[])this.key.clone();
        c.value = (double[])this.value.clone();
        c.link = (long[])this.link.clone();
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == 0) {
                ++i;
            }
            t = this.key[i];
            h += (t ^= HashCommon.double2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.double2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int[] key = this.key;
        double[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeInt(key[e]);
            s.writeDouble(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new int[this.n + 1];
        int[] key = this.key;
        this.value = new double[this.n + 1];
        double[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            int k = s.readInt();
            double v = s.readDouble();
            if (k == 0) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k) & this.mask;
                while (key[pos] != 0) {
                    pos = pos + 1 & this.mask;
                }
            }
            key[pos] = k;
            value[pos] = v;
            if (this.first != -1) {
                long[] arrl = link;
                int n = prev;
                arrl[n] = arrl[n] ^ (link[prev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                long[] arrl2 = link;
                int n2 = pos;
                arrl2[n2] = arrl2[n2] ^ (link[pos] ^ ((long)prev & 0xFFFFFFFFL) << 32) & -4294967296L;
                prev = pos;
                continue;
            }
            prev = this.first = pos;
            long[] arrl = link;
            int n = pos;
            arrl[n] = arrl[n] | -4294967296L;
        }
        this.last = prev;
        if (prev != -1) {
            long[] arrl = link;
            int n = prev;
            arrl[n] = arrl[n] | 0xFFFFFFFFL;
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends MapIterator
    implements DoubleListIterator {
        @Override
        public double previousDouble() {
            return Int2DoubleLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        public ValueIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Int2DoubleLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractIntSortedSet {
        private KeySet() {
        }

        @Override
        public IntListIterator iterator(int from) {
            return new KeyIterator(from);
        }

        @Override
        public IntListIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(IntConsumer consumer) {
            if (Int2DoubleLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.n]);
            }
            int pos = Int2DoubleLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                int k = Int2DoubleLinkedOpenHashMap.this.key[pos];
                if (k == 0) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Int2DoubleLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(int k) {
            return Int2DoubleLinkedOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(int k) {
            int oldSize = Int2DoubleLinkedOpenHashMap.this.size;
            Int2DoubleLinkedOpenHashMap.this.remove(k);
            return Int2DoubleLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Int2DoubleLinkedOpenHashMap.this.clear();
        }

        @Override
        public int firstInt() {
            if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.first];
        }

        @Override
        public int lastInt() {
            if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.last];
        }

        @Override
        public IntComparator comparator() {
            return null;
        }

        @Override
        public IntSortedSet tailSet(int from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntSortedSet headSet(int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements IntListIterator {
        public KeyIterator(int k) {
            super(k);
        }

        @Override
        public int previousInt() {
            return Int2DoubleLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
            super();
        }

        @Override
        public int nextInt() {
            return Int2DoubleLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Int2DoubleMap.Entry>
    implements Int2DoubleSortedMap.FastSortedEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Int2DoubleMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Int2DoubleMap.Entry> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Int2DoubleMap.Entry> subSet(Int2DoubleMap.Entry fromElement, Int2DoubleMap.Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Int2DoubleMap.Entry> headSet(Int2DoubleMap.Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Int2DoubleMap.Entry> tailSet(Int2DoubleMap.Entry fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Int2DoubleMap.Entry first() {
            if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Int2DoubleLinkedOpenHashMap.this.first);
        }

        @Override
        public Int2DoubleMap.Entry last() {
            if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Int2DoubleLinkedOpenHashMap.this.last);
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            int k = (Integer)e.getKey();
            double v = (Double)e.getValue();
            if (k == 0) {
                return Int2DoubleLinkedOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[Int2DoubleLinkedOpenHashMap.this.n]) == Double.doubleToLongBits(v);
            }
            int[] key = Int2DoubleLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Int2DoubleLinkedOpenHashMap.this.mask;
            int curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (k == curr) {
                return Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (k != curr);
            return Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            int k = (Integer)e.getKey();
            double v = (Double)e.getValue();
            if (k == 0) {
                if (Int2DoubleLinkedOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[Int2DoubleLinkedOpenHashMap.this.n]) == Double.doubleToLongBits(v)) {
                    Int2DoubleLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            int[] key = Int2DoubleLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Int2DoubleLinkedOpenHashMap.this.mask;
            int curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (curr == k) {
                if (Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                    Int2DoubleLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (curr != k || Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) != Double.doubleToLongBits(v));
            Int2DoubleLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Int2DoubleLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Int2DoubleLinkedOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Int2DoubleMap.Entry> consumer) {
            if (Int2DoubleLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractInt2DoubleMap.BasicEntry(Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.n], Int2DoubleLinkedOpenHashMap.this.value[Int2DoubleLinkedOpenHashMap.this.n]));
            }
            int pos = Int2DoubleLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Int2DoubleLinkedOpenHashMap.this.key[pos] == 0) continue;
                consumer.accept(new AbstractInt2DoubleMap.BasicEntry(Int2DoubleLinkedOpenHashMap.this.key[pos], Int2DoubleLinkedOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Int2DoubleMap.Entry> consumer) {
            AbstractInt2DoubleMap.BasicEntry entry = new AbstractInt2DoubleMap.BasicEntry();
            if (Int2DoubleLinkedOpenHashMap.this.containsNullKey) {
                entry.key = Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.n];
                entry.value = Int2DoubleLinkedOpenHashMap.this.value[Int2DoubleLinkedOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Int2DoubleLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Int2DoubleLinkedOpenHashMap.this.key[pos] == 0) continue;
                entry.key = Int2DoubleLinkedOpenHashMap.this.key[pos];
                entry.value = Int2DoubleLinkedOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }

        @Override
        public ObjectListIterator<Int2DoubleMap.Entry> iterator(Int2DoubleMap.Entry from) {
            return new EntryIterator(from.getIntKey());
        }

        @Override
        public ObjectListIterator<Int2DoubleMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        public ObjectListIterator<Int2DoubleMap.Entry> fastIterator(Int2DoubleMap.Entry from) {
            return new FastEntryIterator(from.getIntKey());
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectListIterator<Int2DoubleMap.Entry> {
        final MapEntry entry;

        public FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        public FastEntryIterator(int from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends MapIterator
    implements ObjectListIterator<Int2DoubleMap.Entry> {
        private MapEntry entry;

        public EntryIterator() {
            super();
        }

        public EntryIterator(int from) {
            super(from);
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry = new MapEntry(this.previousEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        protected MapIterator() {
            this.next = Int2DoubleLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(int from) {
            if (from == 0) {
                if (Int2DoubleLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[Int2DoubleLinkedOpenHashMap.this.n];
                    this.prev = Int2DoubleLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.last] == from) {
                this.prev = Int2DoubleLinkedOpenHashMap.this.last;
                this.index = Int2DoubleLinkedOpenHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(from) & Int2DoubleLinkedOpenHashMap.this.mask;
            while (Int2DoubleLinkedOpenHashMap.this.key[pos] != 0) {
                if (Int2DoubleLinkedOpenHashMap.this.key[pos] == from) {
                    this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask;
            }
            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
        }

        public boolean hasNext() {
            return this.next != -1;
        }

        public boolean hasPrevious() {
            return this.prev != -1;
        }

        private final void ensureIndexKnown() {
            if (this.index >= 0) {
                return;
            }
            if (this.prev == -1) {
                this.index = 0;
                return;
            }
            if (this.next == -1) {
                this.index = Int2DoubleLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Int2DoubleLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Int2DoubleLinkedOpenHashMap.this.link[pos];
                ++this.index;
            }
        }

        public int nextIndex() {
            this.ensureIndexKnown();
            return this.index;
        }

        public int previousIndex() {
            this.ensureIndexKnown();
            return this.index - 1;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return this.curr;
        }

        public int previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev;
            this.prev = (int)(Int2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return this.curr;
        }

        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(Int2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[this.curr];
            }
            --Int2DoubleLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Int2DoubleLinkedOpenHashMap.this.first = this.next;
            } else {
                long[] arrl = Int2DoubleLinkedOpenHashMap.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (Int2DoubleLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Int2DoubleLinkedOpenHashMap.this.last = this.prev;
            } else {
                long[] arrl = Int2DoubleLinkedOpenHashMap.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (Int2DoubleLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Int2DoubleLinkedOpenHashMap.this.n) {
                int[] key = Int2DoubleLinkedOpenHashMap.this.key;
                do {
                    int curr;
                    int last = pos;
                    pos = last + 1 & Int2DoubleLinkedOpenHashMap.this.mask;
                    do {
                        if ((curr = key[pos]) == 0) {
                            key[last] = 0;
                            return;
                        }
                        int slot = HashCommon.mix(curr) & Int2DoubleLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask;
                    } while (true);
                    key[last] = curr;
                    Int2DoubleLinkedOpenHashMap.this.value[last] = Int2DoubleLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Int2DoubleLinkedOpenHashMap.this.fixPointers(pos, last);
                } while (true);
            }
            Int2DoubleLinkedOpenHashMap.this.containsNullKey = false;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }

        public int back(int n) {
            int i = n;
            while (i-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n - i - 1;
        }

        public void set(Int2DoubleMap.Entry ok) {
            throw new UnsupportedOperationException();
        }

        public void add(Int2DoubleMap.Entry ok) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Int2DoubleMap.Entry,
    Map.Entry<Integer, Double> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public int getIntKey() {
            return Int2DoubleLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public double getDoubleValue() {
            return Int2DoubleLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public double setValue(double v) {
            double oldValue = Int2DoubleLinkedOpenHashMap.this.value[this.index];
            Int2DoubleLinkedOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Integer getKey() {
            return Int2DoubleLinkedOpenHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Double getValue() {
            return Int2DoubleLinkedOpenHashMap.this.value[this.index];
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
            return Int2DoubleLinkedOpenHashMap.this.key[this.index] == (Integer)e.getKey() && Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return Int2DoubleLinkedOpenHashMap.this.key[this.index] ^ HashCommon.double2int(Int2DoubleLinkedOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Int2DoubleLinkedOpenHashMap.this.key[this.index] + "=>" + Int2DoubleLinkedOpenHashMap.this.value[this.index];
        }
    }

}

