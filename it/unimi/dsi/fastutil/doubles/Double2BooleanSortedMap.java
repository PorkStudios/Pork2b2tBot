/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.doubles.Double2BooleanMap;
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

public interface Double2BooleanSortedMap
extends Double2BooleanMap,
SortedMap<Double, Boolean> {
    public Double2BooleanSortedMap subMap(double var1, double var3);

    public Double2BooleanSortedMap headMap(double var1);

    public Double2BooleanSortedMap tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2BooleanSortedMap subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2BooleanSortedMap headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2BooleanSortedMap tailMap(Double from) {
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
    default public ObjectSortedSet<Map.Entry<Double, Boolean>> entrySet() {
        return this.double2BooleanEntrySet();
    }

    public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public BooleanCollection values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Double2BooleanMap.Entry>,
    Double2BooleanMap.FastEntrySet {
        public ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator(Double2BooleanMap.Entry var1);
    }

}

