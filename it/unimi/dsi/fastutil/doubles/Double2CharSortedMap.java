/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.doubles.Double2CharMap;
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

public interface Double2CharSortedMap
extends Double2CharMap,
SortedMap<Double, Character> {
    public Double2CharSortedMap subMap(double var1, double var3);

    public Double2CharSortedMap headMap(double var1);

    public Double2CharSortedMap tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2CharSortedMap subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2CharSortedMap headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2CharSortedMap tailMap(Double from) {
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
    default public ObjectSortedSet<Map.Entry<Double, Character>> entrySet() {
        return this.double2CharEntrySet();
    }

    public ObjectSortedSet<Double2CharMap.Entry> double2CharEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public CharCollection values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Double2CharMap.Entry>,
    Double2CharMap.FastEntrySet {
        public ObjectBidirectionalIterator<Double2CharMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Double2CharMap.Entry> fastIterator(Double2CharMap.Entry var1);
    }

}

