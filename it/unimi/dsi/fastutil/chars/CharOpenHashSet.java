/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharOpenHashSet
extends AbstractCharSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient char[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public CharOpenHashSet(int expected, float f) {
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
        this.key = new char[this.n + 1];
    }

    public CharOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public CharOpenHashSet() {
        this(16, 0.75f);
    }

    public CharOpenHashSet(Collection<? extends Character> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public CharOpenHashSet(Collection<? extends Character> c) {
        this(c, 0.75f);
    }

    public CharOpenHashSet(CharCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public CharOpenHashSet(CharCollection c) {
        this(c, 0.75f);
    }

    public CharOpenHashSet(CharIterator i, float f) {
        this(16, f);
        while (i.hasNext()) {
            this.add(i.nextChar());
        }
    }

    public CharOpenHashSet(CharIterator i) {
        this(i, 0.75f);
    }

    public CharOpenHashSet(Iterator<?> i, float f) {
        this(CharIterators.asCharIterator(i), f);
    }

    public CharOpenHashSet(Iterator<?> i) {
        this(CharIterators.asCharIterator(i));
    }

    public CharOpenHashSet(char[] a, int offset, int length, float f) {
        this(length < 0 ? 0 : length, f);
        CharArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public CharOpenHashSet(char[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public CharOpenHashSet(char[] a, float f) {
        this(a, 0, a.length, f);
    }

    public CharOpenHashSet(char[] a) {
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
    public boolean addAll(CharCollection c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Character> c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(char k) {
        if (k == '\u0000') {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            char[] key = this.key;
            int pos = HashCommon.mix(k) & this.mask;
            char curr = key[pos];
            if (curr != '\u0000') {
                if (curr == k) {
                    return false;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') {
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
        char[] key = this.key;
        do {
            char curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == '\u0000') {
                    key[last] = '\u0000';
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
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
        this.key[this.n] = '\u0000';
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean remove(char k) {
        if (k == '\u0000') {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return false;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return false;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(char k) {
        if (k == '\u0000') {
            return this.containsNull;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
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
        Arrays.fill(this.key, '\u0000');
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
    public CharIterator iterator() {
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
        char[] key = this.key;
        int mask = newN - 1;
        char[] newKey = new char[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == '\u0000') {
            }
            int pos = HashCommon.mix(key[i]) & mask;
            if (newKey[pos] != '\u0000') {
                while (newKey[pos = pos + 1 & mask] != '\u0000') {
                }
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public CharOpenHashSet clone() {
        CharOpenHashSet c;
        try {
            c = (CharOpenHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        while (j-- != 0) {
            while (this.key[i] == '\u0000') {
                ++i;
            }
            h += this.key[i];
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        CharIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeChar(i.nextChar());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new char[this.n + 1];
        char[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            char k = s.readChar();
            if (k == '\u0000') {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = HashCommon.mix(k) & this.mask;
                if (key[pos] != '\u0000') {
                    while (key[pos = pos + 1 & this.mask] != '\u0000') {
                    }
                }
            }
            key[pos] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements CharIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        CharArrayList wrapped;

        private SetIterator() {
            this.pos = CharOpenHashSet.this.n;
            this.last = -1;
            this.c = CharOpenHashSet.this.size;
            this.mustReturnNull = CharOpenHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public char nextChar() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = CharOpenHashSet.this.n;
                return CharOpenHashSet.this.key[CharOpenHashSet.this.n];
            }
            char[] key = CharOpenHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getChar(- this.pos - 1);
            } while (key[this.pos] == '\u0000');
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            char[] key = CharOpenHashSet.this.key;
            do {
                char curr;
                int last = pos;
                pos = last + 1 & CharOpenHashSet.this.mask;
                do {
                    if ((curr = key[pos]) == '\u0000') {
                        key[last] = '\u0000';
                        return;
                    }
                    int slot = HashCommon.mix(curr) & CharOpenHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & CharOpenHashSet.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new CharArrayList(2);
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
            if (this.last == CharOpenHashSet.this.n) {
                CharOpenHashSet.this.containsNull = false;
                CharOpenHashSet.this.key[CharOpenHashSet.this.n] = '\u0000';
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                CharOpenHashSet.this.remove(this.wrapped.getChar(- this.pos - 1));
                this.last = -1;
                return;
            }
            --CharOpenHashSet.this.size;
            this.last = -1;
        }
    }

}

