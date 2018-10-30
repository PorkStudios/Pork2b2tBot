/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2BooleanMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanSortedMaps;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractShort2BooleanSortedMap
extends AbstractShort2BooleanMap
implements Short2BooleanSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractShort2BooleanSortedMap() {
    }

    @Override
    public ShortSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Short2BooleanMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Short2BooleanMap.Entry> i) {
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
            return new ValuesIterator(Short2BooleanSortedMaps.fastIterator(AbstractShort2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractShort2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractShort2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ShortBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Short2BooleanMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Short2BooleanMap.Entry> i) {
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
            return AbstractShort2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractShort2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2BooleanSortedMap.this.clear();
        }

        @Override
        public ShortComparator comparator() {
            return AbstractShort2BooleanSortedMap.this.comparator();
        }

        @Override
        public short firstShort() {
            return AbstractShort2BooleanSortedMap.this.firstShortKey();
        }

        @Override
        public short lastShort() {
            return AbstractShort2BooleanSortedMap.this.lastShortKey();
        }

        @Override
        public ShortSortedSet headSet(short to) {
            return AbstractShort2BooleanSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ShortSortedSet tailSet(short from) {
            return AbstractShort2BooleanSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ShortSortedSet subSet(short from, short to) {
            return AbstractShort2BooleanSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeySetIterator(AbstractShort2BooleanSortedMap.this.short2BooleanEntrySet().iterator(new AbstractShort2BooleanMap.BasicEntry(from, false)));
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeySetIterator(Short2BooleanSortedMaps.fastIterator(AbstractShort2BooleanSortedMap.this));
        }
    }

}

