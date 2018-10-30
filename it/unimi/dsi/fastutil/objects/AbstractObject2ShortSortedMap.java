/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObject2ShortMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortSortedMap;
import it.unimi.dsi.fastutil.objects.Object2ShortSortedMaps;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public abstract class AbstractObject2ShortSortedMap<K>
extends AbstractObject2ShortMap<K>
implements Object2ShortSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2ShortSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public ShortCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements ShortIterator {
        protected final ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public short nextShort() {
            return this.i.next().getShortValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractShortCollection {
        protected ValuesCollection() {
        }

        @Override
        public ShortIterator iterator() {
            return new ValuesIterator(Object2ShortSortedMaps.fastIterator(AbstractObject2ShortSortedMap.this));
        }

        @Override
        public boolean contains(short k) {
            return AbstractObject2ShortSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2ShortSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i) {
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
            return AbstractObject2ShortSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2ShortSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2ShortSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2ShortSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2ShortSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2ShortSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2ShortSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2ShortSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2ShortSortedMap.this.object2ShortEntrySet().iterator(new AbstractObject2ShortMap.BasicEntry<K>(from, 0)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2ShortSortedMaps.fastIterator(AbstractObject2ShortSortedMap.this));
        }
    }

}

