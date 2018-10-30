/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceSortedMaps;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractByte2ReferenceSortedMap<V>
extends AbstractByte2ReferenceMap<V>
implements Byte2ReferenceSortedMap<V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractByte2ReferenceSortedMap() {
    }

    @Override
    public ByteSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ReferenceCollection<V> values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator<V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> i) {
            this.i = i;
        }

        @Override
        public V next() {
            return this.i.next().getValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractReferenceCollection<V> {
        protected ValuesCollection() {
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ValuesIterator(Byte2ReferenceSortedMaps.fastIterator(AbstractByte2ReferenceSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractByte2ReferenceSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractByte2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2ReferenceSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator<V>
    implements ByteBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> i) {
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
            return AbstractByte2ReferenceSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractByte2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractByte2ReferenceSortedMap.this.clear();
        }

        @Override
        public ByteComparator comparator() {
            return AbstractByte2ReferenceSortedMap.this.comparator();
        }

        @Override
        public byte firstByte() {
            return AbstractByte2ReferenceSortedMap.this.firstByteKey();
        }

        @Override
        public byte lastByte() {
            return AbstractByte2ReferenceSortedMap.this.lastByteKey();
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return AbstractByte2ReferenceSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return AbstractByte2ReferenceSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return AbstractByte2ReferenceSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeySetIterator(AbstractByte2ReferenceSortedMap.this.byte2ReferenceEntrySet().iterator(new AbstractByte2ReferenceMap.BasicEntry<Object>(from, null)));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeySetIterator(Byte2ReferenceSortedMaps.fastIterator(AbstractByte2ReferenceSortedMap.this));
        }
    }

}

