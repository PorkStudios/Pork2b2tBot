/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.Float2FloatMap;
import it.unimi.dsi.fastutil.floats.FloatCollection;
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

public interface Float2FloatSortedMap
extends Float2FloatMap,
SortedMap<Float, Float> {
    public Float2FloatSortedMap subMap(float var1, float var2);

    public Float2FloatSortedMap headMap(float var1);

    public Float2FloatSortedMap tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2FloatSortedMap subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2FloatSortedMap headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2FloatSortedMap tailMap(Float from) {
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
    default public ObjectSortedSet<Map.Entry<Float, Float>> entrySet() {
        return this.float2FloatEntrySet();
    }

    public ObjectSortedSet<Float2FloatMap.Entry> float2FloatEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public FloatCollection values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Float2FloatMap.Entry>,
    Float2FloatMap.FastEntrySet {
        public ObjectBidirectionalIterator<Float2FloatMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Float2FloatMap.Entry> fastIterator(Float2FloatMap.Entry var1);
    }

}

