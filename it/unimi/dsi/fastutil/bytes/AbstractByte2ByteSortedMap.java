/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ByteMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2ByteMap;
import it.unimi.dsi.fastutil.bytes.Byte2ByteSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2ByteSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractByte2ByteSortedMap
extends AbstractByte2ByteMap
implements Byte2ByteSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2ByteSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry> i) {
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
            return new ValuesIterator(Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractByte2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry> i) {
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
            return AbstractByte2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2ByteSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2ByteSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2ByteSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2ByteSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2ByteSortedMap.this.byte2ByteEntrySet().iterator(new AbstractByte2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this));
        }
    }

}

