/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.Float2LongMap;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
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

public interface Float2LongSortedMap
extends Float2LongMap,
SortedMap<Float, Long> {
    public Float2LongSortedMap subMap(float var1, float var2);

    public Float2LongSortedMap headMap(float var1);

    public Float2LongSortedMap tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2LongSortedMap subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2LongSortedMap headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2LongSortedMap tailMap(Float from) {
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
    default public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
        return this.float2LongEntrySet();
    }

    public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public LongCollection values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Float2LongMap.Entry>,
    Float2LongMap.FastEntrySet {
        public ObjectBidirectionalIterator<Float2LongMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Float2LongMap.Entry> fastIterator(Float2LongMap.Entry var1);
    }

}

