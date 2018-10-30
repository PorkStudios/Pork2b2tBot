/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.floats.Float2ByteMap;
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

public interface Float2ByteSortedMap
extends Float2ByteMap,
SortedMap<Float, Byte> {
    public Float2ByteSortedMap subMap(float var1, float var2);

    public Float2ByteSortedMap headMap(float var1);

    public Float2ByteSortedMap tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2ByteSortedMap subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2ByteSortedMap headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2ByteSortedMap tailMap(Float from) {
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
    default public ObjectSortedSet<Map.Entry<Float, Byte>> entrySet() {
        return this.float2ByteEntrySet();
    }

    public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public ByteCollection values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Float2ByteMap.Entry>,
    Float2ByteMap.FastEntrySet {
        public ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator(Float2ByteMap.Entry var1);
    }

}

