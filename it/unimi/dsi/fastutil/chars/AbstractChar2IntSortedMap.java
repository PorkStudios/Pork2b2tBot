/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2IntMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSortedSet;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntSortedMap;
import it.unimi.dsi.fastutil.chars.Char2IntSortedMaps;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractChar2IntSortedMap
extends AbstractChar2IntMap
implements Char2IntSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractChar2IntSortedMap() {
    }

    @Override
    public CharSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public IntCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements IntIterator {
        protected final ObjectBidirectionalIterator<Char2IntMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Char2IntMap.Entry> i) {
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
            return new ValuesIterator(Char2IntSortedMaps.fastIterator(AbstractChar2IntSortedMap.this));
        }

        @Override
        public boolean contains(int k) {
            return AbstractChar2IntSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractChar2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2IntSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements CharBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Char2IntMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Char2IntMap.Entry> i) {
            this.i = i;
        }

        @Override
        public char nextChar() {
            return this.i.next().getCharKey();
        }

        @Override
        public char previousChar() {
            return this.i.previous().getCharKey();
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
    extends AbstractCharSortedSet {
        protected KeySet() {
        }

        @Override
        public boolean contains(char k) {
            return AbstractChar2IntSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractChar2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2IntSortedMap.this.clear();
        }

        @Override
        public CharComparator comparator() {
            return AbstractChar2IntSortedMap.this.comparator();
        }

        @Override
        public char firstChar() {
            return AbstractChar2IntSortedMap.this.firstCharKey();
        }

        @Override
        public char lastChar() {
            return AbstractChar2IntSortedMap.this.lastCharKey();
        }

        @Override
        public CharSortedSet headSet(char to) {
            return AbstractChar2IntSortedMap.this.headMap(to).keySet();
        }

        @Override
        public CharSortedSet tailSet(char from) {
            return AbstractChar2IntSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public CharSortedSet subSet(char from, char to) {
            return AbstractChar2IntSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public CharBidirectionalIterator iterator(char from) {
            return new KeySetIterator(AbstractChar2IntSortedMap.this.char2IntEntrySet().iterator(new AbstractChar2IntMap.BasicEntry(from, 0)));
        }

        @Override
        public CharBidirectionalIterator iterator() {
            return new KeySetIterator(Char2IntSortedMaps.fastIterator(AbstractChar2IntSortedMap.this));
        }
    }

}

