/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Reference2LongSortedMap<K>
extends Reference2LongMap<K>,
SortedMap<K, Long> {
    public Reference2LongSortedMap<K> subMap(K var1, K var2);

    public Reference2LongSortedMap<K> headMap(K var1);

    public Reference2LongSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Long>> entrySet() {
        return this.reference2LongEntrySet();
    }

    @Override
    public ObjectSortedSet<Reference2LongMap.Entry<K>> reference2LongEntrySet();

    @Override
    public ReferenceSortedSet<K> keySet();

    @Override
    public LongCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Reference2LongMap.Entry<K>>,
    Reference2LongMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> fastIterator(Reference2LongMap.Entry<K> var1);
    }

}

