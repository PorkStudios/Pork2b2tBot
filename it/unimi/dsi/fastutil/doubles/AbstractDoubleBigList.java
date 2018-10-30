/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrays;
import it.unimi.dsi.fastutil.doubles.DoubleBigList;
import it.unimi.dsi.fastutil.doubles.DoubleBigListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractDoubleBigList
extends AbstractDoubleCollection
implements DoubleBigList,
DoubleStack {
    protected AbstractDoubleBigList() {
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
    public void add(long index, double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(double k) {
        this.add(this.size64(), k);
        return true;
    }

    @Override
    public double removeDouble(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double set(long index, double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(long index, Collection<? extends Double> c) {
        this.ensureIndex(index);
        Iterator<? extends Double> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Double> c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    public DoubleBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public DoubleBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public DoubleBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new DoubleBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractDoubleBigList.this.size64();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractDoubleBigList.this.getDouble(this.last);
            }

            @Override
            public double previousDouble() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractDoubleBigList.this.getDouble(this.pos);
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
            public void add(double k) {
                AbstractDoubleBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(double k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractDoubleBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractDoubleBigList.this.removeDouble(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    @Override
    public boolean contains(double k) {
        return this.indexOf(k) >= 0L;
    }

    @Override
    public long indexOf(double k) {
        DoubleBigListIterator i = this.listIterator();
        while (i.hasNext()) {
            double e = i.nextDouble();
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(e)) continue;
            return i.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(double k) {
        DoubleBigListIterator i = this.listIterator(this.size64());
        while (i.hasPrevious()) {
            double e = i.previousDouble();
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(e)) continue;
            return i.nextIndex();
        }
        return -1L;
    }

    @Override
    public void size(long size) {
        long i = this.size64();
        if (size > i) {
            while (i++ < size) {
                this.add(0.0);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public DoubleBigList subList(long from, long to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new DoubleSubList(this, from, to);
    }

    @Override
    public void removeElements(long from, long to) {
        this.ensureIndex(to);
        DoubleBigListIterator i = this.listIterator(from);
        long n = to - from;
        if (n < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0L) {
            i.nextDouble();
            i.remove();
        }
    }

    @Override
    public void addElements(long index, double[][] a, long offset, long length) {
        this.ensureIndex(index);
        DoubleBigArrays.ensureOffsetLength(a, offset, length);
        while (length-- != 0L) {
            this.add(index++, DoubleBigArrays.get(a, offset++));
        }
    }

    @Override
    public void addElements(long index, double[][] a) {
        this.addElements(index, a, 0L, DoubleBigArrays.length(a));
    }

    @Override
    public void getElements(long from, double[][] a, long offset, long length) {
        DoubleBigListIterator i = this.listIterator(from);
        DoubleBigArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        while (length-- != 0L) {
            DoubleBigArrays.set(a, offset++, i.nextDouble());
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
        DoubleBigListIterator i = this.iterator();
        int h = 1;
        long s = this.size64();
        while (s-- != 0L) {
            double k = i.nextDouble();
            h = 31 * h + HashCommon.double2int(k);
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
        if (l instanceof DoubleBigList) {
            DoubleBigListIterator i1 = this.listIterator();
            DoubleBigListIterator i2 = ((DoubleBigList)l).listIterator();
            while (s-- != 0L) {
                if (i1.nextDouble() == i2.nextDouble()) continue;
                return false;
            }
            return true;
        }
        DoubleBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l.listIterator();
        while (s-- != 0L) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Double> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof DoubleBigList) {
            DoubleBigListIterator i1 = this.listIterator();
            DoubleBigListIterator i2 = ((DoubleBigList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                double e2;
                double e1 = i1.nextDouble();
                int r = Double.compare(e1, e2 = i2.nextDouble());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        DoubleBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Double> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(double o) {
        this.add(o);
    }

    @Override
    public double popDouble() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeDouble(this.size64() - 1L);
    }

    @Override
    public double topDouble() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getDouble(this.size64() - 1L);
    }

    @Override
    public double peekDouble(int i) {
        return this.getDouble(this.size64() - 1L - (long)i);
    }

    @Override
    public boolean rem(double k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeDouble(index);
        return true;
    }

    @Override
    public boolean addAll(long index, DoubleCollection c) {
        return this.addAll(index, (Collection<? extends Double>)c);
    }

    @Override
    public boolean addAll(long index, DoubleBigList l) {
        return this.addAll(index, (DoubleCollection)l);
    }

    @Override
    public boolean addAll(DoubleCollection c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    public boolean addAll(DoubleBigList l) {
        return this.addAll(this.size64(), l);
    }

    @Deprecated
    @Override
    public void add(long index, Double ok) {
        this.add(index, (double)ok);
    }

    @Deprecated
    @Override
    public Double set(long index, Double ok) {
        return this.set(index, (double)ok);
    }

    @Deprecated
    @Override
    public Double get(long index) {
        return this.getDouble(index);
    }

    @Deprecated
    @Override
    public long indexOf(Object ok) {
        return this.indexOf((Double)ok);
    }

    @Deprecated
    @Override
    public long lastIndexOf(Object ok) {
        return this.lastIndexOf((Double)ok);
    }

    @Deprecated
    @Override
    public Double remove(long index) {
        return this.removeDouble(index);
    }

    @Deprecated
    @Override
    public void push(Double o) {
        this.push((double)o);
    }

    @Deprecated
    @Override
    public Double pop() {
        return this.popDouble();
    }

    @Deprecated
    @Override
    public Double top() {
        return this.topDouble();
    }

    @Deprecated
    @Override
    public Double peek(int i) {
        return this.peekDouble(i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        DoubleBigListIterator i = this.iterator();
        long n = this.size64();
        boolean first = true;
        s.append("[");
        while (n-- != 0L) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            double k = i.nextDouble();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class DoubleSubList
    extends AbstractDoubleBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleBigList l;
        protected final long from;
        protected long to;

        public DoubleSubList(DoubleBigList l, long from, long to) {
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
        public boolean add(double k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(long index, double k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(long index, Collection<? extends Double> c) {
            this.ensureIndex(index);
            this.to += (long)c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public double getDouble(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getDouble(this.from + index);
        }

        @Override
        public double removeDouble(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeDouble(this.from + index);
        }

        @Override
        public double set(long index, double k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public long size64() {
            return this.to - this.from;
        }

        @Override
        public void getElements(long from, double[][] a, long offset, long length) {
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
        public void addElements(long index, double[][] a, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public DoubleBigListIterator listIterator(final long index) {
            this.ensureIndex(index);
            return new DoubleBigListIterator(){
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
                public double nextDouble() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return this.l.getDouble(this.from + this.last);
                }

                @Override
                public double previousDouble() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return this.l.getDouble(this.from + this.pos);
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
                public void add(double k) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    this.add(this.pos++, k);
                    this.last = -1L;
                    assert (this.assertRange());
                }

                @Override
                public void set(double k) {
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
                    this.removeDouble(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1L;
                    assert (this.assertRange());
                }
            };
        }

        @Override
        public DoubleBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new DoubleSubList(this, from, to);
        }

        @Override
        public boolean rem(double k) {
            long index = this.indexOf(k);
            if (index == -1L) {
                return false;
            }
            --this.to;
            this.l.removeDouble(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(long index, DoubleCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(long index, DoubleBigList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

    }

}

