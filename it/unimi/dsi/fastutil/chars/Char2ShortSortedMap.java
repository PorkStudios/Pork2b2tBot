/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2ShortMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Char2ShortSortedMap
extends Char2ShortMap,
SortedMap<Character, Short> {
    public Char2ShortSortedMap subMap(char var1, char var2);

    public Char2ShortSortedMap headMap(char var1);

    public Char2ShortSortedMap tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2ShortSortedMap subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2ShortSortedMap headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2ShortSortedMap tailMap(Character from) {
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
    default public ObjectSortedSet<Map.Entry<Character, Short>> entrySet() {
        return this.char2ShortEntrySet();
    }

    public ObjectSortedSet<Char2ShortMap.Entry> char2ShortEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public ShortCollection values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Char2ShortMap.Entry>,
    Char2ShortMap.FastEntrySet {
        public ObjectBidirectionalIterator<Char2ShortMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Char2ShortMap.Entry> fastIterator(Char2ShortMap.Entry var1);
    }

}

