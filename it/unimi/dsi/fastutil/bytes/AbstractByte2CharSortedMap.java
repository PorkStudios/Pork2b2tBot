/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2CharMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2CharMap;
import it.unimi.dsi.fastutil.bytes.Byte2CharSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2CharSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractByte2CharSortedMap
extends AbstractByte2CharMap
implements Byte2CharSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2CharSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public CharCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements CharIterator {
        protected final ObjectBidirectionalIterator<Byte2CharMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2CharMap.Entry> i) {
            this.i = i;
        }

        @Override
        public char nextChar() {
            return this.i.next().getCharValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractCharCollection {
        protected ValuesCollection() {
        }

        @Override
        public CharIterator iterator() {
            return new ValuesIterator(Byte2CharSortedMaps.fastIterator(AbstractByte2CharSortedMap.this));
        }

        @Override
        public boolean contains(char k) {
            return AbstractByte2CharSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2CharSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2CharMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2CharMap.Entry> i) {
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
            return AbstractByte2CharSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2CharSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2CharSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2CharSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2CharSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2CharSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2CharSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2CharSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2CharSortedMap.this.byte2CharEntrySet().iterator(new AbstractByte2CharMap.BasicEntry(from, '\u0000')));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2CharSortedMaps.fastIterator(AbstractByte2CharSortedMap.this));
        }
    }

}

