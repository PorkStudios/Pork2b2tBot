/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2IntMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.Float2IntMap;
import it.unimi.dsi.fastutil.floats.Float2IntSortedMap;
import it.unimi.dsi.fastutil.floats.Float2IntSortedMaps;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractFloat2IntSortedMap
extends AbstractFloat2IntMap
implements Float2IntSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractFloat2IntSortedMap() {
    }

    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public IntCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements IntIterator {
        protected final ObjectBidirectionalIterator<Float2IntMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Float2IntMap.Entry> i) {
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
            return new ValuesIterator(Float2IntSortedMaps.fastIterator(AbstractFloat2IntSortedMap.this));
        }

        @Override
        public boolean contains(int k) {
            return AbstractFloat2IntSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractFloat2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2IntSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements FloatBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Float2IntMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Float2IntMap.Entry> i) {
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
            return AbstractFloat2IntSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractFloat2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2IntSortedMap.this.clear();
        }

        @Override
        public FloatComparator comparator() {
            return AbstractFloat2IntSortedMap.this.comparator();
        }

        @Override
        public float firstFloat() {
            return AbstractFloat2IntSortedMap.this.firstFloatKey();
        }

        @Override
        public float lastFloat() {
            return AbstractFloat2IntSortedMap.this.lastFloatKey();
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return AbstractFloat2IntSortedMap.this.headMap(to).keySet();
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return AbstractFloat2IntSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return AbstractFloat2IntSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return new KeySetIterator(AbstractFloat2IntSortedMap.this.float2IntEntrySet().iterator(new AbstractFloat2IntMap.BasicEntry(from, 0)));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(Float2IntSortedMaps.fastIterator(AbstractFloat2IntSortedMap.this));
        }
    }

}

