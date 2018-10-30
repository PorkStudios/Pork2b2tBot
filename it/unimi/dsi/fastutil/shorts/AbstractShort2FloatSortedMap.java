/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2FloatMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2FloatMap;
import it.unimi.dsi.fastutil.shorts.Short2FloatSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2FloatSortedMaps;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractShort2FloatSortedMap
extends AbstractShort2FloatMap
implements Short2FloatSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractShort2FloatSortedMap() {
    }

    @Override
    public ShortSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public FloatCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements FloatIterator {
        protected final ObjectBidirectionalIterator<Short2FloatMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Short2FloatMap.Entry> i) {
            this.i = i;
        }

        @Override
        public float nextFloat() {
            return this.i.next().getFloatValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractFloatCollection {
        protected ValuesCollection() {
        }

        @Override
        public FloatIterator iterator() {
            return new ValuesIterator(Short2FloatSortedMaps.fastIterator(AbstractShort2FloatSortedMap.this));
        }

        @Override
        public boolean contains(float k) {
            return AbstractShort2FloatSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractShort2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2FloatSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ShortBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Short2FloatMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Short2FloatMap.Entry> i) {
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
            return AbstractShort2FloatSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractShort2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2FloatSortedMap.this.clear();
        }

        @Override
        public ShortComparator comparator() {
            return AbstractShort2FloatSortedMap.this.comparator();
        }

        @Override
        public short firstShort() {
            return AbstractShort2FloatSortedMap.this.firstShortKey();
        }

        @Override
        public short lastShort() {
            return AbstractShort2FloatSortedMap.this.lastShortKey();
        }

        @Override
        public ShortSortedSet headSet(short to) {
            return AbstractShort2FloatSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ShortSortedSet tailSet(short from) {
            return AbstractShort2FloatSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ShortSortedSet subSet(short from, short to) {
            return AbstractShort2FloatSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeySetIterator(AbstractShort2FloatSortedMap.this.short2FloatEntrySet().iterator(new AbstractShort2FloatMap.BasicEntry(from, 0.0f)));
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeySetIterator(Short2FloatSortedMaps.fastIterator(AbstractShort2FloatSortedMap.this));
        }
    }

}

