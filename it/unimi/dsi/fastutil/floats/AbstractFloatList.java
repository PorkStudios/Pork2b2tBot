/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractFloatList
extends AbstractFloatCollection
implements FloatList,
FloatStack {
    protected AbstractFloatList() {
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
    public void add(int index, float k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(float k) {
        this.add(this.size(), k);
        return true;
    }

    @Override
    public float removeFloat(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float set(int index, float k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Float> c) {
        this.ensureIndex(index);
        Iterator<? extends Float> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Float> c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public FloatListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public FloatListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public FloatListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new FloatListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractFloatList.this.size();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public float nextFloat() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractFloatList.this.getFloat(this.last);
            }

            @Override
            public float previousFloat() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractFloatList.this.getFloat(this.pos);
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
            public void add(float k) {
                AbstractFloatList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(float k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractFloatList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractFloatList.this.removeFloat(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    @Override
    public boolean contains(float k) {
        return this.indexOf(k) >= 0;
    }

    @Override
    public int indexOf(float k) {
        FloatListIterator i = this.listIterator();
        while (i.hasNext()) {
            float e = i.nextFloat();
            if (Float.floatToIntBits(k) != Float.floatToIntBits(e)) continue;
            return i.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(float k) {
        FloatListIterator i = this.listIterator(this.size());
        while (i.hasPrevious()) {
            float e = i.previousFloat();
            if (Float.floatToIntBits(k) != Float.floatToIntBits(e)) continue;
            return i.nextIndex();
        }
        return -1;
    }

    @Override
    public void size(int size) {
        int i = this.size();
        if (size > i) {
            while (i++ < size) {
                this.add(0.0f);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public FloatList subList(int from, int to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new FloatSubList(this, from, to);
    }

    @Override
    public void removeElements(int from, int to) {
        this.ensureIndex(to);
        FloatListIterator i = this.listIterator(from);
        int n = to - from;
        if (n < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0) {
            i.nextFloat();
            i.remove();
        }
    }

    @Override
    public void addElements(int index, float[] a, int offset, int length) {
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
    public void addElements(int index, float[] a) {
        this.addElements(index, a, 0, a.length);
    }

    @Override
    public void getElements(int from, float[] a, int offset, int length) {
        FloatListIterator i = this.listIterator(from);
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
            a[offset++] = i.nextFloat();
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
        FloatListIterator i = this.iterator();
        int h = 1;
        int s = this.size();
        while (s-- != 0) {
            float k = i.nextFloat();
            h = 31 * h + HashCommon.float2int(k);
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
        if (l instanceof FloatList) {
            FloatListIterator i1 = this.listIterator();
            FloatListIterator i2 = ((FloatList)l).listIterator();
            while (s-- != 0) {
                if (i1.nextFloat() == i2.nextFloat()) continue;
                return false;
            }
            return true;
        }
        FloatListIterator i1 = this.listIterator();
        ListIterator i2 = l.listIterator();
        while (s-- != 0) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Float> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof FloatList) {
            FloatListIterator i1 = this.listIterator();
            FloatListIterator i2 = ((FloatList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                float e2;
                float e1 = i1.nextFloat();
                int r = Float.compare(e1, e2 = i2.nextFloat());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        FloatListIterator i1 = this.listIterator();
        ListIterator<? extends Float> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(float o) {
        this.add(o);
    }

    @Override
    public float popFloat() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeFloat(this.size() - 1);
    }

    @Override
    public float topFloat() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getFloat(this.size() - 1);
    }

    @Override
    public float peekFloat(int i) {
        return this.getFloat(this.size() - 1 - i);
    }

    @Override
    public boolean rem(float k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeFloat(index);
        return true;
    }

    @Override
    public boolean addAll(int index, FloatCollection c) {
        this.ensureIndex(index);
        FloatIterator i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.nextFloat());
        }
        return retVal;
    }

    @Override
    public boolean addAll(int index, FloatList l) {
        return this.addAll(index, (FloatCollection)l);
    }

    @Override
    public boolean addAll(FloatCollection c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public boolean addAll(FloatList l) {
        return this.addAll(this.size(), l);
    }

    @Deprecated
    @Override
    public void add(int index, Float ok) {
        this.add(index, ok.floatValue());
    }

    @Deprecated
    @Override
    public Float set(int index, Float ok) {
        return Float.valueOf(this.set(index, ok.floatValue()));
    }

    @Deprecated
    @Override
    public Float get(int index) {
        return Float.valueOf(this.getFloat(index));
    }

    @Deprecated
    @Override
    public int indexOf(Object ok) {
        return this.indexOf(((Float)ok).floatValue());
    }

    @Deprecated
    @Override
    public int lastIndexOf(Object ok) {
        return this.lastIndexOf(((Float)ok).floatValue());
    }

    @Deprecated
    @Override
    public Float remove(int index) {
        return Float.valueOf(this.removeFloat(index));
    }

    @Deprecated
    @Override
    public void push(Float o) {
        this.push(o.floatValue());
    }

    @Deprecated
    @Override
    public Float pop() {
        return Float.valueOf(this.popFloat());
    }

    @Deprecated
    @Override
    public Float top() {
        return Float.valueOf(this.topFloat());
    }

    @Deprecated
    @Override
    public Float peek(int i) {
        return Float.valueOf(this.peekFloat(i));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        FloatListIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("[");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            float k = i.nextFloat();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class FloatSubList
    extends AbstractFloatList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatList l;
        protected final int from;
        protected int to;

        public FloatSubList(FloatList l, int from, int to) {
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
        public boolean add(float k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(int index, float k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(int index, Collection<? extends Float> c) {
            this.ensureIndex(index);
            this.to += c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public float getFloat(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getFloat(this.from + index);
        }

        @Override
        public float removeFloat(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeFloat(this.from + index);
        }

        @Override
        public float set(int index, float k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, float[] a, int offset, int length) {
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
        public void addElements(int index, float[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public FloatListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new FloatListIterator(){
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
                public float nextFloat() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return this.l.getFloat(this.from + this.last);
                }

                @Override
                public float previousFloat() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return this.l.getFloat(this.from + this.pos);
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
                public void add(float k) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    this.add(this.pos++, k);
                    this.last = -1;
                    assert (this.assertRange());
                }

                @Override
                public void set(float k) {
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
                    this.removeFloat(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1;
                    assert (this.assertRange());
                }
            };
        }

        @Override
        public FloatList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new FloatSubList(this, from, to);
        }

        @Override
        public boolean rem(float k) {
            int index = this.indexOf(k);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeFloat(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(int index, FloatCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(int index, FloatList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

    }

}

