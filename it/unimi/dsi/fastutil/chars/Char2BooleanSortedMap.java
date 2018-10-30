/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.chars.Char2BooleanMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Char2BooleanSortedMap
extends Char2BooleanMap,
SortedMap<Character, Boolean> {
    public Char2BooleanSortedMap subMap(char var1, char var2);

    public Char2BooleanSortedMap headMap(char var1);

    public Char2BooleanSortedMap tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2BooleanSortedMap subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2BooleanSortedMap headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2BooleanSortedMap tailMap(Character from) {
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
    default public ObjectSortedSet<Map.Entry<Character, Boolean>> entrySet() {
        return this.char2BooleanEntrySet();
    }

    public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public BooleanCollection values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Char2BooleanMap.Entry>,
    Char2BooleanMap.FastEntrySet {
        public ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator(Char2BooleanMap.Entry var1);
    }

}

