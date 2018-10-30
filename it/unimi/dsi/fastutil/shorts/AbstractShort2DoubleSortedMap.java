/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2DoubleMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2DoubleMap;
import it.unimi.dsi.fastutil.shorts.Short2DoubleSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2DoubleSortedMaps;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractShort2DoubleSortedMap
extends AbstractShort2DoubleMap
implements Short2DoubleSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractShort2DoubleSortedMap() {
    }

    @Override
    public ShortSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public DoubleCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements DoubleIterator {
        protected final ObjectBidirectionalIterator<Short2DoubleMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Short2DoubleMap.Entry> i) {
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
            return new ValuesIterator(Short2DoubleSortedMaps.fastIterator(AbstractShort2DoubleSortedMap.this));
        }

        @Override
        public boolean contains(double k) {
            return AbstractShort2DoubleSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractShort2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2DoubleSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ShortBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Short2DoubleMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Short2DoubleMap.Entry> i) {
            this.i = i;
        }

        @Override
        public short nextShort() {
            return this.i.next().getShortKey();
        }

        @Override
        public short previousShort() {
            return this.i.previous().getShortKey();
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
    extends AbstractShortSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(short k) {
            return AbstractShort2DoubleSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractShort2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2DoubleSortedMap.this.clear();
        }

        @Override
        public ShortComparator comparator() {
            return AbstractShort2DoubleSortedMap.this.comparator();
        }

        @Override
        public short firstShort() {
            return AbstractShort2DoubleSortedMap.this.firstShortKey();
        }

        @Override
        public short lastShort() {
            return AbstractShort2DoubleSortedMap.this.lastShortKey();
        }

        @Override
        public ShortSortedSet headSet(short to) {
            return AbstractShort2DoubleSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ShortSortedSet tailSet(short from) {
            return AbstractShort2DoubleSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ShortSortedSet subSet(short from, short to) {
            return AbstractShort2DoubleSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeySetIterator(AbstractShort2DoubleSortedMap.this.short2DoubleEntrySet().iterator(new AbstractShort2DoubleMap.BasicEntry(from, 0.0)));
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeySetIterator(Short2DoubleSortedMaps.fastIterator(AbstractShort2DoubleSortedMap.this));
        }
    }

}

