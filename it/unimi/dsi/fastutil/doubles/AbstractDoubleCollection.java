/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractDoubleCollection
extends AbstractCollection<Double>
implements DoubleCollection {
    protected AbstractDoubleCollection() {
    }

    @Override
    public abstract DoubleIterator iterator();

    @Override
    public boolean add(double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(double k) {
        DoubleIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextDouble()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(double k) {
        DoubleIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextDouble()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Deprecated
    @Override
    public boolean add(Double o) {
        return this.add((double)o);
    }

    @Deprecated
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Double)o);
    }

    @Deprecated
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        return this.rem((Double)o);
    }

    @Override
    public double[] toArray(double[] a) {
        if (a == null || a.length < this.size()) {
            a = new double[this.size()];
        }
        DoubleIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public double[] toDoubleArray() {
        return this.toDoubleArray(null);
    }

    @Deprecated
    @Override
    public double[] toDoubleArray(double[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(DoubleCollection c) {
        boolean retVal = false;
        DoubleIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextDouble())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(DoubleCollection c) {
        DoubleIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextDouble())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(DoubleCollection c) {
        boolean retVal = false;
        DoubleIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextDouble())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(DoubleCollection c) {
        boolean retVal = false;
        DoubleIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextDouble())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        DoubleIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            double k = i.nextDouble();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

