/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterable;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Iterator;

public interface ShortBidirectionalIterable
extends ShortIterable {
    @Override
    public ShortBidirectionalIterator iterator();
}

