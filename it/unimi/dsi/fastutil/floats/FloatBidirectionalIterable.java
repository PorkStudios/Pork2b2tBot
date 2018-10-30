/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatIterable;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Iterator;

public interface FloatBidirectionalIterable
extends FloatIterable {
    @Override
    public FloatBidirectionalIterator iterator();
}

