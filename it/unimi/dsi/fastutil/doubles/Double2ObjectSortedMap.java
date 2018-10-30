/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Double2ObjectSortedMap<V>
extends Double2ObjectMap<V>,
SortedMap<Double, V> {
    public Double2ObjectSortedMap<V> subMap(double var1, double var3);

    public Double2ObjectSortedMap<V> headMap(double var1);

    public Double2ObjectSortedMap<V> tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2ObjectSortedMap<V> subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2ObjectSortedMap<V> headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2ObjectSortedMap<V> tailMap(Double from) {
        return this.tailMap((double)from);
    }

    @Deprecated
    @Override
    default public Double firstKey() {
        return this.firstDoubleKey();
    }

    @Deprecated
    @Override
    default public Double lastKey() {
        return this.lastDoubleKey();
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Double, V>> entrySet() {
        return this.double2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public ObjectCollection<V> values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Double2ObjectMap.Entry<V>>,
    Double2ObjectMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> fastIterator(Double2ObjectMap.Entry<V> var1);
    }

}

