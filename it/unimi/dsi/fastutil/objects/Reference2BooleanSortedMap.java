/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Reference2BooleanSortedMap<K>
extends Reference2BooleanMap<K>,
SortedMap<K, Boolean> {
    public Reference2BooleanSortedMap<K> subMap(K var1, K var2);

    public Reference2BooleanSortedMap<K> headMap(K var1);

    public Reference2BooleanSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Boolean>> entrySet() {
        return this.reference2BooleanEntrySet();
    }

    @Override
    public ObjectSortedSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet();

    @Override
    public ReferenceSortedSet<K> keySet();

    @Override
    public BooleanCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Reference2BooleanMap.Entry<K>>,
    Reference2BooleanMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> fastIterator(Reference2BooleanMap.Entry<K> var1);
    }

}

