/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Reference2DoubleSortedMap<K>
extends Reference2DoubleMap<K>,
SortedMap<K, Double> {
    public Reference2DoubleSortedMap<K> subMap(K var1, K var2);

    public Reference2DoubleSortedMap<K> headMap(K var1);

    public Reference2DoubleSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Double>> entrySet() {
        return this.reference2DoubleEntrySet();
    }

    @Override
    public ObjectSortedSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet();

    @Override
    public ReferenceSortedSet<K> keySet();

    @Override
    public DoubleCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Reference2DoubleMap.Entry<K>>,
    Reference2DoubleMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> fastIterator(Reference2DoubleMap.Entry<K> var1);
    }

}

