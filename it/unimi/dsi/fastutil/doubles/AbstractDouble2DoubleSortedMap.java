/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDouble2DoubleMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.Double2DoubleMap;
import it.unimi.dsi.fastutil.doubles.Double2DoubleSortedMap;
import it.unimi.dsi.fastutil.doubles.Double2DoubleSortedMaps;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractDouble2DoubleSortedMap
extends AbstractDouble2DoubleMap
implements Double2DoubleSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractDouble2DoubleSortedMap() {
    }

    @Override
    public DoubleSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public DoubleCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements DoubleIterator {
        protected final ObjectBidirectionalIterator<Double2DoubleMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Double2DoubleMap.Entry> i) {
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
            return new ValuesIterator(Double2DoubleSortedMaps.fastIterator(AbstractDouble2DoubleSortedMap.this));
        }

        @Override
        public boolean contains(double k) {
            return AbstractDouble2DoubleSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractDouble2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2DoubleSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements DoubleBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Double2DoubleMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Double2DoubleMap.Entry> i) {
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
            return AbstractDouble2DoubleSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractDouble2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2DoubleSortedMap.this.clear();
        }

        @Override
        public DoubleComparator comparator() {
            return AbstractDouble2DoubleSortedMap.this.comparator();
        }

        @Override
        public double firstDouble() {
            return AbstractDouble2DoubleSortedMap.this.firstDoubleKey();
        }

        @Override
        public double lastDouble() {
            return AbstractDouble2DoubleSortedMap.this.lastDoubleKey();
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            return AbstractDouble2DoubleSortedMap.this.headMap(to).keySet();
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            return AbstractDouble2DoubleSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return AbstractDouble2DoubleSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return new KeySetIterator(AbstractDouble2DoubleSortedMap.this.double2DoubleEntrySet().iterator(new AbstractDouble2DoubleMap.BasicEntry(from, 0.0)));
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return new KeySetIterator(Double2DoubleSortedMaps.fastIterator(AbstractDouble2DoubleSortedMap.this));
        }
    }

}

