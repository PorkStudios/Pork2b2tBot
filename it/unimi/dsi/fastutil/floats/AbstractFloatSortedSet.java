/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import java.util.Iterator;

public abstract class AbstractFloatSortedSet
extends AbstractFloatSet
implements FloatSortedSet {
    protected AbstractFloatSortedSet() {
    }

    @Override
    public abstract FloatBidirectionalIterator iterator();
}

