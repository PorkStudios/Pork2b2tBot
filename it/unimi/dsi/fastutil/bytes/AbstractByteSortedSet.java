/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import java.util.Iterator;

public abstract class AbstractByteSortedSet
extends AbstractByteSet
implements ByteSortedSet {
    protected AbstractByteSortedSet() {
    }

    @Override
    public abstract ByteBidirectionalIterator iterator();
}

