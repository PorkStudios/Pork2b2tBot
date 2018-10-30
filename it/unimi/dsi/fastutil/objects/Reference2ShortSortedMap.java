/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2ShortMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Reference2ShortSortedMap<K>
extends Reference2ShortMap<K>,
SortedMap<K, Short> {
    public Reference2ShortSortedMap<K> subMap(K var1, K var2);

    public Reference2ShortSortedMap<K> headMap(K var1);

    public Reference2ShortSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Short>> entrySet() {
        return this.reference2ShortEntrySet();
    }

    @Override
    public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet();

    @Override
    public ReferenceSortedSet<K> keySet();

    @Override
    public ShortCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Reference2ShortMap.Entry<K>>,
    Reference2ShortMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> fastIterator(Reference2ShortMap.Entry<K> var1);
    }

}

