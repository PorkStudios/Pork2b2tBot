/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
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

public class ByteArrayFrontCodedList
extends AbstractObjectList<byte[]>
implements Serializable,
Cloneable,
RandomAccess {
    private static final long serialVersionUID = 1L;
    protected final int n;
    protected final int ratio;
    protected final byte[][] array;
    protected transient long[] p;

    public ByteArrayFrontCodedList(Iterator<byte[]> arrays, int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
        }
        byte[][] array = ByteBigArrays.EMPTY_BIG_ARRAY;
        long[] p = LongArrays.EMPTY_ARRAY;
        byte[][] a = new byte[2][];
        long curSize = 0L;
        int n = 0;
        int b = 0;
        while (arrays.hasNext()) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % ratio == 0) {
                p = LongArrays.grow(p, n / ratio + 1);
                p[n / ratio] = curSize;
                array = ByteBigArrays.grow(array, curSize + (long)ByteArrayFrontCodedList.count(length) + (long)length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, length, curSize);
                ByteBigArrays.copyToBig(a[b], 0, array, curSize, length);
                curSize += (long)length;
            } else {
                int common;
                int minLength = a[1 - b].length;
                if (length < minLength) {
                    minLength = length;
                }
                for (common = 0; common < minLength && a[0][common] == a[1][common]; ++common) {
                }
                array = ByteBigArrays.grow(array, curSize + (long)ByteArrayFrontCodedList.count(length) + (long)ByteArrayFrontCodedList.count(common) + (long)(length -= common), curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, common, curSize);
                ByteBigArrays.copyToBig(a[b], common, array, curSize, length);
                curSize += (long)length;
            }
            b = 1 - b;
            ++n;
        }
        this.n = n;
        this.ratio = ratio;
        this.array = ByteBigArrays.trim(array, curSize);
        this.p = LongArrays.trim(p, (n + ratio - 1) / ratio);
    }

    public ByteArrayFrontCodedList(Collection<byte[]> c, int ratio) {
        this(c.iterator(), ratio);
    }

    private static int readInt(byte[][] a, long pos) {
        byte b0 = ByteBigArrays.get(a, pos);
        if (b0 >= 0) {
            return b0;
        }
        byte b1 = ByteBigArrays.get(a, pos + 1L);
        if (b1 >= 0) {
            return - b0 - 1 << 7 | b1;
        }
        byte b2 = ByteBigArrays.get(a, pos + 2L);
        if (b2 >= 0) {
            return - b0 - 1 << 14 | - b1 - 1 << 7 | b2;
        }
        byte b3 = ByteBigArrays.get(a, pos + 3L);
        if (b3 >= 0) {
            return - b0 - 1 << 21 | - b1 - 1 << 14 | - b2 - 1 << 7 | b3;
        }
        return - b0 - 1 << 28 | - b1 - 1 << 21 | - b2 - 1 << 14 | - b3 - 1 << 7 | ByteBigArrays.get(a, pos + 4L);
    }

    private static int count(int length) {
        if (length < 128) {
            return 1;
        }
        if (length < 16384) {
            return 2;
        }
        if (length < 2097152) {
            return 3;
        }
        if (length < 268435456) {
            return 4;
        }
        return 5;
    }

    private static int writeInt(byte[][] a, int length, long pos) {
        int count = ByteArrayFrontCodedList.count(length);
        ByteBigArrays.set(a, pos + (long)count - 1L, (byte)(length & 127));
        if (count != 1) {
            int i = count - 1;
            while (i-- != 0) {
                ByteBigArrays.set(a, pos + (long)i, (byte)(- ((length >>>= 7) & 127) - 1));
            }
        }
        return count;
    }

    public int ratio() {
        return this.ratio;
    }

    private int length(int index) {
        byte[][] array = this.array;
        int delta = index % this.ratio;
        long pos = this.p[index / this.ratio];
        int length = ByteArrayFrontCodedList.readInt(array, pos);
        if (delta == 0) {
            return length;
        }
        length = ByteArrayFrontCodedList.readInt(array, pos += (long)(ByteArrayFrontCodedList.count(length) + length));
        int common = ByteArrayFrontCodedList.readInt(array, pos + (long)ByteArrayFrontCodedList.count(length));
        for (int i = 0; i < delta - 1; ++i) {
            length = ByteArrayFrontCodedList.readInt(array, pos += (long)(ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length));
            common = ByteArrayFrontCodedList.readInt(array, pos + (long)ByteArrayFrontCodedList.count(length));
        }
        return length + common;
    }

    public int arrayLength(int index) {
        this.ensureRestrictedIndex(index);
        return this.length(index);
    }

    private int extract(int index, byte[] a, int offset, int length) {
        long startPos;
        int delta = index % this.ratio;
        long pos = startPos = this.p[index / this.ratio];
        int arrayLength = ByteArrayFrontCodedList.readInt(this.array, pos);
        int currLen = 0;
        if (delta == 0) {
            pos = this.p[index / this.ratio] + (long)ByteArrayFrontCodedList.count(arrayLength);
            ByteBigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
            return arrayLength;
        }
        int common = 0;
        for (int i = 0; i < delta; ++i) {
            long prevArrayPos = pos + (long)ByteArrayFrontCodedList.count(arrayLength) + (long)(i != 0 ? ByteArrayFrontCodedList.count(common) : 0);
            pos = prevArrayPos + (long)arrayLength;
            arrayLength = ByteArrayFrontCodedList.readInt(this.array, pos);
            common = ByteArrayFrontCodedList.readInt(this.array, pos + (long)ByteArrayFrontCodedList.count(arrayLength));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
                currLen = actualCommon;
                continue;
            }
            ByteBigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
            currLen = actualCommon;
        }
        if (currLen < length) {
            ByteBigArrays.copyFromBig(this.array, pos + (long)ByteArrayFrontCodedList.count(arrayLength) + (long)ByteArrayFrontCodedList.count(common), a, currLen + offset, Math.min(arrayLength, length - currLen));
        }
        return arrayLength + common;
    }

    @Override
    public byte[] get(int index) {
        return this.getArray(index);
    }

    public byte[] getArray(int index) {
        this.ensureRestrictedIndex(index);
        int length = this.length(index);
        byte[] a = new byte[length];
        this.extract(index, a, 0, length);
        return a;
    }

    public int get(int index, byte[] a, int offset, int length) {
        this.ensureRestrictedIndex(index);
        ByteArrays.ensureOffsetLength(a, offset, length);
        int arrayLength = this.extract(index, a, offset, length);
        if (length >= arrayLength) {
            return arrayLength;
        }
        return length - arrayLength;
    }

    public int get(int index, byte[] a) {
        return this.get(index, a, 0, a.length);
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public ObjectListIterator<byte[]> listIterator(final int start) {
        this.ensureIndex(start);
        return new ObjectListIterator<byte[]>(){
            byte[] s = ByteArrays.EMPTY_ARRAY;
            int i = 0;
            long pos = 0L;
            boolean inSync;
            {
                if (start != 0) {
                    if (start == ByteArrayFrontCodedList.this.n) {
                        this.i = start;
                    } else {
                        this.pos = ByteArrayFrontCodedList.this.p[start / ByteArrayFrontCodedList.this.ratio];
                        int j = start % ByteArrayFrontCodedList.this.ratio;
                        this.i = start - j;
                        while (j-- != 0) {
                            this.next();
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return this.i < ByteArrayFrontCodedList.this.n;
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
            public byte[] next() {
                int length;
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                if (this.i % ByteArrayFrontCodedList.this.ratio == 0) {
                    this.pos = ByteArrayFrontCodedList.this.p[this.i / ByteArrayFrontCodedList.this.ratio];
                    length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedList.this.array, this.pos);
                    this.s = ByteArrays.ensureCapacity(this.s, length, 0);
                    ByteBigArrays.copyFromBig(ByteArrayFrontCodedList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length), this.s, 0, length);
                    this.pos += (long)(length + ByteArrayFrontCodedList.count(length));
                    this.inSync = true;
                } else if (this.inSync) {
                    length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedList.this.array, this.pos);
                    int common = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length));
                    this.s = ByteArrays.ensureCapacity(this.s, length + common, common);
                    ByteBigArrays.copyFromBig(ByteArrayFrontCodedList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length) + (long)ByteArrayFrontCodedList.count(common), this.s, common, length);
                    this.pos += (long)(ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length);
                    length += common;
                } else {
                    length = ByteArrayFrontCodedList.this.length(this.i);
                    this.s = ByteArrays.ensureCapacity(this.s, length, 0);
                    ByteArrayFrontCodedList.this.extract(this.i, this.s, 0, length);
                }
                ++this.i;
                return ByteArrays.copy(this.s, 0, length);
            }

            @Override
            public byte[] previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.inSync = false;
                return ByteArrayFrontCodedList.this.getArray(--this.i);
            }
        };
    }

    public ByteArrayFrontCodedList clone() {
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
            s.append(ByteArrayList.wrap(this.getArray(i)).toString());
        }
        s.append("]");
        return s.toString();
    }

    protected long[] rebuildPointerArray() {
        long[] p = new long[(this.n + this.ratio - 1) / this.ratio];
        byte[][] a = this.array;
        long pos = 0L;
        int j = 0;
        int skip = this.ratio - 1;
        for (int i = 0; i < this.n; ++i) {
            int length = ByteArrayFrontCodedList.readInt(a, pos);
            int count = ByteArrayFrontCodedList.count(length);
            if (++skip == this.ratio) {
                skip = 0;
                p[j++] = pos;
                pos += (long)(count + length);
                continue;
            }
            pos += (long)(count + ByteArrayFrontCodedList.count(ByteArrayFrontCodedList.readInt(a, pos + (long)count)) + length);
        }
        return p;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.p = this.rebuildPointerArray();
    }

}

