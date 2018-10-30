/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2BooleanMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanSortedMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanSortedMaps;
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

public abstract class AbstractInt2BooleanSortedMap
extends AbstractInt2BooleanMap
implements Int2BooleanSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2BooleanSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Int2BooleanMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2BooleanMap.Entry> i) {
            this.i = i;
        }

        @Override
        public boolean nextBoolean() {
            return this.i.next().getBooleanValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractBooleanCollection {
        protected ValuesCollection() {
        }

        @Override
        public BooleanIterator iterator() {
            return new ValuesIterator(Int2BooleanSortedMaps.fastIterator(AbstractInt2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractInt2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2BooleanMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2BooleanMap.Entry> i) {
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
            return AbstractInt2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2BooleanSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2BooleanSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2BooleanSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2BooleanSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2BooleanSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2BooleanSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2BooleanSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2BooleanSortedMap.this.int2BooleanEntrySet().iterator(new AbstractInt2BooleanMap.BasicEntry(from, false)));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2BooleanSortedMaps.fastIterator(AbstractInt2BooleanSortedMap.this));
        }
    }

}

