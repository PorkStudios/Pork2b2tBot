/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Iterator;

public interface DoubleBidirectionalIterable
extends DoubleIterable {
    @Override
    public DoubleBidirectionalIterator iterator();
}

