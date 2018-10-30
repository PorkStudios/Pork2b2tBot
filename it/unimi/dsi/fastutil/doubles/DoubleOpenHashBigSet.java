/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleOpenHashBigSet
extends AbstractDoubleSet
implements Serializable,
Cloneable,
Hash,
Size64 {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient double[][] key;
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

    public DoubleOpenHashBigSet(long expected, float f) {
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (this.n < 0L) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.bigArraySize(expected, f);
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = DoubleBigArrays.newBigArray(this.n);
        this.initMasks();
    }

    public DoubleOpenHashBigSet(long expected) {
        this(expected, 0.75f);
    }

    public DoubleOpenHashBigSet() {
        this(16L, 0.75f);
    }

    public DoubleOpenHashBigSet(Collection<? extends Double> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public DoubleOpenHashBigSet(Collection<? extends Double> c) {
        this(c, 0.75f);
    }

    public DoubleOpenHashBigSet(DoubleCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public DoubleOpenHashBigSet(DoubleCollection c) {
        this(c, 0.75f);
    }

    public DoubleOpenHashBigSet(DoubleIterator i, float f) {
        this(16L, f);
        while (i.hasNext()) {
            this.add(i.nextDouble());
        }
    }

    public DoubleOpenHashBigSet(DoubleIterator i) {
        this(i, 0.75f);
    }

    public DoubleOpenHashBigSet(Iterator<?> i, float f) {
        this(DoubleIterators.asDoubleIterator(i), f);
    }

    public DoubleOpenHashBigSet(Iterator<?> i) {
        this(DoubleIterators.asDoubleIterator(i));
    }

    public DoubleOpenHashBigSet(double[] a, int offset, int length, float f) {
        this(length < 0 ? 0L : (long)length, f);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public DoubleOpenHashBigSet(double[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public DoubleOpenHashBigSet(double[] a, float f) {
        this(a, 0, a.length, f);
    }

    public DoubleOpenHashBigSet(double[] a) {
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
    public boolean addAll(Collection<? extends Double> c) {
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
    public boolean addAll(DoubleCollection c) {
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
    public boolean add(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            int displ;
            double[][] key = this.key;
            long h = HashCommon.mix(Double.doubleToRawLongBits(k));
            int base = (int)((h & this.mask) >>> 27);
            double curr = key[base][displ = (int)(h & (long)this.segmentMask)];
            if (Double.doubleToLongBits(curr) != 0L) {
                if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                    return false;
                }
                while (Double.doubleToLongBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
                    if (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k)) continue;
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
        double[][] key = this.key;
        do {
            long last = pos;
            pos = last + 1L & this.mask;
            do {
                if (Double.doubleToLongBits(DoubleBigArrays.get(key, pos)) == 0L) {
                    DoubleBigArrays.set(key, last, 0.0);
                    return;
                }
                long slot = HashCommon.mix(Double.doubleToRawLongBits(DoubleBigArrays.get(key, pos))) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1L & this.mask;
            } while (true);
            DoubleBigArrays.set(key, last, DoubleBigArrays.get(key, pos));
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
    public boolean remove(double k) {
        int displ;
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        double[][] key = this.key;
        long h = HashCommon.mix(Double.doubleToRawLongBits(k));
        int base = (int)((h & this.mask) >>> 27);
        double curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
            return this.removeEntry(base, displ);
        }
        do {
            if (Double.doubleToLongBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) continue;
            return false;
        } while (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k));
        return this.removeEntry(base, displ);
    }

    @Override
    public boolean contains(double k) {
        int displ;
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNull;
        }
        double[][] key = this.key;
        long h = HashCommon.mix(Double.doubleToRawLongBits(k));
        int base = (int)((h & this.mask) >>> 27);
        double curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) continue;
            return false;
        } while (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k));
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0L) {
            return;
        }
        this.size = 0L;
        this.containsNull = false;
        DoubleBigArrays.fill(this.key, 0.0);
    }

    @Override
    public DoubleIterator iterator() {
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
        double[][] key = this.key;
        double[][] newKey = DoubleBigArrays.newBigArray(newN);
        long mask = newN - 1L;
        int newSegmentMask = newKey[0].length - 1;
        int newBaseMask = newKey.length - 1;
        int base = 0;
        int displ = 0;
        long i = this.realSize();
        while (i-- != 0L) {
            int d;
            while (Double.doubleToLongBits(key[base][displ]) == 0L) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            double k = key[base][displ];
            long h = HashCommon.mix(Double.doubleToRawLongBits(k));
            int b = (int)((h & mask) >>> 27);
            if (Double.doubleToLongBits(newKey[b][d = (int)(h & (long)newSegmentMask)]) != 0L) {
                while (Double.doubleToLongBits(newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d]) != 0L) {
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

    public DoubleOpenHashBigSet clone() {
        DoubleOpenHashBigSet c;
        try {
            c = (DoubleOpenHashBigSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = DoubleBigArrays.copy(this.key);
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        double[][] key = this.key;
        int h = 0;
        int base = 0;
        int displ = 0;
        long j = this.realSize();
        while (j-- != 0L) {
            while (Double.doubleToLongBits(key[base][displ]) == 0L) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            h += HashCommon.double2int(key[base][displ]);
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        DoubleIterator i = this.iterator();
        s.defaultWriteObject();
        long j = this.size;
        while (j-- != 0L) {
            s.writeDouble(i.nextDouble());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.bigArraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = DoubleBigArrays.newBigArray(this.n);
        double[][] key = this.key;
        this.initMasks();
        long i = this.size;
        while (i-- != 0L) {
            int displ;
            double k = s.readDouble();
            if (Double.doubleToLongBits(k) == 0L) {
                this.containsNull = true;
                continue;
            }
            long h = HashCommon.mix(Double.doubleToRawLongBits(k));
            int base = (int)((h & this.mask) >>> 27);
            if (Double.doubleToLongBits(key[base][displ = (int)(h & (long)this.segmentMask)]) != 0L) {
                while (Double.doubleToLongBits(key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
                }
            }
            key[base][displ] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements DoubleIterator {
        int base;
        int displ;
        long last;
        long c;
        boolean mustReturnNull;
        DoubleArrayList wrapped;

        private SetIterator() {
            this.base = DoubleOpenHashBigSet.this.key.length;
            this.last = -1L;
            this.c = DoubleOpenHashBigSet.this.size;
            this.mustReturnNull = DoubleOpenHashBigSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0L;
        }

        @Override
        public double nextDouble() {
            double k;
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = DoubleOpenHashBigSet.this.n;
                return 0.0;
            }
            double[][] key = DoubleOpenHashBigSet.this.key;
            do {
                if (this.displ == 0 && this.base <= 0) {
                    this.last = Long.MIN_VALUE;
                    return this.wrapped.getDouble(- --this.base - 1);
                }
                if (this.displ-- != 0) continue;
                this.displ = key[--this.base].length - 1;
            } while (Double.doubleToLongBits(k = key[this.base][this.displ]) == 0L);
            this.last = (long)this.base * 0x8000000L + (long)this.displ;
            return k;
        }

        private final void shiftKeys(long pos) {
            double[][] key = DoubleOpenHashBigSet.this.key;
            do {
                double curr;
                long last = pos;
                pos = last + 1L & DoubleOpenHashBigSet.this.mask;
                do {
                    if (Double.doubleToLongBits(curr = DoubleBigArrays.get(key, pos)) == 0L) {
                        DoubleBigArrays.set(key, last, 0.0);
                        return;
                    }
                    long slot = HashCommon.mix(Double.doubleToRawLongBits(curr)) & DoubleOpenHashBigSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1L & DoubleOpenHashBigSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList();
                    }
                    this.wrapped.add(DoubleBigArrays.get(key, pos));
                }
                DoubleBigArrays.set(key, last, curr);
            } while (true);
        }

        @Override
        public void remove() {
            if (this.last == -1L) {
                throw new IllegalStateException();
            }
            if (this.last == DoubleOpenHashBigSet.this.n) {
                DoubleOpenHashBigSet.this.containsNull = false;
            } else if (this.base >= 0) {
                this.shiftKeys(this.last);
            } else {
                DoubleOpenHashBigSet.this.remove(this.wrapped.getDouble(- this.base - 1));
                this.last = -1L;
                return;
            }
            --DoubleOpenHashBigSet.this.size;
            this.last = -1L;
        }
    }

}

