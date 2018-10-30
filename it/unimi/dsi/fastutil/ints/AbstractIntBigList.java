/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.ints.IntBigListIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractIntBigList
extends AbstractIntCollection
implements IntBigList,
IntStack {
    protected AbstractIntBigList() {
    }

    protected void ensureIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
        }
    }

    protected void ensureRestrictedIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size64() + ")");
        }
    }

    @Override
    public void add(long index, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(int k) {
        this.add(this.size64(), k);
        return true;
    }

    @Override
    public int removeInt(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int set(long index, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(long index, Collection<? extends Integer> c) {
        this.ensureIndex(index);
        Iterator<? extends Integer> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    public IntBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public IntBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public IntBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new IntBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractIntBigList.this.size64();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public int nextInt() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractIntBigList.this.getInt(this.last);
            }

            @Override
            public int previousInt() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractIntBigList.this.getInt(this.pos);
            }

            @Override
            public long nextIndex() {
                return this.pos;
            }

            @Override
            public long previousIndex() {
                return this.pos - 1L;
            }

            @Override
            public void add(int k) {
                AbstractIntBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(int k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractIntBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractIntBigList.this.removeInt(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    @Override
    public boolean contains(int k) {
        return this.indexOf(k) >= 0L;
    }

    @Override
    public long indexOf(int k) {
        IntBigListIterator i = this.listIterator();
        while (i.hasNext()) {
            int e = i.nextInt();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(int k) {
        IntBigListIterator i = this.listIterator(this.size64());
        while (i.hasPrevious()) {
            int e = i.previousInt();
            if (k != e) continue;
            return i.nextIndex();
        }
        return -1L;
    }

    @Override
    public void size(long size) {
        long i = this.size64();
        if (size > i) {
            while (i++ < size) {
                this.add(0);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public IntBigList subList(long from, long to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new IntSubList(this, from, to);
    }

    @Override
    public void removeElements(long from, long to) {
        this.ensureIndex(to);
        IntBigListIterator i = this.listIterator(from);
        long n = to - from;
        if (n < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0L) {
            i.nextInt();
            i.remove();
        }
    }

    @Override
    public void addElements(long index, int[][] a, long offset, long length) {
        this.ensureIndex(index);
        IntBigArrays.ensureOffsetLength(a, offset, length);
        while (length-- != 0L) {
            this.add(index++, IntBigArrays.get(a, offset++));
        }
    }

    @Override
    public void addElements(long index, int[][] a) {
        this.addElements(index, a, 0L, IntBigArrays.length(a));
    }

    @Override
    public void getElements(long from, int[][] a, long offset, long length) {
        IntBigListIterator i = this.listIterator(from);
        IntBigArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        while (length-- != 0L) {
            IntBigArrays.set(a, offset++, i.nextInt());
        }
    }

    @Override
    public void clear() {
        this.removeElements(0L, this.size64());
    }

    @Deprecated
    @Override
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size64());
    }

    private boolean valEquals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public int hashCode() {
        IntBigListIterator i = this.iterator();
        int h = 1;
        long s = this.size64();
        while (s-- != 0L) {
            int k = i.nextInt();
            h = 31 * h + k;
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BigList)) {
            return false;
        }
        BigList l = (BigList)o;
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        if (l instanceof IntBigList) {
            IntBigListIterator i1 = this.listIterator();
            IntBigListIterator i2 = ((IntBigList)l).listIterator();
            while (s-- != 0L) {
                if (i1.nextInt() == i2.nextInt()) continue;
                return false;
            }
            return true;
        }
        IntBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l.listIterator();
        while (s-- != 0L) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Integer> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof IntBigList) {
            IntBigListIterator i1 = this.listIterator();
            IntBigListIterator i2 = ((IntBigList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                int e2;
                int e1 = i1.nextInt();
                int r = Integer.compare(e1, e2 = i2.nextInt());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        IntBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Integer> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(int o) {
        this.add(o);
    }

    @Override
    public int popInt() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeInt(this.size64() - 1L);
    }

    @Override
    public int topInt() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getInt(this.size64() - 1L);
    }

    @Override
    public int peekInt(int i) {
        return this.getInt(this.size64() - 1L - (long)i);
    }

    @Override
    public boolean rem(int k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeInt(index);
        return true;
    }

    @Override
    public boolean addAll(long index, IntCollection c) {
        return this.addAll(index, (Collection<? extends Integer>)c);
    }

    @Override
    public boolean addAll(long index, IntBigList l) {
        return this.addAll(index, (IntCollection)l);
    }

    @Override
    public boolean addAll(IntCollection c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    public boolean addAll(IntBigList l) {
        return this.addAll(this.size64(), l);
    }

    @Deprecated
    @Override
    public void add(long index, Integer ok) {
        this.add(index, (int)ok);
    }

    @Deprecated
    @Override
    public Integer set(long index, Integer ok) {
        return this.set(index, (int)ok);
    }

    @Deprecated
    @Override
    public Integer get(long index) {
        return this.getInt(index);
    }

    @Deprecated
    @Override
    public long indexOf(Object ok) {
        return this.indexOf((Integer)ok);
    }

    @Deprecated
    @Override
    public long lastIndexOf(Object ok) {
        return this.lastIndexOf((Integer)ok);
    }

    @Deprecated
    @Override
    public Integer remove(long index) {
        return this.removeInt(index);
    }

    @Deprecated
    @Override
    public void push(Integer o) {
        this.push((int)o);
    }

    @Deprecated
    @Override
    public Integer pop() {
        return this.popInt();
    }

    @Deprecated
    @Override
    public Integer top() {
        return this.topInt();
    }

    @Deprecated
    @Override
    public Integer peek(int i) {
        return this.peekInt(i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        IntBigListIterator i = this.iterator();
        long n = this.size64();
        boolean first = true;
        s.append("[");
        while (n-- != 0L) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            int k = i.nextInt();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class IntSubList
    extends AbstractIntBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntBigList l;
        protected final long from;
        protected long to;

        public IntSubList(IntBigList l, long from, long to) {
            this.l = l;
            this.from = from;
            this.to = to;
        }

        private boolean assertRange() {
            assert (this.from <= this.l.size64());
            assert (this.to <= this.l.size64());
            assert (this.to >= this.from);
            return true;
        }

        @Override
        public boolean add(int k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(long index, int k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(long index, Collection<? extends Integer> c) {
            this.ensureIndex(index);
            this.to += (long)c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public int getInt(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getInt(this.from + index);
        }

        @Override
        public int removeInt(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeInt(this.from + index);
        }

        @Override
        public int set(long index, int k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public long size64() {
            return this.to - this.from;
        }

        @Override
        public void getElements(long from, int[][] a, long offset, long length) {
            this.ensureIndex(from);
            if (from + length > this.size64()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size64() + ")");
            }
            this.l.getElements(this.from + from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            this.l.removeElements(this.from + from, this.from + to);
            this.to -= to - from;
            assert (this.assertRange());
        }

        @Override
        public void addElements(long index, int[][] a, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public IntBigListIterator listIterator(final long index) {
            this.ensureIndex(index);
            return new IntBigListIterator(){
                long pos;
                long last;
                {
                    this.pos = index;
                    this.last = -1L;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < this.size64();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0L;
                }

                @Override
                public int nextInt() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return this.l.getInt(this.from + this.last);
                }

                @Override
                public int previousInt() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return this.l.getInt(this.from + this.pos);
                }

                @Override
                public long nextIndex() {
                    return this.pos;
                }

                @Override
                public long previousIndex() {
                    return this.pos - 1L;
                }

                @Override
                public void add(int k) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    this.add(this.pos++, k);
                    this.last = -1L;
                    assert (this.assertRange());
                }

                @Override
                public void set(int k) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    this.set(this.last, k);
                }

                @Override
                public void remove() {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    this.removeInt(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1L;
                    assert (this.assertRange());
                }
            };
        }

        @Override
        public IntBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new IntSubList(this, from, to);
        }

        @Override
        public boolean rem(int k) {
            long index = this.indexOf(k);
            if (index == -1L) {
                return false;
            }
            --this.to;
            this.l.removeInt(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(long index, IntCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(long index, IntBigList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

    }

}

