/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharIterable;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Iterator;

public interface CharBidirectionalIterable
extends CharIterable {
    @Override
    public CharBidirectionalIterator iterator();
}

