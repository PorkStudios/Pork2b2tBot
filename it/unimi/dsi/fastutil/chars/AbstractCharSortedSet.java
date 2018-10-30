/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import java.util.Iterator;

public abstract class AbstractCharSortedSet
extends AbstractCharSet
implements CharSortedSet {
    protected AbstractCharSortedSet() {
    }

    @Override
    public abstract CharBidirectionalIterator iterator();
}

