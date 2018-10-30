/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2ReferenceMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceSortedMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceSortedMaps;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractLong2ReferenceSortedMap<V>
extends AbstractLong2ReferenceMap<V>
implements Long2ReferenceSortedMap<V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2ReferenceSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ReferenceCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> i) {
            this.i = i;
        }

        @Override
        public V next() {
            return this.i.next().getValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractReferenceCollection<V> {
        protected ValuesCollection() {
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ValuesIterator(Long2ReferenceSortedMaps.fastIterator(AbstractLong2ReferenceSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractLong2ReferenceSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ReferenceSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<V>
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> i) {
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
            return AbstractLong2ReferenceSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ReferenceSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2ReferenceSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2ReferenceSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2ReferenceSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2ReferenceSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2ReferenceSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2ReferenceSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2ReferenceSortedMap.this.long2ReferenceEntrySet().iterator(new AbstractLong2ReferenceMap.BasicEntry<Object>(from, null)));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2ReferenceSortedMaps.fastIterator(AbstractLong2ReferenceSortedMap.this));
        }
    }

}

