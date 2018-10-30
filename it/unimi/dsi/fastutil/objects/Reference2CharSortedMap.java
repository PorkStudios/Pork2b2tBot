/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2CharMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Reference2CharSortedMap<K>
extends Reference2CharMap<K>,
SortedMap<K, Character> {
    public Reference2CharSortedMap<K> subMap(K var1, K var2);

    public Reference2CharSortedMap<K> headMap(K var1);

    public Reference2CharSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Character>> entrySet() {
        return this.reference2CharEntrySet();
    }

    @Override
    public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet();

    @Override
    public ReferenceSortedSet<K> keySet();

    @Override
    public CharCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Reference2CharMap.Entry<K>>,
    Reference2CharMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> fastIterator(Reference2CharMap.Entry<K> var1);
    }

}

