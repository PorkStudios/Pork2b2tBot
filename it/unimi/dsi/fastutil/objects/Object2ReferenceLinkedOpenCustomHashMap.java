/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObject2ReferenceMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2ReferenceSortedMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceSortedMap;
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
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Consumer;

public class Object2ReferenceLinkedOpenCustomHashMap<K, V>
extends AbstractObject2ReferenceSortedMap<K, V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient V[] value;
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
    protected transient Object2ReferenceSortedMap.FastSortedEntrySet<K, V> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient ReferenceCollection<V> values;

    public Object2ReferenceLinkedOpenCustomHashMap(int expected, float f, Hash.Strategy<K> strategy) {
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
        this.value = new Object[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Object2ReferenceLinkedOpenCustomHashMap(int expected, Hash.Strategy<K> strategy) {
        this(expected, 0.75f, strategy);
    }

    public Object2ReferenceLinkedOpenCustomHashMap(Hash.Strategy<K> strategy) {
        this(16, 0.75f, strategy);
    }

    public Object2ReferenceLinkedOpenCustomHashMap(Map<? extends K, ? extends V> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2ReferenceLinkedOpenCustomHashMap(Map<? extends K, ? extends V> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2ReferenceLinkedOpenCustomHashMap(Object2ReferenceMap<K, V> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2ReferenceLinkedOpenCustomHashMap(Object2ReferenceMap<K, V> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2ReferenceLinkedOpenCustomHashMap(K[] k, V[] v, float f, Hash.Strategy<K> strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2ReferenceLinkedOpenCustomHashMap(K[] k, V[] v, Hash.Strategy<K> strategy) {
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
        this.key[this.n] = null;
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
    public void putAll(Map<? extends K, ? extends V> m) {
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

    private void insert(int pos, K k, V v) {
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
    public V put(K k, V v) {
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
        K[] key = this.key;
        do {
            K curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == null) {
                    key[last] = null;
                    this.value[last] = null;
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
    public V remove(Object k) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
        } while (!this.strategy.equals(k, curr));
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
            this.key[this.n] = null;
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
            this.key[this.n] = null;
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

    public V getAndMoveToFirst(K k) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public V getAndMoveToLast(K k) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public V putAndMoveToFirst(K k, V v) {
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
        return (V)this.defRetValue;
    }

    public V putAndMoveToLast(K k, V v) {
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
        return (V)this.defRetValue;
    }

    @Override
    public V get(Object k) {
        if (this.strategy.equals(k, null)) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
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
    public boolean containsValue(Object v) {
        V[] value = this.value;
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
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, null);
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
    public Object2ReferenceSortedMap<K, V> tailMap(K from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2ReferenceSortedMap<K, V> headMap(K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public Object2ReferenceSortedMap.FastSortedEntrySet<K, V> object2ReferenceEntrySet() {
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
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Object2ReferenceLinkedOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Object2ReferenceLinkedOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Object2ReferenceLinkedOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n]);
                    }
                    int pos = Object2ReferenceLinkedOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Object2ReferenceLinkedOpenCustomHashMap.this.key[pos] == null) continue;
                        consumer.accept(Object2ReferenceLinkedOpenCustomHashMap.this.value[pos]);
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
        V[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
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

    public Object2ReferenceLinkedOpenCustomHashMap<K, V> clone() {
        Object2ReferenceLinkedOpenCustomHashMap c;
        try {
            c = (Object2ReferenceLinkedOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (Object[])this.value.clone();
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
        K[] key = this.key;
        V[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeObject(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
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
            Object k = s.readObject();
            Object v = s.readObject();
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
    extends Object2ReferenceLinkedOpenCustomHashMap<K, V>
    implements ObjectListIterator<V> {
        @Override
        public V previous() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.value[this.previousEntry()];
        }

        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.value[this.nextEntry()];
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
            if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.n]);
            }
            int pos = Object2ReferenceLinkedOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                Object k = Object2ReferenceLinkedOpenCustomHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Object2ReferenceLinkedOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Object2ReferenceLinkedOpenCustomHashMap.this.size;
            Object2ReferenceLinkedOpenCustomHashMap.this.remove(k);
            return Object2ReferenceLinkedOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Object2ReferenceLinkedOpenCustomHashMap.this.clear();
        }

        @Override
        public K first() {
            if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.first];
        }

        @Override
        public K last() {
            if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.last];
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
    extends Object2ReferenceLinkedOpenCustomHashMap<K, V>
    implements ObjectListIterator<K> {
        public KeyIterator(K k) {
            super(k);
        }

        @Override
        public K previous() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Object2ReferenceMap.Entry<K, V>>
    implements Object2ReferenceSortedMap.FastSortedEntrySet<K, V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Object2ReferenceMap.Entry<K, V>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> subSet(Object2ReferenceMap.Entry<K, V> fromElement, Object2ReferenceMap.Entry<K, V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> headSet(Object2ReferenceMap.Entry<K, V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> tailSet(Object2ReferenceMap.Entry<K, V> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object2ReferenceMap.Entry<K, V> first() {
            if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2ReferenceLinkedOpenCustomHashMap.this.first);
        }

        @Override
        public Object2ReferenceMap.Entry<K, V> last() {
            if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2ReferenceLinkedOpenCustomHashMap.this.last);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, null)) {
                return Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey && Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n] == v;
            }
            K[] key = Object2ReferenceLinkedOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(k)) & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, curr));
            return Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, null)) {
                if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey && Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n] == v) {
                    Object2ReferenceLinkedOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Object2ReferenceLinkedOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(k)) & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] == v) {
                    Object2ReferenceLinkedOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(curr, k) || Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] != v);
            Object2ReferenceLinkedOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Object2ReferenceLinkedOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Object2ReferenceMap.Entry<K, V>> consumer) {
            if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractObject2ReferenceMap.BasicEntry(Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.n], Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n]));
            }
            int pos = Object2ReferenceLinkedOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2ReferenceLinkedOpenCustomHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractObject2ReferenceMap.BasicEntry(Object2ReferenceLinkedOpenCustomHashMap.this.key[pos], Object2ReferenceLinkedOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2ReferenceMap.Entry<K, V>> consumer) {
            AbstractObject2ReferenceMap.BasicEntry entry = new AbstractObject2ReferenceMap.BasicEntry();
            if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey) {
                entry.key = Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.n];
                entry.value = Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Object2ReferenceLinkedOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2ReferenceLinkedOpenCustomHashMap.this.key[pos] == null) continue;
                entry.key = Object2ReferenceLinkedOpenCustomHashMap.this.key[pos];
                entry.value = Object2ReferenceLinkedOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }

        @Override
        public ObjectListIterator<Object2ReferenceMap.Entry<K, V>> iterator(Object2ReferenceMap.Entry<K, V> from) {
            return new EntryIterator(from.getKey());
        }

        @Override
        public ObjectListIterator<Object2ReferenceMap.Entry<K, V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectListIterator<Object2ReferenceMap.Entry<K, V>> fastIterator(Object2ReferenceMap.Entry<K, V> from) {
            return new FastEntryIterator(from.getKey());
        }
    }

    private class FastEntryIterator
    extends Object2ReferenceLinkedOpenCustomHashMap<K, V>
    implements ObjectListIterator<Object2ReferenceMap.Entry<K, V>> {
        final Object2ReferenceLinkedOpenCustomHashMap<K, V> entry;

        public FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        public FastEntryIterator(K from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        public Object2ReferenceLinkedOpenCustomHashMap<K, V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public Object2ReferenceLinkedOpenCustomHashMap<K, V> previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Object2ReferenceLinkedOpenCustomHashMap<K, V>
    implements ObjectListIterator<Object2ReferenceMap.Entry<K, V>> {
        private Object2ReferenceLinkedOpenCustomHashMap<K, V> entry;

        public EntryIterator() {
            super();
        }

        public EntryIterator(K from) {
            super(from);
        }

        @Override
        public Object2ReferenceLinkedOpenCustomHashMap<K, V> next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public Object2ReferenceLinkedOpenCustomHashMap<K, V> previous() {
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
            this.next = Object2ReferenceLinkedOpenCustomHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(K from) {
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(from, null)) {
                if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey) {
                    this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[Object2ReferenceLinkedOpenCustomHashMap.this.n];
                    this.prev = Object2ReferenceLinkedOpenCustomHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.last], from)) {
                this.prev = Object2ReferenceLinkedOpenCustomHashMap.this.last;
                this.index = Object2ReferenceLinkedOpenCustomHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(from)) & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
            while (Object2ReferenceLinkedOpenCustomHashMap.this.key[pos] != null) {
                if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(Object2ReferenceLinkedOpenCustomHashMap.this.key[pos], from)) {
                    this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
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
                this.index = Object2ReferenceLinkedOpenCustomHashMap.this.size;
                return;
            }
            int pos = Object2ReferenceLinkedOpenCustomHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[pos];
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
            this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr];
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
            this.prev = (int)(Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
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
                this.prev = (int)(Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr];
            }
            --Object2ReferenceLinkedOpenCustomHashMap.this.size;
            if (this.prev == -1) {
                Object2ReferenceLinkedOpenCustomHashMap.this.first = this.next;
            } else {
                long[] arrl = Object2ReferenceLinkedOpenCustomHashMap.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (Object2ReferenceLinkedOpenCustomHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Object2ReferenceLinkedOpenCustomHashMap.this.last = this.prev;
            } else {
                long[] arrl = Object2ReferenceLinkedOpenCustomHashMap.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (Object2ReferenceLinkedOpenCustomHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Object2ReferenceLinkedOpenCustomHashMap.this.n) {
                K[] key = Object2ReferenceLinkedOpenCustomHashMap.this.key;
                do {
                    Object curr;
                    int last = pos;
                    pos = last + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
                    do {
                        if ((curr = key[pos]) == null) {
                            key[last] = null;
                            Object2ReferenceLinkedOpenCustomHashMap.this.value[last] = null;
                            return;
                        }
                        int slot = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(curr)) & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
                    } while (true);
                    key[last] = curr;
                    Object2ReferenceLinkedOpenCustomHashMap.this.value[last] = Object2ReferenceLinkedOpenCustomHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Object2ReferenceLinkedOpenCustomHashMap.this.fixPointers(pos, last);
                } while (true);
            }
            Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey = false;
            Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.n] = null;
            Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n] = null;
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

        public void set(Object2ReferenceMap.Entry<K, V> ok) {
            throw new UnsupportedOperationException();
        }

        public void add(Object2ReferenceMap.Entry<K, V> ok) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Object2ReferenceMap.Entry<K, V>,
    Map.Entry<K, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index];
            Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index], e.getKey()) && Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index] == e.getValue();
        }

        @Override
        public int hashCode() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index]) ^ (Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index]));
        }

        public String toString() {
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index] + "=>" + Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index];
        }
    }

}

