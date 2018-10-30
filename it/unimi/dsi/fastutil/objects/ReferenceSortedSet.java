/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.util.Iterator;
import java.util.SortedSet;

public interface ReferenceSortedSet<K>
extends ReferenceSet<K>,
SortedSet<K>,
ObjectBidirectionalIterable<K> {
    public ObjectBidirectionalIterator<K> iterator(K var1);

    @Override
    public ObjectBidirectionalIterator<K> iterator();

    @Override
    public ReferenceSortedSet<K> subSet(K var1, K var2);

    @Override
    public ReferenceSortedSet<K> headSet(K var1);

    @Override
    public ReferenceSortedSet<K> tailSet(K var1);
}

