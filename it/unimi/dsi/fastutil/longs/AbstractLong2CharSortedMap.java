/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2CharMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2CharMap;
import it.unimi.dsi.fastutil.longs.Long2CharSortedMap;
import it.unimi.dsi.fastutil.longs.Long2CharSortedMaps;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractLong2CharSortedMap
extends AbstractLong2CharMap
implements Long2CharSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2CharSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public CharCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements CharIterator {
        protected final ObjectBidirectionalIterator<Long2CharMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2CharMap.Entry> i) {
            this.i = i;
        }

        @Override
        public char nextChar() {
            return this.i.next().getCharValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractCharCollection {
        protected ValuesCollection() {
        }

        @Override
        public CharIterator iterator() {
            return new ValuesIterator(Long2CharSortedMaps.fastIterator(AbstractLong2CharSortedMap.this));
        }

        @Override
        public boolean contains(char k) {
            return AbstractLong2CharSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2CharSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2CharMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2CharMap.Entry> i) {
            this.i = i;
        }

        @Override
        public long nextLong() {
            return this.i.next().getLongKey();
        }

        @Override
        public long previousLong() {
            return this.i.previous().getLongKey();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }
    }

    protected class KeySet
    extends AbstractLongSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(long k) {
            return AbstractLong2CharSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2CharSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2CharSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2CharSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2CharSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2CharSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2CharSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2CharSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2CharSortedMap.this.long2CharEntrySet().iterator(new AbstractLong2CharMap.BasicEntry(from, '\u0000')));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2CharSortedMaps.fastIterator(AbstractLong2CharSortedMap.this));
        }
    }

}

