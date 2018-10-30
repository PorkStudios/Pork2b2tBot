/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractDoubleList
extends AbstractDoubleCollection
implements DoubleList,
DoubleStack {
    protected AbstractDoubleList() {
    }

    protected void ensureIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
    }

    protected void ensureRestrictedIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size() + ")");
        }
    }

    @Override
    public void add(int index, double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(double k) {
        this.add(this.size(), k);
        return true;
    }

    @Override
    public double removeDouble(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double set(int index, double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Double> c) {
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
        return this.addAll(this.size(), c);
    }

    @Override
    public DoubleListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public DoubleListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public DoubleListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new DoubleListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractDoubleList.this.size();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractDoubleList.this.getDouble(this.last);
            }

            @Override
            public double previousDouble() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractDoubleList.this.getDouble(this.pos);
            }

            @Override
            public int nextIndex() {
                return this.pos;
            }

            @Override
            public int previousIndex() {
                return this.pos - 1;
            }

            @Override
            public void add(double k) {
                AbstractDoubleList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(double k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractDoubleList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractDoubleList.this.removeDouble(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    @Override
    public boolean contains(double k) {
        return this.indexOf(k) >= 0;
    }

    @Override
    public int indexOf(double k) {
        DoubleListIterator i = this.listIterator();
        while (i.hasNext()) {
            double e = i.nextDouble();
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(e)) continue;
            return i.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(double k) {
        DoubleListIterator i = this.listIterator(this.size());
        while (i.hasPrevious()) {
            double e = i.previousDouble();
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(e)) continue;
            return i.nextIndex();
        }
        return -1;
    }

    @Override
    public void size(int size) {
        int i = this.size();
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
    public DoubleList subList(int from, int to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new DoubleSubList(this, from, to);
    }

    @Override
    public void removeElements(int from, int to) {
        this.ensureIndex(to);
        DoubleListIterator i = this.listIterator(from);
        int n = to - from;
        if (n < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0) {
            i.nextDouble();
            i.remove();
        }
    }

    @Override
    public void addElements(int index, double[] a, int offset, int length) {
        this.ensureIndex(index);
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (offset + length > a.length) {
            throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
        }
        while (length-- != 0) {
            this.add(index++, a[offset++]);
        }
    }

    @Override
    public void addElements(int index, double[] a) {
        this.addElements(index, a, 0, a.length);
    }

    @Override
    public void getElements(int from, double[] a, int offset, int length) {
        DoubleListIterator i = this.listIterator(from);
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (offset + length > a.length) {
            throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
        }
        if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
        }
        while (length-- != 0) {
            a[offset++] = i.nextDouble();
        }
    }

    @Override
    public void clear() {
        this.removeElements(0, this.size());
    }

    private boolean valEquals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public int hashCode() {
        DoubleListIterator i = this.iterator();
        int h = 1;
        int s = this.size();
        while (s-- != 0) {
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
        if (!(o instanceof List)) {
            return false;
        }
        List l = (List)o;
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        if (l instanceof DoubleList) {
            DoubleListIterator i1 = this.listIterator();
            DoubleListIterator i2 = ((DoubleList)l).listIterator();
            while (s-- != 0) {
                if (i1.nextDouble() == i2.nextDouble()) continue;
                return false;
            }
            return true;
        }
        DoubleListIterator i1 = this.listIterator();
        ListIterator i2 = l.listIterator();
        while (s-- != 0) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Double> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof DoubleList) {
            DoubleListIterator i1 = this.listIterator();
            DoubleListIterator i2 = ((DoubleList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                double e2;
                double e1 = i1.nextDouble();
                int r = Double.compare(e1, e2 = i2.nextDouble());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        DoubleListIterator i1 = this.listIterator();
        ListIterator<? extends Double> i2 = l.listIterator();
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
        return this.removeDouble(this.size() - 1);
    }

    @Override
    public double topDouble() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getDouble(this.size() - 1);
    }

    @Override
    public double peekDouble(int i) {
        return this.getDouble(this.size() - 1 - i);
    }

    @Override
    public boolean rem(double k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeDouble(index);
        return true;
    }

    @Override
    public boolean addAll(int index, DoubleCollection c) {
        this.ensureIndex(index);
        DoubleIterator i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.nextDouble());
        }
        return retVal;
    }

    @Override
    public boolean addAll(int index, DoubleList l) {
        return this.addAll(index, (DoubleCollection)l);
    }

    @Override
    public boolean addAll(DoubleCollection c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public boolean addAll(DoubleList l) {
        return this.addAll(this.size(), l);
    }

    @Deprecated
    @Override
    public void add(int index, Double ok) {
        this.add(index, (double)ok);
    }

    @Deprecated
    @Override
    public Double set(int index, Double ok) {
        return this.set(index, (double)ok);
    }

    @Deprecated
    @Override
    public Double get(int index) {
        return this.getDouble(index);
    }

    @Deprecated
    @Override
    public int indexOf(Object ok) {
        return this.indexOf((Double)ok);
    }

    @Deprecated
    @Override
    public int lastIndexOf(Object ok) {
        return this.lastIndexOf((Double)ok);
    }

    @Deprecated
    @Override
    public Double remove(int index) {
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
        DoubleListIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("[");
        while (n-- != 0) {
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
    extends AbstractDoubleList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleList l;
        protected final int from;
        protected int to;

        public DoubleSubList(DoubleList l, int from, int to) {
            this.l = l;
            this.from = from;
            this.to = to;
        }

        private boolean assertRange() {
            assert (this.from <= this.l.size());
            assert (this.to <= this.l.size());
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
        public void add(int index, double k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(int index, Collection<? extends Double> c) {
            this.ensureIndex(index);
            this.to += c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public double getDouble(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getDouble(this.from + index);
        }

        @Override
        public double removeDouble(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeDouble(this.from + index);
        }

        @Override
        public double set(int index, double k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, double[] a, int offset, int length) {
            this.ensureIndex(from);
            if (from + length > this.size()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size() + ")");
            }
            this.l.getElements(this.from + from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            this.l.removeElements(this.from + from, this.from + to);
            this.to -= to - from;
            assert (this.assertRange());
        }

        @Override
        public void addElements(int index, double[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public DoubleListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new DoubleListIterator(){
                int pos;
                int last;
                {
                    this.pos = index;
                    this.last = -1;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < this.size();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0;
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
                public int nextIndex() {
                    return this.pos;
                }

                @Override
                public int previousIndex() {
                    return this.pos - 1;
                }

                @Override
                public void add(double k) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    this.add(this.pos++, k);
                    this.last = -1;
                    assert (this.assertRange());
                }

                @Override
                public void set(double k) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    this.set(this.last, k);
                }

                @Override
                public void remove() {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    this.removeDouble(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1;
                    assert (this.assertRange());
                }
            };
        }

        @Override
        public DoubleList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new DoubleSubList(this, from, to);
        }

        @Override
        public boolean rem(double k) {
            int index = this.indexOf(k);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeDouble(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(int index, DoubleCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(int index, DoubleList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

    }

}

