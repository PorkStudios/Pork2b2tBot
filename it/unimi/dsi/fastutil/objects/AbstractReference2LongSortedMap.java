/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2LongMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2LongSortedMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public abstract class AbstractReference2LongSortedMap<K>
extends AbstractReference2LongMap<K>
implements Reference2LongSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractReference2LongSortedMap() {
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public long nextLong() {
            return this.i.next().getLongValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractLongCollection {
        protected ValuesCollection() {
        }

        @Override
        public LongIterator iterator() {
            return new ValuesIterator(Reference2LongSortedMaps.fastIterator(AbstractReference2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractReference2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractReference2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return this.i.next().getKey();
        }

        @Override
        public K previous() {
            return this.i.previous().getKey();
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
    extends AbstractReferenceSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractReference2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractReference2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2LongSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractReference2LongSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractReference2LongSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractReference2LongSortedMap.this.lastKey();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return AbstractReference2LongSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return AbstractReference2LongSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return AbstractReference2LongSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractReference2LongSortedMap.this.reference2LongEntrySet().iterator(new AbstractReference2LongMap.BasicEntry<K>(from, 0L)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Reference2LongSortedMaps.fastIterator(AbstractReference2LongSortedMap.this));
        }
    }

}

