/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2CharMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2CharSortedMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharSortedMap;
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
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;

public class Object2CharLinkedOpenCustomHashMap<K>
extends AbstractObject2CharSortedMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient char[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected Hash.Strategy<K> strategy;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Object2CharSortedMap.FastSortedEntrySet<K> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient CharCollection values;

    public Object2CharLinkedOpenCustomHashMap(int expected, float f, Hash.Strategy<K> strategy) {
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
        this.key = new Object[this.n + 1];
        this.value = new char[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Object2CharLinkedOpenCustomHashMap(int expected, Hash.Strategy<K> strategy) {
        this(expected, 0.75f, strategy);
    }

    public Object2CharLinkedOpenCustomHashMap(Hash.Strategy<K> strategy) {
        this(16, 0.75f, strategy);
    }

    public Object2CharLinkedOpenCustomHashMap(Map<? extends K, ? extends Character> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2CharLinkedOpenCustomHashMap(Map<? extends K, ? extends Character> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2CharLinkedOpenCustomHashMap(Object2CharMap<K> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2CharLinkedOpenCustomHashMap(Object2CharMap<K> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2CharLinkedOpenCustomHashMap(K[] k, char[] v, float f, Hash.Strategy<K> strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2CharLinkedOpenCustomHashMap(K[] k, char[] v, Hash.Strategy<K> strategy) {
        this(k, v, 0.75f, strategy);
    }

    public Hash.Strategy<K> strategy() {
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
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private char removeNullEntry() {
        this.containsNullKey = false;
        this.key[this.n] = null;
        char oldValue = this.value[this.n];
        --this.size;
        this.fixPointers(this.n);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Character> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(K k) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return - pos + 1;
        }
        if (this.strategy.equals(k, curr)) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return - pos + 1;
        } while (!this.strategy.equals(k, curr));
        return pos;
    }

    private void insert(int pos, K k, char v) {
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
    public char put(K k, char v) {
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
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
            this.fixPointers(pos, last);
        } while (true);
    }

    @Override
    public char removeChar(Object k) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    private char setValue(int pos, char v) {
        char oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    public char removeFirstChar() {
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
        char v = this.value[pos];
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

    public char removeLastChar() {
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
        char v = this.value[pos];
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

    public char getAndMoveToFirst(K k) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public char getAndMoveToLast(K k) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public char putAndMoveToFirst(K k, char v) {
        int pos;
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (this.strategy.equals(curr, k)) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!this.strategy.equals(curr, k)) continue;
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

    public char putAndMoveToLast(K k, char v) {
        int pos;
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (this.strategy.equals(curr, k)) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!this.strategy.equals(curr, k)) continue;
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
    public char getChar(Object k) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(Object k) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return true;
    }

    @Override
    public boolean containsValue(char v) {
        char[] value = this.value;
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
    public char getOrDefault(Object k, char defaultValue) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return defaultValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return defaultValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public char putIfAbsent(K k, char v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, char v) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!this.strategy.equals(k, curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(K k, char oldValue, char v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public char replace(K k, char v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        char oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public char computeCharIfAbsent(K k, ToIntFunction<? super K> mappingFunction) {
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
    public char computeCharIfPresent(K k, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Character newValue = remappingFunction.apply(k, Character.valueOf(this.value[pos]));
        if (newValue == null) {
            if (this.strategy.equals(k, null)) {
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
    public char computeChar(K k, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Character newValue = remappingFunction.apply(k, pos >= 0 ? Character.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, null)) {
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
    public char mergeChar(K k, char v, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Character newValue = remappingFunction.apply(Character.valueOf(this.value[pos]), Character.valueOf(v));
        if (newValue == null) {
            if (this.strategy.equals(k, null)) {
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
    public Object2CharSortedMap<K> tailMap(K from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2CharSortedMap<K> headMap(K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2CharSortedMap<K> subMap(K from, K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public Object2CharSortedMap.FastSortedEntrySet<K> object2CharEntrySet() {
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
    public CharCollection values() {
        if (this.values == null) {
            this.values = new AbstractCharCollection(){

                @Override
                public CharIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Object2CharLinkedOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(char v) {
                    return Object2CharLinkedOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Object2CharLinkedOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(IntConsumer consumer) {
                    if (Object2CharLinkedOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Object2CharLinkedOpenCustomHashMap.this.value[Object2CharLinkedOpenCustomHashMap.this.n]);
                    }
                    int pos = Object2CharLinkedOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Object2CharLinkedOpenCustomHashMap.this.key[pos] == null) continue;
                        consumer.accept(Object2CharLinkedOpenCustomHashMap.this.value[pos]);
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
        char[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        char[] newValue = new char[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (this.strategy.equals(key[i], null)) {
                pos = newN;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
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

    public Object2CharLinkedOpenCustomHashMap<K> clone() {
        Object2CharLinkedOpenCustomHashMap c;
        try {
            c = (Object2CharLinkedOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (char[])this.value.clone();
        c.link = (long[])this.link.clone();
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
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = this.strategy.hashCode(this.key[i]);
            }
            h += (t ^= this.value[i]);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n];
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        char[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeChar(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new char[this.n + 1];
        char[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            char v = s.readChar();
            if (this.strategy.equals(k, null)) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
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
    extends Object2CharLinkedOpenCustomHashMap<K>
    implements CharListIterator {
        @Override
        public char previousChar() {
            return Object2CharLinkedOpenCustomHashMap.this.value[this.previousEntry()];
        }

        public ValueIterator() {
            super();
        }

        @Override
        public char nextChar() {
            return Object2CharLinkedOpenCustomHashMap.this.value[this.nextEntry()];
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
            if (Object2CharLinkedOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Object2CharLinkedOpenCustomHashMap.this.key[Object2CharLinkedOpenCustomHashMap.this.n]);
            }
            int pos = Object2CharLinkedOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                Object k = Object2CharLinkedOpenCustomHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Object2CharLinkedOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Object2CharLinkedOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Object2CharLinkedOpenCustomHashMap.this.size;
            Object2CharLinkedOpenCustomHashMap.this.removeChar(k);
            return Object2CharLinkedOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Object2CharLinkedOpenCustomHashMap.this.clear();
        }

        @Override
        public K first() {
            if (Object2CharLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2CharLinkedOpenCustomHashMap.this.key[Object2CharLinkedOpenCustomHashMap.this.first];
        }

        @Override
        public K last() {
            if (Object2CharLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2CharLinkedOpenCustomHashMap.this.key[Object2CharLinkedOpenCustomHashMap.this.last];
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
    extends Object2CharLinkedOpenCustomHashMap<K>
    implements ObjectListIterator<K> {
        public KeyIterator(K k) {
            super(k);
        }

        @Override
        public K previous() {
            return Object2CharLinkedOpenCustomHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Object2CharLinkedOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Object2CharMap.Entry<K>>
    implements Object2CharSortedMap.FastSortedEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Object2CharMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Object2CharMap.Entry<K>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Object2CharMap.Entry<K>> subSet(Object2CharMap.Entry<K> fromElement, Object2CharMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2CharMap.Entry<K>> headSet(Object2CharMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2CharMap.Entry<K>> tailSet(Object2CharMap.Entry<K> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object2CharMap.Entry<K> first() {
            if (Object2CharLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2CharLinkedOpenCustomHashMap.this.first);
        }

        @Override
        public Object2CharMap.Entry<K> last() {
            if (Object2CharLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2CharLinkedOpenCustomHashMap.this.last);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            Object k = e.getKey();
            char v = ((Character)e.getValue()).charValue();
            if (Object2CharLinkedOpenCustomHashMap.this.strategy.equals(k, null)) {
                return Object2CharLinkedOpenCustomHashMap.this.containsNullKey && Object2CharLinkedOpenCustomHashMap.this.value[Object2CharLinkedOpenCustomHashMap.this.n] == v;
            }
            K[] key = Object2CharLinkedOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2CharLinkedOpenCustomHashMap.this.strategy.hashCode(k)) & Object2CharLinkedOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2CharLinkedOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Object2CharLinkedOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2CharLinkedOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2CharLinkedOpenCustomHashMap.this.strategy.equals(k, curr));
            return Object2CharLinkedOpenCustomHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            Object k = e.getKey();
            char v = ((Character)e.getValue()).charValue();
            if (Object2CharLinkedOpenCustomHashMap.this.strategy.equals(k, null)) {
                if (Object2CharLinkedOpenCustomHashMap.this.containsNullKey && Object2CharLinkedOpenCustomHashMap.this.value[Object2CharLinkedOpenCustomHashMap.this.n] == v) {
                    Object2CharLinkedOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Object2CharLinkedOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2CharLinkedOpenCustomHashMap.this.strategy.hashCode(k)) & Object2CharLinkedOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2CharLinkedOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Object2CharLinkedOpenCustomHashMap.this.value[pos] == v) {
                    Object2CharLinkedOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2CharLinkedOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2CharLinkedOpenCustomHashMap.this.strategy.equals(curr, k) || Object2CharLinkedOpenCustomHashMap.this.value[pos] != v);
            Object2CharLinkedOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Object2CharLinkedOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Object2CharLinkedOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Object2CharMap.Entry<K>> consumer) {
            if (Object2CharLinkedOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractObject2CharMap.BasicEntry(Object2CharLinkedOpenCustomHashMap.this.key[Object2CharLinkedOpenCustomHashMap.this.n], Object2CharLinkedOpenCustomHashMap.this.value[Object2CharLinkedOpenCustomHashMap.this.n]));
            }
            int pos = Object2CharLinkedOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2CharLinkedOpenCustomHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractObject2CharMap.BasicEntry(Object2CharLinkedOpenCustomHashMap.this.key[pos], Object2CharLinkedOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2CharMap.Entry<K>> consumer) {
            AbstractObject2CharMap.BasicEntry entry = new AbstractObject2CharMap.BasicEntry();
            if (Object2CharLinkedOpenCustomHashMap.this.containsNullKey) {
                entry.key = Object2CharLinkedOpenCustomHashMap.this.key[Object2CharLinkedOpenCustomHashMap.this.n];
                entry.value = Object2CharLinkedOpenCustomHashMap.this.value[Object2CharLinkedOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Object2CharLinkedOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2CharLinkedOpenCustomHashMap.this.key[pos] == null) continue;
                entry.key = Object2CharLinkedOpenCustomHashMap.this.key[pos];
                entry.value = Object2CharLinkedOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }

        @Override
        public ObjectListIterator<Object2CharMap.Entry<K>> iterator(Object2CharMap.Entry<K> from) {
            return new EntryIterator(from.getKey());
        }

        @Override
        public ObjectListIterator<Object2CharMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectListIterator<Object2CharMap.Entry<K>> fastIterator(Object2CharMap.Entry<K> from) {
            return new FastEntryIterator(from.getKey());
        }
    }

    private class FastEntryIterator
    extends Object2CharLinkedOpenCustomHashMap<K>
    implements ObjectListIterator<Object2CharMap.Entry<K>> {
        final Object2CharLinkedOpenCustomHashMap<K> entry;

        public FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        public FastEntryIterator(K from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        public Object2CharLinkedOpenCustomHashMap<K> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public Object2CharLinkedOpenCustomHashMap<K> previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Object2CharLinkedOpenCustomHashMap<K>
    implements ObjectListIterator<Object2CharMap.Entry<K>> {
        private Object2CharLinkedOpenCustomHashMap<K> entry;

        public EntryIterator() {
            super();
        }

        public EntryIterator(K from) {
            super(from);
        }

        @Override
        public Object2CharLinkedOpenCustomHashMap<K> next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public Object2CharLinkedOpenCustomHashMap<K> previous() {
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
            this.next = Object2CharLinkedOpenCustomHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(K from) {
            if (Object2CharLinkedOpenCustomHashMap.this.strategy.equals(from, null)) {
                if (Object2CharLinkedOpenCustomHashMap.this.containsNullKey) {
                    this.next = (int)Object2CharLinkedOpenCustomHashMap.this.link[Object2CharLinkedOpenCustomHashMap.this.n];
                    this.prev = Object2CharLinkedOpenCustomHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Object2CharLinkedOpenCustomHashMap.this.strategy.equals(Object2CharLinkedOpenCustomHashMap.this.key[Object2CharLinkedOpenCustomHashMap.this.last], from)) {
                this.prev = Object2CharLinkedOpenCustomHashMap.this.last;
                this.index = Object2CharLinkedOpenCustomHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(Object2CharLinkedOpenCustomHashMap.this.strategy.hashCode(from)) & Object2CharLinkedOpenCustomHashMap.this.mask;
            while (Object2CharLinkedOpenCustomHashMap.this.key[pos] != null) {
                if (Object2CharLinkedOpenCustomHashMap.this.strategy.equals(Object2CharLinkedOpenCustomHashMap.this.key[pos], from)) {
                    this.next = (int)Object2CharLinkedOpenCustomHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Object2CharLinkedOpenCustomHashMap.this.mask;
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
                this.index = Object2CharLinkedOpenCustomHashMap.this.size;
                return;
            }
            int pos = Object2CharLinkedOpenCustomHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Object2CharLinkedOpenCustomHashMap.this.link[pos];
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
            this.next = (int)Object2CharLinkedOpenCustomHashMap.this.link[this.curr];
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
            this.prev = (int)(Object2CharLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
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
                this.prev = (int)(Object2CharLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Object2CharLinkedOpenCustomHashMap.this.link[this.curr];
            }
            --Object2CharLinkedOpenCustomHashMap.this.size;
            if (this.prev == -1) {
                Object2CharLinkedOpenCustomHashMap.this.first = this.next;
            } else {
                long[] arrl = Object2CharLinkedOpenCustomHashMap.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (Object2CharLinkedOpenCustomHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Object2CharLinkedOpenCustomHashMap.this.last = this.prev;
            } else {
                long[] arrl = Object2CharLinkedOpenCustomHashMap.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (Object2CharLinkedOpenCustomHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Object2CharLinkedOpenCustomHashMap.this.n) {
                K[] key = Object2CharLinkedOpenCustomHashMap.this.key;
                do {
                    Object curr;
                    int last = pos;
                    pos = last + 1 & Object2CharLinkedOpenCustomHashMap.this.mask;
                    do {
                        if ((curr = key[pos]) == null) {
                            key[last] = null;
                            return;
                        }
                        int slot = HashCommon.mix(Object2CharLinkedOpenCustomHashMap.this.strategy.hashCode(curr)) & Object2CharLinkedOpenCustomHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Object2CharLinkedOpenCustomHashMap.this.mask;
                    } while (true);
                    key[last] = curr;
                    Object2CharLinkedOpenCustomHashMap.this.value[last] = Object2CharLinkedOpenCustomHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Object2CharLinkedOpenCustomHashMap.this.fixPointers(pos, last);
                } while (true);
            }
            Object2CharLinkedOpenCustomHashMap.this.containsNullKey = false;
            Object2CharLinkedOpenCustomHashMap.this.key[Object2CharLinkedOpenCustomHashMap.this.n] = null;
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

        public void set(Object2CharMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }

        public void add(Object2CharMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Object2CharMap.Entry<K>,
    Map.Entry<K, Character> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Object2CharLinkedOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public char getCharValue() {
            return Object2CharLinkedOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public char setValue(char v) {
            char oldValue = Object2CharLinkedOpenCustomHashMap.this.value[this.index];
            Object2CharLinkedOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Character getValue() {
            return Character.valueOf(Object2CharLinkedOpenCustomHashMap.this.value[this.index]);
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
            return Object2CharLinkedOpenCustomHashMap.this.strategy.equals(Object2CharLinkedOpenCustomHashMap.this.key[this.index], e.getKey()) && Object2CharLinkedOpenCustomHashMap.this.value[this.index] == ((Character)e.getValue()).charValue();
        }

        @Override
        public int hashCode() {
            return Object2CharLinkedOpenCustomHashMap.this.strategy.hashCode(Object2CharLinkedOpenCustomHashMap.this.key[this.index]) ^ Object2CharLinkedOpenCustomHashMap.this.value[this.index];
        }

        public String toString() {
            return Object2CharLinkedOpenCustomHashMap.this.key[this.index] + "=>" + Object2CharLinkedOpenCustomHashMap.this.value[this.index];
        }
    }

}

