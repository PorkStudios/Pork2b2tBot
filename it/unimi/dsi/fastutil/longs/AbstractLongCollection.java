/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractLongCollection
extends AbstractCollection<Long>
implements LongCollection {
    protected AbstractLongCollection() {
    }

    @Override
    public abstract LongIterator iterator();

    @Override
    public boolean add(long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(long k) {
        LongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextLong()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(long k) {
        LongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextLong()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Deprecated
    @Override
    public boolean add(Long o) {
        return this.add((long)o);
    }

    @Deprecated
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Long)o);
    }

    @Deprecated
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        return this.rem((Long)o);
    }

    @Override
    public long[] toArray(long[] a) {
        if (a == null || a.length < this.size()) {
            a = new long[this.size()];
        }
        LongIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public long[] toLongArray() {
        return this.toLongArray(null);
    }

    @Deprecated
    @Override
    public long[] toLongArray(long[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextLong())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(LongCollection c) {
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextLong())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextLong())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextLong())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        LongIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            long k = i.nextLong();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

