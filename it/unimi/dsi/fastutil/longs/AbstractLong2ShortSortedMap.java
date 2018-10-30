/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2ShortMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortSortedMap;
import it.unimi.dsi.fastutil.longs.Long2ShortSortedMaps;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractLong2ShortSortedMap
extends AbstractLong2ShortMap
implements Long2ShortSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2ShortSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ShortCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ShortIterator {
        protected final ObjectBidirectionalIterator<Long2ShortMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2ShortMap.Entry> i) {
            this.i = i;
        }

        @Override
        public short nextShort() {
            return this.i.next().getShortValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractShortCollection {
        protected ValuesCollection() {
        }

        @Override
        public ShortIterator iterator() {
            return new ValuesIterator(Long2ShortSortedMaps.fastIterator(AbstractLong2ShortSortedMap.this));
        }

        @Override
        public boolean contains(short k) {
            return AbstractLong2ShortSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ShortSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2ShortMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2ShortMap.Entry> i) {
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
            return AbstractLong2ShortSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ShortSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2ShortSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2ShortSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2ShortSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2ShortSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2ShortSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2ShortSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2ShortSortedMap.this.long2ShortEntrySet().iterator(new AbstractLong2ShortMap.BasicEntry(from, 0)));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2ShortSortedMaps.fastIterator(AbstractLong2ShortSortedMap.this));
        }
    }

}

