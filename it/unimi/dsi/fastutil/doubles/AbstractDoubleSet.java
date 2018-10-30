/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractDoubleSet
extends AbstractDoubleCollection
implements Cloneable,
DoubleSet {
    protected AbstractDoubleSet() {
    }

    @Override
    public abstract DoubleIterator iterator();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        Set s = (Set)o;
        if (s.size() != this.size()) {
            return false;
        }
        return this.containsAll(s);
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        DoubleIterator i = this.iterator();
        while (n-- != 0) {
            double k = i.nextDouble();
            h += HashCommon.double2int(k);
        }
        return h;
    }

    @Override
    public boolean remove(double k) {
        return super.rem(k);
    }

    @Deprecated
    @Override
    public boolean rem(double k) {
        return this.remove(k);
    }
}

