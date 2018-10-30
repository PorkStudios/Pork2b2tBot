/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2CharMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2CharMap;
import it.unimi.dsi.fastutil.ints.Int2CharSortedMap;
import it.unimi.dsi.fastutil.ints.Int2CharSortedMaps;
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

public abstract class AbstractInt2CharSortedMap
extends AbstractInt2CharMap
implements Int2CharSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2CharSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public CharCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements CharIterator {
        protected final ObjectBidirectionalIterator<Int2CharMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2CharMap.Entry> i) {
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
            return new ValuesIterator(Int2CharSortedMaps.fastIterator(AbstractInt2CharSortedMap.this));
        }

        @Override
        public boolean contains(char k) {
            return AbstractInt2CharSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2CharSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2CharMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2CharMap.Entry> i) {
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
            return AbstractInt2CharSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2CharSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2CharSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2CharSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2CharSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2CharSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2CharSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2CharSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2CharSortedMap.this.int2CharEntrySet().iterator(new AbstractInt2CharMap.BasicEntry(from, '\u0000')));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2CharSortedMaps.fastIterator(AbstractInt2CharSortedMap.this));
        }
    }

}

