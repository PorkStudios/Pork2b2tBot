/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSortedSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMaps;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractInt2ObjectSortedMap<V>
extends AbstractInt2ObjectMap<V>
implements Int2ObjectSortedMap<V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractInt2ObjectSortedMap() {
    }

    @Override
    public IntSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ObjectCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> i) {
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
    extends AbstractObjectCollection<V> {
        protected ValuesCollection() {
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ValuesIterator(Int2ObjectSortedMaps.fastIterator(AbstractInt2ObjectSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractInt2ObjectSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractInt2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2ObjectSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<V>
    implements IntBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> i) {
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
            return AbstractInt2ObjectSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractInt2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractInt2ObjectSortedMap.this.clear();
        }

        @Override
        public IntComparator comparator() {
            return AbstractInt2ObjectSortedMap.this.comparator();
        }

        @Override
        public int firstInt() {
            return AbstractInt2ObjectSortedMap.this.firstIntKey();
        }

        @Override
        public int lastInt() {
            return AbstractInt2ObjectSortedMap.this.lastIntKey();
        }

        @Override
        public IntSortedSet headSet(int to) {
            return AbstractInt2ObjectSortedMap.this.headMap(to).keySet();
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return AbstractInt2ObjectSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return AbstractInt2ObjectSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return new KeySetIterator(AbstractInt2ObjectSortedMap.this.int2ObjectEntrySet().iterator(new AbstractInt2ObjectMap.BasicEntry<Object>(from, null)));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return new KeySetIterator(Int2ObjectSortedMaps.fastIterator(AbstractInt2ObjectSortedMap.this));
        }
    }

}

