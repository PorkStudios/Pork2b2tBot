/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2ObjectMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class Reference2ObjectOpenHashMap<K, V>
extends AbstractReference2ObjectMap<K, V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Reference2ObjectMap.FastEntrySet<K, V> entries;
    protected transient ReferenceSet<K> keys;
    protected transient ObjectCollection<V> values;

    public Reference2ObjectOpenHashMap(int expected, float f) {
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

    public Reference2ObjectOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Reference2ObjectOpenHashMap() {
        this(16, 0.75f);
    }

    public Reference2ObjectOpenHashMap(Map<? extends K, ? extends V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2ObjectOpenHashMap(Map<? extends K, ? extends V> m) {
        this(m, 0.75f);
    }

    public Reference2ObjectOpenHashMap(Reference2ObjectMap<K, V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2ObjectOpenHashMap(Reference2ObjectMap<K, V> m) {
        this(m, 0.75f);
    }

    public Reference2ObjectOpenHashMap(K[] k, V[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Reference2ObjectOpenHashMap(K[] k, V[] v) {
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
        if (this.containsNullKey && Objects.equals(value[this.n], v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == null || !Objects.equals(value[i], v)) continue;
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

    public Reference2ObjectMap.FastEntrySet<K, V> reference2ObjectEntrySet() {
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
    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Reference2ObjectOpenHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Reference2ObjectOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Reference2ObjectOpenHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Reference2ObjectOpenHashMap.this.containsNullKey) {
                        consumer.accept(Reference2ObjectOpenHashMap.this.value[Reference2ObjectOpenHashMap.this.n]);
                    }
                    int pos = Reference2ObjectOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Reference2ObjectOpenHashMap.this.key[pos] == null) continue;
                        consumer.accept(Reference2ObjectOpenHashMap.this.value[pos]);
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
            int pos = HashCommon.mix(System.identityHashCode(key[i])) & mask;
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

    public Reference2ObjectOpenHashMap<K, V> clone() {
        Reference2ObjectOpenHashMap c;
        try {
            c = (Reference2ObjectOpenHashMap)Object.super.clone();
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
                t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
            }
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
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
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends Reference2ObjectOpenHashMap<K, V>
    implements ObjectIterator<V> {
        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Reference2ObjectOpenHashMap.this.value[this.nextEntry()];
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
            if (Reference2ObjectOpenHashMap.this.containsNullKey) {
                consumer.accept(Reference2ObjectOpenHashMap.this.key[Reference2ObjectOpenHashMap.this.n]);
            }
            int pos = Reference2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                Object k = Reference2ObjectOpenHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Reference2ObjectOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Reference2ObjectOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Reference2ObjectOpenHashMap.this.size;
            Reference2ObjectOpenHashMap.this.remove(k);
            return Reference2ObjectOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Reference2ObjectOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Reference2ObjectOpenHashMap<K, V>
    implements ObjectIterator<K> {
        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Reference2ObjectOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Reference2ObjectMap.Entry<K, V>>
    implements Reference2ObjectMap.FastEntrySet<K, V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Reference2ObjectMap.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Reference2ObjectMap.Entry<K, V>> fastIterator() {
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
            if (k == null) {
                return Reference2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Reference2ObjectOpenHashMap.this.value[Reference2ObjectOpenHashMap.this.n], v);
            }
            K[] key = Reference2ObjectOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2ObjectOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k == curr) {
                return Objects.equals(Reference2ObjectOpenHashMap.this.value[pos], v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2ObjectOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (k != curr);
            return Objects.equals(Reference2ObjectOpenHashMap.this.value[pos], v);
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
                if (Reference2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Reference2ObjectOpenHashMap.this.value[Reference2ObjectOpenHashMap.this.n], v)) {
                    Reference2ObjectOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Reference2ObjectOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2ObjectOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr == k) {
                if (Objects.equals(Reference2ObjectOpenHashMap.this.value[pos], v)) {
                    Reference2ObjectOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2ObjectOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (curr != k || !Objects.equals(Reference2ObjectOpenHashMap.this.value[pos], v));
            Reference2ObjectOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Reference2ObjectOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Reference2ObjectOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Reference2ObjectMap.Entry<K, V>> consumer) {
            if (Reference2ObjectOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractReference2ObjectMap.BasicEntry(Reference2ObjectOpenHashMap.this.key[Reference2ObjectOpenHashMap.this.n], Reference2ObjectOpenHashMap.this.value[Reference2ObjectOpenHashMap.this.n]));
            }
            int pos = Reference2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2ObjectOpenHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractReference2ObjectMap.BasicEntry(Reference2ObjectOpenHashMap.this.key[pos], Reference2ObjectOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Reference2ObjectMap.Entry<K, V>> consumer) {
            AbstractReference2ObjectMap.BasicEntry entry = new AbstractReference2ObjectMap.BasicEntry();
            if (Reference2ObjectOpenHashMap.this.containsNullKey) {
                entry.key = Reference2ObjectOpenHashMap.this.key[Reference2ObjectOpenHashMap.this.n];
                entry.value = Reference2ObjectOpenHashMap.this.value[Reference2ObjectOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Reference2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2ObjectOpenHashMap.this.key[pos] == null) continue;
                entry.key = Reference2ObjectOpenHashMap.this.key[pos];
                entry.value = Reference2ObjectOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Reference2ObjectOpenHashMap<K, V>
    implements ObjectIterator<Reference2ObjectMap.Entry<K, V>> {
        private final Reference2ObjectOpenHashMap<K, V> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Reference2ObjectOpenHashMap<K, V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Reference2ObjectOpenHashMap<K, V>
    implements ObjectIterator<Reference2ObjectMap.Entry<K, V>> {
        private Reference2ObjectOpenHashMap<K, V> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Reference2ObjectOpenHashMap<K, V> next() {
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
            this.pos = Reference2ObjectOpenHashMap.this.n;
            this.last = -1;
            this.c = Reference2ObjectOpenHashMap.this.size;
            this.mustReturnNullKey = Reference2ObjectOpenHashMap.this.containsNullKey;
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
                this.last = Reference2ObjectOpenHashMap.this.n;
                return this.last;
            }
            K[] key = Reference2ObjectOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                K k = this.wrapped.get(- this.pos - 1);
                int p = HashCommon.mix(System.identityHashCode(k)) & Reference2ObjectOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Reference2ObjectOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            K[] key = Reference2ObjectOpenHashMap.this.key;
            do {
                Object curr;
                int last = pos;
                pos = last + 1 & Reference2ObjectOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        Reference2ObjectOpenHashMap.this.value[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2ObjectOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Reference2ObjectOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Reference2ObjectOpenHashMap.this.value[last] = Reference2ObjectOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Reference2ObjectOpenHashMap.this.n) {
                Reference2ObjectOpenHashMap.this.containsNullKey = false;
                Reference2ObjectOpenHashMap.this.key[Reference2ObjectOpenHashMap.this.n] = null;
                Reference2ObjectOpenHashMap.this.value[Reference2ObjectOpenHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Reference2ObjectOpenHashMap.this.remove(this.wrapped.set(- this.pos - 1, null));
                this.last = -1;
                return;
            }
            --Reference2ObjectOpenHashMap.this.size;
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
    implements Reference2ObjectMap.Entry<K, V>,
    Map.Entry<K, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Reference2ObjectOpenHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Reference2ObjectOpenHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Reference2ObjectOpenHashMap.this.value[this.index];
            Reference2ObjectOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Reference2ObjectOpenHashMap.this.key[this.index] == e.getKey() && Objects.equals(Reference2ObjectOpenHashMap.this.value[this.index], e.getValue());
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(Reference2ObjectOpenHashMap.this.key[this.index]) ^ (Reference2ObjectOpenHashMap.this.value[this.index] == null ? 0 : Reference2ObjectOpenHashMap.this.value[this.index].hashCode());
        }

        public String toString() {
            return Reference2ObjectOpenHashMap.this.key[this.index] + "=>" + Reference2ObjectOpenHashMap.this.value[this.index];
        }
    }

}

