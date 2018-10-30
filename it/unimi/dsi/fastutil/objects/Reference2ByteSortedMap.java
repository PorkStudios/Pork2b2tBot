/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Reference2ByteSortedMap<K>
extends Reference2ByteMap<K>,
SortedMap<K, Byte> {
    public Reference2ByteSortedMap<K> subMap(K var1, K var2);

    public Reference2ByteSortedMap<K> headMap(K var1);

    public Reference2ByteSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Byte>> entrySet() {
        return this.reference2ByteEntrySet();
    }

    @Override
    public ObjectSortedSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet();

    @Override
    public ReferenceSortedSet<K> keySet();

    @Override
    public ByteCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Reference2ByteMap.Entry<K>>,
    Reference2ByteMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> fastIterator(Reference2ByteMap.Entry<K> var1);
    }

}

