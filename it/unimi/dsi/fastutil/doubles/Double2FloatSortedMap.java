/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2FloatMap;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Double2FloatSortedMap
extends Double2FloatMap,
SortedMap<Double, Float> {
    public Double2FloatSortedMap subMap(double var1, double var3);

    public Double2FloatSortedMap headMap(double var1);

    public Double2FloatSortedMap tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2FloatSortedMap subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2FloatSortedMap headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2FloatSortedMap tailMap(Double from) {
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
    default public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() {
        return this.double2FloatEntrySet();
    }

    public ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public FloatCollection values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Double2FloatMap.Entry>,
    Double2FloatMap.FastEntrySet {
        public ObjectBidirectionalIterator<Double2FloatMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Double2FloatMap.Entry> fastIterator(Double2FloatMap.Entry var1);
    }

}

