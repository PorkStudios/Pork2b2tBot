/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2DoubleMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractInt2DoubleSortedMap
extends AbstractInt2DoubleMap
implements Int2DoubleSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2DoubleSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public DoubleCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements DoubleIterator {
        protected final ObjectBidirectionalIterator<Int2DoubleMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2DoubleMap.Entry> i) {
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
            return new ValuesIterator(Int2DoubleSortedMaps.fastIterator(AbstractInt2DoubleSortedMap.this));
        }

        @Override
        public boolean contains(double k) {
            return AbstractInt2DoubleSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2DoubleSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2DoubleMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2DoubleMap.Entry> i) {
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
            return AbstractInt2DoubleSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2DoubleSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2DoubleSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2DoubleSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2DoubleSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2DoubleSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2DoubleSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2DoubleSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2DoubleSortedMap.this.int2DoubleEntrySet().iterator(new AbstractInt2DoubleMap.BasicEntry(from, 0.0)));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2DoubleSortedMaps.fastIterator(AbstractInt2DoubleSortedMap.this));
        }
    }

}

