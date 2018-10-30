/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2FloatMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2FloatMap;
import it.unimi.dsi.fastutil.bytes.Byte2FloatSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2FloatSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractByte2FloatSortedMap
extends AbstractByte2FloatMap
implements Byte2FloatSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2FloatSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public FloatCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements FloatIterator {
        protected final ObjectBidirectionalIterator<Byte2FloatMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2FloatMap.Entry> i) {
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
            return new ValuesIterator(Byte2FloatSortedMaps.fastIterator(AbstractByte2FloatSortedMap.this));
        }

        @Override
        public boolean contains(float k) {
            return AbstractByte2FloatSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2FloatSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2FloatMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2FloatMap.Entry> i) {
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
            return AbstractByte2FloatSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2FloatSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2FloatSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2FloatSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2FloatSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2FloatSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2FloatSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2FloatSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2FloatSortedMap.this.byte2FloatEntrySet().iterator(new AbstractByte2FloatMap.BasicEntry(from, 0.0f)));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2FloatSortedMaps.fastIterator(AbstractByte2FloatSortedMap.this));
        }
    }

}

