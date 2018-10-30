/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2ObjectMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMaps;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractLong2ObjectSortedMap<V>
extends AbstractLong2ObjectMap<V>
implements Long2ObjectSortedMap<V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2ObjectSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ObjectCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i) {
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
    extends AbstractObjectCollection<V> {
        protected ValuesCollection() {
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ValuesIterator(Long2ObjectSortedMaps.fastIterator(AbstractLong2ObjectSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractLong2ObjectSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ObjectSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<V>
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i) {
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
            return AbstractLong2ObjectSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2ObjectSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2ObjectSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2ObjectSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2ObjectSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2ObjectSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2ObjectSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2ObjectSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2ObjectSortedMap.this.long2ObjectEntrySet().iterator(new AbstractLong2ObjectMap.BasicEntry<Object>(from, null)));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2ObjectSortedMaps.fastIterator(AbstractLong2ObjectSortedMap.this));
        }
    }

}

