/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Double2ReferenceSortedMap<V>
extends Double2ReferenceMap<V>,
SortedMap<Double, V> {
    public Double2ReferenceSortedMap<V> subMap(double var1, double var3);

    public Double2ReferenceSortedMap<V> headMap(double var1);

    public Double2ReferenceSortedMap<V> tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2ReferenceSortedMap<V> subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2ReferenceSortedMap<V> headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2ReferenceSortedMap<V> tailMap(Double from) {
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
        return this.double2ReferenceEntrySet();
    }

    @Override
    public ObjectSortedSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public ReferenceCollection<V> values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Double2ReferenceMap.Entry<V>>,
    Double2ReferenceMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> fastIterator(Double2ReferenceMap.Entry<V> var1);
    }

}

