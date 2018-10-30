/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2LongMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2LongMap;
import it.unimi.dsi.fastutil.shorts.Short2LongSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2LongSortedMaps;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractShort2LongSortedMap
extends AbstractShort2LongMap
implements Short2LongSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractShort2LongSortedMap() {
    }

    @Override
    public ShortSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Short2LongMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Short2LongMap.Entry> i) {
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
            return new ValuesIterator(Short2LongSortedMaps.fastIterator(AbstractShort2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractShort2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractShort2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ShortBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Short2LongMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Short2LongMap.Entry> i) {
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
            return AbstractShort2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractShort2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2LongSortedMap.this.clear();
        }

        @Override
        public ShortComparator comparator() {
            return AbstractShort2LongSortedMap.this.comparator();
        }

        @Override
        public short firstShort() {
            return AbstractShort2LongSortedMap.this.firstShortKey();
        }

        @Override
        public short lastShort() {
            return AbstractShort2LongSortedMap.this.lastShortKey();
        }

        @Override
        public ShortSortedSet headSet(short to) {
            return AbstractShort2LongSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ShortSortedSet tailSet(short from) {
            return AbstractShort2LongSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ShortSortedSet subSet(short from, short to) {
            return AbstractShort2LongSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeySetIterator(AbstractShort2LongSortedMap.this.short2LongEntrySet().iterator(new AbstractShort2LongMap.BasicEntry(from, 0L)));
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeySetIterator(Short2LongSortedMaps.fastIterator(AbstractShort2LongSortedMap.this));
        }
    }

}

