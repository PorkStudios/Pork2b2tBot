/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.Iterator;

public abstract class AbstractIntSortedSet
extends AbstractIntSet
implements IntSortedSet {
    protected AbstractIntSortedSet() {
    }

    @Override
    public abstract IntBidirectionalIterator iterator();
}

