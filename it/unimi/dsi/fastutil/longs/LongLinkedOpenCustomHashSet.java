/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongHash;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongLinkedOpenCustomHashSet
extends AbstractLongSortedSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected LongHash.Strategy strategy;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public LongLinkedOpenCustomHashSet(int expected, float f, LongHash.Strategy strategy) {
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
        this.key = new long[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public LongLinkedOpenCustomHashSet(int expected, LongHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public LongLinkedOpenCustomHashSet(LongHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public LongLinkedOpenCustomHashSet(Collection<? extends Long> c, float f, LongHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public LongLinkedOpenCustomHashSet(Collection<? extends Long> c, LongHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public LongLinkedOpenCustomHashSet(LongCollection c, float f, LongHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public LongLinkedOpenCustomHashSet(LongCollection c, LongHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public LongLinkedOpenCustomHashSet(LongIterator i, float f, LongHash.Strategy strategy) {
        this(16, f, strategy);
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public LongLinkedOpenCustomHashSet(LongIterator i, LongHash.Strategy strategy) {
        this(i, 0.75f, strategy);
    }

    public LongLinkedOpenCustomHashSet(Iterator<?> i, float f, LongHash.Strategy strategy) {
        this(LongIterators.asLongIterator(i), f, strategy);
    }

    public LongLinkedOpenCustomHashSet(Iterator<?> i, LongHash.Strategy strategy) {
        this(LongIterators.asLongIterator(i), strategy);
    }

    public LongLinkedOpenCustomHashSet(long[] a, int offset, int length, float f, LongHash.Strategy strategy) {
        this(length < 0 ? 0 : length, f, strategy);
        LongArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public LongLinkedOpenCustomHashSet(long[] a, int offset, int length, LongHash.Strategy strategy) {
        this(a, offset, length, 0.75f, strategy);
    }

    public LongLinkedOpenCustomHashSet(long[] a, float f, LongHash.Strategy strategy) {
        this(a, 0, a.length, f, strategy);
    }

    public LongLinkedOpenCustomHashSet(long[] a, LongHash.Strategy strategy) {
        this(a, 0.75f, strategy);
    }

    public LongHash.Strategy strategy() {
        return this.strategy;
    }

    private int realSize() {
        return this.containsNull ? this.size - 1 : this.size;
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

    @Override
    public boolean addAll(LongCollection c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Long> c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(long k) {
        int pos;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNull) {
                return false;
            }
            pos = this.n;
            this.containsNull = true;
            this.key[this.n] = k;
        } else {
            long[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            long curr = key[pos];
            if (curr != 0L) {
                if (this.strategy.equals(curr, k)) {
                    return false;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (!this.strategy.equals(curr, k)) continue;
                    return false;
                }
            }
            key[pos] = k;
        }
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
        return true;
    }

    protected final void shiftKeys(int pos) {
        long[] key = this.key;
        do {
            long curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == 0L) {
                    key[last] = 0L;
                    return;
                }
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.fixPointers(pos, last);
        } while (true);
    }

    private boolean removeEntry(int pos) {
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    private boolean removeNullEntry() {
        this.containsNull = false;
        this.key[this.n] = 0L;
        --this.size;
        this.fixPointers(this.n);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean remove(long k) {
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(long k) {
        if (this.strategy.equals(k, 0L)) {
            return this.containsNull;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return true;
    }

    public long removeFirstLong() {
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
        long k = this.key[pos];
        --this.size;
        if (this.strategy.equals(k, 0L)) {
            this.containsNull = false;
            this.key[this.n] = 0L;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return k;
    }

    public long removeLastLong() {
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
        long k = this.key[pos];
        --this.size;
        if (this.strategy.equals(k, 0L)) {
            this.containsNull = false;
            this.key[this.n] = 0L;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return k;
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

    public boolean addAndMoveToFirst(long k) {
        int pos;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNull) {
                this.moveIndexToFirst(this.n);
                return false;
            }
            this.containsNull = true;
            pos = this.n;
        } else {
            long[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            while (key[pos] != 0L) {
                if (this.strategy.equals(k, key[pos])) {
                    this.moveIndexToFirst(pos);
                    return false;
                }
                pos = pos + 1 & this.mask;
            }
        }
        this.key[pos] = k;
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
        return true;
    }

    public boolean addAndMoveToLast(long k) {
        int pos;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNull) {
                this.moveIndexToLast(this.n);
                return false;
            }
            this.containsNull = true;
            pos = this.n;
        } else {
            long[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            while (key[pos] != 0L) {
                if (this.strategy.equals(k, key[pos])) {
                    this.moveIndexToLast(pos);
                    return false;
                }
                pos = pos + 1 & this.mask;
            }
        }
        this.key[pos] = k;
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
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, 0L);
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
    public long firstLong() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public long lastLong() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public LongSortedSet tailSet(long from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongSortedSet headSet(long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongSortedSet subSet(long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongComparator comparator() {
        return null;
    }

    @Override
    public LongListIterator iterator(long from) {
        return new SetIterator(from);
    }

    @Override
    public LongListIterator iterator() {
        return new SetIterator();
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
        long[] key = this.key;
        int mask = newN - 1;
        long[] newKey = new long[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (this.strategy.equals(key[i], 0L)) {
                pos = newN;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
                while (newKey[pos] != 0L) {
                    pos = pos + 1 & mask;
                }
            }
            newKey[pos] = key[i];
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
    }

    public LongLinkedOpenCustomHashSet clone() {
        LongLinkedOpenCustomHashSet c;
        try {
            c = (LongLinkedOpenCustomHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.containsNull = this.containsNull;
        c.link = (long[])this.link.clone();
        c.strategy = this.strategy;
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        while (j-- != 0) {
            while (this.key[i] == 0L) {
                ++i;
            }
            h += this.strategy.hashCode(this.key[i]);
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        LongListIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeLong(i.nextLong());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new long[this.n + 1];
        long[] key = this.key;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            long k = s.readLong();
            if (this.strategy.equals(k, 0L)) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
                if (key[pos] != 0L) {
                    while (key[pos = pos + 1 & this.mask] != 0L) {
                    }
                }
            }
            key[pos] = k;
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

    private class SetIterator
    implements LongListIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        SetIterator() {
            this.next = LongLinkedOpenCustomHashSet.this.first;
            this.index = 0;
        }

        SetIterator(long from) {
            if (LongLinkedOpenCustomHashSet.this.strategy.equals(from, 0L)) {
                if (LongLinkedOpenCustomHashSet.this.containsNull) {
                    this.next = (int)LongLinkedOpenCustomHashSet.this.link[LongLinkedOpenCustomHashSet.this.n];
                    this.prev = LongLinkedOpenCustomHashSet.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this set.");
            }
            if (LongLinkedOpenCustomHashSet.this.strategy.equals(LongLinkedOpenCustomHashSet.this.key[LongLinkedOpenCustomHashSet.this.last], from)) {
                this.prev = LongLinkedOpenCustomHashSet.this.last;
                this.index = LongLinkedOpenCustomHashSet.this.size;
                return;
            }
            long[] key = LongLinkedOpenCustomHashSet.this.key;
            int pos = HashCommon.mix(LongLinkedOpenCustomHashSet.this.strategy.hashCode(from)) & LongLinkedOpenCustomHashSet.this.mask;
            while (key[pos] != 0L) {
                if (LongLinkedOpenCustomHashSet.this.strategy.equals(key[pos], from)) {
                    this.next = (int)LongLinkedOpenCustomHashSet.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & LongLinkedOpenCustomHashSet.this.mask;
            }
            throw new NoSuchElementException("The key " + from + " does not belong to this set.");
        }

        @Override
        public boolean hasNext() {
            return this.next != -1;
        }

        @Override
        public boolean hasPrevious() {
            return this.prev != -1;
        }

        @Override
        public long nextLong() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)LongLinkedOpenCustomHashSet.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return LongLinkedOpenCustomHashSet.this.key[this.curr];
        }

        @Override
        public long previousLong() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev;
            this.prev = (int)(LongLinkedOpenCustomHashSet.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return LongLinkedOpenCustomHashSet.this.key[this.curr];
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
                this.index = LongLinkedOpenCustomHashSet.this.size;
                return;
            }
            int pos = LongLinkedOpenCustomHashSet.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)LongLinkedOpenCustomHashSet.this.link[pos];
                ++this.index;
            }
        }

        @Override
        public int nextIndex() {
            this.ensureIndexKnown();
            return this.index;
        }

        @Override
        public int previousIndex() {
            this.ensureIndexKnown();
            return this.index - 1;
        }

        @Override
        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(LongLinkedOpenCustomHashSet.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)LongLinkedOpenCustomHashSet.this.link[this.curr];
            }
            --LongLinkedOpenCustomHashSet.this.size;
            if (this.prev == -1) {
                LongLinkedOpenCustomHashSet.this.first = this.next;
            } else {
                long[] arrl = LongLinkedOpenCustomHashSet.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (LongLinkedOpenCustomHashSet.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                LongLinkedOpenCustomHashSet.this.last = this.prev;
            } else {
                long[] arrl = LongLinkedOpenCustomHashSet.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (LongLinkedOpenCustomHashSet.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != LongLinkedOpenCustomHashSet.this.n) {
                long[] key = LongLinkedOpenCustomHashSet.this.key;
                do {
                    long curr;
                    int last = pos;
                    pos = last + 1 & LongLinkedOpenCustomHashSet.this.mask;
                    do {
                        if ((curr = key[pos]) == 0L) {
                            key[last] = 0L;
                            return;
                        }
                        int slot = HashCommon.mix(LongLinkedOpenCustomHashSet.this.strategy.hashCode(curr)) & LongLinkedOpenCustomHashSet.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & LongLinkedOpenCustomHashSet.this.mask;
                    } while (true);
                    key[last] = curr;
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    LongLinkedOpenCustomHashSet.this.fixPointers(pos, last);
                } while (true);
            }
            LongLinkedOpenCustomHashSet.this.containsNull = false;
            LongLinkedOpenCustomHashSet.this.key[LongLinkedOpenCustomHashSet.this.n] = 0L;
        }
    }

}

