/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
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

public interface Char2ObjectSortedMap<V>
extends Char2ObjectMap<V>,
SortedMap<Character, V> {
    public Char2ObjectSortedMap<V> subMap(char var1, char var2);

    public Char2ObjectSortedMap<V> headMap(char var1);

    public Char2ObjectSortedMap<V> tailMap(char var1);

    public char firstCharKey();

    public char lastCharKey();

    @Deprecated
    default public Char2ObjectSortedMap<V> subMap(Character from, Character to) {
        return this.subMap(from.charValue(), to.charValue());
    }

    @Deprecated
    default public Char2ObjectSortedMap<V> headMap(Character to) {
        return this.headMap(to.charValue());
    }

    @Deprecated
    default public Char2ObjectSortedMap<V> tailMap(Character from) {
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
        return this.char2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet();

    @Override
    public CharSortedSet keySet();

    @Override
    public ObjectCollection<V> values();

    public CharComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Char2ObjectMap.Entry<V>>,
    Char2ObjectMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator(Char2ObjectMap.Entry<V> var1);
    }

}

