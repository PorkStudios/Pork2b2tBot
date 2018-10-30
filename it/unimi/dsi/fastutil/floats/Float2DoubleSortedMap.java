/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.floats.Float2DoubleMap;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Float2DoubleSortedMap
extends Float2DoubleMap,
SortedMap<Float, Double> {
    public Float2DoubleSortedMap subMap(float var1, float var2);

    public Float2DoubleSortedMap headMap(float var1);

    public Float2DoubleSortedMap tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2DoubleSortedMap subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2DoubleSortedMap headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2DoubleSortedMap tailMap(Float from) {
        return this.tailMap(from.floatValue());
    }

    @Deprecated
    @Override
    default public Float firstKey() {
        return Float.valueOf(this.firstFloatKey());
    }

    @Deprecated
    @Override
    default public Float lastKey() {
        return Float.valueOf(this.lastFloatKey());
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Float, Double>> entrySet() {
        return this.float2DoubleEntrySet();
    }

    public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public DoubleCollection values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Float2DoubleMap.Entry>,
    Float2DoubleMap.FastEntrySet {
        public ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleMap.Entry var1);
    }

}

