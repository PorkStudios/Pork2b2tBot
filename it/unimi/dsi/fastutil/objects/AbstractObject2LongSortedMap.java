/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2LongMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongSortedMap;
import it.unimi.dsi.fastutil.objects.Object2LongSortedMaps;
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

public abstract class AbstractObject2LongSortedMap<K>
extends AbstractObject2LongMap<K>
implements Object2LongSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2LongSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public long nextLong() {
            return this.i.next().getLongValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractLongCollection {
        protected ValuesCollection() {
        }

        @Override
        public LongIterator iterator() {
            return new ValuesIterator(Object2LongSortedMaps.fastIterator(AbstractObject2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractObject2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i) {
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
            return AbstractObject2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2LongSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2LongSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2LongSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2LongSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2LongSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2LongSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2LongSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2LongSortedMap.this.object2LongEntrySet().iterator(new AbstractObject2LongMap.BasicEntry<K>(from, 0L)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2LongSortedMaps.fastIterator(AbstractObject2LongSortedMap.this));
        }
    }

}

