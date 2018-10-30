/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Object2BooleanSortedMap<K>
extends Object2BooleanMap<K>,
SortedMap<K, Boolean> {
    public Object2BooleanSortedMap<K> subMap(K var1, K var2);

    public Object2BooleanSortedMap<K> headMap(K var1);

    public Object2BooleanSortedMap<K> tailMap(K var1);

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<K, Boolean>> entrySet() {
        return this.object2BooleanEntrySet();
    }

    @Override
    public ObjectSortedSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet();

    @Override
    public ObjectSortedSet<K> keySet();

    @Override
    public BooleanCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Object2BooleanMap.Entry<K>>,
    Object2BooleanMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> fastIterator(Object2BooleanMap.Entry<K> var1);
    }

}

