/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2LongMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
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

public interface Char2LongSortedMap
extends Char2LongMap,
SortedMap<Character, Long> {
    public Char2LongSortedMap subMap(char var1, char var2);

    public Char2LongSortedMap headMap(char var1);

    public Char2LongSortedMap tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2LongSortedMap subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2LongSortedMap headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2LongSortedMap tailMap(Character from) {
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
    default public ObjectSortedSet<Map.Entry<Character, Long>> entrySet() {
        return this.char2LongEntrySet();
    }

    public ObjectSortedSet<Char2LongMap.Entry> char2LongEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public LongCollection values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Char2LongMap.Entry>,
    Char2LongMap.FastEntrySet {
        public ObjectBidirectionalIterator<Char2LongMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Char2LongMap.Entry> fastIterator(Char2LongMap.Entry var1);
    }

}

