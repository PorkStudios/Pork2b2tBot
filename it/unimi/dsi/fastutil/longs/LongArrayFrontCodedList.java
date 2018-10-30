/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
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

public class LongArrayFrontCodedList
extends AbstractObjectList<long[]>
implements Serializable,
Cloneable,
RandomAccess {
    private static final long serialVersionUID = 1L;
    protected final int n;
    protected final int ratio;
    protected final long[][] array;
    protected transient long[] p;

    public LongArrayFrontCodedList(Iterator<long[]> arrays, int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
        }
        long[][] array = LongBigArrays.EMPTY_BIG_ARRAY;
        long[] p = LongArrays.EMPTY_ARRAY;
        long[][] a = new long[2][];
        long curSize = 0L;
        int n = 0;
        int b = 0;
        while (arrays.hasNext()) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % ratio == 0) {
                p = LongArrays.grow(p, n / ratio + 1);
                p[n / ratio] = curSize;
                array = LongBigArrays.grow(array, curSize + (long)LongArrayFrontCodedList.count(length) + (long)length, curSize);
                curSize += (long)LongArrayFrontCodedList.writeInt(array, length, curSize);
                LongBigArrays.copyToBig(a[b], 0, array, curSize, length);
                curSize += (long)length;
            } else {
                int common;
                int minLength = a[1 - b].length;
                if (length < minLength) {
                    minLength = length;
                }
                for (common = 0; common < minLength && a[0][common] == a[1][common]; ++common) {
                }
                array = LongBigArrays.grow(array, curSize + (long)LongArrayFrontCodedList.count(length) + (long)LongArrayFrontCodedList.count(common) + (long)(length -= common), curSize);
                curSize += (long)LongArrayFrontCodedList.writeInt(array, length, curSize);
                curSize += (long)LongArrayFrontCodedList.writeInt(array, common, curSize);
                LongBigArrays.copyToBig(a[b], common, array, curSize, length);
                curSize += (long)length;
            }
            b = 1 - b;
            ++n;
        }
        this.n = n;
        this.ratio = ratio;
        this.array = LongBigArrays.trim(array, curSize);
        this.p = LongArrays.trim(p, (n + ratio - 1) / ratio);
    }

    public LongArrayFrontCodedList(Collection<long[]> c, int ratio) {
        this(c.iterator(), ratio);
    }

    private static int readInt(long[][] a, long pos) {
        return (int)LongBigArrays.get(a, pos);
    }

    private static int count(int length) {
        return 1;
    }

    private static int writeInt(long[][] a, int length, long pos) {
        LongBigArrays.set(a, pos, length);
        return 1;
    }

    public int ratio() {
        return this.ratio;
    }

    private int length(int index) {
        long[][] array = this.array;
        int delta = index % this.ratio;
        long pos = this.p[index / this.ratio];
        int length = LongArrayFrontCodedList.readInt(array, pos);
        if (delta == 0) {
            return length;
        }
        length = LongArrayFrontCodedList.readInt(array, pos += (long)(LongArrayFrontCodedList.count(length) + length));
        int common = LongArrayFrontCodedList.readInt(array, pos + (long)LongArrayFrontCodedList.count(length));
        for (int i = 0; i < delta - 1; ++i) {
            length = LongArrayFrontCodedList.readInt(array, pos += (long)(LongArrayFrontCodedList.count(length) + LongArrayFrontCodedList.count(common) + length));
            common = LongArrayFrontCodedList.readInt(array, pos + (long)LongArrayFrontCodedList.count(length));
        }
        return length + common;
    }

    public int arrayLength(int index) {
        this.ensureRestrictedIndex(index);
        return this.length(index);
    }

    private int extract(int index, long[] a, int offset, int length) {
        long startPos;
        int delta = index % this.ratio;
        long pos = startPos = this.p[index / this.ratio];
        int arrayLength = LongArrayFrontCodedList.readInt(this.array, pos);
        int currLen = 0;
        if (delta == 0) {
            pos = this.p[index / this.ratio] + (long)LongArrayFrontCodedList.count(arrayLength);
            LongBigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
            return arrayLength;
        }
        int common = 0;
        for (int i = 0; i < delta; ++i) {
            long prevArrayPos = pos + (long)LongArrayFrontCodedList.count(arrayLength) + (long)(i != 0 ? LongArrayFrontCodedList.count(common) : 0);
            pos = prevArrayPos + (long)arrayLength;
            arrayLength = LongArrayFrontCodedList.readInt(this.array, pos);
            common = LongArrayFrontCodedList.readInt(this.array, pos + (long)LongArrayFrontCodedList.count(arrayLength));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
                currLen = actualCommon;
                continue;
            }
            LongBigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
            currLen = actualCommon;
        }
        if (currLen < length) {
            LongBigArrays.copyFromBig(this.array, pos + (long)LongArrayFrontCodedList.count(arrayLength) + (long)LongArrayFrontCodedList.count(common), a, currLen + offset, Math.min(arrayLength, length - currLen));
        }
        return arrayLength + common;
    }

    @Override
    public long[] get(int index) {
        return this.getArray(index);
    }

    public long[] getArray(int index) {
        this.ensureRestrictedIndex(index);
        int length = this.length(index);
        long[] a = new long[length];
        this.extract(index, a, 0, length);
        return a;
    }

    public int get(int index, long[] a, int offset, int length) {
        this.ensureRestrictedIndex(index);
        LongArrays.ensureOffsetLength(a, offset, length);
        int arrayLength = this.extract(index, a, offset, length);
        if (length >= arrayLength) {
            return arrayLength;
        }
        return length - arrayLength;
    }

    public int get(int index, long[] a) {
        return this.get(index, a, 0, a.length);
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public ObjectListIterator<long[]> listIterator(final int start) {
        this.ensureIndex(start);
        return new ObjectListIterator<long[]>(){
            long[] s = LongArrays.EMPTY_ARRAY;
            int i = 0;
            long pos = 0L;
            boolean inSync;
            {
                if (start != 0) {
                    if (start == LongArrayFrontCodedList.this.n) {
                        this.i = start;
                    } else {
                        this.pos = LongArrayFrontCodedList.this.p[start / LongArrayFrontCodedList.this.ratio];
                        int j = start % LongArrayFrontCodedList.this.ratio;
                        this.i = start - j;
                        while (j-- != 0) {
                            this.next();
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return this.i < LongArrayFrontCodedList.this.n;
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
            public long[] next() {
                int length;
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                if (this.i % LongArrayFrontCodedList.this.ratio == 0) {
                    this.pos = LongArrayFrontCodedList.this.p[this.i / LongArrayFrontCodedList.this.ratio];
                    length = LongArrayFrontCodedList.readInt(LongArrayFrontCodedList.this.array, this.pos);
                    this.s = LongArrays.ensureCapacity(this.s, length, 0);
                    LongBigArrays.copyFromBig(LongArrayFrontCodedList.this.array, this.pos + (long)LongArrayFrontCodedList.count(length), this.s, 0, length);
                    this.pos += (long)(length + LongArrayFrontCodedList.count(length));
                    this.inSync = true;
                } else if (this.inSync) {
                    length = LongArrayFrontCodedList.readInt(LongArrayFrontCodedList.this.array, this.pos);
                    int common = LongArrayFrontCodedList.readInt(LongArrayFrontCodedList.this.array, this.pos + (long)LongArrayFrontCodedList.count(length));
                    this.s = LongArrays.ensureCapacity(this.s, length + common, common);
                    LongBigArrays.copyFromBig(LongArrayFrontCodedList.this.array, this.pos + (long)LongArrayFrontCodedList.count(length) + (long)LongArrayFrontCodedList.count(common), this.s, common, length);
                    this.pos += (long)(LongArrayFrontCodedList.count(length) + LongArrayFrontCodedList.count(common) + length);
                    length += common;
                } else {
                    length = LongArrayFrontCodedList.this.length(this.i);
                    this.s = LongArrays.ensureCapacity(this.s, length, 0);
                    LongArrayFrontCodedList.this.extract(this.i, this.s, 0, length);
                }
                ++this.i;
                return LongArrays.copy(this.s, 0, length);
            }

            @Override
            public long[] previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.inSync = false;
                return LongArrayFrontCodedList.this.getArray(--this.i);
            }
        };
    }

    public LongArrayFrontCodedList clone() {
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
            s.append(LongArrayList.wrap(this.getArray(i)).toString());
        }
        s.append("]");
        return s.toString();
    }

    protected long[] rebuildPointerArray() {
        long[] p = new long[(this.n + this.ratio - 1) / this.ratio];
        long[][] a = this.array;
        long pos = 0L;
        int j = 0;
        int skip = this.ratio - 1;
        for (int i = 0; i < this.n; ++i) {
            int length = LongArrayFrontCodedList.readInt(a, pos);
            int count = LongArrayFrontCodedList.count(length);
            if (++skip == this.ratio) {
                skip = 0;
                p[j++] = pos;
                pos += (long)(count + length);
                continue;
            }
            pos += (long)(count + LongArrayFrontCodedList.count(LongArrayFrontCodedList.readInt(a, pos + (long)count)) + length);
        }
        return p;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.p = this.rebuildPointerArray();
    }

}

