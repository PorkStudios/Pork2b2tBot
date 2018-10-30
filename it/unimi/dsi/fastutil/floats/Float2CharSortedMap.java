/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.floats.Float2CharMap;
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

public interface Float2CharSortedMap
extends Float2CharMap,
SortedMap<Float, Character> {
    public Float2CharSortedMap subMap(float var1, float var2);

    public Float2CharSortedMap headMap(float var1);

    public Float2CharSortedMap tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2CharSortedMap subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2CharSortedMap headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2CharSortedMap tailMap(Float from) {
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
    default public ObjectSortedSet<Map.Entry<Float, Character>> entrySet() {
        return this.float2CharEntrySet();
    }

    public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public CharCollection values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Float2CharMap.Entry>,
    Float2CharMap.FastEntrySet {
        public ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator(Float2CharMap.Entry var1);
    }

}

