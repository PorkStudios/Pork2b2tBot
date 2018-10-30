/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2ByteMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteSortedMap;
import it.unimi.dsi.fastutil.longs.Long2ByteSortedMaps;
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

public abstract class AbstractLong2ByteSortedMap
extends AbstractLong2ByteMap
implements Long2ByteSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2ByteSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Long2ByteMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2ByteMap.Entry> i) {
            this.i = i;
        }

        @Override
        public byte nextByte() {
            return this.i.next().getByteValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractByteCollection {
        protected ValuesCollection() {
        }

        @Override
        public ByteIterator iterator() {
            return new ValuesIterator(Long2ByteSortedMaps.fastIterator(AbstractLong2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractLong2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2ByteMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2ByteMap.Entry> i) {
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
            return AbstractLong2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ByteSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2ByteSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2ByteSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2ByteSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2ByteSortedMap.this.long2ByteEntrySet().iterator(new AbstractLong2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2ByteSortedMaps.fastIterator(AbstractLong2ByteSortedMap.this));
        }
    }

}

