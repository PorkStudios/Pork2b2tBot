/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloat2DoubleMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.Float2DoubleMap;
import it.unimi.dsi.fastutil.floats.Float2DoubleSortedMap;
import it.unimi.dsi.fastutil.floats.Float2DoubleSortedMaps;
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

public abstract class AbstractFloat2DoubleSortedMap
extends AbstractFloat2DoubleMap
implements Float2DoubleSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractFloat2DoubleSortedMap() {
    }

    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public DoubleCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements DoubleIterator {
        protected final ObjectBidirectionalIterator<Float2DoubleMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Float2DoubleMap.Entry> i) {
            this.i = i;
        }

        @Override
        public double nextDouble() {
            return this.i.next().getDoubleValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractDoubleCollection {
        protected ValuesCollection() {
        }

        @Override
        public DoubleIterator iterator() {
            return new ValuesIterator(Float2DoubleSortedMaps.fastIterator(AbstractFloat2DoubleSortedMap.this));
        }

        @Override
        public boolean contains(double k) {
            return AbstractFloat2DoubleSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractFloat2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2DoubleSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements FloatBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Float2DoubleMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Float2DoubleMap.Entry> i) {
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
            return AbstractFloat2DoubleSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractFloat2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2DoubleSortedMap.this.clear();
        }

        @Override
        public FloatComparator comparator() {
            return AbstractFloat2DoubleSortedMap.this.comparator();
        }

        @Override
        public float firstFloat() {
            return AbstractFloat2DoubleSortedMap.this.firstFloatKey();
        }

        @Override
        public float lastFloat() {
            return AbstractFloat2DoubleSortedMap.this.lastFloatKey();
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return AbstractFloat2DoubleSortedMap.this.headMap(to).keySet();
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return AbstractFloat2DoubleSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return AbstractFloat2DoubleSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return new KeySetIterator(AbstractFloat2DoubleSortedMap.this.float2DoubleEntrySet().iterator(new AbstractFloat2DoubleMap.BasicEntry(from, 0.0)));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(Float2DoubleSortedMaps.fastIterator(AbstractFloat2DoubleSortedMap.this));
        }
    }

}

