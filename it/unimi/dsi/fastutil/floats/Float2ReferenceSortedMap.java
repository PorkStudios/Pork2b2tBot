/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.Float2ReferenceMap;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
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

public interface Float2ReferenceSortedMap<V>
extends Float2ReferenceMap<V>,
SortedMap<Float, V> {
    public Float2ReferenceSortedMap<V> subMap(float var1, float var2);

    public Float2ReferenceSortedMap<V> headMap(float var1);

    public Float2ReferenceSortedMap<V> tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2ReferenceSortedMap<V> subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2ReferenceSortedMap<V> headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2ReferenceSortedMap<V> tailMap(Float from) {
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
    default public ObjectSortedSet<Map.Entry<Float, V>> entrySet() {
        return this.float2ReferenceEntrySet();
    }

    @Override
    public ObjectSortedSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public ReferenceCollection<V> values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Float2ReferenceMap.Entry<V>>,
    Float2ReferenceMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> fastIterator(Float2ReferenceMap.Entry<V> var1);
    }

}

