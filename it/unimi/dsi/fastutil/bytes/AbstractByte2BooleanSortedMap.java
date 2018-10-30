/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
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

public abstract class AbstractByte2BooleanSortedMap
extends AbstractByte2BooleanMap
implements Byte2BooleanSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2BooleanSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Byte2BooleanMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2BooleanMap.Entry> i) {
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
            return new ValuesIterator(Byte2BooleanSortedMaps.fastIterator(AbstractByte2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractByte2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2BooleanMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2BooleanMap.Entry> i) {
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
            return AbstractByte2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2BooleanSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2BooleanSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2BooleanSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2BooleanSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2BooleanSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2BooleanSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2BooleanSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2BooleanSortedMap.this.byte2BooleanEntrySet().iterator(new AbstractByte2BooleanMap.BasicEntry(from, false)));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2BooleanSortedMaps.fastIterator(AbstractByte2BooleanSortedMap.this));
        }
    }

}

