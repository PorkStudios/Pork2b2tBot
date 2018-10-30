/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2ReferenceMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
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

public interface Char2ReferenceSortedMap<V>
extends Char2ReferenceMap<V>,
SortedMap<Character, V> {
    public Char2ReferenceSortedMap<V> subMap(char var1, char var2);

    public Char2ReferenceSortedMap<V> headMap(char var1);

    public Char2ReferenceSortedMap<V> tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2ReferenceSortedMap<V> subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2ReferenceSortedMap<V> headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2ReferenceSortedMap<V> tailMap(Character from) {
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
    default public ObjectSortedSet<Map.Entry<Character, V>> entrySet() {
        return this.char2ReferenceEntrySet();
    }

    @Override
    public ObjectSortedSet<Char2ReferenceMap.Entry<V>> char2ReferenceEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public ReferenceCollection<V> values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Char2ReferenceMap.Entry<V>>,
    Char2ReferenceMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> fastIterator(Char2ReferenceMap.Entry<V> var1);
    }

}

