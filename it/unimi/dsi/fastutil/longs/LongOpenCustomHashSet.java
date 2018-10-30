/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongHash;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongOpenCustomHashSet
extends AbstractLongSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected LongHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public LongOpenCustomHashSet(int expected, float f, LongHash.Strategy strategy) {
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
    }

    public LongOpenCustomHashSet(int expected, LongHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public LongOpenCustomHashSet(LongHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public LongOpenCustomHashSet(Collection<? extends Long> c, float f, LongHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public LongOpenCustomHashSet(Collection<? extends Long> c, LongHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public LongOpenCustomHashSet(LongCollection c, float f, LongHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public LongOpenCustomHashSet(LongCollection c, LongHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public LongOpenCustomHashSet(LongIterator i, float f, LongHash.Strategy strategy) {
        this(16, f, strategy);
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public LongOpenCustomHashSet(LongIterator i, LongHash.Strategy strategy) {
        this(i, 0.75f, strategy);
    }

    public LongOpenCustomHashSet(Iterator<?> i, float f, LongHash.Strategy strategy) {
        this(LongIterators.asLongIterator(i), f, strategy);
    }

    public LongOpenCustomHashSet(Iterator<?> i, LongHash.Strategy strategy) {
        this(LongIterators.asLongIterator(i), strategy);
    }

    public LongOpenCustomHashSet(long[] a, int offset, int length, float f, LongHash.Strategy strategy) {
        this(length < 0 ? 0 : length, f, strategy);
        LongArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public LongOpenCustomHashSet(long[] a, int offset, int length, LongHash.Strategy strategy) {
        this(a, offset, length, 0.75f, strategy);
    }

    public LongOpenCustomHashSet(long[] a, float f, LongHash.Strategy strategy) {
        this(a, 0, a.length, f, strategy);
    }

    public LongOpenCustomHashSet(long[] a, LongHash.Strategy strategy) {
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
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
            this.key[this.n] = k;
        } else {
            long[] key = this.key;
            int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
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
        } while (true);
    }

    private boolean removeEntry(int pos) {
        --this.size;
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

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, 0L);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public LongIterator iterator() {
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
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0L) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
            if (newKey[pos] != 0L) {
                while (newKey[pos = pos + 1 & mask] != 0L) {
                }
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public LongOpenCustomHashSet clone() {
        LongOpenCustomHashSet c;
        try {
            c = (LongOpenCustomHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.containsNull = this.containsNull;
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
        LongIterator i = this.iterator();
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
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements LongIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        LongArrayList wrapped;

        private SetIterator() {
            this.pos = LongOpenCustomHashSet.this.n;
            this.last = -1;
            this.c = LongOpenCustomHashSet.this.size;
            this.mustReturnNull = LongOpenCustomHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public long nextLong() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = LongOpenCustomHashSet.this.n;
                return LongOpenCustomHashSet.this.key[LongOpenCustomHashSet.this.n];
            }
            long[] key = LongOpenCustomHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getLong(- this.pos - 1);
            } while (key[this.pos] == 0L);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            long[] key = LongOpenCustomHashSet.this.key;
            do {
                long curr;
                int last = pos;
                pos = last + 1 & LongOpenCustomHashSet.this.mask;
                do {
                    if ((curr = key[pos]) == 0L) {
                        key[last] = 0L;
                        return;
                    }
                    int slot = HashCommon.mix(LongOpenCustomHashSet.this.strategy.hashCode(curr)) & LongOpenCustomHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & LongOpenCustomHashSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
            } while (true);
        }

        @Override
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == LongOpenCustomHashSet.this.n) {
                LongOpenCustomHashSet.this.containsNull = false;
                LongOpenCustomHashSet.this.key[LongOpenCustomHashSet.this.n] = 0L;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                LongOpenCustomHashSet.this.remove(this.wrapped.getLong(- this.pos - 1));
                this.last = -1;
                return;
            }
            --LongOpenCustomHashSet.this.size;
            this.last = -1;
        }
    }

}

