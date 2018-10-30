/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Iterator;

public interface IntBidirectionalIterable
extends IntIterable {
    @Override
    public IntBidirectionalIterator iterator();
}

