/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDouble2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceSortedMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceSortedMaps;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractDouble2ReferenceSortedMap<V>
extends AbstractDouble2ReferenceMap<V>
implements Double2ReferenceSortedMap<V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractDouble2ReferenceSortedMap() {
    }

    @Override
    public DoubleSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ReferenceCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> i) {
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
            return new ValuesIterator(Double2ReferenceSortedMaps.fastIterator(AbstractDouble2ReferenceSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractDouble2ReferenceSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractDouble2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2ReferenceSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<V>
    implements DoubleBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> i) {
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
            return AbstractDouble2ReferenceSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractDouble2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2ReferenceSortedMap.this.clear();
        }

        @Override
        public DoubleComparator comparator() {
            return AbstractDouble2ReferenceSortedMap.this.comparator();
        }

        @Override
        public double firstDouble() {
            return AbstractDouble2ReferenceSortedMap.this.firstDoubleKey();
        }

        @Override
        public double lastDouble() {
            return AbstractDouble2ReferenceSortedMap.this.lastDoubleKey();
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            return AbstractDouble2ReferenceSortedMap.this.headMap(to).keySet();
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            return AbstractDouble2ReferenceSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return AbstractDouble2ReferenceSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return new KeySetIterator(AbstractDouble2ReferenceSortedMap.this.double2ReferenceEntrySet().iterator(new AbstractDouble2ReferenceMap.BasicEntry<Object>(from, null)));
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return new KeySetIterator(Double2ReferenceSortedMaps.fastIterator(AbstractDouble2ReferenceSortedMap.this));
        }
    }

}

