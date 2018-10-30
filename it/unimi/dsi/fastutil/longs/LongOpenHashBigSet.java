/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongOpenHashBigSet
extends AbstractLongSet
implements Serializable,
Cloneable,
Hash,
Size64 {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[][] key;
    protected transient long mask;
    protected transient int segmentMask;
    protected transient int baseMask;
    protected transient boolean containsNull;
    protected transient long n;
    protected transient long maxFill;
    protected final transient long minN;
    protected final float f;
    protected long size;

    private void initMasks() {
        this.mask = this.n - 1L;
        this.segmentMask = this.key[0].length - 1;
        this.baseMask = this.key.length - 1;
    }

    public LongOpenHashBigSet(long expected, float f) {
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (this.n < 0L) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.bigArraySize(expected, f);
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = LongBigArrays.newBigArray(this.n);
        this.initMasks();
    }

    public LongOpenHashBigSet(long expected) {
        this(expected, 0.75f);
    }

    public LongOpenHashBigSet() {
        this(16L, 0.75f);
    }

    public LongOpenHashBigSet(Collection<? extends Long> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public LongOpenHashBigSet(Collection<? extends Long> c) {
        this(c, 0.75f);
    }

    public LongOpenHashBigSet(LongCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public LongOpenHashBigSet(LongCollection c) {
        this(c, 0.75f);
    }

    public LongOpenHashBigSet(LongIterator i, float f) {
        this(16L, f);
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public LongOpenHashBigSet(LongIterator i) {
        this(i, 0.75f);
    }

    public LongOpenHashBigSet(Iterator<?> i, float f) {
        this(LongIterators.asLongIterator(i), f);
    }

    public LongOpenHashBigSet(Iterator<?> i) {
        this(LongIterators.asLongIterator(i));
    }

    public LongOpenHashBigSet(long[] a, int offset, int length, float f) {
        this(length < 0 ? 0L : (long)length, f);
        LongArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public LongOpenHashBigSet(long[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public LongOpenHashBigSet(long[] a, float f) {
        this(a, 0, a.length, f);
    }

    public LongOpenHashBigSet(long[] a) {
        this(a, 0.75f);
    }

    private long realSize() {
        return this.containsNull ? this.size - 1L : this.size;
    }

    private void ensureCapacity(long capacity) {
        long needed = HashCommon.bigArraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Long> c) {
        long size;
        long l = size = c instanceof Size64 ? ((Size64)((Object)c)).size64() : (long)c.size();
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(size);
        } else {
            this.ensureCapacity(this.size64() + size);
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(LongCollection c) {
        long size;
        long l = size = c instanceof Size64 ? ((Size64)((Object)c)).size64() : (long)c.size();
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(size);
        } else {
            this.ensureCapacity(this.size64() + size);
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(long k) {
        if (k == 0L) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            int displ;
            long[][] key = this.key;
            long h = HashCommon.mix(k);
            int base = (int)((h & this.mask) >>> 27);
            long curr = key[base][displ = (int)(h & (long)this.segmentMask)];
            if (curr != 0L) {
                if (curr == k) {
                    return false;
                }
                while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
                    if (curr != k) continue;
                    return false;
                }
            }
            key[base][displ] = k;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(2L * this.n);
        }
        return true;
    }

    protected final void shiftKeys(long pos) {
        long[][] key = this.key;
        do {
            long last = pos;
            pos = last + 1L & this.mask;
            do {
                if (LongBigArrays.get(key, pos) == 0L) {
                    LongBigArrays.set(key, last, 0L);
                    return;
                }
                long slot = HashCommon.mix(LongBigArrays.get(key, pos)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1L & this.mask;
            } while (true);
            LongBigArrays.set(key, last, LongBigArrays.get(key, pos));
        } while (true);
    }

    private boolean removeEntry(int base, int displ) {
        --this.size;
        this.shiftKeys((long)base * 0x8000000L + (long)displ);
        if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
            this.rehash(this.n / 2L);
        }
        return true;
    }

    private boolean removeNullEntry() {
        this.containsNull = false;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
            this.rehash(this.n / 2L);
        }
        return true;
    }

    @Override
    public boolean remove(long k) {
        int displ;
        if (k == 0L) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        long[][] key = this.key;
        long h = HashCommon.mix(k);
        int base = (int)((h & this.mask) >>> 27);
        long curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (curr == 0L) {
            return false;
        }
        if (curr == k) {
            return this.removeEntry(base, displ);
        }
        do {
            if ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) continue;
            return false;
        } while (curr != k);
        return this.removeEntry(base, displ);
    }

    @Override
    public boolean contains(long k) {
        int displ;
        if (k == 0L) {
            return this.containsNull;
        }
        long[][] key = this.key;
        long h = HashCommon.mix(k);
        int base = (int)((h & this.mask) >>> 27);
        long curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (curr == 0L) {
            return false;
        }
        if (curr == k) {
            return true;
        }
        do {
            if ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) continue;
            return false;
        } while (curr != k);
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0L) {
            return;
        }
        this.size = 0L;
        this.containsNull = false;
        LongBigArrays.fill(this.key, 0L);
    }

    @Override
    public LongIterator iterator() {
        return new SetIterator();
    }

    public boolean trim() {
        long l = HashCommon.bigArraySize(this.size, this.f);
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

    public boolean trim(long n) {
        long l = HashCommon.bigArraySize(n, this.f);
        if (this.n <= l) {
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

    protected void rehash(long newN) {
        long[][] key = this.key;
        long[][] newKey = LongBigArrays.newBigArray(newN);
        long mask = newN - 1L;
        int newSegmentMask = newKey[0].length - 1;
        int newBaseMask = newKey.length - 1;
        int base = 0;
        int displ = 0;
        long i = this.realSize();
        while (i-- != 0L) {
            int d;
            while (key[base][displ] == 0L) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            long k = key[base][displ];
            long h = HashCommon.mix(k);
            int b = (int)((h & mask) >>> 27);
            if (newKey[b][d = (int)(h & (long)newSegmentMask)] != 0L) {
                while (newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d] != 0L) {
                }
            }
            newKey[b][d] = k;
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
        }
        this.n = newN;
        this.key = newKey;
        this.initMasks();
        this.maxFill = HashCommon.maxFill(this.n, this.f);
    }

    @Deprecated
    @Override
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size);
    }

    @Override
    public long size64() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    public LongOpenHashBigSet clone() {
        LongOpenHashBigSet c;
        try {
            c = (LongOpenHashBigSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = LongBigArrays.copy(this.key);
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        long[][] key = this.key;
        int h = 0;
        int base = 0;
        int displ = 0;
        long j = this.realSize();
        while (j-- != 0L) {
            while (key[base][displ] == 0L) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            h += HashCommon.long2int(key[base][displ]);
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        LongIterator i = this.iterator();
        s.defaultWriteObject();
        long j = this.size;
        while (j-- != 0L) {
            s.writeLong(i.nextLong());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.bigArraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = LongBigArrays.newBigArray(this.n);
        long[][] key = this.key;
        this.initMasks();
        long i = this.size;
        while (i-- != 0L) {
            int displ;
            long k = s.readLong();
            if (k == 0L) {
                this.containsNull = true;
                continue;
            }
            long h = HashCommon.mix(k);
            int base = (int)((h & this.mask) >>> 27);
            if (key[base][displ = (int)(h & (long)this.segmentMask)] != 0L) {
                while (key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ] != 0L) {
                }
            }
            key[base][displ] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements LongIterator {
        int base;
        int displ;
        long last;
        long c;
        boolean mustReturnNull;
        LongArrayList wrapped;

        private SetIterator() {
            this.base = LongOpenHashBigSet.this.key.length;
            this.last = -1L;
            this.c = LongOpenHashBigSet.this.size;
            this.mustReturnNull = LongOpenHashBigSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0L;
        }

        @Override
        public long nextLong() {
            long k;
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = LongOpenHashBigSet.this.n;
                return 0L;
            }
            long[][] key = LongOpenHashBigSet.this.key;
            do {
                if (this.displ == 0 && this.base <= 0) {
                    this.last = Long.MIN_VALUE;
                    return this.wrapped.getLong(- --this.base - 1);
                }
                if (this.displ-- != 0) continue;
                this.displ = key[--this.base].length - 1;
            } while ((k = key[this.base][this.displ]) == 0L);
            this.last = (long)this.base * 0x8000000L + (long)this.displ;
            return k;
        }

        private final void shiftKeys(long pos) {
            long[][] key = LongOpenHashBigSet.this.key;
            do {
                long curr;
                long last = pos;
                pos = last + 1L & LongOpenHashBigSet.this.mask;
                do {
                    if ((curr = LongBigArrays.get(key, pos)) == 0L) {
                        LongBigArrays.set(key, last, 0L);
                        return;
                    }
                    long slot = HashCommon.mix(curr) & LongOpenHashBigSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1L & LongOpenHashBigSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new LongArrayList();
                    }
                    this.wrapped.add(LongBigArrays.get(key, pos));
                }
                LongBigArrays.set(key, last, curr);
            } while (true);
        }

        @Override
        public void remove() {
            if (this.last == -1L) {
                throw new IllegalStateException();
            }
            if (this.last == LongOpenHashBigSet.this.n) {
                LongOpenHashBigSet.this.containsNull = false;
            } else if (this.base >= 0) {
                this.shiftKeys(this.last);
            } else {
                LongOpenHashBigSet.this.remove(this.wrapped.getLong(- this.base - 1));
                this.last = -1L;
                return;
            }
            --LongOpenHashBigSet.this.size;
            this.last = -1L;
        }
    }

}

