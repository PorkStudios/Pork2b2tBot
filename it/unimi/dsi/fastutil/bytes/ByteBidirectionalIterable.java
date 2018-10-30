/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Iterator;

public interface ByteBidirectionalIterable
extends ByteIterable {
    @Override
    public ByteBidirectionalIterator iterator();
}

