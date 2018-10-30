/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2CharMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.Double2CharMap;
import it.unimi.dsi.fastutil.doubles.Double2CharSortedMap;
import it.unimi.dsi.fastutil.doubles.Double2CharSortedMaps;
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

public abstract class AbstractDouble2CharSortedMap
extends AbstractDouble2CharMap
implements Double2CharSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractDouble2CharSortedMap() {
    }

    @Override
    public DoubleSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public CharCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements CharIterator {
        protected final ObjectBidirectionalIterator<Double2CharMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Double2CharMap.Entry> i) {
            this.i = i;
        }

        @Override
        public char nextChar() {
            return this.i.next().getCharValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractCharCollection {
        protected ValuesCollection() {
        }

        @Override
        public CharIterator iterator() {
            return new ValuesIterator(Double2CharSortedMaps.fastIterator(AbstractDouble2CharSortedMap.this));
        }

        @Override
        public boolean contains(char k) {
            return AbstractDouble2CharSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractDouble2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2CharSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements DoubleBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Double2CharMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Double2CharMap.Entry> i) {
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
            return AbstractDouble2CharSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractDouble2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDouble2CharSortedMap.this.clear();
        }

        @Override
        public DoubleComparator comparator() {
            return AbstractDouble2CharSortedMap.this.comparator();
        }

        @Override
        public double firstDouble() {
            return AbstractDouble2CharSortedMap.this.firstDoubleKey();
        }

        @Override
        public double lastDouble() {
            return AbstractDouble2CharSortedMap.this.lastDoubleKey();
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            return AbstractDouble2CharSortedMap.this.headMap(to).keySet();
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            return AbstractDouble2CharSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return AbstractDouble2CharSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return new KeySetIterator(AbstractDouble2CharSortedMap.this.double2CharEntrySet().iterator(new AbstractDouble2CharMap.BasicEntry(from, '\u0000')));
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return new KeySetIterator(Double2CharSortedMaps.fastIterator(AbstractDouble2CharSortedMap.this));
        }
    }

}

