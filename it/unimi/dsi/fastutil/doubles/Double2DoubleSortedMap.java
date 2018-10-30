/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2DoubleMap;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Double2DoubleSortedMap
extends Double2DoubleMap,
SortedMap<Double, Double> {
    public Double2DoubleSortedMap subMap(double var1, double var3);

    public Double2DoubleSortedMap headMap(double var1);

    public Double2DoubleSortedMap tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2DoubleSortedMap subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2DoubleSortedMap headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2DoubleSortedMap tailMap(Double from) {
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
    default public ObjectSortedSet<Map.Entry<Double, Double>> entrySet() {
        return this.double2DoubleEntrySet();
    }

    public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public DoubleCollection values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Double2DoubleMap.Entry>,
    Double2DoubleMap.FastEntrySet {
        public ObjectBidirectionalIterator<Double2DoubleMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleMap.Entry var1);
    }

}

