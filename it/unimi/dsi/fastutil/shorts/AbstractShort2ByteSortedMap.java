/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ByteMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteSortedMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteSortedMaps;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractShort2ByteSortedMap
extends AbstractShort2ByteMap
implements Short2ByteSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractShort2ByteSortedMap() {
    }

    @Override
    public ShortSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Short2ByteMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Short2ByteMap.Entry> i) {
            this.i = i;
        }

        @Override
        public byte nextByte() {
            return this.i.next().getByteValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractByteCollection {
        protected ValuesCollection() {
        }

        @Override
        public ByteIterator iterator() {
            return new ValuesIterator(Short2ByteSortedMaps.fastIterator(AbstractShort2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractShort2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractShort2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ShortBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Short2ByteMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Short2ByteMap.Entry> i) {
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
            return AbstractShort2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractShort2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractShort2ByteSortedMap.this.clear();
        }

        @Override
        public ShortComparator comparator() {
            return AbstractShort2ByteSortedMap.this.comparator();
        }

        @Override
        public short firstShort() {
            return AbstractShort2ByteSortedMap.this.firstShortKey();
        }

        @Override
        public short lastShort() {
            return AbstractShort2ByteSortedMap.this.lastShortKey();
        }

        @Override
        public ShortSortedSet headSet(short to) {
            return AbstractShort2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ShortSortedSet tailSet(short from) {
            return AbstractShort2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ShortSortedSet subSet(short from, short to) {
            return AbstractShort2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ShortBidirectionalIterator iterator(short from) {
            return new KeySetIterator(AbstractShort2ByteSortedMap.this.short2ByteEntrySet().iterator(new AbstractShort2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return new KeySetIterator(Short2ByteSortedMaps.fastIterator(AbstractShort2ByteSortedMap.this));
        }
    }

}

