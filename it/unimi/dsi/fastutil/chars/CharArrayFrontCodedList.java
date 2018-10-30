/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharBigArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class CharArrayFrontCodedList
extends AbstractObjectList<char[]>
implements Serializable,
Cloneable,
RandomAccess {
    private static final long serialVersionUID = 1L;
    protected final int n;
    protected final int ratio;
    protected final char[][] array;
    protected transient long[] p;

    public CharArrayFrontCodedList(Iterator<char[]> arrays, int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
        }
        char[][] array = CharBigArrays.EMPTY_BIG_ARRAY;
        long[] p = LongArrays.EMPTY_ARRAY;
        char[][] a = new char[2][];
        long curSize = 0L;
        int n = 0;
        int b = 0;
        while (arrays.hasNext()) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % ratio == 0) {
                p = LongArrays.grow(p, n / ratio + 1);
                p[n / ratio] = curSize;
                array = CharBigArrays.grow(array, curSize + (long)CharArrayFrontCodedList.count(length) + (long)length, curSize);
                curSize += (long)CharArrayFrontCodedList.writeInt(array, length, curSize);
                CharBigArrays.copyToBig(a[b], 0, array, curSize, length);
                curSize += (long)length;
            } else {
                int common;
                int minLength = a[1 - b].length;
                if (length < minLength) {
                    minLength = length;
                }
                for (common = 0; common < minLength && a[0][common] == a[1][common]; ++common) {
                }
                array = CharBigArrays.grow(array, curSize + (long)CharArrayFrontCodedList.count(length) + (long)CharArrayFrontCodedList.count(common) + (long)(length -= common), curSize);
                curSize += (long)CharArrayFrontCodedList.writeInt(array, length, curSize);
                curSize += (long)CharArrayFrontCodedList.writeInt(array, common, curSize);
                CharBigArrays.copyToBig(a[b], common, array, curSize, length);
                curSize += (long)length;
            }
            b = 1 - b;
            ++n;
        }
        this.n = n;
        this.ratio = ratio;
        this.array = CharBigArrays.trim(array, curSize);
        this.p = LongArrays.trim(p, (n + ratio - 1) / ratio);
    }

    public CharArrayFrontCodedList(Collection<char[]> c, int ratio) {
        this(c.iterator(), ratio);
    }

    private static int readInt(char[][] a, long pos) {
        int c0 = CharBigArrays.get(a, pos);
        return c0 < 32768 ? c0 : (c0 & 32767) << 16 | CharBigArrays.get(a, pos + 1L);
    }

    private static int count(int length) {
        return length < 32768 ? 1 : 2;
    }

    private static int writeInt(char[][] a, int length, long pos) {
        if (length < 32768) {
            CharBigArrays.set(a, pos, (char)length);
            return 1;
        }
        CharBigArrays.set(a, pos++, (char)(length >>> 16 | 32768));
        CharBigArrays.set(a, pos, (char)(length & 65535));
        return 2;
    }

    public int ratio() {
        return this.ratio;
    }

    private int length(int index) {
        char[][] array = this.array;
        int delta = index % this.ratio;
        long pos = this.p[index / this.ratio];
        int length = CharArrayFrontCodedList.readInt(array, pos);
        if (delta == 0) {
            return length;
        }
        length = CharArrayFrontCodedList.readInt(array, pos += (long)(CharArrayFrontCodedList.count(length) + length));
        int common = CharArrayFrontCodedList.readInt(array, pos + (long)CharArrayFrontCodedList.count(length));
        for (int i = 0; i < delta - 1; ++i) {
            length = CharArrayFrontCodedList.readInt(array, pos += (long)(CharArrayFrontCodedList.count(length) + CharArrayFrontCodedList.count(common) + length));
            common = CharArrayFrontCodedList.readInt(array, pos + (long)CharArrayFrontCodedList.count(length));
        }
        return length + common;
    }

    public int arrayLength(int index) {
        this.ensureRestrictedIndex(index);
        return this.length(index);
    }

    private int extract(int index, char[] a, int offset, int length) {
        long startPos;
        int delta = index % this.ratio;
        long pos = startPos = this.p[index / this.ratio];
        int arrayLength = CharArrayFrontCodedList.readInt(this.array, pos);
        int currLen = 0;
        if (delta == 0) {
            pos = this.p[index / this.ratio] + (long)CharArrayFrontCodedList.count(arrayLength);
            CharBigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
            return arrayLength;
        }
        int common = 0;
        for (int i = 0; i < delta; ++i) {
            long prevArrayPos = pos + (long)CharArrayFrontCodedList.count(arrayLength) + (long)(i != 0 ? CharArrayFrontCodedList.count(common) : 0);
            pos = prevArrayPos + (long)arrayLength;
            arrayLength = CharArrayFrontCodedList.readInt(this.array, pos);
            common = CharArrayFrontCodedList.readInt(this.array, pos + (long)CharArrayFrontCodedList.count(arrayLength));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
                currLen = actualCommon;
                continue;
            }
            CharBigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
            currLen = actualCommon;
        }
        if (currLen < length) {
            CharBigArrays.copyFromBig(this.array, pos + (long)CharArrayFrontCodedList.count(arrayLength) + (long)CharArrayFrontCodedList.count(common), a, currLen + offset, Math.min(arrayLength, length - currLen));
        }
        return arrayLength + common;
    }

    @Override
    public char[] get(int index) {
        return this.getArray(index);
    }

    public char[] getArray(int index) {
        this.ensureRestrictedIndex(index);
        int length = this.length(index);
        char[] a = new char[length];
        this.extract(index, a, 0, length);
        return a;
    }

    public int get(int index, char[] a, int offset, int length) {
        this.ensureRestrictedIndex(index);
        CharArrays.ensureOffsetLength(a, offset, length);
        int arrayLength = this.extract(index, a, offset, length);
        if (length >= arrayLength) {
            return arrayLength;
        }
        return length - arrayLength;
    }

    public int get(int index, char[] a) {
        return this.get(index, a, 0, a.length);
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public ObjectListIterator<char[]> listIterator(final int start) {
        this.ensureIndex(start);
        return new ObjectListIterator<char[]>(){
            char[] s = CharArrays.EMPTY_ARRAY;
            int i = 0;
            long pos = 0L;
            boolean inSync;
            {
                if (start != 0) {
                    if (start == CharArrayFrontCodedList.this.n) {
                        this.i = start;
                    } else {
                        this.pos = CharArrayFrontCodedList.this.p[start / CharArrayFrontCodedList.this.ratio];
                        int j = start % CharArrayFrontCodedList.this.ratio;
                        this.i = start - j;
                        while (j-- != 0) {
                            this.next();
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return this.i < CharArrayFrontCodedList.this.n;
            }

            @Override
            public boolean hasPrevious() {
                return this.i > 0;
            }

            @Override
            public int previousIndex() {
                return this.i - 1;
            }

            @Override
            public int nextIndex() {
                return this.i;
            }

            @Override
            public char[] next() {
                int length;
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                if (this.i % CharArrayFrontCodedList.this.ratio == 0) {
                    this.pos = CharArrayFrontCodedList.this.p[this.i / CharArrayFrontCodedList.this.ratio];
                    length = CharArrayFrontCodedList.readInt(CharArrayFrontCodedList.this.array, this.pos);
                    this.s = CharArrays.ensureCapacity(this.s, length, 0);
                    CharBigArrays.copyFromBig(CharArrayFrontCodedList.this.array, this.pos + (long)CharArrayFrontCodedList.count(length), this.s, 0, length);
                    this.pos += (long)(length + CharArrayFrontCodedList.count(length));
                    this.inSync = true;
                } else if (this.inSync) {
                    length = CharArrayFrontCodedList.readInt(CharArrayFrontCodedList.this.array, this.pos);
                    int common = CharArrayFrontCodedList.readInt(CharArrayFrontCodedList.this.array, this.pos + (long)CharArrayFrontCodedList.count(length));
                    this.s = CharArrays.ensureCapacity(this.s, length + common, common);
                    CharBigArrays.copyFromBig(CharArrayFrontCodedList.this.array, this.pos + (long)CharArrayFrontCodedList.count(length) + (long)CharArrayFrontCodedList.count(common), this.s, common, length);
                    this.pos += (long)(CharArrayFrontCodedList.count(length) + CharArrayFrontCodedList.count(common) + length);
                    length += common;
                } else {
                    length = CharArrayFrontCodedList.this.length(this.i);
                    this.s = CharArrays.ensureCapacity(this.s, length, 0);
                    CharArrayFrontCodedList.this.extract(this.i, this.s, 0, length);
                }
                ++this.i;
                return CharArrays.copy(this.s, 0, length);
            }

            @Override
            public char[] previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.inSync = false;
                return CharArrayFrontCodedList.this.getArray(--this.i);
            }
        };
    }

    public CharArrayFrontCodedList clone() {
        return this;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("[");
        for (int i = 0; i < this.n; ++i) {
            if (i != 0) {
                s.append(", ");
            }
            s.append(CharArrayList.wrap(this.getArray(i)).toString());
        }
        s.append("]");
        return s.toString();
    }

    protected long[] rebuildPointerArray() {
        long[] p = new long[(this.n + this.ratio - 1) / this.ratio];
        char[][] a = this.array;
        long pos = 0L;
        int j = 0;
        int skip = this.ratio - 1;
        for (int i = 0; i < this.n; ++i) {
            int length = CharArrayFrontCodedList.readInt(a, pos);
            int count = CharArrayFrontCodedList.count(length);
            if (++skip == this.ratio) {
                skip = 0;
                p[j++] = pos;
                pos += (long)(count + length);
                continue;
            }
            pos += (long)(count + CharArrayFrontCodedList.count(CharArrayFrontCodedList.readInt(a, pos + (long)count)) + length);
        }
        return p;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.p = this.rebuildPointerArray();
    }

}

