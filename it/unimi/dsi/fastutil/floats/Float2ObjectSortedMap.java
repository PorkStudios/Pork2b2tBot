/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Float2ObjectSortedMap<V>
extends Float2ObjectMap<V>,
SortedMap<Float, V> {
    public Float2ObjectSortedMap<V> subMap(float var1, float var2);

    public Float2ObjectSortedMap<V> headMap(float var1);

    public Float2ObjectSortedMap<V> tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2ObjectSortedMap<V> subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2ObjectSortedMap<V> headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2ObjectSortedMap<V> tailMap(Float from) {
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
        return this.float2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public ObjectCollection<V> values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Float2ObjectMap.Entry<V>>,
    Float2ObjectMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator(Float2ObjectMap.Entry<V> var1);
    }

}

