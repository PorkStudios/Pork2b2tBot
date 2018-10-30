/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2IntMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntSortedMap;
import it.unimi.dsi.fastutil.longs.Long2IntSortedMaps;
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

public abstract class AbstractLong2IntSortedMap
extends AbstractLong2IntMap
implements Long2IntSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2IntSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public IntCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements IntIterator {
        protected final ObjectBidirectionalIterator<Long2IntMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2IntMap.Entry> i) {
            this.i = i;
        }

        @Override
        public int nextInt() {
            return this.i.next().getIntValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractIntCollection {
        protected ValuesCollection() {
        }

        @Override
        public IntIterator iterator() {
            return new ValuesIterator(Long2IntSortedMaps.fastIterator(AbstractLong2IntSortedMap.this));
        }

        @Override
        public boolean contains(int k) {
            return AbstractLong2IntSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2IntSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2IntMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2IntMap.Entry> i) {
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
            return AbstractLong2IntSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2IntSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2IntSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2IntSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2IntSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2IntSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2IntSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2IntSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2IntSortedMap.this.long2IntEntrySet().iterator(new AbstractLong2IntMap.BasicEntry(from, 0)));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2IntSortedMaps.fastIterator(AbstractLong2IntSortedMap.this));
        }
    }

}

