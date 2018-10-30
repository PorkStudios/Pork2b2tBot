/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanSet;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BooleanOpenHashSet
extends AbstractBooleanSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient boolean[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public BooleanOpenHashSet(int expected, float f) {
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
        this.key = new boolean[this.n + 1];
    }

    public BooleanOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public BooleanOpenHashSet() {
        this(16, 0.75f);
    }

    public BooleanOpenHashSet(Collection<? extends Boolean> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public BooleanOpenHashSet(Collection<? extends Boolean> c) {
        this(c, 0.75f);
    }

    public BooleanOpenHashSet(BooleanCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public BooleanOpenHashSet(BooleanCollection c) {
        this(c, 0.75f);
    }

    public BooleanOpenHashSet(BooleanIterator i, float f) {
        this(16, f);
        while (i.hasNext()) {
            this.add(i.nextBoolean());
        }
    }

    public BooleanOpenHashSet(BooleanIterator i) {
        this(i, 0.75f);
    }

    public BooleanOpenHashSet(Iterator<?> i, float f) {
        this(BooleanIterators.asBooleanIterator(i), f);
    }

    public BooleanOpenHashSet(Iterator<?> i) {
        this(BooleanIterators.asBooleanIterator(i));
    }

    public BooleanOpenHashSet(boolean[] a, int offset, int length, float f) {
        this(length < 0 ? 0 : length, f);
        BooleanArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public BooleanOpenHashSet(boolean[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public BooleanOpenHashSet(boolean[] a, float f) {
        this(a, 0, a.length, f);
    }

    public BooleanOpenHashSet(boolean[] a) {
        this(a, 0.75f);
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
    public boolean addAll(BooleanCollection c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(boolean k) {
        if (!k) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            boolean[] key = this.key;
            int pos = (k ? 262886248 : -878682501) & this.mask;
            boolean curr = key[pos];
            if (curr) {
                if (curr == k) {
                    return false;
                }
                while (curr = key[pos = pos + 1 & this.mask]) {
                    if (curr != k) continue;
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
        boolean[] key = this.key;
        do {
            boolean curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if (!(curr = key[pos])) {
                    key[last] = false;
                    return;
                }
                int slot = (curr ? 262886248 : -878682501) & this.mask;
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
        this.key[this.n] = false;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean remove(boolean k) {
        if (!k) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        boolean[] key = this.key;
        int pos = (k ? 262886248 : -878682501) & this.mask;
        boolean curr = key[pos];
        if (!curr) {
            return false;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if (curr = key[pos = pos + 1 & this.mask]) continue;
            return false;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(boolean k) {
        if (!k) {
            return this.containsNull;
        }
        boolean[] key = this.key;
        int pos = (k ? 262886248 : -878682501) & this.mask;
        boolean curr = key[pos];
        if (!curr) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if (curr = key[pos = pos + 1 & this.mask]) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, false);
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
    public BooleanIterator iterator() {
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
        boolean[] key = this.key;
        int mask = newN - 1;
        boolean[] newKey = new boolean[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (!key[--i]) {
            }
            int pos = (key[i] ? 262886248 : -878682501) & mask;
            if (newKey[pos]) {
                while (newKey[pos = pos + 1 & mask]) {
                }
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public BooleanOpenHashSet clone() {
        BooleanOpenHashSet c;
        try {
            c = (BooleanOpenHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (boolean[])this.key.clone();
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        while (j-- != 0) {
            while (!this.key[i]) {
                ++i;
            }
            h += this.key[i] ? 1231 : 1237;
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        BooleanIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeBoolean(i.nextBoolean());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new boolean[this.n + 1];
        boolean[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            boolean k = s.readBoolean();
            if (!k) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = (k ? 262886248 : -878682501) & this.mask;
                if (key[pos]) {
                    while (key[pos = pos + 1 & this.mask]) {
                    }
                }
            }
            key[pos] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements BooleanIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        BooleanArrayList wrapped;

        private SetIterator() {
            this.pos = BooleanOpenHashSet.this.n;
            this.last = -1;
            this.c = BooleanOpenHashSet.this.size;
            this.mustReturnNull = BooleanOpenHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = BooleanOpenHashSet.this.n;
                return BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n];
            }
            boolean[] key = BooleanOpenHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getBoolean(- this.pos - 1);
            } while (!key[this.pos]);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            boolean[] key = BooleanOpenHashSet.this.key;
            do {
                boolean curr;
                int last = pos;
                pos = last + 1 & BooleanOpenHashSet.this.mask;
                do {
                    if (!(curr = key[pos])) {
                        key[last] = false;
                        return;
                    }
                    int slot = (curr ? 262886248 : -878682501) & BooleanOpenHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & BooleanOpenHashSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new BooleanArrayList(2);
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
            if (this.last == BooleanOpenHashSet.this.n) {
                BooleanOpenHashSet.this.containsNull = false;
                BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n] = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                BooleanOpenHashSet.this.remove(this.wrapped.getBoolean(- this.pos - 1));
                this.last = -1;
                return;
            }
            --BooleanOpenHashSet.this.size;
            this.last = -1;
        }
    }

}

