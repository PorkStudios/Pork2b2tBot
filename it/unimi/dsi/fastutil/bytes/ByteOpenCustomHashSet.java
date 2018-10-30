/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteHash;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteOpenCustomHashSet
extends AbstractByteSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient byte[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected ByteHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public ByteOpenCustomHashSet(int expected, float f, ByteHash.Strategy strategy) {
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
        this.key = new byte[this.n + 1];
    }

    public ByteOpenCustomHashSet(int expected, ByteHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(ByteHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(Collection<? extends Byte> c, float f, ByteHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public ByteOpenCustomHashSet(Collection<? extends Byte> c, ByteHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(ByteCollection c, float f, ByteHash.Strategy strategy) {
        this(c.size(), f, strategy);
        this.addAll(c);
    }

    public ByteOpenCustomHashSet(ByteCollection c, ByteHash.Strategy strategy) {
        this(c, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(ByteIterator i, float f, ByteHash.Strategy strategy) {
        this(16, f, strategy);
        while (i.hasNext()) {
            this.add(i.nextByte());
        }
    }

    public ByteOpenCustomHashSet(ByteIterator i, ByteHash.Strategy strategy) {
        this(i, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(Iterator<?> i, float f, ByteHash.Strategy strategy) {
        this(ByteIterators.asByteIterator(i), f, strategy);
    }

    public ByteOpenCustomHashSet(Iterator<?> i, ByteHash.Strategy strategy) {
        this(ByteIterators.asByteIterator(i), strategy);
    }

    public ByteOpenCustomHashSet(byte[] a, int offset, int length, float f, ByteHash.Strategy strategy) {
        this(length < 0 ? 0 : length, f, strategy);
        ByteArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public ByteOpenCustomHashSet(byte[] a, int offset, int length, ByteHash.Strategy strategy) {
        this(a, offset, length, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(byte[] a, float f, ByteHash.Strategy strategy) {
        this(a, 0, a.length, f, strategy);
    }

    public ByteOpenCustomHashSet(byte[] a, ByteHash.Strategy strategy) {
        this(a, 0.75f, strategy);
    }

    public ByteHash.Strategy strategy() {
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
    public boolean addAll(ByteCollection c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(byte k) {
        if (this.strategy.equals(k, (byte)0)) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
            this.key[this.n] = k;
        } else {
            byte[] key = this.key;
            int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (this.strategy.equals(curr, k)) {
                    return false;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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
        byte[] key = this.key;
        do {
            byte curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
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
        this.key[this.n] = 0;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean remove(byte k) {
        if (this.strategy.equals(k, (byte)0)) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(byte k) {
        if (this.strategy.equals(k, (byte)0)) {
            return this.containsNull;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
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
        Arrays.fill(this.key, (byte)0);
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
    public ByteIterator iterator() {
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
        byte[] key = this.key;
        int mask = newN - 1;
        byte[] newKey = new byte[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
            if (newKey[pos] != 0) {
                while (newKey[pos = pos + 1 & mask] != 0) {
                }
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public ByteOpenCustomHashSet clone() {
        ByteOpenCustomHashSet c;
        try {
            c = (ByteOpenCustomHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (byte[])this.key.clone();
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
            while (this.key[i] == 0) {
                ++i;
            }
            h += this.strategy.hashCode(this.key[i]);
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        ByteIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeByte(i.nextByte());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new byte[this.n + 1];
        byte[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            byte k = s.readByte();
            if (this.strategy.equals(k, (byte)0)) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
                if (key[pos] != 0) {
                    while (key[pos = pos + 1 & this.mask] != 0) {
                    }
                }
            }
            key[pos] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements ByteIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        ByteArrayList wrapped;

        private SetIterator() {
            this.pos = ByteOpenCustomHashSet.this.n;
            this.last = -1;
            this.c = ByteOpenCustomHashSet.this.size;
            this.mustReturnNull = ByteOpenCustomHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = ByteOpenCustomHashSet.this.n;
                return ByteOpenCustomHashSet.this.key[ByteOpenCustomHashSet.this.n];
            }
            byte[] key = ByteOpenCustomHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getByte(- this.pos - 1);
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            byte[] key = ByteOpenCustomHashSet.this.key;
            do {
                byte curr;
                int last = pos;
                pos = last + 1 & ByteOpenCustomHashSet.this.mask;
                do {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(ByteOpenCustomHashSet.this.strategy.hashCode(curr)) & ByteOpenCustomHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & ByteOpenCustomHashSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
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
            if (this.last == ByteOpenCustomHashSet.this.n) {
                ByteOpenCustomHashSet.this.containsNull = false;
                ByteOpenCustomHashSet.this.key[ByteOpenCustomHashSet.this.n] = 0;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                ByteOpenCustomHashSet.this.remove(this.wrapped.getByte(- this.pos - 1));
                this.last = -1;
                return;
            }
            --ByteOpenCustomHashSet.this.size;
            this.last = -1;
        }
    }

}

