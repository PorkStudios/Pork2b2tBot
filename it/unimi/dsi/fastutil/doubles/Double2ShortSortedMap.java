/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2ShortMap;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Double2ShortSortedMap
extends Double2ShortMap,
SortedMap<Double, Short> {
    public Double2ShortSortedMap subMap(double var1, double var3);

    public Double2ShortSortedMap headMap(double var1);

    public Double2ShortSortedMap tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2ShortSortedMap subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2ShortSortedMap headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2ShortSortedMap tailMap(Double from) {
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
    default public ObjectSortedSet<Map.Entry<Double, Short>> entrySet() {
        return this.double2ShortEntrySet();
    }

    public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public ShortCollection values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Double2ShortMap.Entry>,
    Double2ShortMap.FastEntrySet {
        public ObjectBidirectionalIterator<Double2ShortMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Double2ShortMap.Entry> fastIterator(Double2ShortMap.Entry var1);
    }

}

