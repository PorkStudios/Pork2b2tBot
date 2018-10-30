/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDouble2LongMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.Double2LongMap;
import it.unimi.dsi.fastutil.doubles.Double2LongSortedMap;
import it.unimi.dsi.fastutil.doubles.Double2LongSortedMaps;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractDouble2LongSortedMap
extends AbstractDouble2LongMap
implements Double2LongSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractDouble2LongSortedMap() {
    }

    @Override
    public DoubleSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Double2LongMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Double2LongMap.Entry> i) {
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
            return new ValuesIterator(Double2LongSortedMaps.fastIterator(AbstractDouble2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractDouble2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractDouble2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements DoubleBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Double2LongMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Double2LongMap.Entry> i) {
            this.i = i;
        }

        @Override
        public double nextDouble() {
            return this.i.next().getDoubleKey();
        }

        @Override
        public double previousDouble() {
            return this.i.previous().getDoubleKey();
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
    extends AbstractDoubleSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(double k) {
            return AbstractDouble2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractDouble2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2LongSortedMap.this.clear();
        }

        @Override
        public DoubleComparator comparator() {
            return AbstractDouble2LongSortedMap.this.comparator();
        }

        @Override
        public double firstDouble() {
            return AbstractDouble2LongSortedMap.this.firstDoubleKey();
        }

        @Override
        public double lastDouble() {
            return AbstractDouble2LongSortedMap.this.lastDoubleKey();
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            return AbstractDouble2LongSortedMap.this.headMap(to).keySet();
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            return AbstractDouble2LongSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return AbstractDouble2LongSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return new KeySetIterator(AbstractDouble2LongSortedMap.this.double2LongEntrySet().iterator(new AbstractDouble2LongMap.BasicEntry(from, 0L)));
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return new KeySetIterator(Double2LongSortedMaps.fastIterator(AbstractDouble2LongSortedMap.this));
        }
    }

}

