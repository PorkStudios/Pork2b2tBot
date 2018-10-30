/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractCharSortedSet;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharLinkedOpenHashSet
extends AbstractCharSortedSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient char[] key;
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

    public CharLinkedOpenHashSet(int expected, float f) {
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
        this.link = new long[this.n + 1];
    }

    public CharLinkedOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public CharLinkedOpenHashSet() {
        this(16, 0.75f);
    }

    public CharLinkedOpenHashSet(Collection<? extends Character> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public CharLinkedOpenHashSet(Collection<? extends Character> c) {
        this(c, 0.75f);
    }

    public CharLinkedOpenHashSet(CharCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public CharLinkedOpenHashSet(CharCollection c) {
        this(c, 0.75f);
    }

    public CharLinkedOpenHashSet(CharIterator i, float f) {
        this(16, f);
        while (i.hasNext()) {
            this.add(i.nextChar());
        }
    }

    public CharLinkedOpenHashSet(CharIterator i) {
        this(i, 0.75f);
    }

    public CharLinkedOpenHashSet(Iterator<?> i, float f) {
        this(CharIterators.asCharIterator(i), f);
    }

    public CharLinkedOpenHashSet(Iterator<?> i) {
        this(CharIterators.asCharIterator(i));
    }

    public CharLinkedOpenHashSet(char[] a, int offset, int length, float f) {
        this(length < 0 ? 0 : length, f);
        CharArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public CharLinkedOpenHashSet(char[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public CharLinkedOpenHashSet(char[] a, float f) {
        this(a, 0, a.length, f);
    }

    public CharLinkedOpenHashSet(char[] a) {
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
        int pos;
        if (k == '\u0000') {
            if (this.containsNull) {
                return false;
            }
            pos = this.n;
            this.containsNull = true;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
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
        this.key[this.n] = '\u0000';
        --this.size;
        this.fixPointers(this.n);
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

    public char removeFirstChar() {
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
        char k = this.key[pos];
        --this.size;
        if (k == '\u0000') {
            this.containsNull = false;
            this.key[this.n] = '\u0000';
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return k;
    }

    public char removeLastChar() {
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
        char k = this.key[pos];
        --this.size;
        if (k == '\u0000') {
            this.containsNull = false;
            this.key[this.n] = '\u0000';
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

    public boolean addAndMoveToFirst(char k) {
        int pos;
        if (k == '\u0000') {
            if (this.containsNull) {
                this.moveIndexToFirst(this.n);
                return false;
            }
            this.containsNull = true;
            pos = this.n;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
            while (key[pos] != '\u0000') {
                if (k == key[pos]) {
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

    public boolean addAndMoveToLast(char k) {
        int pos;
        if (k == '\u0000') {
            if (this.containsNull) {
                this.moveIndexToLast(this.n);
                return false;
            }
            this.containsNull = true;
            pos = this.n;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
            while (key[pos] != '\u0000') {
                if (k == key[pos]) {
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
        Arrays.fill(this.key, '\u0000');
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
    public char firstChar() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public char lastChar() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public CharSortedSet tailSet(char from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSortedSet headSet(char to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSortedSet subSet(char from, char to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharComparator comparator() {
        return null;
    }

    @Override
    public CharListIterator iterator(char from) {
        return new SetIterator(from);
    }

    @Override
    public CharListIterator iterator() {
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
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (key[i] == '\u0000') {
                pos = newN;
            } else {
                pos = HashCommon.mix(key[i]) & mask;
                while (newKey[pos] != '\u0000') {
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

    public CharLinkedOpenHashSet clone() {
        CharLinkedOpenHashSet c;
        try {
            c = (CharLinkedOpenHashSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (char[])this.key.clone();
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
            while (this.key[i] == '\u0000') {
                ++i;
            }
            h += this.key[i];
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        CharListIterator i = this.iterator();
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
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
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
    implements CharListIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        SetIterator() {
            this.next = CharLinkedOpenHashSet.this.first;
            this.index = 0;
        }

        SetIterator(char from) {
            if (from == '\u0000') {
                if (CharLinkedOpenHashSet.this.containsNull) {
                    this.next = (int)CharLinkedOpenHashSet.this.link[CharLinkedOpenHashSet.this.n];
                    this.prev = CharLinkedOpenHashSet.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this set.");
            }
            if (CharLinkedOpenHashSet.this.key[CharLinkedOpenHashSet.this.last] == from) {
                this.prev = CharLinkedOpenHashSet.this.last;
                this.index = CharLinkedOpenHashSet.this.size;
                return;
            }
            char[] key = CharLinkedOpenHashSet.this.key;
            int pos = HashCommon.mix(from) & CharLinkedOpenHashSet.this.mask;
            while (key[pos] != '\u0000') {
                if (key[pos] == from) {
                    this.next = (int)CharLinkedOpenHashSet.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & CharLinkedOpenHashSet.this.mask;
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
        public char nextChar() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)CharLinkedOpenHashSet.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return CharLinkedOpenHashSet.this.key[this.curr];
        }

        @Override
        public char previousChar() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev;
            this.prev = (int)(CharLinkedOpenHashSet.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return CharLinkedOpenHashSet.this.key[this.curr];
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
                this.index = CharLinkedOpenHashSet.this.size;
                return;
            }
            int pos = CharLinkedOpenHashSet.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)CharLinkedOpenHashSet.this.link[pos];
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
                this.prev = (int)(CharLinkedOpenHashSet.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)CharLinkedOpenHashSet.this.link[this.curr];
            }
            --CharLinkedOpenHashSet.this.size;
            if (this.prev == -1) {
                CharLinkedOpenHashSet.this.first = this.next;
            } else {
                long[] arrl = CharLinkedOpenHashSet.this.link;
                int n = this.prev;
                arrl[n] = arrl[n] ^ (CharLinkedOpenHashSet.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                CharLinkedOpenHashSet.this.last = this.prev;
            } else {
                long[] arrl = CharLinkedOpenHashSet.this.link;
                int n = this.next;
                arrl[n] = arrl[n] ^ (CharLinkedOpenHashSet.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & -4294967296L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != CharLinkedOpenHashSet.this.n) {
                char[] key = CharLinkedOpenHashSet.this.key;
                do {
                    char curr;
                    int last = pos;
                    pos = last + 1 & CharLinkedOpenHashSet.this.mask;
                    do {
                        if ((curr = key[pos]) == '\u0000') {
                            key[last] = '\u0000';
                            return;
                        }
                        int slot = HashCommon.mix(curr) & CharLinkedOpenHashSet.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & CharLinkedOpenHashSet.this.mask;
                    } while (true);
                    key[last] = curr;
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    CharLinkedOpenHashSet.this.fixPointers(pos, last);
                } while (true);
            }
            CharLinkedOpenHashSet.this.containsNull = false;
            CharLinkedOpenHashSet.this.key[CharLinkedOpenHashSet.this.n] = '\u0000';
        }
    }

}

