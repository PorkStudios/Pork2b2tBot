/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatOpenHashSet
extends AbstractFloatSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient float[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public FloatOpenHashSet(int expected, float f) {
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
        this.key = new float[this.n + 1];
    }

    public FloatOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public FloatOpenHashSet() {
        this(16, 0.75f);
    }

    public FloatOpenHashSet(Collection<? extends Float> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public FloatOpenHashSet(Collection<? extends Float> c) {
        this(c, 0.75f);
    }

    public FloatOpenHashSet(FloatCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public FloatOpenHashSet(FloatCollection c) {
        this(c, 0.75f);
    }

    public FloatOpenHashSet(FloatIterator i, float f) {
        this(16, f);
        while (i.hasNext()) {
            this.add(i.nextFloat());
        }
    }

    public FloatOpenHashSet(FloatIterator i) {
        this(i, 0.75f);
    }

    public FloatOpenHashSet(Iterator<?> i, float f) {
        this(FloatIterators.asFloatIterator(i), f);
    }

    public FloatOpenHashSet(Iterator<?> i) {
        this(FloatIterators.asFloatIterator(i));
    }

    public FloatOpenHashSet(float[] a, int offset, int length, float f) {
        this(length < 0 ? 0 : length, f);
        FloatArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public FloatOpenHashSet(float[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public FloatOpenHashSet(float[] a, float f) {
        this(a, 0, a.length, f);
    }

    public FloatOpenHashSet(float[] a) {
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
    public boolean addAll(FloatCollection c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Float> c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(float k) {
        if (Float.floatToIntBits(k) == 0) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            float[] key = this.key;
            int pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) != 0) {
                if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
                    return false;
                }
                while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (Float.floatToIntBits(curr) != Float.floatToIntBits(k)) continue;
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
        float[] key = this.key;
        do {
            float curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if (Float.floatToIntBits(curr = key[pos]) == 0) {
                    key[last] = 0.0f;
                    return;
                }
                int slot = HashCommon.mix(HashCommon.float2int(curr)) & this.mask;
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
        this.key[this.n] = 0.0f;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean remove(float k) {
        if (Float.floatToIntBits(k) == 0) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return false;
        }
        if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (Float.floatToIntBits(k) != Float.floatToIntBits(curr));
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(float k) {
        if (Float.floatToIntBits(k) == 0) {
            return this.containsNull;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return false;
        }
        if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return true;
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (Float.floatToIntBits(k) != Float.floatToIntBits(curr));
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, 0.0f);
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
    public FloatIterator iterator() {
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
        float[] key = this.key;
        int mask = newN - 1;
        float[] newKey = new float[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (Float.floatToIntBits(key[--i]) == 0) {
            }
            int pos = HashCommon.mix(HashCommon.float2int(key[i])) & mask;
            if (Float.floatToIntBits(newKey[pos]) != 0) {
                while (Float.floatToIntBits(newKey[pos = pos + 1 & mask]) != 0) {
                }
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public FloatOpenHashSet clone() {
        FloatOpenHashSet c;
        try {
            c = (FloatOpenHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (float[])this.key.clone();
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        while (j-- != 0) {
            while (Float.floatToIntBits(this.key[i]) == 0) {
                ++i;
            }
            h += HashCommon.float2int(this.key[i]);
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        FloatIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeFloat(i.nextFloat());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new float[this.n + 1];
        float[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            float k = s.readFloat();
            if (Float.floatToIntBits(k) == 0) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;
                if (Float.floatToIntBits(key[pos]) != 0) {
                    while (Float.floatToIntBits(key[pos = pos + 1 & this.mask]) != 0) {
                    }
                }
            }
            key[pos] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements FloatIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        FloatArrayList wrapped;

        private SetIterator() {
            this.pos = FloatOpenHashSet.this.n;
            this.last = -1;
            this.c = FloatOpenHashSet.this.size;
            this.mustReturnNull = FloatOpenHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public float nextFloat() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = FloatOpenHashSet.this.n;
                return FloatOpenHashSet.this.key[FloatOpenHashSet.this.n];
            }
            float[] key = FloatOpenHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getFloat(- this.pos - 1);
            } while (Float.floatToIntBits(key[this.pos]) == 0);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            float[] key = FloatOpenHashSet.this.key;
            do {
                float curr;
                int last = pos;
                pos = last + 1 & FloatOpenHashSet.this.mask;
                do {
                    if (Float.floatToIntBits(curr = key[pos]) == 0) {
                        key[last] = 0.0f;
                        return;
                    }
                    int slot = HashCommon.mix(HashCommon.float2int(curr)) & FloatOpenHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & FloatOpenHashSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList(2);
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
            if (this.last == FloatOpenHashSet.this.n) {
                FloatOpenHashSet.this.containsNull = false;
                FloatOpenHashSet.this.key[FloatOpenHashSet.this.n] = 0.0f;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                FloatOpenHashSet.this.remove(this.wrapped.getFloat(- this.pos - 1));
                this.last = -1;
                return;
            }
            --FloatOpenHashSet.this.size;
            this.last = -1;
        }
    }

}

