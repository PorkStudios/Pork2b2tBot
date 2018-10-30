/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2LongMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2LongMap;
import it.unimi.dsi.fastutil.bytes.Byte2LongSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2LongSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractByte2LongSortedMap
extends AbstractByte2LongMap
implements Byte2LongSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2LongSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Byte2LongMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2LongMap.Entry> i) {
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
            return new ValuesIterator(Byte2LongSortedMaps.fastIterator(AbstractByte2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractByte2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2LongMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2LongMap.Entry> i) {
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
            return AbstractByte2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2LongSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2LongSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2LongSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2LongSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2LongSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2LongSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2LongSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2LongSortedMap.this.byte2LongEntrySet().iterator(new AbstractByte2LongMap.BasicEntry(from, 0L)));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2LongSortedMaps.fastIterator(AbstractByte2LongSortedMap.this));
        }
    }

}

