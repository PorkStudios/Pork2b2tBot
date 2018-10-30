/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Reference2ObjectSortedMap<K, V>
extends Reference2ObjectMap<K, V>,
SortedMap<K, V> {
    @Override
    public Reference2ObjectSortedMap<K, V> subMap(K var1, K var2);

    @Override
    public Reference2ObjectSortedMap<K, V> headMap(K var1);

    @Override
    public Reference2ObjectSortedMap<K, V> tailMap(K var1);

    @Override
    default public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
        return this.reference2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet();

    @Override
    public ReferenceSortedSet<K> keySet();

    @Override
    public ObjectCollection<V> values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K, V>
    extends ObjectSortedSet<Reference2ObjectMap.Entry<K, V>>,
    Reference2ObjectMap.FastEntrySet<K, V> {
        @Override
        public ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> fastIterator();

        public ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> fastIterator(Reference2ObjectMap.Entry<K, V> var1);
    }

}

