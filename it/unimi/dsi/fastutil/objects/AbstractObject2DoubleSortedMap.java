/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2DoubleMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleSortedMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleSortedMaps;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public abstract class AbstractObject2DoubleSortedMap<K>
extends AbstractObject2DoubleMap<K>
implements Object2DoubleSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2DoubleSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public DoubleCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements DoubleIterator {
        protected final ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> i) {
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
            return new ValuesIterator(Object2DoubleSortedMaps.fastIterator(AbstractObject2DoubleSortedMap.this));
        }

        @Override
        public boolean contains(double k) {
            return AbstractObject2DoubleSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2DoubleSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return this.i.next().getKey();
        }

        @Override
        public K previous() {
            return this.i.previous().getKey();
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
    extends AbstractObjectSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractObject2DoubleSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2DoubleSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2DoubleSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2DoubleSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2DoubleSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2DoubleSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2DoubleSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2DoubleSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2DoubleSortedMap.this.object2DoubleEntrySet().iterator(new AbstractObject2DoubleMap.BasicEntry<K>(from, 0.0)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2DoubleSortedMaps.fastIterator(AbstractObject2DoubleSortedMap.this));
        }
    }

}

