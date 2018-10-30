/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.Float2IntMap;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Float2IntSortedMap
extends Float2IntMap,
SortedMap<Float, Integer> {
    public Float2IntSortedMap subMap(float var1, float var2);

    public Float2IntSortedMap headMap(float var1);

    public Float2IntSortedMap tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2IntSortedMap subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2IntSortedMap headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2IntSortedMap tailMap(Float from) {
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
    default public ObjectSortedSet<Map.Entry<Float, Integer>> entrySet() {
        return this.float2IntEntrySet();
    }

    public ObjectSortedSet<Float2IntMap.Entry> float2IntEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public IntCollection values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Float2IntMap.Entry>,
    Float2IntMap.FastEntrySet {
        public ObjectBidirectionalIterator<Float2IntMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Float2IntMap.Entry> fastIterator(Float2IntMap.Entry var1);
    }

}

