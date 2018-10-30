/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractByte2ShortSortedMap
extends AbstractByte2ShortMap
implements Byte2ShortSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2ShortSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ShortCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ShortIterator {
        protected final ObjectBidirectionalIterator<Byte2ShortMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2ShortMap.Entry> i) {
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
            return new ValuesIterator(Byte2ShortSortedMaps.fastIterator(AbstractByte2ShortSortedMap.this));
        }

        @Override
        public boolean contains(short k) {
            return AbstractByte2ShortSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2ShortSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2ShortMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2ShortMap.Entry> i) {
            this.i = i;
        }

        @Override
        public byte nextByte() {
            return this.i.next().getByteKey();
        }

        @Override
        public byte previousByte() {
            return this.i.previous().getByteKey();
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
    extends AbstractByteSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(byte k) {
            return AbstractByte2ShortSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2ShortSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2ShortSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2ShortSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2ShortSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2ShortSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2ShortSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2ShortSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2ShortSortedMap.this.byte2ShortEntrySet().iterator(new AbstractByte2ShortMap.BasicEntry(from, 0)));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2ShortSortedMaps.fastIterator(AbstractByte2ShortSortedMap.this));
        }
    }

}

