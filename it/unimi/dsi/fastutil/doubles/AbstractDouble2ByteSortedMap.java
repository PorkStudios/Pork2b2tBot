/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ByteMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.Double2ByteMap;
import it.unimi.dsi.fastutil.doubles.Double2ByteSortedMap;
import it.unimi.dsi.fastutil.doubles.Double2ByteSortedMaps;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
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

public abstract class AbstractDouble2ByteSortedMap
extends AbstractDouble2ByteMap
implements Double2ByteSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractDouble2ByteSortedMap() {
    }

    @Override
    public DoubleSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Double2ByteMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Double2ByteMap.Entry> i) {
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
            return new ValuesIterator(Double2ByteSortedMaps.fastIterator(AbstractDouble2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractDouble2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractDouble2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements DoubleBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Double2ByteMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Double2ByteMap.Entry> i) {
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
            return AbstractDouble2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractDouble2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2ByteSortedMap.this.clear();
        }

        @Override
        public DoubleComparator comparator() {
            return AbstractDouble2ByteSortedMap.this.comparator();
        }

        @Override
        public double firstDouble() {
            return AbstractDouble2ByteSortedMap.this.firstDoubleKey();
        }

        @Override
        public double lastDouble() {
            return AbstractDouble2ByteSortedMap.this.lastDoubleKey();
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            return AbstractDouble2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            return AbstractDouble2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return AbstractDouble2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return new KeySetIterator(AbstractDouble2ByteSortedMap.this.double2ByteEntrySet().iterator(new AbstractDouble2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return new KeySetIterator(Double2ByteSortedMaps.fastIterator(AbstractDouble2ByteSortedMap.this));
        }
    }

}

