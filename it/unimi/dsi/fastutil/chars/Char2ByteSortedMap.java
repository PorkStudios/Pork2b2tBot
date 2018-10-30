/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.chars.Char2ByteMap;
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

public interface Char2ByteSortedMap
extends Char2ByteMap,
SortedMap<Character, Byte> {
    public Char2ByteSortedMap subMap(char var1, char var2);

    public Char2ByteSortedMap headMap(char var1);

    public Char2ByteSortedMap tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2ByteSortedMap subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2ByteSortedMap headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2ByteSortedMap tailMap(Character from) {
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
    default public ObjectSortedSet<Map.Entry<Character, Byte>> entrySet() {
        return this.char2ByteEntrySet();
    }

    public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public ByteCollection values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Char2ByteMap.Entry>,
    Char2ByteMap.FastEntrySet {
        public ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator(Char2ByteMap.Entry var1);
    }

}

