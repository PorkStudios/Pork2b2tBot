/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2FloatMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Char2FloatSortedMap
extends Char2FloatMap,
SortedMap<Character, Float> {
    public Char2FloatSortedMap subMap(char var1, char var2);

    public Char2FloatSortedMap headMap(char var1);

    public Char2FloatSortedMap tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2FloatSortedMap subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2FloatSortedMap headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2FloatSortedMap tailMap(Character from) {
        return this.tailMap(from.charValue());
    }

    @Deprecated
    @Override
    default public Character firstKey() {
        return Character.valueOf(this.firstCharKey());
    }

    @Deprecated
    @Override
    default public Character lastKey() {
        return Character.valueOf(this.lastCharKey());
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Character, Float>> entrySet() {
        return this.char2FloatEntrySet();
    }

    public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public FloatCollection values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Char2FloatMap.Entry>,
    Char2FloatMap.FastEntrySet {
        public ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator(Char2FloatMap.Entry var1);
    }

}

