/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import java.util.Iterator;

public abstract class AbstractDoubleSortedSet
extends AbstractDoubleSet
implements DoubleSortedSet {
    protected AbstractDoubleSortedSet() {
    }

    @Override
    public abstract DoubleBidirectionalIterator iterator();
}

