/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2LongMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongSortedMap;
import it.unimi.dsi.fastutil.ints.Int2LongSortedMaps;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractInt2LongSortedMap
extends AbstractInt2LongMap
implements Int2LongSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2LongSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Int2LongMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2LongMap.Entry> i) {
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
            return new ValuesIterator(Int2LongSortedMaps.fastIterator(AbstractInt2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractInt2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2LongMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2LongMap.Entry> i) {
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
            return AbstractInt2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2LongSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2LongSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2LongSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2LongSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2LongSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2LongSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2LongSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2LongSortedMap.this.int2LongEntrySet().iterator(new AbstractInt2LongMap.BasicEntry(from, 0L)));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2LongSortedMaps.fastIterator(AbstractInt2LongSortedMap.this));
        }
    }

}

