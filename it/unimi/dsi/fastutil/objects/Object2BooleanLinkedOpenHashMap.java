/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanSortedMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanSortedMap;
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
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Object2BooleanLinkedOpenHashMap<K>
extends AbstractObject2BooleanSortedMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient boolean[] value;
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
    protected transient Object2BooleanSortedMap.FastSortedEntrySet<K> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient BooleanCollection values;

    public Object2BooleanLinkedOpenHashMap(int expected, float f) {
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
        this.key = new Object[this.n + 1];
        this.value = new boolean[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Object2BooleanLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Object2BooleanLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Object2BooleanLinkedOpenHashMap(Map<? extends K, ? extends Boolean> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Object2BooleanLinkedOpenHashMap(Map<? extends K, ? extends Boolean> m) {
        this(m, 0.75f);
    }

    public Object2BooleanLinkedOpenHashMap(Object2BooleanMap<K> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Object2BooleanLinkedOpenHashMap(Object2BooleanMap<K> m) {
        this(m, 0.75f);
    }

    public Object2BooleanLinkedOpenHashMap(K[] k, boolean[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2BooleanLinkedOpenHashMap(K[] k, boolean[] v) {
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

    private boolean removeEntry(int pos) {
        boolean oldValue = this.value[pos];
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private boolean removeNullEntry() {
        this.containsNullKey = false;
        this.key[this.n] = null;
        boolean oldValue = this.value[this.n];
        --this.size;
        this.fixPointers(this.n);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Boolean> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(K k) {
        if (k == null) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return - pos + 1;
        }
        if (k.equals(curr)) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return - pos + 1;
        } while (!k.equals(curr));
        return pos;
    }

    private void insert(int pos, K k, boolean v) {
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
    public boolean put(K k, boolean v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    protected final void shiftKeys(int pos) {
        K[] key = this.key;
        do {
            K curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == null) {
                    key[last] = null;
                    return;
                }
                int slot = HashCommon.mix(curr.hashCode()) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
            this.fixPointers(pos, last);
        } while (true);
    }

    @Override
    public boolean removeBoolean(Object k) {
        if (k == null) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        return this.removeEntry(pos);
    }

    private boolean setValue(int pos, boolean v) {
        boolean oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    public boolean removeFirstBoolean() {
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
        boolean v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }

    public boolean removeLastBoolean() {
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
        boolean v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
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

    public boolean getAndMoveToFirst(K k) {
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public boolean getAndMoveToLast(K k) {
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public boolean putAndMoveToFirst(K k, boolean v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(k.hashCode()) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr.equals(k)) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!curr.equals(k)) continue;
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

    public boolean putAndMoveToLast(K k, boolean v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(k.hashCode()) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr.equals(k)) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!curr.equals(k)) continue;
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
    public boolean getBoolean(Object k) {
        if (k == null) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(Object k) {
        if (k == null) {
            return this.containsNullKey;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k.equals(curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!k.equals(curr));
        return true;
    }

    @Override
    public boolean containsValue(boolean v) {
        boolean[] value = this.value;
        K[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == null || value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean getOrDefault(Object k, boolean defaultValue) {
        if (k == null) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return defaultValue;
        }
        if (k.equals(curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return defaultValue;
        } while (!k.equals(curr));
        return this.value[pos];
    }

    @Override
    public boolean putIfAbsent(K k, boolean v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, boolean v) {
        if (k == null) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k.equals(curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!k.equals(curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(K k, boolean oldValue, boolean v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public boolean replace(K k, boolean v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public boolean computeBooleanIfAbsent(K k, Predicate<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        boolean newValue = mappingFunction.test(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public boolean computeBooleanIfPresent(K k, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Boolean newValue = remappingFunction.apply(k, (Boolean)this.value[pos]);
        if (newValue == null) {
            if (k == null) {
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
    public boolean computeBoolean(K k, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Boolean newValue = remappingFunction.apply(k, pos >= 0 ? Boolean.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == null) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        boolean newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public boolean mergeBoolean(K k, boolean v, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Boolean newValue = remappingFunction.apply((Boolean)this.value[pos], (Boolean)v);
        if (newValue == null) {
            if (k == null) {
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
        Arrays.fill(this.key, null);
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
    public K firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public K lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public Object2BooleanSortedMap<K> tailMap(K from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2BooleanSortedMap<K> headMap(K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2BooleanSortedMap<K> subMap(K from, K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public Object2BooleanSortedMap.FastSortedEntrySet<K> object2BooleanEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new AbstractBooleanCollection(){

                @Override
                public BooleanIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Object2BooleanLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(boolean v) {
                    return Object2BooleanLinkedOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Object2BooleanLinkedOpenHashMap.this.clear();
                }

                @Override
                public void forEach(BooleanConsumer consumer) {
                    if (Object2BooleanLinkedOpenHashMap.this.containsNullKey) {
                        consumer.accept(Object2BooleanLinkedOpenHashMap.this.value[Object2BooleanLinkedOpenHashMap.this.n]);
                    }
                    int pos = Object2BooleanLinkedOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Object2BooleanLinkedOpenHashMap.this.key[pos] == null) continue;
                        consumer.accept(Object2BooleanLinkedOpenHashMap.this.value[pos]);
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
        K[] key = this.key;
        boolean[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        boolean[] newValue = new boolean[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (key[i] == null) {
                pos = newN;
            } else {
                pos = HashCommon.mix(key[i].hashCode()) & mask;
                while (newKey[pos] != null) {
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

    public Object2BooleanLinkedOpenHashMap<K> clone() {
        Object2BooleanLinkedOpenHashMap c;
        try {
            c = (Object2BooleanLinkedOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (boolean[])this.value.clone();
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
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = this.key[i].hashCode();
            }
            h += (t ^= this.value[i] ? 1231 : 1237);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] ? 1231 : 1237;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        boolean[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeBoolean(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new boolean[this.n + 1];
        boolean[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            boolean v = s.readBoolean();
            if (k == null) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k.hashCode()) & this.mask;
                while (key[pos] != null) {
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
    extends Object2BooleanLinkedOpenHashMap<K>
    implements BooleanListIterator {
        @Override
        public boolean previousBoolean() {
            return Object2BooleanLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        public ValueIterator() {
            super();
        }

        @Override
        public boolean nextBoolean() {
            return Object2BooleanLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractObjectSortedSet<K> {
        private KeySet() {
        }

        @Override
        public ObjectListIterator<K> iterator(K from) {
            return new KeyIterator(from);
        }

        @Override
        public ObjectListIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(Consumer<? super K> consumer) {
            if (Object2BooleanLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(Object2BooleanLinkedOpenHashMap.this.key[Object2BooleanLinkedOpenHashMap.this.n]);
            }
            int pos = Object2BooleanLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                Object k = Object2BooleanLinkedOpenHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Object2BooleanLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Object2BooleanLinkedOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Object2BooleanLinkedOpenHashMap.this.size;
            Object2BooleanLinkedOpenHashMap.this.removeBoolean(k);
            return Object2BooleanLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Object2BooleanLinkedOpenHashMap.this.clear();
        }

        @Override
        public K first() {
            if (Object2BooleanLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2BooleanLinkedOpenHashMap.this.key[Object2BooleanLinkedOpenHashMap.this.first];
        }

        @Override
        public K last() {
            if (Object2BooleanLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2BooleanLinkedOpenHashMap.this.key[Object2BooleanLinkedOpenHashMap.this.last];
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator
    extends Object2BooleanLinkedOpenHashMap<K>
    implements ObjectListIterator<K> {
        public KeyIterator(K k) {
            super(k);
        }

        @Override
        public K previous() {
            return Object2BooleanLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Object2BooleanLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Object2BooleanMap.Entry<K>>
    implements Object2BooleanSortedMap.FastSortedEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Object2BooleanMap.Entry<K>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Object2BooleanMap.Entry<K>> subSet(Object2BooleanMap.Entry<K> fromElement, Object2BooleanMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2BooleanMap.Entry<K>> headSet(Object2BooleanMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2BooleanMap.Entry<K>> tailSet(Object2BooleanMap.Entry<K> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object2BooleanMap.Entry<K> first() {
            if (Object2BooleanLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2BooleanLinkedOpenHashMap.this.first);
        }

        @Override
        public Object2BooleanMap.Entry<K> last() {
            if (Object2BooleanLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2BooleanLinkedOpenHashMap.this.last);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            boolean v = (Boolean)e.getValue();
            if (k == null) {
                return Object2BooleanLinkedOpenHashMap.this.containsNullKey && Object2BooleanLinkedOpenHashMap.this.value[Object2BooleanLinkedOpenHashMap.this.n] == v;
            }
            K[] key = Object2BooleanLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k.hashCode()) & Object2BooleanLinkedOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k.equals(curr)) {
                return Object2BooleanLinkedOpenHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2BooleanLinkedOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (!k.equals(curr));
            return Object2BooleanLinkedOpenHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            boolean v = (Boolean)e.getValue();
            if (k == null) {
                if (Object2BooleanLinkedOpenHashMap.this.containsNullKey && Object2BooleanLinkedOpenHashMap.this.value[Object2BooleanLinkedOpenHashMap.this.n] == v) {
                    Object2BooleanLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Object2BooleanLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k.hashCode()) & Object2BooleanLinkedOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr.equals(k)) {
                if (Object2BooleanLinkedOpenHashMap.this.value[pos] == v) {
                    Object2BooleanLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2BooleanLinkedOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (!curr.equals(k) || Object2BooleanLinkedOpenHashMap.this.value[pos] != v);
            Object2BooleanLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Object2BooleanLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Object2BooleanLinkedOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
            if (Object2BooleanLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractObject2BooleanMap.BasicEntry(Object2BooleanLinkedOpenHashMap.this.key[Object2BooleanLinkedOpenHashMap.this.n], Object2BooleanLinkedOpenHashMap.this.value[Object2BooleanLinkedOpenHashMap.this.n]));
            }
            int pos = Object2BooleanLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Object2BooleanLinkedOpenHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractObject2BooleanMap.BasicEntry(Object2BooleanLinkedOpenHashMap.this.key[pos], Object2BooleanLinkedOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
            AbstractObject2BooleanMap.BasicEntry entry = new AbstractObject2BooleanMap.BasicEntry();
            if (Object2BooleanLinkedOpenHashMap.this.containsNullKey) {
                entry.key = Object2BooleanLinkedOpenHashMap.this.key[Object2BooleanLinkedOpenHashMap.this.n];
                entry.value = Object2BooleanLinkedOpenHashMap.this.value[Object2BooleanLinkedOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Object2BooleanLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Object2BooleanLinkedOpenHashMap.this.key[pos] == null) continue;
                entry.key = Object2BooleanLinkedOpenHashMap.this.key[pos];
                entry.value = Object2BooleanLinkedOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }

        @Override
        public ObjectListIterator<Object2BooleanMap.Entry<K>> iterator(Object2BooleanMap.Entry<K> from) {
            return new EntryIterator(from.getKey());
        }

        @Override
        public ObjectListIterator<Object2BooleanMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectListIterator<Object2BooleanMap.Entry<K>> fastIterator(Object2BooleanMap.Entry<K> from) {
            return new FastEntryIterator(from.getKey());
        }
    }

    private class FastEntryIterator
    extends Object2BooleanLinkedOpenHashMap<K>
    implements ObjectListIterator<Object2BooleanMap.Entry<K>> {
        final Object2BooleanLinkedOpenHashMap<K> entry;

        public FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        public FastEntryIterator(K from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        public Object2BooleanLinkedOpenHashMap<K> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public Object2BooleanLinkedOpenHashMap<K> previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Object2BooleanLinkedOpenHashMap<K>
    implements ObjectListIterator<Object2BooleanMap.Entry<K>> {
        private Object2BooleanLinkedOpenHashMap<K> entry;

        public EntryIterator() {
            super();
        }

        public EntryIterator(K from) {
            super(from);
        }

        @Override
        public Object2BooleanLinkedOpenHashMap<K> next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public Object2BooleanLinkedOpenHashMap<K> previous() {
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
            this.next = Object2BooleanLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(K from) {
            if (from == null) {
                if (Object2BooleanLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Object2BooleanLinkedOpenHashMap.this.link[Object2BooleanLinkedOpenHashMap.this.n];
                    this.prev = Object2BooleanLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Objects.equals(Object2BooleanLinkedOpenHashMap.this.key[Object2BooleanLinkedOpenHashMap.this.last], from)) {
                this.prev = Object2BooleanLinkedOpenHashMap.this.last;
                this.index = Object2BooleanLinkedOpenHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(from.hashCode()) & Object2BooleanLinkedOpenHashMap.this.mask;
            while (Object2BooleanLinkedOpenHashMap.this.key[pos] != null) {
                if (Object2BooleanLinkedOpenHashMap.this.key[pos].equals(from)) {
                    this.next = (int)Object2BooleanLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Object2BooleanLinkedOpenHashMap.this.mask;
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
                this.index = Object2BooleanLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Object2BooleanLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Object2BooleanLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Object2BooleanLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Object2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
                this.prev = (int)(Object2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Object2BooleanLinkedOpenHashMap.this.link[this.curr];
            }
            --Object2BooleanLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Object2BooleanLinkedOpenHashMap.this.first = this.next;
            } else {
                long[] arrl = Object2BooleanLinkedOpenHashMap.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (Object2BooleanLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Object2BooleanLinkedOpenHashMap.this.last = this.prev;
            } else {
                long[] arrl = Object2BooleanLinkedOpenHashMap.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (Object2BooleanLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Object2BooleanLinkedOpenHashMap.this.n) {
                K[] key = Object2BooleanLinkedOpenHashMap.this.key;
                do {
                    Object curr;
                    int last = pos;
                    pos = last + 1 & Object2BooleanLinkedOpenHashMap.this.mask;
                    do {
                        if ((curr = key[pos]) == null) {
                            key[last] = null;
                            return;
                        }
                        int slot = HashCommon.mix(curr.hashCode()) & Object2BooleanLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Object2BooleanLinkedOpenHashMap.this.mask;
                    } while (true);
                    key[last] = curr;
                    Object2BooleanLinkedOpenHashMap.this.value[last] = Object2BooleanLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Object2BooleanLinkedOpenHashMap.this.fixPointers(pos, last);
                } while (true);
            }
            Object2BooleanLinkedOpenHashMap.this.containsNullKey = false;
            Object2BooleanLinkedOpenHashMap.this.key[Object2BooleanLinkedOpenHashMap.this.n] = null;
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

        public void set(Object2BooleanMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }

        public void add(Object2BooleanMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Object2BooleanMap.Entry<K>,
    Map.Entry<K, Boolean> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Object2BooleanLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public boolean getBooleanValue() {
            return Object2BooleanLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public boolean setValue(boolean v) {
            boolean oldValue = Object2BooleanLinkedOpenHashMap.this.value[this.index];
            Object2BooleanLinkedOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Boolean getValue() {
            return Object2BooleanLinkedOpenHashMap.this.value[this.index];
        }

        @Deprecated
        @Override
        public Boolean setValue(Boolean v) {
            return this.setValue((boolean)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Objects.equals(Object2BooleanLinkedOpenHashMap.this.key[this.index], e.getKey()) && Object2BooleanLinkedOpenHashMap.this.value[this.index] == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return (Object2BooleanLinkedOpenHashMap.this.key[this.index] == null ? 0 : Object2BooleanLinkedOpenHashMap.this.key[this.index].hashCode()) ^ (Object2BooleanLinkedOpenHashMap.this.value[this.index] ? 1231 : 1237);
        }

        public String toString() {
            return Object2BooleanLinkedOpenHashMap.this.key[this.index] + "=>" + Object2BooleanLinkedOpenHashMap.this.value[this.index];
        }
    }

}

