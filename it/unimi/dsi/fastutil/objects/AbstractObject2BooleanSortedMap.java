/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanSortedMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanSortedMaps;
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

public abstract class AbstractObject2BooleanSortedMap<K>
extends AbstractObject2BooleanMap<K>
implements Object2BooleanSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2BooleanSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i) {
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
            return new ValuesIterator(Object2BooleanSortedMaps.fastIterator(AbstractObject2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractObject2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i) {
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
            return AbstractObject2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2BooleanSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2BooleanSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2BooleanSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2BooleanSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2BooleanSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2BooleanSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2BooleanSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2BooleanSortedMap.this.object2BooleanEntrySet().iterator(new AbstractObject2BooleanMap.BasicEntry<K>(from, false)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2BooleanSortedMaps.fastIterator(AbstractObject2BooleanSortedMap.this));
        }
    }

}

