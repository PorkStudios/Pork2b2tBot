/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2CharMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharSortedMap;
import it.unimi.dsi.fastutil.objects.Object2CharSortedMaps;
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

public abstract class AbstractObject2CharSortedMap<K>
extends AbstractObject2CharMap<K>
implements Object2CharSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2CharSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public CharCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<K>
    implements CharIterator {
        protected final ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i) {
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
            return new ValuesIterator(Object2CharSortedMaps.fastIterator(AbstractObject2CharSortedMap.this));
        }

        @Override
        public boolean contains(char k) {
            return AbstractObject2CharSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2CharSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i) {
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
            return AbstractObject2CharSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2CharSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2CharSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2CharSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2CharSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2CharSortedMap.this.headMap((Object)to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2CharSortedMap.this.tailMap((Object)from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2CharSortedMap.this.subMap((Object)from, (Object)to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2CharSortedMap.this.object2CharEntrySet().iterator(new AbstractObject2CharMap.BasicEntry<K>(from, '\u0000')));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2CharSortedMaps.fastIterator(AbstractObject2CharSortedMap.this));
        }
    }

}

