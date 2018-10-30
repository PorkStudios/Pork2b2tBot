/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleHash;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleOpenCustomHashSet
extends AbstractDoubleSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient double[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected DoubleHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public DoubleOpenCustomHashSet(int expected, float f, DoubleHash.Strategy strategy) {
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
        this.key = new double[this.n + 1];
    }

    public DoubleOpenCustomHashSet(int expected, DoubleHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public DoubleOpenCustomHashSet(DoubleHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public DoubleOpenCustomHashSet(Collection<? extends Double> c, float f, DoubleHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public DoubleOpenCustomHashSet(Collection<? extends Double> c, DoubleHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public DoubleOpenCustomHashSet(DoubleCollection c, float f, DoubleHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public DoubleOpenCustomHashSet(DoubleCollection c, DoubleHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public DoubleOpenCustomHashSet(DoubleIterator i, float f, DoubleHash.Strategy strategy) {
        this(16, f, strategy);
        while (i.hasNext()) {
            this.add(i.nextDouble());
        }
    }

    public DoubleOpenCustomHashSet(DoubleIterator i, DoubleHash.Strategy strategy) {
        this(i, 0.75f, strategy);
    }

    public DoubleOpenCustomHashSet(Iterator<?> i, float f, DoubleHash.Strategy strategy) {
        this(DoubleIterators.asDoubleIterator(i), f, strategy);
    }

    public DoubleOpenCustomHashSet(Iterator<?> i, DoubleHash.Strategy strategy) {
        this(DoubleIterators.asDoubleIterator(i), strategy);
    }

    public DoubleOpenCustomHashSet(double[] a, int offset, int length, float f, DoubleHash.Strategy strategy) {
        this(length < 0 ? 0 : length, f, strategy);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public DoubleOpenCustomHashSet(double[] a, int offset, int length, DoubleHash.Strategy strategy) {
        this(a, offset, length, 0.75f, strategy);
    }

    public DoubleOpenCustomHashSet(double[] a, float f, DoubleHash.Strategy strategy) {
        this(a, 0, a.length, f, strategy);
    }

    public DoubleOpenCustomHashSet(double[] a, DoubleHash.Strategy strategy) {
        this(a, 0.75f, strategy);
    }

    public DoubleHash.Strategy strategy() {
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
    public boolean addAll(DoubleCollection c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Double> c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(double k) {
        if (this.strategy.equals(k, 0.0)) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
            this.key[this.n] = k;
        } else {
            double[] key = this.key;
            int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) != 0L) {
                if (this.strategy.equals(curr, k)) {
                    return false;
                }
                while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
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
        double[] key = this.key;
        do {
            double curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                    key[last] = 0.0;
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
        this.key[this.n] = 0.0;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean remove(double k) {
        if (this.strategy.equals(k, 0.0)) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(double k) {
        if (this.strategy.equals(k, 0.0)) {
            return this.containsNull;
        }
        double[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
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
        Arrays.fill(this.key, 0.0);
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
    public DoubleIterator iterator() {
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
        double[] key = this.key;
        int mask = newN - 1;
        double[] newKey = new double[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (Double.doubleToLongBits(key[--i]) == 0L) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
            if (Double.doubleToLongBits(newKey[pos]) != 0L) {
                while (Double.doubleToLongBits(newKey[pos = pos + 1 & mask]) != 0L) {
                }
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public DoubleOpenCustomHashSet clone() {
        DoubleOpenCustomHashSet c;
        try {
            c = (DoubleOpenCustomHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (double[])this.key.clone();
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
            while (Double.doubleToLongBits(this.key[i]) == 0L) {
                ++i;
            }
            h += this.strategy.hashCode(this.key[i]);
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        DoubleIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeDouble(i.nextDouble());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new double[this.n + 1];
        double[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            double k = s.readDouble();
            if (this.strategy.equals(k, 0.0)) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
                if (Double.doubleToLongBits(key[pos]) != 0L) {
                    while (Double.doubleToLongBits(key[pos = pos + 1 & this.mask]) != 0L) {
                    }
                }
            }
            key[pos] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements DoubleIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        DoubleArrayList wrapped;

        private SetIterator() {
            this.pos = DoubleOpenCustomHashSet.this.n;
            this.last = -1;
            this.c = DoubleOpenCustomHashSet.this.size;
            this.mustReturnNull = DoubleOpenCustomHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = DoubleOpenCustomHashSet.this.n;
                return DoubleOpenCustomHashSet.this.key[DoubleOpenCustomHashSet.this.n];
            }
            double[] key = DoubleOpenCustomHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getDouble(- this.pos - 1);
            } while (Double.doubleToLongBits(key[this.pos]) == 0L);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            double[] key = DoubleOpenCustomHashSet.this.key;
            do {
                double curr;
                int last = pos;
                pos = last + 1 & DoubleOpenCustomHashSet.this.mask;
                do {
                    if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                        key[last] = 0.0;
                        return;
                    }
                    int slot = HashCommon.mix(DoubleOpenCustomHashSet.this.strategy.hashCode(curr)) & DoubleOpenCustomHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & DoubleOpenCustomHashSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList(2);
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
            if (this.last == DoubleOpenCustomHashSet.this.n) {
                DoubleOpenCustomHashSet.this.containsNull = false;
                DoubleOpenCustomHashSet.this.key[DoubleOpenCustomHashSet.this.n] = 0.0;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                DoubleOpenCustomHashSet.this.remove(this.wrapped.getDouble(- this.pos - 1));
                this.last = -1;
                return;
            }
            --DoubleOpenCustomHashSet.this.size;
            this.last = -1;
        }
    }

}

