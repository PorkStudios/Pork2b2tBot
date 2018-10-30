/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatIterators;
import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractFloatCollection
extends AbstractCollection<Float>
implements FloatCollection {
    protected AbstractFloatCollection() {
    }

    @Override
    public abstract FloatIterator iterator();

    @Override
    public boolean add(float k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(float k) {
        FloatIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextFloat()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(float k) {
        FloatIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextFloat()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Deprecated
    @Override
    public boolean add(Float o) {
        return this.add(o.floatValue());
    }

    @Deprecated
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains(((Float)o).floatValue());
    }

    @Deprecated
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        return this.rem(((Float)o).floatValue());
    }

    @Override
    public float[] toArray(float[] a) {
        if (a == null || a.length < this.size()) {
            a = new float[this.size()];
        }
        FloatIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public float[] toFloatArray() {
        return this.toFloatArray(null);
    }

    @Deprecated
    @Override
    public float[] toFloatArray(float[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(FloatCollection c) {
        boolean retVal = false;
        FloatIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextFloat())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(FloatCollection c) {
        FloatIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextFloat())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(FloatCollection c) {
        boolean retVal = false;
        FloatIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextFloat())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(FloatCollection c) {
        boolean retVal = false;
        FloatIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextFloat())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        FloatIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            float k = i.nextFloat();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

