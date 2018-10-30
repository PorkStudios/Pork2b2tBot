/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2LongMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.Float2LongMap;
import it.unimi.dsi.fastutil.floats.Float2LongSortedMap;
import it.unimi.dsi.fastutil.floats.Float2LongSortedMaps;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractFloat2LongSortedMap
extends AbstractFloat2LongMap
implements Float2LongSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractFloat2LongSortedMap() {
    }

    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Float2LongMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Float2LongMap.Entry> i) {
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
            return new ValuesIterator(Float2LongSortedMaps.fastIterator(AbstractFloat2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractFloat2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractFloat2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements FloatBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Float2LongMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Float2LongMap.Entry> i) {
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
            return AbstractFloat2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractFloat2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2LongSortedMap.this.clear();
        }

        @Override
        public FloatComparator comparator() {
            return AbstractFloat2LongSortedMap.this.comparator();
        }

        @Override
        public float firstFloat() {
            return AbstractFloat2LongSortedMap.this.firstFloatKey();
        }

        @Override
        public float lastFloat() {
            return AbstractFloat2LongSortedMap.this.lastFloatKey();
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return AbstractFloat2LongSortedMap.this.headMap(to).keySet();
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return AbstractFloat2LongSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return AbstractFloat2LongSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return new KeySetIterator(AbstractFloat2LongSortedMap.this.float2LongEntrySet().iterator(new AbstractFloat2LongMap.BasicEntry(from, 0L)));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(Float2LongSortedMaps.fastIterator(AbstractFloat2LongSortedMap.this));
        }
    }

}

