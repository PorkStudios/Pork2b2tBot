/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2ByteMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteSortedMap;
import it.unimi.dsi.fastutil.objects.Object2ByteSortedMaps;
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

public abstract class AbstractObject2ByteSortedMap<K>
extends AbstractObject2ByteMap<K>
implements Object2ByteSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2ByteSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i) {
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
            return new ValuesIterator(Object2ByteSortedMaps.fastIterator(AbstractObject2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractObject2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i) {
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
            return AbstractObject2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2ByteSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2ByteSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2ByteSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2ByteSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2ByteSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2ByteSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2ByteSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2ByteSortedMap.this.object2ByteEntrySet().iterator(new AbstractObject2ByteMap.BasicEntry<K>(from, 0)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2ByteSortedMaps.fastIterator(AbstractObject2ByteSortedMap.this));
        }
    }

}

