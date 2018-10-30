/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2BooleanMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanSortedMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanSortedMaps;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractLong2BooleanSortedMap
extends AbstractLong2BooleanMap
implements Long2BooleanSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractLong2BooleanSortedMap() {
    }

    @Override
    public LongSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Long2BooleanMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Long2BooleanMap.Entry> i) {
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
            return new ValuesIterator(Long2BooleanSortedMaps.fastIterator(AbstractLong2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractLong2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractLong2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements LongBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Long2BooleanMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Long2BooleanMap.Entry> i) {
            this.i = i;
        }

        @Override
        public long nextLong() {
            return this.i.next().getLongKey();
        }

        @Override
        public long previousLong() {
            return this.i.previous().getLongKey();
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
    extends AbstractLongSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(long k) {
            return AbstractLong2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractLong2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractLong2BooleanSortedMap.this.clear();
        }

        @Override
        public LongComparator comparator() {
            return AbstractLong2BooleanSortedMap.this.comparator();
        }

        @Override
        public long firstLong() {
            return AbstractLong2BooleanSortedMap.this.firstLongKey();
        }

        @Override
        public long lastLong() {
            return AbstractLong2BooleanSortedMap.this.lastLongKey();
        }

        @Override
        public LongSortedSet headSet(long to) {
            return AbstractLong2BooleanSortedMap.this.headMap(to).keySet();
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return AbstractLong2BooleanSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return AbstractLong2BooleanSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new KeySetIterator(AbstractLong2BooleanSortedMap.this.long2BooleanEntrySet().iterator(new AbstractLong2BooleanMap.BasicEntry(from, false)));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new KeySetIterator(Long2BooleanSortedMaps.fastIterator(AbstractLong2BooleanSortedMap.this));
        }
    }

}

