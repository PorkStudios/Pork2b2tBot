/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ShortMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShortSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2ShortMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortSortedMaps;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractShort2ShortSortedMap
extends AbstractShort2ShortMap
implements Short2ShortSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractShort2ShortSortedMap() {
    }

    @Override
    public ShortSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ShortCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ShortIterator {
        protected final ObjectBidirectionalIterator<Short2ShortMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Short2ShortMap.Entry> i) {
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
            return new ValuesIterator(Short2ShortSortedMaps.fastIterator(AbstractShort2ShortSortedMap.this));
        }

        @Override
        public boolean contains(short k) {
            return AbstractShort2ShortSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractShort2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2ShortSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ShortBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Short2ShortMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Short2ShortMap.Entry> i) {
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
            return AbstractShort2ShortSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractShort2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2ShortSortedMap.this.clear();
        }

        @Override
        public ShortComparator comparator() {
            return AbstractShort2ShortSortedMap.this.comparator();
        }

        @Override
        public short firstShort() {
            return AbstractShort2ShortSortedMap.this.firstShortKey();
        }

        @Override
        public short lastShort() {
            return AbstractShort2ShortSortedMap.this.lastShortKey();
        }

        @Override
        public ShortSortedSet headSet(short to) {
            return AbstractShort2ShortSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ShortSortedSet tailSet(short from) {
            return AbstractShort2ShortSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ShortSortedSet subSet(short from, short to) {
            return AbstractShort2ShortSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeySetIterator(AbstractShort2ShortSortedMap.this.short2ShortEntrySet().iterator(new AbstractShort2ShortMap.BasicEntry(from, 0)));
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeySetIterator(Short2ShortSortedMaps.fastIterator(AbstractShort2ShortSortedMap.this));
        }
    }

}

