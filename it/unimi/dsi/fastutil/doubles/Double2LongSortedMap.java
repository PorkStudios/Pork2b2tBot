/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2LongMap;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Double2LongSortedMap
extends Double2LongMap,
SortedMap<Double, Long> {
    public Double2LongSortedMap subMap(double var1, double var3);

    public Double2LongSortedMap headMap(double var1);

    public Double2LongSortedMap tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2LongSortedMap subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2LongSortedMap headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2LongSortedMap tailMap(Double from) {
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
    default public ObjectSortedSet<Map.Entry<Double, Long>> entrySet() {
        return this.double2LongEntrySet();
    }

    public ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public LongCollection values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Double2LongMap.Entry>,
    Double2LongMap.FastEntrySet {
        public ObjectBidirectionalIterator<Double2LongMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Double2LongMap.Entry> fastIterator(Double2LongMap.Entry var1);
    }

}

