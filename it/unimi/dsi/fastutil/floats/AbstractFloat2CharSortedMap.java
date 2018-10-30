/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloat2CharMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSortedSet;
import it.unimi.dsi.fastutil.floats.Float2CharMap;
import it.unimi.dsi.fastutil.floats.Float2CharSortedMap;
import it.unimi.dsi.fastutil.floats.Float2CharSortedMaps;
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

public abstract class AbstractFloat2CharSortedMap
extends AbstractFloat2CharMap
implements Float2CharSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractFloat2CharSortedMap() {
    }

    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public CharCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements CharIterator {
        protected final ObjectBidirectionalIterator<Float2CharMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Float2CharMap.Entry> i) {
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
            return new ValuesIterator(Float2CharSortedMaps.fastIterator(AbstractFloat2CharSortedMap.this));
        }

        @Override
        public boolean contains(char k) {
            return AbstractFloat2CharSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractFloat2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2CharSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements FloatBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Float2CharMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Float2CharMap.Entry> i) {
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
            return AbstractFloat2CharSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractFloat2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractFloat2CharSortedMap.this.clear();
        }

        @Override
        public FloatComparator comparator() {
            return AbstractFloat2CharSortedMap.this.comparator();
        }

        @Override
        public float firstFloat() {
            return AbstractFloat2CharSortedMap.this.firstFloatKey();
        }

        @Override
        public float lastFloat() {
            return AbstractFloat2CharSortedMap.this.lastFloatKey();
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return AbstractFloat2CharSortedMap.this.headMap(to).keySet();
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return AbstractFloat2CharSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return AbstractFloat2CharSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return new KeySetIterator(AbstractFloat2CharSortedMap.this.float2CharEntrySet().iterator(new AbstractFloat2CharMap.BasicEntry(from, '\u0000')));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(Float2CharSortedMaps.fastIterator(AbstractFloat2CharSortedMap.this));
        }
    }

}

