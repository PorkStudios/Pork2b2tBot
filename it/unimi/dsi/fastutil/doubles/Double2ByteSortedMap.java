/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.doubles.Double2ByteMap;
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

public interface Double2ByteSortedMap
extends Double2ByteMap,
SortedMap<Double, Byte> {
    public Double2ByteSortedMap subMap(double var1, double var3);

    public Double2ByteSortedMap headMap(double var1);

    public Double2ByteSortedMap tailMap(double var1);

    public double firstDoubleKey();

    public double lastDoubleKey();

    @Deprecated
    default public Double2ByteSortedMap subMap(Double from, Double to) {
        return this.subMap((double)from, (double)to);
    }

    @Deprecated
    default public Double2ByteSortedMap headMap(Double to) {
        return this.headMap((double)to);
    }

    @Deprecated
    default public Double2ByteSortedMap tailMap(Double from) {
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
    default public ObjectSortedSet<Map.Entry<Double, Byte>> entrySet() {
        return this.double2ByteEntrySet();
    }

    public ObjectSortedSet<Double2ByteMap.Entry> double2ByteEntrySet();

    @Override
    public DoubleSortedSet keySet();

    @Override
    public ByteCollection values();

    public DoubleComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Double2ByteMap.Entry>,
    Double2ByteMap.FastEntrySet {
        public ObjectBidirectionalIterator<Double2ByteMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Double2ByteMap.Entry> fastIterator(Double2ByteMap.Entry var1);
    }

}

