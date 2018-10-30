/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2FloatMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatSortedMap;
import it.unimi.dsi.fastutil.ints.Int2FloatSortedMaps;
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

public abstract class AbstractInt2FloatSortedMap
extends AbstractInt2FloatMap
implements Int2FloatSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2FloatSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public FloatCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements FloatIterator {
        protected final ObjectBidirectionalIterator<Int2FloatMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2FloatMap.Entry> i) {
            this.i = i;
        }

        @Override
        public float nextFloat() {
            return this.i.next().getFloatValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractFloatCollection {
        protected ValuesCollection() {
        }

        @Override
        public FloatIterator iterator() {
            return new ValuesIterator(Int2FloatSortedMaps.fastIterator(AbstractInt2FloatSortedMap.this));
        }

        @Override
        public boolean contains(float k) {
            return AbstractInt2FloatSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2FloatSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2FloatMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2FloatMap.Entry> i) {
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
            return AbstractInt2FloatSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2FloatSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2FloatSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2FloatSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2FloatSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2FloatSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2FloatSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2FloatSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2FloatSortedMap.this.int2FloatEntrySet().iterator(new AbstractInt2FloatMap.BasicEntry(from, 0.0f)));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2FloatSortedMaps.fastIterator(AbstractInt2FloatSortedMap.this));
        }
    }

}

