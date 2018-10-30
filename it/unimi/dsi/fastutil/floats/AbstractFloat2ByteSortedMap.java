/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ByteMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.Float2ByteMap;
import it.unimi.dsi.fastutil.floats.Float2ByteSortedMap;
import it.unimi.dsi.fastutil.floats.Float2ByteSortedMaps;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractFloat2ByteSortedMap
extends AbstractFloat2ByteMap
implements Float2ByteSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractFloat2ByteSortedMap() {
    }

    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Float2ByteMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Float2ByteMap.Entry> i) {
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
            return new ValuesIterator(Float2ByteSortedMaps.fastIterator(AbstractFloat2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractFloat2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractFloat2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements FloatBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Float2ByteMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Float2ByteMap.Entry> i) {
            this.i = i;
        }

        @Override
        public float nextFloat() {
            return this.i.next().getFloatKey();
        }

        @Override
        public float previousFloat() {
            return this.i.previous().getFloatKey();
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
    extends AbstractFloatSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(float k) {
            return AbstractFloat2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractFloat2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2ByteSortedMap.this.clear();
        }

        @Override
        public FloatComparator comparator() {
            return AbstractFloat2ByteSortedMap.this.comparator();
        }

        @Override
        public float firstFloat() {
            return AbstractFloat2ByteSortedMap.this.firstFloatKey();
        }

        @Override
        public float lastFloat() {
            return AbstractFloat2ByteSortedMap.this.lastFloatKey();
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return AbstractFloat2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return AbstractFloat2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return AbstractFloat2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return new KeySetIterator(AbstractFloat2ByteSortedMap.this.float2ByteEntrySet().iterator(new AbstractFloat2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(Float2ByteSortedMaps.fastIterator(AbstractFloat2ByteSortedMap.this));
        }
    }

}

