/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2DoubleMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Char2DoubleSortedMap
extends Char2DoubleMap,
SortedMap<Character, Double> {
    public Char2DoubleSortedMap subMap(char var1, char var2);

    public Char2DoubleSortedMap headMap(char var1);

    public Char2DoubleSortedMap tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2DoubleSortedMap subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2DoubleSortedMap headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2DoubleSortedMap tailMap(Character from) {
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
    default public ObjectSortedSet<Map.Entry<Character, Double>> entrySet() {
        return this.char2DoubleEntrySet();
    }

    public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public DoubleCollection values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Char2DoubleMap.Entry>,
    Char2DoubleMap.FastEntrySet {
        public ObjectBidirectionalIterator<Char2DoubleMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Char2DoubleMap.Entry> fastIterator(Char2DoubleMap.Entry var1);
    }

}

