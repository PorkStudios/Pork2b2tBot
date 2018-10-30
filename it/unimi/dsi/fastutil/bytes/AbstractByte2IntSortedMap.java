/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2IntMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractByte2IntSortedMap
extends AbstractByte2IntMap
implements Byte2IntSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2IntSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public IntCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements IntIterator {
        protected final ObjectBidirectionalIterator<Byte2IntMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2IntMap.Entry> i) {
            this.i = i;
        }

        @Override
        public int nextInt() {
            return this.i.next().getIntValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractIntCollection {
        protected ValuesCollection() {
        }

        @Override
        public IntIterator iterator() {
            return new ValuesIterator(Byte2IntSortedMaps.fastIterator(AbstractByte2IntSortedMap.this));
        }

        @Override
        public boolean contains(int k) {
            return AbstractByte2IntSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2IntSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2IntMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2IntMap.Entry> i) {
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
            return AbstractByte2IntSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2IntSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2IntSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2IntSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2IntSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2IntSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2IntSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2IntSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2IntSortedMap.this.byte2IntEntrySet().iterator(new AbstractByte2IntMap.BasicEntry(from, 0)));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2IntSortedMaps.fastIterator(AbstractByte2IntSortedMap.this));
        }
    }

}

