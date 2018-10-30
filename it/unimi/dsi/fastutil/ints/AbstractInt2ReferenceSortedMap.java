/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2ReferenceMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceSortedMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceSortedMaps;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractInt2ReferenceSortedMap<V>
extends AbstractInt2ReferenceMap<V>
implements Int2ReferenceSortedMap<V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2ReferenceSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ReferenceCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> i) {
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
            return new ValuesIterator(Int2ReferenceSortedMaps.fastIterator(AbstractInt2ReferenceSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractInt2ReferenceSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2ReferenceSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<V>
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> i) {
            this.i = i;
        }

        @Override
        public int nextInt() {
            return this.i.next().getIntKey();
        }

        @Override
        public int previousInt() {
            return this.i.previous().getIntKey();
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
    extends AbstractIntSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(int k) {
            return AbstractInt2ReferenceSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2ReferenceSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2ReferenceSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2ReferenceSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2ReferenceSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2ReferenceSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2ReferenceSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2ReferenceSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2ReferenceSortedMap.this.int2ReferenceEntrySet().iterator(new AbstractInt2ReferenceMap.BasicEntry<Object>(from, null)));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2ReferenceSortedMaps.fastIterator(AbstractInt2ReferenceSortedMap.this));
        }
    }

}

