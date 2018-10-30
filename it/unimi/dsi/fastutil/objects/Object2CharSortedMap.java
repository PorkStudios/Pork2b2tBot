/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Object2CharSortedMap<K>
extends Object2CharMap<K>,
SortedMap<K, Character> {
    public Object2CharSortedMap<K> subMap(K var1, K var2);

    public Object2CharSortedMap<K> headMap(K var1);

    public Object2CharSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Character>> entrySet() {
        return this.object2CharEntrySet();
    }

    @Override
    public ObjectSortedSet<Object2CharMap.Entry<K>> object2CharEntrySet();

    @Override
    public ObjectSortedSet<K> keySet();

    @Override
    public CharCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Object2CharMap.Entry<K>>,
    Object2CharMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Object2CharMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Object2CharMap.Entry<K>> fastIterator(Object2CharMap.Entry<K> var1);
    }

}

