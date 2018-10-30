/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanSortedMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public abstract class AbstractReference2BooleanSortedMap<K>
extends AbstractReference2BooleanMap<K>
implements Reference2BooleanSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractReference2BooleanSortedMap() {
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public boolean nextBoolean() {
            return this.i.next().getBooleanValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractBooleanCollection {
        protected ValuesCollection() {
        }

        @Override
        public BooleanIterator iterator() {
            return new ValuesIterator(Reference2BooleanSortedMaps.fastIterator(AbstractReference2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractReference2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractReference2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i) {
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
            return AbstractReference2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractReference2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2BooleanSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractReference2BooleanSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractReference2BooleanSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractReference2BooleanSortedMap.this.lastKey();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return AbstractReference2BooleanSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return AbstractReference2BooleanSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return AbstractReference2BooleanSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractReference2BooleanSortedMap.this.reference2BooleanEntrySet().iterator(new AbstractReference2BooleanMap.BasicEntry<K>(from, false)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Reference2BooleanSortedMaps.fastIterator(AbstractReference2BooleanSortedMap.this));
        }
    }

}

