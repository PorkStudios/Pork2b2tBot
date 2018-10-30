/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractBooleanList
extends AbstractBooleanCollection
implements BooleanList,
BooleanStack {
    protected AbstractBooleanList() {
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
    public void add(int index, boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(boolean k) {
        this.add(this.size(), k);
        return true;
    }

    @Override
    public boolean removeBoolean(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean set(int index, boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Boolean> c) {
        this.ensureIndex(index);
        Iterator<? extends Boolean> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public BooleanListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public BooleanListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public BooleanListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new BooleanListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractBooleanList.this.size();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public boolean nextBoolean() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractBooleanList.this.getBoolean(this.last);
            }

            @Override
            public boolean previousBoolean() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractBooleanList.this.getBoolean(this.pos);
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
            public void add(boolean k) {
                AbstractBooleanList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(boolean k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractBooleanList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractBooleanList.this.removeBoolean(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    @Override
    public boolean contains(boolean k) {
        return this.indexOf(k) >= 0;
    }

    @Override
    public int indexOf(boolean k) {
        BooleanListIterator i = this.listIterator();
        while (i.hasNext()) {
            boolean e = i.nextBoolean();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(boolean k) {
        BooleanListIterator i = this.listIterator(this.size());
        while (i.hasPrevious()) {
            boolean e = i.previousBoolean();
            if (k != e) continue;
            return i.nextIndex();
        }
        return -1;
    }

    @Override
    public void size(int size) {
        int i = this.size();
        if (size > i) {
            while (i++ < size) {
                this.add(false);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public BooleanList subList(int from, int to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new BooleanSubList(this, from, to);
    }

    @Override
    public void removeElements(int from, int to) {
        this.ensureIndex(to);
        BooleanListIterator i = this.listIterator(from);
        int n = to - from;
        if (n < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0) {
            i.nextBoolean();
            i.remove();
        }
    }

    @Override
    public void addElements(int index, boolean[] a, int offset, int length) {
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
    public void addElements(int index, boolean[] a) {
        this.addElements(index, a, 0, a.length);
    }

    @Override
    public void getElements(int from, boolean[] a, int offset, int length) {
        BooleanListIterator i = this.listIterator(from);
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
            a[offset++] = i.nextBoolean();
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
        BooleanListIterator i = this.iterator();
        int h = 1;
        int s = this.size();
        while (s-- != 0) {
            boolean k = i.nextBoolean();
            h = 31 * h + (k ? 1231 : 1237);
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
        if (l instanceof BooleanList) {
            BooleanListIterator i1 = this.listIterator();
            BooleanListIterator i2 = ((BooleanList)l).listIterator();
            while (s-- != 0) {
                if (i1.nextBoolean() == i2.nextBoolean()) continue;
                return false;
            }
            return true;
        }
        BooleanListIterator i1 = this.listIterator();
        ListIterator i2 = l.listIterator();
        while (s-- != 0) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Boolean> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof BooleanList) {
            BooleanListIterator i1 = this.listIterator();
            BooleanListIterator i2 = ((BooleanList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                boolean e2;
                boolean e1 = i1.nextBoolean();
                int r = Boolean.compare(e1, e2 = i2.nextBoolean());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        BooleanListIterator i1 = this.listIterator();
        ListIterator<? extends Boolean> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(boolean o) {
        this.add(o);
    }

    @Override
    public boolean popBoolean() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeBoolean(this.size() - 1);
    }

    @Override
    public boolean topBoolean() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getBoolean(this.size() - 1);
    }

    @Override
    public boolean peekBoolean(int i) {
        return this.getBoolean(this.size() - 1 - i);
    }

    @Override
    public boolean rem(boolean k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeBoolean(index);
        return true;
    }

    @Override
    public boolean addAll(int index, BooleanCollection c) {
        this.ensureIndex(index);
        BooleanIterator i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.nextBoolean());
        }
        return retVal;
    }

    @Override
    public boolean addAll(int index, BooleanList l) {
        return this.addAll(index, (BooleanCollection)l);
    }

    @Override
    public boolean addAll(BooleanCollection c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public boolean addAll(BooleanList l) {
        return this.addAll(this.size(), l);
    }

    @Deprecated
    @Override
    public void add(int index, Boolean ok) {
        this.add(index, (boolean)ok);
    }

    @Deprecated
    @Override
    public Boolean set(int index, Boolean ok) {
        return this.set(index, (boolean)ok);
    }

    @Deprecated
    @Override
    public Boolean get(int index) {
        return this.getBoolean(index);
    }

    @Deprecated
    @Override
    public int indexOf(Object ok) {
        return this.indexOf((Boolean)ok);
    }

    @Deprecated
    @Override
    public int lastIndexOf(Object ok) {
        return this.lastIndexOf((Boolean)ok);
    }

    @Deprecated
    @Override
    public Boolean remove(int index) {
        return this.removeBoolean(index);
    }

    @Deprecated
    @Override
    public void push(Boolean o) {
        this.push((boolean)o);
    }

    @Deprecated
    @Override
    public Boolean pop() {
        return this.popBoolean();
    }

    @Deprecated
    @Override
    public Boolean top() {
        return this.topBoolean();
    }

    @Deprecated
    @Override
    public Boolean peek(int i) {
        return this.peekBoolean(i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        BooleanListIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("[");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            boolean k = i.nextBoolean();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class BooleanSubList
    extends AbstractBooleanList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanList l;
        protected final int from;
        protected int to;

        public BooleanSubList(BooleanList l, int from, int to) {
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
        public boolean add(boolean k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(int index, boolean k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(int index, Collection<? extends Boolean> c) {
            this.ensureIndex(index);
            this.to += c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public boolean getBoolean(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getBoolean(this.from + index);
        }

        @Override
        public boolean removeBoolean(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeBoolean(this.from + index);
        }

        @Override
        public boolean set(int index, boolean k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, boolean[] a, int offset, int length) {
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
        public void addElements(int index, boolean[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public BooleanListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new BooleanListIterator(){
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
                public boolean nextBoolean() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return this.l.getBoolean(this.from + this.last);
                }

                @Override
                public boolean previousBoolean() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return this.l.getBoolean(this.from + this.pos);
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
                public void add(boolean k) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    this.add(this.pos++, k);
                    this.last = -1;
                    assert (this.assertRange());
                }

                @Override
                public void set(boolean k) {
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
                    this.removeBoolean(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1;
                    assert (this.assertRange());
                }
            };
        }

        @Override
        public BooleanList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new BooleanSubList(this, from, to);
        }

        @Override
        public boolean rem(boolean k) {
            int index = this.indexOf(k);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeBoolean(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(int index, BooleanCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(int index, BooleanList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

    }

}

