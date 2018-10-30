/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;

public interface ObjectBidirectionalIterable<K>
extends ObjectIterable<K> {
    @Override
    public ObjectBidirectionalIterator<K> iterator();
}

