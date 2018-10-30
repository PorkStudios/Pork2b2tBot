/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2ByteMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteSortedMap;
import it.unimi.dsi.fastutil.ints.Int2ByteSortedMaps;
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

public abstract class AbstractInt2ByteSortedMap
extends AbstractInt2ByteMap
implements Int2ByteSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2ByteSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Int2ByteMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2ByteMap.Entry> i) {
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
            return new ValuesIterator(Int2ByteSortedMaps.fastIterator(AbstractInt2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractInt2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2ByteMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2ByteMap.Entry> i) {
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
            return AbstractInt2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2ByteSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2ByteSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2ByteSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2ByteSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2ByteSortedMap.this.int2ByteEntrySet().iterator(new AbstractInt2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2ByteSortedMaps.fastIterator(AbstractInt2ByteSortedMap.this));
        }
    }

}

