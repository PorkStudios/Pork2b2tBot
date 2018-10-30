/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMaps;
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

public abstract class AbstractObject2IntSortedMap<K>
extends AbstractObject2IntMap<K>
implements Object2IntSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2IntSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public IntCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements IntIterator {
        protected final ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public int nextInt() {
            return this.i.next().getIntValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractIntCollection {
        protected ValuesCollection() {
        }

        @Override
        public IntIterator iterator() {
            return new ValuesIterator(Object2IntSortedMaps.fastIterator(AbstractObject2IntSortedMap.this));
        }

        @Override
        public boolean contains(int k) {
            return AbstractObject2IntSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2IntSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i) {
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
            return AbstractObject2IntSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2IntSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2IntSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2IntSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2IntSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2IntSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2IntSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2IntSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2IntSortedMap.this.object2IntEntrySet().iterator(new AbstractObject2IntMap.BasicEntry<K>(from, 0)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2IntSortedMaps.fastIterator(AbstractObject2IntSortedMap.this));
        }
    }

}

