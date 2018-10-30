/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloat2BooleanMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.Float2BooleanMap;
import it.unimi.dsi.fastutil.floats.Float2BooleanSortedMap;
import it.unimi.dsi.fastutil.floats.Float2BooleanSortedMaps;
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

public abstract class AbstractFloat2BooleanSortedMap
extends AbstractFloat2BooleanMap
implements Float2BooleanSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractFloat2BooleanSortedMap() {
    }

    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Float2BooleanMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Float2BooleanMap.Entry> i) {
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
            return new ValuesIterator(Float2BooleanSortedMaps.fastIterator(AbstractFloat2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractFloat2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractFloat2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements FloatBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Float2BooleanMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Float2BooleanMap.Entry> i) {
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
            return AbstractFloat2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractFloat2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2BooleanSortedMap.this.clear();
        }

        @Override
        public FloatComparator comparator() {
            return AbstractFloat2BooleanSortedMap.this.comparator();
        }

        @Override
        public float firstFloat() {
            return AbstractFloat2BooleanSortedMap.this.firstFloatKey();
        }

        @Override
        public float lastFloat() {
            return AbstractFloat2BooleanSortedMap.this.lastFloatKey();
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return AbstractFloat2BooleanSortedMap.this.headMap(to).keySet();
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return AbstractFloat2BooleanSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return AbstractFloat2BooleanSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return new KeySetIterator(AbstractFloat2BooleanSortedMap.this.float2BooleanEntrySet().iterator(new AbstractFloat2BooleanMap.BasicEntry(from, false)));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(Float2BooleanSortedMaps.fastIterator(AbstractFloat2BooleanSortedMap.this));
        }
    }

}

