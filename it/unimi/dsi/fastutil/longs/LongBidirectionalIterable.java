/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;

public interface LongBidirectionalIterable
extends LongIterable {
    @Override
    public LongBidirectionalIterator iterator();
}

