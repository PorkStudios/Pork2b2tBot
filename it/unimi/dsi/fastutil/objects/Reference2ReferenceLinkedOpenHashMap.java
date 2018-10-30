/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.AbstractReference2ReferenceSortedMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceSortedMap;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
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

public class Reference2ReferenceLinkedOpenHashMap<K, V>
extends AbstractReference2ReferenceSortedMap<K, V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
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
    protected transient Reference2ReferenceSortedMap.FastSortedEntrySet<K, V> entries;
    protected transient ReferenceSortedSet<K> keys;
    protected transient ReferenceCollection<V> values;

    public Reference2ReferenceLinkedOpenHashMap(int expected, float f) {
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

    public Reference2ReferenceLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Reference2ReferenceLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Reference2ReferenceLinkedOpenHashMap(Map<? extends K, ? extends V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2ReferenceLinkedOpenHashMap(Map<? extends K, ? extends V> m) {
        this(m, 0.75f);
    }

    public Reference2ReferenceLinkedOpenHashMap(Reference2ReferenceMap<K, V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2ReferenceLinkedOpenHashMap(Reference2ReferenceMap<K, V> m) {
        this(m, 0.75f);
    }

    public Reference2ReferenceLinkedOpenHashMap(K[] k, V[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Reference2ReferenceLinkedOpenHashMap(K[] k, V[] v) {
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
        if (k == null) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return - pos + 1;
        }
        if (k == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return - pos + 1;
        } while (k != curr);
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
                int slot = HashCommon.mix(System.identityHashCode(curr)) & this.mask;
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
        if (k == null) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
        } while (k != curr);
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
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (k == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
        } while (k != curr);
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public V getAndMoveToLast(K k) {
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (k == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
        } while (k != curr);
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public V putAndMoveToFirst(K k, V v) {
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
            pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr == k) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
        return (V)this.defRetValue;
    }

    public V putAndMoveToLast(K k, V v) {
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
            pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr == k) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
        return (V)this.defRetValue;
    }

    @Override
    public V get(Object k) {
        if (k == null) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return (V)this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return (V)this.defRetValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(Object k) {
        if (k == null) {
            return this.containsNullKey;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (k != curr);
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
    public Reference2ReferenceSortedMap<K, V> tailMap(K from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reference2ReferenceSortedMap<K, V> headMap(K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reference2ReferenceSortedMap<K, V> subMap(K from, K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public Reference2ReferenceSortedMap.FastSortedEntrySet<K, V> reference2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
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
                    return Reference2ReferenceLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Reference2ReferenceLinkedOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Reference2ReferenceLinkedOpenHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Reference2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                        consumer.accept(Reference2ReferenceLinkedOpenHashMap.this.value[Reference2ReferenceLinkedOpenHashMap.this.n]);
                    }
                    int pos = Reference2ReferenceLinkedOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Reference2ReferenceLinkedOpenHashMap.this.key[pos] == null) continue;
                        consumer.accept(Reference2ReferenceLinkedOpenHashMap.this.value[pos]);
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
            if (key[i] == null) {
                pos = newN;
            } else {
                pos = HashCommon.mix(System.identityHashCode(key[i])) & mask;
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

    public Reference2ReferenceLinkedOpenHashMap<K, V> clone() {
        Reference2ReferenceLinkedOpenHashMap c;
        try {
            c = (Reference2ReferenceLinkedOpenHashMap)Object.super.clone();
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
                t = System.identityHashCode(this.key[i]);
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
            if (k == null) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
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
    extends Reference2ReferenceLinkedOpenHashMap<K, V>
    implements ObjectListIterator<V> {
        @Override
        public V previous() {
            return Reference2ReferenceLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Reference2ReferenceLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractReferenceSortedSet<K> {
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
            if (Reference2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(Reference2ReferenceLinkedOpenHashMap.this.key[Reference2ReferenceLinkedOpenHashMap.this.n]);
            }
            int pos = Reference2ReferenceLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                Object k = Reference2ReferenceLinkedOpenHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Reference2ReferenceLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Reference2ReferenceLinkedOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Reference2ReferenceLinkedOpenHashMap.this.size;
            Reference2ReferenceLinkedOpenHashMap.this.remove(k);
            return Reference2ReferenceLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Reference2ReferenceLinkedOpenHashMap.this.clear();
        }

        @Override
        public K first() {
            if (Reference2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Reference2ReferenceLinkedOpenHashMap.this.key[Reference2ReferenceLinkedOpenHashMap.this.first];
        }

        @Override
        public K last() {
            if (Reference2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Reference2ReferenceLinkedOpenHashMap.this.key[Reference2ReferenceLinkedOpenHashMap.this.last];
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator
    extends Reference2ReferenceLinkedOpenHashMap<K, V>
    implements ObjectListIterator<K> {
        public KeyIterator(K k) {
            super(k);
        }

        @Override
        public K previous() {
            return Reference2ReferenceLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Reference2ReferenceLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Reference2ReferenceMap.Entry<K, V>>
    implements Reference2ReferenceSortedMap.FastSortedEntrySet<K, V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Reference2ReferenceMap.Entry<K, V>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> subSet(Reference2ReferenceMap.Entry<K, V> fromElement, Reference2ReferenceMap.Entry<K, V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> headSet(Reference2ReferenceMap.Entry<K, V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> tailSet(Reference2ReferenceMap.Entry<K, V> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reference2ReferenceMap.Entry<K, V> first() {
            if (Reference2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Reference2ReferenceLinkedOpenHashMap.this.first);
        }

        @Override
        public Reference2ReferenceMap.Entry<K, V> last() {
            if (Reference2ReferenceLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Reference2ReferenceLinkedOpenHashMap.this.last);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k == null) {
                return Reference2ReferenceLinkedOpenHashMap.this.containsNullKey && Reference2ReferenceLinkedOpenHashMap.this.value[Reference2ReferenceLinkedOpenHashMap.this.n] == v;
            }
            K[] key = Reference2ReferenceLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2ReferenceLinkedOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k == curr) {
                return Reference2ReferenceLinkedOpenHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2ReferenceLinkedOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (k != curr);
            return Reference2ReferenceLinkedOpenHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k == null) {
                if (Reference2ReferenceLinkedOpenHashMap.this.containsNullKey && Reference2ReferenceLinkedOpenHashMap.this.value[Reference2ReferenceLinkedOpenHashMap.this.n] == v) {
                    Reference2ReferenceLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Reference2ReferenceLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2ReferenceLinkedOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr == k) {
                if (Reference2ReferenceLinkedOpenHashMap.this.value[pos] == v) {
                    Reference2ReferenceLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2ReferenceLinkedOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (curr != k || Reference2ReferenceLinkedOpenHashMap.this.value[pos] != v);
            Reference2ReferenceLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Reference2ReferenceLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Reference2ReferenceLinkedOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> consumer) {
            if (Reference2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractReference2ReferenceMap.BasicEntry(Reference2ReferenceLinkedOpenHashMap.this.key[Reference2ReferenceLinkedOpenHashMap.this.n], Reference2ReferenceLinkedOpenHashMap.this.value[Reference2ReferenceLinkedOpenHashMap.this.n]));
            }
            int pos = Reference2ReferenceLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2ReferenceLinkedOpenHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractReference2ReferenceMap.BasicEntry(Reference2ReferenceLinkedOpenHashMap.this.key[pos], Reference2ReferenceLinkedOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> consumer) {
            AbstractReference2ReferenceMap.BasicEntry entry = new AbstractReference2ReferenceMap.BasicEntry();
            if (Reference2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                entry.key = Reference2ReferenceLinkedOpenHashMap.this.key[Reference2ReferenceLinkedOpenHashMap.this.n];
                entry.value = Reference2ReferenceLinkedOpenHashMap.this.value[Reference2ReferenceLinkedOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Reference2ReferenceLinkedOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2ReferenceLinkedOpenHashMap.this.key[pos] == null) continue;
                entry.key = Reference2ReferenceLinkedOpenHashMap.this.key[pos];
                entry.value = Reference2ReferenceLinkedOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }

        @Override
        public ObjectListIterator<Reference2ReferenceMap.Entry<K, V>> iterator(Reference2ReferenceMap.Entry<K, V> from) {
            return new EntryIterator(from.getKey());
        }

        @Override
        public ObjectListIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectListIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator(Reference2ReferenceMap.Entry<K, V> from) {
            return new FastEntryIterator(from.getKey());
        }
    }

    private class FastEntryIterator
    extends Reference2ReferenceLinkedOpenHashMap<K, V>
    implements ObjectListIterator<Reference2ReferenceMap.Entry<K, V>> {
        final Reference2ReferenceLinkedOpenHashMap<K, V> entry;

        public FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        public FastEntryIterator(K from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        public Reference2ReferenceLinkedOpenHashMap<K, V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public Reference2ReferenceLinkedOpenHashMap<K, V> previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Reference2ReferenceLinkedOpenHashMap<K, V>
    implements ObjectListIterator<Reference2ReferenceMap.Entry<K, V>> {
        private Reference2ReferenceLinkedOpenHashMap<K, V> entry;

        public EntryIterator() {
            super();
        }

        public EntryIterator(K from) {
            super(from);
        }

        @Override
        public Reference2ReferenceLinkedOpenHashMap<K, V> next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public Reference2ReferenceLinkedOpenHashMap<K, V> previous() {
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
            this.next = Reference2ReferenceLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(K from) {
            if (from == null) {
                if (Reference2ReferenceLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Reference2ReferenceLinkedOpenHashMap.this.link[Reference2ReferenceLinkedOpenHashMap.this.n];
                    this.prev = Reference2ReferenceLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Reference2ReferenceLinkedOpenHashMap.this.key[Reference2ReferenceLinkedOpenHashMap.this.last] == from) {
                this.prev = Reference2ReferenceLinkedOpenHashMap.this.last;
                this.index = Reference2ReferenceLinkedOpenHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(System.identityHashCode(from)) & Reference2ReferenceLinkedOpenHashMap.this.mask;
            while (Reference2ReferenceLinkedOpenHashMap.this.key[pos] != null) {
                if (Reference2ReferenceLinkedOpenHashMap.this.key[pos] == from) {
                    this.next = (int)Reference2ReferenceLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Reference2ReferenceLinkedOpenHashMap.this.mask;
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
                this.index = Reference2ReferenceLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Reference2ReferenceLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Reference2ReferenceLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Reference2ReferenceLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Reference2ReferenceLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
                this.prev = (int)(Reference2ReferenceLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Reference2ReferenceLinkedOpenHashMap.this.link[this.curr];
            }
            --Reference2ReferenceLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Reference2ReferenceLinkedOpenHashMap.this.first = this.next;
            } else {
                long[] arrl = Reference2ReferenceLinkedOpenHashMap.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (Reference2ReferenceLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Reference2ReferenceLinkedOpenHashMap.this.last = this.prev;
            } else {
                long[] arrl = Reference2ReferenceLinkedOpenHashMap.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (Reference2ReferenceLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Reference2ReferenceLinkedOpenHashMap.this.n) {
                K[] key = Reference2ReferenceLinkedOpenHashMap.this.key;
                do {
                    Object curr;
                    int last = pos;
                    pos = last + 1 & Reference2ReferenceLinkedOpenHashMap.this.mask;
                    do {
                        if ((curr = key[pos]) == null) {
                            key[last] = null;
                            Reference2ReferenceLinkedOpenHashMap.this.value[last] = null;
                            return;
                        }
                        int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2ReferenceLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Reference2ReferenceLinkedOpenHashMap.this.mask;
                    } while (true);
                    key[last] = curr;
                    Reference2ReferenceLinkedOpenHashMap.this.value[last] = Reference2ReferenceLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Reference2ReferenceLinkedOpenHashMap.this.fixPointers(pos, last);
                } while (true);
            }
            Reference2ReferenceLinkedOpenHashMap.this.containsNullKey = false;
            Reference2ReferenceLinkedOpenHashMap.this.key[Reference2ReferenceLinkedOpenHashMap.this.n] = null;
            Reference2ReferenceLinkedOpenHashMap.this.value[Reference2ReferenceLinkedOpenHashMap.this.n] = null;
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

        public void set(Reference2ReferenceMap.Entry<K, V> ok) {
            throw new UnsupportedOperationException();
        }

        public void add(Reference2ReferenceMap.Entry<K, V> ok) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Reference2ReferenceMap.Entry<K, V>,
    Map.Entry<K, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Reference2ReferenceLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Reference2ReferenceLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Reference2ReferenceLinkedOpenHashMap.this.value[this.index];
            Reference2ReferenceLinkedOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Reference2ReferenceLinkedOpenHashMap.this.key[this.index] == e.getKey() && Reference2ReferenceLinkedOpenHashMap.this.value[this.index] == e.getValue();
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(Reference2ReferenceLinkedOpenHashMap.this.key[this.index]) ^ (Reference2ReferenceLinkedOpenHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Reference2ReferenceLinkedOpenHashMap.this.value[this.index]));
        }

        public String toString() {
            return Reference2ReferenceLinkedOpenHashMap.this.key[this.index] + "=>" + Reference2ReferenceLinkedOpenHashMap.this.value[this.index];
        }
    }

}

