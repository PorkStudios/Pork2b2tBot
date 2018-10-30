/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractReference2ObjectMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectSortedMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public abstract class AbstractReference2ObjectSortedMap<K, V>
extends AbstractReference2ObjectMap<K, V>
implements Reference2ObjectSortedMap<K, V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractReference2ObjectSortedMap() {
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public ObjectCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K, V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i) {
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
            return new ValuesIterator(Reference2ObjectSortedMaps.fastIterator(AbstractReference2ObjectSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractReference2ObjectSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractReference2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2ObjectSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K, V>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i) {
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
    extends AbstractReferenceSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractReference2ObjectSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractReference2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2ObjectSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractReference2ObjectSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractReference2ObjectSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractReference2ObjectSortedMap.this.lastKey();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return AbstractReference2ObjectSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return AbstractReference2ObjectSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return AbstractReference2ObjectSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractReference2ObjectSortedMap.this.reference2ObjectEntrySet().iterator(new AbstractReference2ObjectMap.BasicEntry<K, Object>(from, null)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Reference2ObjectSortedMaps.fastIterator(AbstractReference2ObjectSortedMap.this));
        }
    }

}

