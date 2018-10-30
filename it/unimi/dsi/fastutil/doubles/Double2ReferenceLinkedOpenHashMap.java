/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ReferenceSortedMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceSortedMap;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
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
import java.util.function.DoubleFunction;

public class Double2ReferenceLinkedOpenHashMap<V>
extends AbstractDouble2ReferenceSortedMap<V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient double[] key;
    protected transient V[] value;
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
    protected transient Double2ReferenceSortedMap.FastSortedEntrySet<V> entries;
    protected transient DoubleSortedSet keys;
    protected transient ReferenceCollection<V> values;

    public Double2ReferenceLinkedOpenHashMap(int expected, float f) {
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
        this.value = new Object[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Double2ReferenceLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Double2ReferenceLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Double2ReferenceLinkedOpenHashMap(Map<? extends Double, ? extends V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Double2ReferenceLinkedOpenHashMap(Map<? extends Double, ? extends V> m) {
        this(m, 0.75f);
    }

    public Double2ReferenceLinkedOpenHashMap(Double2ReferenceMap<V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Double2ReferenceLinkedOpenHashMap(Double2ReferenceMap<V> m) {
        this(m, 0.75f);
    }

    public Double2ReferenceLinkedOpenHashMap(double[] k, V[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Double2ReferenceLinkedOpenHashMap(double[] k, V[] v) {
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

    private V removeEntry(int pos) {
        V oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        V oldValue = this.value[this.n];
        this.value[this.n] = null;
        --this.size;
        this.fixPointers(this.n);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Double, ? extends V> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return - pos + 1;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return pos;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return - pos + 1;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return pos;
    }

    private void insert(int pos, double k, V v) {
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
    public V put(double k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
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
                    this.value[last] = null;
                    return;
                }
                int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
            this.fixPointers(pos, last);
        } while (true);
    }

    @Override
    public V remove(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return (V)this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return this.removeEntry(pos);
    }

    private V setValue(int pos, V v) {
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    public V removeFirst() {
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
        V v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }

    public V removeLast() {
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
        V v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
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

    public V getAndMoveToFirst(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return (V)this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public V getAndMoveToLast(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return (V)this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public V putAndMoveToFirst(double k, V v) {
        int pos;
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            double[] key = this.key;
            pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) != 0L) {
                if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
                while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k)) continue;
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
        return (V)this.defRetValue;
    }

    public V putAndMoveToLast(double k, V v) {
        int pos;
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            double[] key = this.key;
            pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) != 0L) {
                if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
                while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k)) continue;
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
        return (V)this.defRetValue;
    }

    @Override
    public V get(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return (V)this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNullKey;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return true;
    }

    @Override
    public boolean containsValue(Object v) {
        V[] value = this.value;
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
    public V getOrDefault(double k, V defaultValue) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return defaultValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return defaultValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return this.value[pos];
    }

    @Override
    public V putIfAbsent(double k, V v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return (V)this.defRetValue;
    }

    @Override
    public boolean remove(double k, Object v) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(double k, V oldValue, V v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public V replace(double k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public V computeIfAbsent(double k, DoubleFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        V newValue = mappingFunction.apply(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public V computeIfPresent(double k, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V newValue = remappingFunction.apply((Double)k, this.value[pos]);
        if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public V compute(double k, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        V newValue = remappingFunction.apply((Double)k, pos >= 0 ? (Object)this.value[pos] : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (Double.doubleToLongBits(k) == 0L) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return (V)this.defRetValue;
        }
        V newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public V merge(double k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0 || this.value[pos] == null) {
            if (v == null) {
                return (V)this.defRetValue;
            }
            this.insert(- pos - 1, k, v);
            return v;
        }
        V newValue = remappingFunction.apply(this.value[pos], v);
        if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
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
        Arrays.fill(this.key, 0.0);
        Arrays.fill(this.value, null);
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
    public double firstDoubleKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public double lastDoubleKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public Double2ReferenceSortedMap<V> tailMap(double from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double2ReferenceSortedMap<V> headMap(double to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double2ReferenceSortedMap<V> subMap(double from, double to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoubleComparator comparator() {
        return null;
    }

    @Override
    public Double2ReferenceSortedMap.FastSortedEntrySet<V> double2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public DoubleSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Double2ReferenceLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Double2ReferenceLinkedOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Double2ReferenceLinkedOpenHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Double2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                        consumer.accept(Double2ReferenceLinkedOpenHashMap.this.value[Double2ReferenceLinkedOpenHashMap.this.n]);
                    }
                    int pos = Double2ReferenceLinkedOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Double.doubleToLongBits(Double2ReferenceLinkedOpenHashMap.this.key[pos]) == 0L) continue;
                        consumer.accept(Double2ReferenceLinkedOpenHashMap.this.value[pos]);
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
        V[] value = this.value;
        int mask = newN - 1;
        double[] newKey = new double[newN + 1];
        Object[] newValue = new Object[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (Double.doubleToLongBits(key[i]) == 0L) {
                pos = newN;
            } else {
                pos = (int)HashCommon.mix(Double.doubleToRawLongBits(key[i])) & mask;
                while (Double.doubleToLongBits(newKey[pos]) != 0L) {
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

    public Double2ReferenceLinkedOpenHashMap<V> clone() {
        Double2ReferenceLinkedOpenHashMap c;
        try {
            c = (Double2ReferenceLinkedOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (double[])this.key.clone();
        c.value = (Object[])this.value.clone();
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
            while (Double.doubleToLongBits(this.key[i]) == 0L) {
                ++i;
            }
            t = HashCommon.double2int(this.key[i]);
            if (this != this.value[i]) {
                t ^= this.value[i] == null ? 0 : System.identityHashCode(this.value[i]);
            }
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] == null ? 0 : System.identityHashCode(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        double[] key = this.key;
        V[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeDouble(key[e]);
            s.writeObject(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new double[this.n + 1];
        double[] key = this.key;
        this.value = new Object[this.n + 1];
        Object[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            double k = s.readDouble();
            Object v = s.readObject();
            if (Double.doubleToLongBits(k) == 0L) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
                while (Double.doubleToLongBits(key[pos]) != 0L) {
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
    extends Double2ReferenceLinkedOpenHashMap<V>
    implements ObjectListIterator<V> {
        @Override
        public V previous() {
            return Double2ReferenceLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Double2ReferenceLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractDoubleSortedSet {
        private KeySet() {
        }

        @Override
        public DoubleListIterator iterator(double from) {
            return new KeyIterator(from);
        }

        @Override
        public DoubleListIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(DoubleConsumer consumer) {
            if (Double2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(Double2ReferenceLinkedOpenHashMap.this.key[Double2ReferenceLinkedOpenHashMap.this.n]);
            }
            int pos = Double2ReferenceLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                double k = Double2ReferenceLinkedOpenHashMap.this.key[pos];
                if (Double.doubleToLongBits(k) == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Double2ReferenceLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(double k) {
            return Double2ReferenceLinkedOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(double k) {
            int oldSize = Double2ReferenceLinkedOpenHashMap.this.size;
            Double2ReferenceLinkedOpenHashMap.this.remove(k);
            return Double2ReferenceLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Double2ReferenceLinkedOpenHashMap.this.clear();
        }

        @Override
        public double firstDouble() {
            if (Double2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Double2ReferenceLinkedOpenHashMap.this.key[Double2ReferenceLinkedOpenHashMap.this.first];
        }

        @Override
        public double lastDouble() {
            if (Double2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Double2ReferenceLinkedOpenHashMap.this.key[Double2ReferenceLinkedOpenHashMap.this.last];
        }

        @Override
        public DoubleComparator comparator() {
            return null;
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator
    extends Double2ReferenceLinkedOpenHashMap<V>
    implements DoubleListIterator {
        public KeyIterator(double k) {
            super(k);
        }

        @Override
        public double previousDouble() {
            return Double2ReferenceLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Double2ReferenceLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Double2ReferenceMap.Entry<V>>
    implements Double2ReferenceSortedMap.FastSortedEntrySet<V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Double2ReferenceMap.Entry<V>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Double2ReferenceMap.Entry<V>> subSet(Double2ReferenceMap.Entry<V> fromElement, Double2ReferenceMap.Entry<V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Double2ReferenceMap.Entry<V>> headSet(Double2ReferenceMap.Entry<V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Double2ReferenceMap.Entry<V>> tailSet(Double2ReferenceMap.Entry<V> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double2ReferenceMap.Entry<V> first() {
            if (Double2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Double2ReferenceLinkedOpenHashMap.this.first);
        }

        @Override
        public Double2ReferenceMap.Entry<V> last() {
            if (Double2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Double2ReferenceLinkedOpenHashMap.this.last);
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
            double k = (Double)e.getKey();
            Object v = e.getValue();
            if (Double.doubleToLongBits(k) == 0L) {
                return Double2ReferenceLinkedOpenHashMap.this.containsNullKey && Double2ReferenceLinkedOpenHashMap.this.value[Double2ReferenceLinkedOpenHashMap.this.n] == v;
            }
            double[] key = Double2ReferenceLinkedOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2ReferenceLinkedOpenHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                return Double2ReferenceLinkedOpenHashMap.this.value[pos] == v;
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2ReferenceLinkedOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
            return Double2ReferenceLinkedOpenHashMap.this.value[pos] == v;
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
            double k = (Double)e.getKey();
            Object v = e.getValue();
            if (Double.doubleToLongBits(k) == 0L) {
                if (Double2ReferenceLinkedOpenHashMap.this.containsNullKey && Double2ReferenceLinkedOpenHashMap.this.value[Double2ReferenceLinkedOpenHashMap.this.n] == v) {
                    Double2ReferenceLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            double[] key = Double2ReferenceLinkedOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2ReferenceLinkedOpenHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                if (Double2ReferenceLinkedOpenHashMap.this.value[pos] == v) {
                    Double2ReferenceLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2ReferenceLinkedOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k) || Double2ReferenceLinkedOpenHashMap.this.value[pos] != v);
            Double2ReferenceLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Double2ReferenceLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Double2ReferenceLinkedOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Double2ReferenceMap.Entry<V>> consumer) {
            if (Double2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractDouble2ReferenceMap.BasicEntry(Double2ReferenceLinkedOpenHashMap.this.key[Double2ReferenceLinkedOpenHashMap.this.n], Double2ReferenceLinkedOpenHashMap.this.value[Double2ReferenceLinkedOpenHashMap.this.n]));
            }
            int pos = Double2ReferenceLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2ReferenceLinkedOpenHashMap.this.key[pos]) == 0L) continue;
                consumer.accept(new AbstractDouble2ReferenceMap.BasicEntry(Double2ReferenceLinkedOpenHashMap.this.key[pos], Double2ReferenceLinkedOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Double2ReferenceMap.Entry<V>> consumer) {
            AbstractDouble2ReferenceMap.BasicEntry entry = new AbstractDouble2ReferenceMap.BasicEntry();
            if (Double2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                entry.key = Double2ReferenceLinkedOpenHashMap.this.key[Double2ReferenceLinkedOpenHashMap.this.n];
                entry.value = Double2ReferenceLinkedOpenHashMap.this.value[Double2ReferenceLinkedOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Double2ReferenceLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2ReferenceLinkedOpenHashMap.this.key[pos]) == 0L) continue;
                entry.key = Double2ReferenceLinkedOpenHashMap.this.key[pos];
                entry.value = Double2ReferenceLinkedOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }

        @Override
        public ObjectListIterator<Double2ReferenceMap.Entry<V>> iterator(Double2ReferenceMap.Entry<V> from) {
            return new EntryIterator(from.getDoubleKey());
        }

        @Override
        public ObjectListIterator<Double2ReferenceMap.Entry<V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectListIterator<Double2ReferenceMap.Entry<V>> fastIterator(Double2ReferenceMap.Entry<V> from) {
            return new FastEntryIterator(from.getDoubleKey());
        }
    }

    private class FastEntryIterator
    extends Double2ReferenceLinkedOpenHashMap<V>
    implements ObjectListIterator<Double2ReferenceMap.Entry<V>> {
        final Double2ReferenceLinkedOpenHashMap<V> entry;

        public FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        public FastEntryIterator(double from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        public Double2ReferenceLinkedOpenHashMap<V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public Double2ReferenceLinkedOpenHashMap<V> previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Double2ReferenceLinkedOpenHashMap<V>
    implements ObjectListIterator<Double2ReferenceMap.Entry<V>> {
        private Double2ReferenceLinkedOpenHashMap<V> entry;

        public EntryIterator() {
            super();
        }

        public EntryIterator(double from) {
            super(from);
        }

        @Override
        public Double2ReferenceLinkedOpenHashMap<V> next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public Double2ReferenceLinkedOpenHashMap<V> previous() {
            this.entry = new MapEntry(this.previousEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            MapIterator.super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        protected MapIterator() {
            this.next = Double2ReferenceLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(double from) {
            if (Double.doubleToLongBits(from) == 0L) {
                if (Double2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Double2ReferenceLinkedOpenHashMap.this.link[Double2ReferenceLinkedOpenHashMap.this.n];
                    this.prev = Double2ReferenceLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Double.doubleToLongBits(Double2ReferenceLinkedOpenHashMap.this.key[Double2ReferenceLinkedOpenHashMap.this.last]) == Double.doubleToLongBits(from)) {
                this.prev = Double2ReferenceLinkedOpenHashMap.this.last;
                this.index = Double2ReferenceLinkedOpenHashMap.this.size;
                return;
            }
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(from)) & Double2ReferenceLinkedOpenHashMap.this.mask;
            while (Double.doubleToLongBits(Double2ReferenceLinkedOpenHashMap.this.key[pos]) != 0L) {
                if (Double.doubleToLongBits(Double2ReferenceLinkedOpenHashMap.this.key[pos]) == Double.doubleToLongBits(from)) {
                    this.next = (int)Double2ReferenceLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Double2ReferenceLinkedOpenHashMap.this.mask;
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
                this.index = Double2ReferenceLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Double2ReferenceLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Double2ReferenceLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Double2ReferenceLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Double2ReferenceLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
                this.prev = (int)(Double2ReferenceLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Double2ReferenceLinkedOpenHashMap.this.link[this.curr];
            }
            --Double2ReferenceLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Double2ReferenceLinkedOpenHashMap.this.first = this.next;
            } else {
                long[] arrl = Double2ReferenceLinkedOpenHashMap.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (Double2ReferenceLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Double2ReferenceLinkedOpenHashMap.this.last = this.prev;
            } else {
                long[] arrl = Double2ReferenceLinkedOpenHashMap.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (Double2ReferenceLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Double2ReferenceLinkedOpenHashMap.this.n) {
                double[] key = Double2ReferenceLinkedOpenHashMap.this.key;
                do {
                    double curr;
                    int last = pos;
                    pos = last + 1 & Double2ReferenceLinkedOpenHashMap.this.mask;
                    do {
                        if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                            key[last] = 0.0;
                            Double2ReferenceLinkedOpenHashMap.this.value[last] = null;
                            return;
                        }
                        int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & Double2ReferenceLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Double2ReferenceLinkedOpenHashMap.this.mask;
                    } while (true);
                    key[last] = curr;
                    Double2ReferenceLinkedOpenHashMap.this.value[last] = Double2ReferenceLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Double2ReferenceLinkedOpenHashMap.this.fixPointers(pos, last);
                } while (true);
            }
            Double2ReferenceLinkedOpenHashMap.this.containsNullKey = false;
            Double2ReferenceLinkedOpenHashMap.this.value[Double2ReferenceLinkedOpenHashMap.this.n] = null;
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

        public void set(Double2ReferenceMap.Entry<V> ok) {
            throw new UnsupportedOperationException();
        }

        public void add(Double2ReferenceMap.Entry<V> ok) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Double2ReferenceMap.Entry<V>,
    Map.Entry<Double, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public double getDoubleKey() {
            return Double2ReferenceLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Double2ReferenceLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Double2ReferenceLinkedOpenHashMap.this.value[this.index];
            Double2ReferenceLinkedOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Double getKey() {
            return Double2ReferenceLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Double.doubleToLongBits(Double2ReferenceLinkedOpenHashMap.this.key[this.index]) == Double.doubleToLongBits((Double)e.getKey()) && Double2ReferenceLinkedOpenHashMap.this.value[this.index] == e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(Double2ReferenceLinkedOpenHashMap.this.key[this.index]) ^ (Double2ReferenceLinkedOpenHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Double2ReferenceLinkedOpenHashMap.this.value[this.index]));
        }

        public String toString() {
            return "" + Double2ReferenceLinkedOpenHashMap.this.key[this.index] + "=>" + Double2ReferenceLinkedOpenHashMap.this.value[this.index];
        }
    }

}

