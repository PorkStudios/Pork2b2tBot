/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

public class Reference2ReferenceOpenCustomHashMap<K, V>
extends AbstractReference2ReferenceMap<K, V>
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
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Reference2ReferenceMap.FastEntrySet<K, V> entries;
    protected transient ReferenceSet<K> keys;
    protected transient ReferenceCollection<V> values;

    public Reference2ReferenceOpenCustomHashMap(int expected, float f, Hash.Strategy<K> strategy) {
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
    }

    public Reference2ReferenceOpenCustomHashMap(int expected, Hash.Strategy<K> strategy) {
        this(expected, 0.75f, strategy);
    }

    public Reference2ReferenceOpenCustomHashMap(Hash.Strategy<K> strategy) {
        this(16, 0.75f, strategy);
    }

    public Reference2ReferenceOpenCustomHashMap(Map<? extends K, ? extends V> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Reference2ReferenceOpenCustomHashMap(Map<? extends K, ? extends V> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Reference2ReferenceOpenCustomHashMap(Reference2ReferenceMap<K, V> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Reference2ReferenceOpenCustomHashMap(Reference2ReferenceMap<K, V> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Reference2ReferenceOpenCustomHashMap(K[] k, V[] v, float f, Hash.Strategy<K> strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Reference2ReferenceOpenCustomHashMap(K[] k, V[] v, Hash.Strategy<K> strategy) {
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
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Reference2ReferenceMap.FastEntrySet<K, V> reference2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ReferenceSet<K> keySet() {
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
                    return Reference2ReferenceOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Reference2ReferenceOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Reference2ReferenceOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n]);
                    }
                    int pos = Reference2ReferenceOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Reference2ReferenceOpenCustomHashMap.this.key[pos] == null) continue;
                        consumer.accept(Reference2ReferenceOpenCustomHashMap.this.value[pos]);
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
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == null) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
            if (newKey[pos] != null) {
                while (newKey[pos = pos + 1 & mask] != null) {
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

    public Reference2ReferenceOpenCustomHashMap<K, V> clone() {
        Reference2ReferenceOpenCustomHashMap c;
        try {
            c = (Reference2ReferenceOpenCustomHashMap)Object.super.clone();
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
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends Reference2ReferenceOpenCustomHashMap<K, V>
    implements ObjectIterator<V> {
        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Reference2ReferenceOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractReferenceSet<K> {
        private KeySet() {
        }

        @Override
        public ObjectIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(Consumer<? super K> consumer) {
            if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Reference2ReferenceOpenCustomHashMap.this.key[Reference2ReferenceOpenCustomHashMap.this.n]);
            }
            int pos = Reference2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                Object k = Reference2ReferenceOpenCustomHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Reference2ReferenceOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Reference2ReferenceOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Reference2ReferenceOpenCustomHashMap.this.size;
            Reference2ReferenceOpenCustomHashMap.this.remove(k);
            return Reference2ReferenceOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Reference2ReferenceOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Reference2ReferenceOpenCustomHashMap<K, V>
    implements ObjectIterator<K> {
        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Reference2ReferenceOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Reference2ReferenceMap.Entry<K, V>>
    implements Reference2ReferenceMap.FastEntrySet<K, V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, null)) {
                return Reference2ReferenceOpenCustomHashMap.this.containsNullKey && Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n] == v;
            }
            K[] key = Reference2ReferenceOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Reference2ReferenceOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Reference2ReferenceOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2ReferenceOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr));
            return Reference2ReferenceOpenCustomHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, null)) {
                if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey && Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n] == v) {
                    Reference2ReferenceOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Reference2ReferenceOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Reference2ReferenceOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Reference2ReferenceOpenCustomHashMap.this.value[pos] == v) {
                    Reference2ReferenceOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2ReferenceOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Reference2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k) || Reference2ReferenceOpenCustomHashMap.this.value[pos] != v);
            Reference2ReferenceOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Reference2ReferenceOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Reference2ReferenceOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> consumer) {
            if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractReference2ReferenceMap.BasicEntry(Reference2ReferenceOpenCustomHashMap.this.key[Reference2ReferenceOpenCustomHashMap.this.n], Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n]));
            }
            int pos = Reference2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2ReferenceOpenCustomHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractReference2ReferenceMap.BasicEntry(Reference2ReferenceOpenCustomHashMap.this.key[pos], Reference2ReferenceOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> consumer) {
            AbstractReference2ReferenceMap.BasicEntry entry = new AbstractReference2ReferenceMap.BasicEntry();
            if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
                entry.key = Reference2ReferenceOpenCustomHashMap.this.key[Reference2ReferenceOpenCustomHashMap.this.n];
                entry.value = Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Reference2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2ReferenceOpenCustomHashMap.this.key[pos] == null) continue;
                entry.key = Reference2ReferenceOpenCustomHashMap.this.key[pos];
                entry.value = Reference2ReferenceOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Reference2ReferenceOpenCustomHashMap<K, V>
    implements ObjectIterator<Reference2ReferenceMap.Entry<K, V>> {
        private final Reference2ReferenceOpenCustomHashMap<K, V> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Reference2ReferenceOpenCustomHashMap<K, V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Reference2ReferenceOpenCustomHashMap<K, V>
    implements ObjectIterator<Reference2ReferenceMap.Entry<K, V>> {
        private Reference2ReferenceOpenCustomHashMap<K, V> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Reference2ReferenceOpenCustomHashMap<K, V> next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            MapIterator.super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        ReferenceArrayList<K> wrapped;

        private MapIterator() {
            this.pos = Reference2ReferenceOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Reference2ReferenceOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Reference2ReferenceOpenCustomHashMap.this.containsNullKey;
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
                this.last = Reference2ReferenceOpenCustomHashMap.this.n;
                return this.last;
            }
            K[] key = Reference2ReferenceOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                K k = this.wrapped.get(- this.pos - 1);
                int p = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Reference2ReferenceOpenCustomHashMap.this.mask;
                while (!Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Reference2ReferenceOpenCustomHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            K[] key = Reference2ReferenceOpenCustomHashMap.this.key;
            do {
                Object curr;
                int last = pos;
                pos = last + 1 & Reference2ReferenceOpenCustomHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        Reference2ReferenceOpenCustomHashMap.this.value[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(curr)) & Reference2ReferenceOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Reference2ReferenceOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Reference2ReferenceOpenCustomHashMap.this.value[last] = Reference2ReferenceOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Reference2ReferenceOpenCustomHashMap.this.n) {
                Reference2ReferenceOpenCustomHashMap.this.containsNullKey = false;
                Reference2ReferenceOpenCustomHashMap.this.key[Reference2ReferenceOpenCustomHashMap.this.n] = null;
                Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Reference2ReferenceOpenCustomHashMap.this.remove(this.wrapped.set(- this.pos - 1, null));
                this.last = -1;
                return;
            }
            --Reference2ReferenceOpenCustomHashMap.this.size;
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
            return Reference2ReferenceOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Reference2ReferenceOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Reference2ReferenceOpenCustomHashMap.this.value[this.index];
            Reference2ReferenceOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Reference2ReferenceOpenCustomHashMap.this.strategy.equals(Reference2ReferenceOpenCustomHashMap.this.key[this.index], e.getKey()) && Reference2ReferenceOpenCustomHashMap.this.value[this.index] == e.getValue();
        }

        @Override
        public int hashCode() {
            return Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(Reference2ReferenceOpenCustomHashMap.this.key[this.index]) ^ (Reference2ReferenceOpenCustomHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Reference2ReferenceOpenCustomHashMap.this.value[this.index]));
        }

        public String toString() {
            return Reference2ReferenceOpenCustomHashMap.this.key[this.index] + "=>" + Reference2ReferenceOpenCustomHashMap.this.value[this.index];
        }
    }

}

