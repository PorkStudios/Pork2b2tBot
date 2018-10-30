/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ObjectMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectSortedMaps;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractShort2ObjectSortedMap<V>
extends AbstractShort2ObjectMap<V>
implements Short2ObjectSortedMap<V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractShort2ObjectSortedMap() {
    }

    @Override
    public ShortSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ObjectCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> i) {
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
            return new ValuesIterator(Short2ObjectSortedMaps.fastIterator(AbstractShort2ObjectSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractShort2ObjectSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractShort2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2ObjectSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<V>
    implements ShortBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> i) {
            this.i = i;
        }

        @Override
        public short nextShort() {
            return this.i.next().getShortKey();
        }

        @Override
        public short previousShort() {
            return this.i.previous().getShortKey();
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
    extends AbstractShortSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(short k) {
            return AbstractShort2ObjectSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractShort2ObjectSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2ObjectSortedMap.this.clear();
        }

        @Override
        public ShortComparator comparator() {
            return AbstractShort2ObjectSortedMap.this.comparator();
        }

        @Override
        public short firstShort() {
            return AbstractShort2ObjectSortedMap.this.firstShortKey();
        }

        @Override
        public short lastShort() {
            return AbstractShort2ObjectSortedMap.this.lastShortKey();
        }

        @Override
        public ShortSortedSet headSet(short to) {
            return AbstractShort2ObjectSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ShortSortedSet tailSet(short from) {
            return AbstractShort2ObjectSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ShortSortedSet subSet(short from, short to) {
            return AbstractShort2ObjectSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeySetIterator(AbstractShort2ObjectSortedMap.this.short2ObjectEntrySet().iterator(new AbstractShort2ObjectMap.BasicEntry<Object>(from, null)));
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeySetIterator(Short2ObjectSortedMaps.fastIterator(AbstractShort2ObjectSortedMap.this));
        }
    }

}

