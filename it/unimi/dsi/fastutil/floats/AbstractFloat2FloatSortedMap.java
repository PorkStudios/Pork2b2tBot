/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2FloatMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.Float2FloatMap;
import it.unimi.dsi.fastutil.floats.Float2FloatSortedMap;
import it.unimi.dsi.fastutil.floats.Float2FloatSortedMaps;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
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

public abstract class AbstractFloat2FloatSortedMap
extends AbstractFloat2FloatMap
implements Float2FloatSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractFloat2FloatSortedMap() {
    }

    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public FloatCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements FloatIterator {
        protected final ObjectBidirectionalIterator<Float2FloatMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Float2FloatMap.Entry> i) {
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
            return new ValuesIterator(Float2FloatSortedMaps.fastIterator(AbstractFloat2FloatSortedMap.this));
        }

        @Override
        public boolean contains(float k) {
            return AbstractFloat2FloatSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractFloat2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2FloatSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements FloatBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Float2FloatMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Float2FloatMap.Entry> i) {
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
            return AbstractFloat2FloatSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractFloat2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2FloatSortedMap.this.clear();
        }

        @Override
        public FloatComparator comparator() {
            return AbstractFloat2FloatSortedMap.this.comparator();
        }

        @Override
        public float firstFloat() {
            return AbstractFloat2FloatSortedMap.this.firstFloatKey();
        }

        @Override
        public float lastFloat() {
            return AbstractFloat2FloatSortedMap.this.lastFloatKey();
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return AbstractFloat2FloatSortedMap.this.headMap(to).keySet();
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return AbstractFloat2FloatSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return AbstractFloat2FloatSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return new KeySetIterator(AbstractFloat2FloatSortedMap.this.float2FloatEntrySet().iterator(new AbstractFloat2FloatMap.BasicEntry(from, 0.0f)));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(Float2FloatSortedMaps.fastIterator(AbstractFloat2FloatSortedMap.this));
        }
    }

}

