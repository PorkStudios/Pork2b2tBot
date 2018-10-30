/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatIterators;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatLinkedOpenHashSet
extends AbstractFloatSortedSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient float[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public FloatLinkedOpenHashSet(int expected, float f) {
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
        this.link = new long[this.n + 1];
    }

    public FloatLinkedOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public FloatLinkedOpenHashSet() {
        this(16, 0.75f);
    }

    public FloatLinkedOpenHashSet(Collection<? extends Float> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public FloatLinkedOpenHashSet(Collection<? extends Float> c) {
        this(c, 0.75f);
    }

    public FloatLinkedOpenHashSet(FloatCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public FloatLinkedOpenHashSet(FloatCollection c) {
        this(c, 0.75f);
    }

    public FloatLinkedOpenHashSet(FloatIterator i, float f) {
        this(16, f);
        while (i.hasNext()) {
            this.add(i.nextFloat());
        }
    }

    public FloatLinkedOpenHashSet(FloatIterator i) {
        this(i, 0.75f);
    }

    public FloatLinkedOpenHashSet(Iterator<?> i, float f) {
        this(FloatIterators.asFloatIterator(i), f);
    }

    public FloatLinkedOpenHashSet(Iterator<?> i) {
        this(FloatIterators.asFloatIterator(i));
    }

    public FloatLinkedOpenHashSet(float[] a, int offset, int length, float f) {
        this(length < 0 ? 0 : length, f);
        FloatArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public FloatLinkedOpenHashSet(float[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public FloatLinkedOpenHashSet(float[] a, float f) {
        this(a, 0, a.length, f);
    }

    public FloatLinkedOpenHashSet(float[] a) {
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
        int pos;
        if (Float.floatToIntBits(k) == 0) {
            if (this.containsNull) {
                return false;
            }
            pos = this.n;
            this.containsNull = true;
        } else {
            float[] key = this.key;
            pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;
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
        this.key[this.n] = 0.0f;
        --this.size;
        this.fixPointers(this.n);
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

    public float removeFirstFloat() {
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
        float k = this.key[pos];
        --this.size;
        if (Float.floatToIntBits(k) == 0) {
            this.containsNull = false;
            this.key[this.n] = 0.0f;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return k;
    }

    public float removeLastFloat() {
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
        float k = this.key[pos];
        --this.size;
        if (Float.floatToIntBits(k) == 0) {
            this.containsNull = false;
            this.key[this.n] = 0.0f;
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

    public boolean addAndMoveToFirst(float k) {
        int pos;
        if (Float.floatToIntBits(k) == 0) {
            if (this.containsNull) {
                this.moveIndexToFirst(this.n);
                return false;
            }
            this.containsNull = true;
            pos = this.n;
        } else {
            float[] key = this.key;
            pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;
            while (Float.floatToIntBits(key[pos]) != 0) {
                if (Float.floatToIntBits(k) == Float.floatToIntBits(key[pos])) {
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

    public boolean addAndMoveToLast(float k) {
        int pos;
        if (Float.floatToIntBits(k) == 0) {
            if (this.containsNull) {
                this.moveIndexToLast(this.n);
                return false;
            }
            this.containsNull = true;
            pos = this.n;
        } else {
            float[] key = this.key;
            pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;
            while (Float.floatToIntBits(key[pos]) != 0) {
                if (Float.floatToIntBits(k) == Float.floatToIntBits(key[pos])) {
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
        Arrays.fill(this.key, 0.0f);
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
    public float firstFloat() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public float lastFloat() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public FloatSortedSet tailSet(float from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatSortedSet headSet(float to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatSortedSet subSet(float from, float to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatComparator comparator() {
        return null;
    }

    @Override
    public FloatListIterator iterator(float from) {
        return new SetIterator(from);
    }

    @Override
    public FloatListIterator iterator() {
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
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (Float.floatToIntBits(key[i]) == 0) {
                pos = newN;
            } else {
                pos = HashCommon.mix(HashCommon.float2int(key[i])) & mask;
                while (Float.floatToIntBits(newKey[pos]) != 0) {
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

    public FloatLinkedOpenHashSet clone() {
        FloatLinkedOpenHashSet c;
        try {
            c = (FloatLinkedOpenHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (float[])this.key.clone();
        c.containsNull = this.containsNull;
        c.link = (long[])this.link.clone();
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
        FloatListIterator i = this.iterator();
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
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
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
    implements FloatListIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        SetIterator() {
            this.next = FloatLinkedOpenHashSet.this.first;
            this.index = 0;
        }

        SetIterator(float from) {
            if (Float.floatToIntBits(from) == 0) {
                if (FloatLinkedOpenHashSet.this.containsNull) {
                    this.next = (int)FloatLinkedOpenHashSet.this.link[FloatLinkedOpenHashSet.this.n];
                    this.prev = FloatLinkedOpenHashSet.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this set.");
            }
            if (Float.floatToIntBits(FloatLinkedOpenHashSet.this.key[FloatLinkedOpenHashSet.this.last]) == Float.floatToIntBits(from)) {
                this.prev = FloatLinkedOpenHashSet.this.last;
                this.index = FloatLinkedOpenHashSet.this.size;
                return;
            }
            float[] key = FloatLinkedOpenHashSet.this.key;
            int pos = HashCommon.mix(HashCommon.float2int(from)) & FloatLinkedOpenHashSet.this.mask;
            while (Float.floatToIntBits(key[pos]) != 0) {
                if (Float.floatToIntBits(key[pos]) == Float.floatToIntBits(from)) {
                    this.next = (int)FloatLinkedOpenHashSet.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & FloatLinkedOpenHashSet.this.mask;
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
        public float nextFloat() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)FloatLinkedOpenHashSet.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return FloatLinkedOpenHashSet.this.key[this.curr];
        }

        @Override
        public float previousFloat() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev;
            this.prev = (int)(FloatLinkedOpenHashSet.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return FloatLinkedOpenHashSet.this.key[this.curr];
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
                this.index = FloatLinkedOpenHashSet.this.size;
                return;
            }
            int pos = FloatLinkedOpenHashSet.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)FloatLinkedOpenHashSet.this.link[pos];
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
                this.prev = (int)(FloatLinkedOpenHashSet.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)FloatLinkedOpenHashSet.this.link[this.curr];
            }
            --FloatLinkedOpenHashSet.this.size;
            if (this.prev == -1) {
                FloatLinkedOpenHashSet.this.first = this.next;
            } else {
                long[] arrl = FloatLinkedOpenHashSet.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (FloatLinkedOpenHashSet.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                FloatLinkedOpenHashSet.this.last = this.prev;
            } else {
                long[] arrl = FloatLinkedOpenHashSet.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (FloatLinkedOpenHashSet.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != FloatLinkedOpenHashSet.this.n) {
                float[] key = FloatLinkedOpenHashSet.this.key;
                do {
                    float curr;
                    int last = pos;
                    pos = last + 1 & FloatLinkedOpenHashSet.this.mask;
                    do {
                        if (Float.floatToIntBits(curr = key[pos]) == 0) {
                            key[last] = 0.0f;
                            return;
                        }
                        int slot = HashCommon.mix(HashCommon.float2int(curr)) & FloatLinkedOpenHashSet.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & FloatLinkedOpenHashSet.this.mask;
                    } while (true);
                    key[last] = curr;
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    FloatLinkedOpenHashSet.this.fixPointers(pos, last);
                } while (true);
            }
            FloatLinkedOpenHashSet.this.containsNull = false;
            FloatLinkedOpenHashSet.this.key[FloatLinkedOpenHashSet.this.n] = 0.0f;
        }
    }

}

