/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Iterator;

public interface BooleanBidirectionalIterable
extends BooleanIterable {
    @Override
    public BooleanBidirectionalIterator iterator();
}

