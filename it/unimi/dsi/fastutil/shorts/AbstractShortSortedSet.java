/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Iterator;

public abstract class AbstractShortSortedSet
extends AbstractShortSet
implements ShortSortedSet {
    protected AbstractShortSortedSet() {
    }

    @Override
    public abstract ShortBidirectionalIterator iterator();
}

