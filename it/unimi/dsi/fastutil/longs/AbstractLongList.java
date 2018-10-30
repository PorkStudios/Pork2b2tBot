/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractLongList
extends AbstractLongCollection
implements LongList,
LongStack {
    protected AbstractLongList() {
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
    public void add(int index, long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(long k) {
        this.add(this.size(), k);
        return true;
    }

    @Override
    public long removeLong(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long set(int index, long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Long> c) {
        this.ensureIndex(index);
        Iterator<? extends Long> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Long> c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public LongListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public LongListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public LongListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new LongListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractLongList.this.size();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractLongList.this.getLong(this.last);
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractLongList.this.getLong(this.pos);
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
            public void add(long k) {
                AbstractLongList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(long k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractLongList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractLongList.this.removeLong(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    @Override
    public boolean contains(long k) {
        return this.indexOf(k) >= 0;
    }

    @Override
    public int indexOf(long k) {
        LongListIterator i = this.listIterator();
        while (i.hasNext()) {
            long e = i.nextLong();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(long k) {
        LongListIterator i = this.listIterator(this.size());
        while (i.hasPrevious()) {
            long e = i.previousLong();
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
                this.add(0L);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public LongList subList(int from, int to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new LongSubList(this, from, to);
    }

    @Override
    public void removeElements(int from, int to) {
        this.ensureIndex(to);
        LongListIterator i = this.listIterator(from);
        int n = to - from;
        if (n < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0) {
            i.nextLong();
            i.remove();
        }
    }

    @Override
    public void addElements(int index, long[] a, int offset, int length) {
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
    public void addElements(int index, long[] a) {
        this.addElements(index, a, 0, a.length);
    }

    @Override
    public void getElements(int from, long[] a, int offset, int length) {
        LongListIterator i = this.listIterator(from);
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
            a[offset++] = i.nextLong();
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
        LongListIterator i = this.iterator();
        int h = 1;
        int s = this.size();
        while (s-- != 0) {
            long k = i.nextLong();
            h = 31 * h + HashCommon.long2int(k);
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
        if (l instanceof LongList) {
            LongListIterator i1 = this.listIterator();
            LongListIterator i2 = ((LongList)l).listIterator();
            while (s-- != 0) {
                if (i1.nextLong() == i2.nextLong()) continue;
                return false;
            }
            return true;
        }
        LongListIterator i1 = this.listIterator();
        ListIterator i2 = l.listIterator();
        while (s-- != 0) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Long> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof LongList) {
            LongListIterator i1 = this.listIterator();
            LongListIterator i2 = ((LongList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                long e2;
                long e1 = i1.nextLong();
                int r = Long.compare(e1, e2 = i2.nextLong());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        LongListIterator i1 = this.listIterator();
        ListIterator<? extends Long> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(long o) {
        this.add(o);
    }

    @Override
    public long popLong() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeLong(this.size() - 1);
    }

    @Override
    public long topLong() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getLong(this.size() - 1);
    }

    @Override
    public long peekLong(int i) {
        return this.getLong(this.size() - 1 - i);
    }

    @Override
    public boolean rem(long k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeLong(index);
        return true;
    }

    @Override
    public boolean addAll(int index, LongCollection c) {
        this.ensureIndex(index);
        LongIterator i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.nextLong());
        }
        return retVal;
    }

    @Override
    public boolean addAll(int index, LongList l) {
        return this.addAll(index, (LongCollection)l);
    }

    @Override
    public boolean addAll(LongCollection c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public boolean addAll(LongList l) {
        return this.addAll(this.size(), l);
    }

    @Deprecated
    @Override
    public void add(int index, Long ok) {
        this.add(index, (long)ok);
    }

    @Deprecated
    @Override
    public Long set(int index, Long ok) {
        return this.set(index, (long)ok);
    }

    @Deprecated
    @Override
    public Long get(int index) {
        return this.getLong(index);
    }

    @Deprecated
    @Override
    public int indexOf(Object ok) {
        return this.indexOf((Long)ok);
    }

    @Deprecated
    @Override
    public int lastIndexOf(Object ok) {
        return this.lastIndexOf((Long)ok);
    }

    @Deprecated
    @Override
    public Long remove(int index) {
        return this.removeLong(index);
    }

    @Deprecated
    @Override
    public void push(Long o) {
        this.push((long)o);
    }

    @Deprecated
    @Override
    public Long pop() {
        return this.popLong();
    }

    @Deprecated
    @Override
    public Long top() {
        return this.topLong();
    }

    @Deprecated
    @Override
    public Long peek(int i) {
        return this.peekLong(i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        LongListIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("[");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            long k = i.nextLong();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class LongSubList
    extends AbstractLongList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongList l;
        protected final int from;
        protected int to;

        public LongSubList(LongList l, int from, int to) {
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
        public boolean add(long k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(int index, long k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(int index, Collection<? extends Long> c) {
            this.ensureIndex(index);
            this.to += c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public long getLong(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getLong(this.from + index);
        }

        @Override
        public long removeLong(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeLong(this.from + index);
        }

        @Override
        public long set(int index, long k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, long[] a, int offset, int length) {
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
        public void addElements(int index, long[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public LongListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new LongListIterator(){
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
                public long nextLong() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return this.l.getLong(this.from + this.last);
                }

                @Override
                public long previousLong() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return this.l.getLong(this.from + this.pos);
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
                public void add(long k) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    this.add(this.pos++, k);
                    this.last = -1;
                    assert (this.assertRange());
                }

                @Override
                public void set(long k) {
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
                    this.removeLong(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1;
                    assert (this.assertRange());
                }
            };
        }

        @Override
        public LongList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new LongSubList(this, from, to);
        }

        @Override
        public boolean rem(long k) {
            int index = this.indexOf(k);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeLong(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(int index, LongCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(int index, LongList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

    }

}

