/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2DoubleMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleSortedMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleSortedMaps;
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

public abstract class AbstractLong2DoubleSortedMap
extends AbstractLong2DoubleMap
implements Long2DoubleSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2DoubleSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public DoubleCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements DoubleIterator {
        protected final ObjectBidirectionalIterator<Long2DoubleMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2DoubleMap.Entry> i) {
            this.i = i;
        }

        @Override
        public double nextDouble() {
            return this.i.next().getDoubleValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractDoubleCollection {
        protected ValuesCollection() {
        }

        @Override
        public DoubleIterator iterator() {
            return new ValuesIterator(Long2DoubleSortedMaps.fastIterator(AbstractLong2DoubleSortedMap.this));
        }

        @Override
        public boolean contains(double k) {
            return AbstractLong2DoubleSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2DoubleSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2DoubleMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2DoubleMap.Entry> i) {
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
            return AbstractLong2DoubleSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2DoubleSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2DoubleSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2DoubleSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2DoubleSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2DoubleSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2DoubleSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2DoubleSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2DoubleSortedMap.this.long2DoubleEntrySet().iterator(new AbstractLong2DoubleMap.BasicEntry(from, 0.0)));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2DoubleSortedMaps.fastIterator(AbstractLong2DoubleSortedMap.this));
        }
    }

}

