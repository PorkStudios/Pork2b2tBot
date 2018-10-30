/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractLongSet
extends AbstractLongCollection
implements Cloneable,
LongSet {
    protected AbstractLongSet() {
    }

    @Override
    public abstract LongIterator iterator();

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
        LongIterator i = this.iterator();
        while (n-- != 0) {
            long k = i.nextLong();
            h += HashCommon.long2int(k);
        }
        return h;
    }

    @Override
    public boolean remove(long k) {
        return super.rem(k);
    }

    @Deprecated
    @Override
    public boolean rem(long k) {
        return this.remove(k);
    }
}

