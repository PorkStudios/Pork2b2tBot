/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2LongMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSortedSet;
import it.unimi.dsi.fastutil.chars.Char2LongMap;
import it.unimi.dsi.fastutil.chars.Char2LongSortedMap;
import it.unimi.dsi.fastutil.chars.Char2LongSortedMaps;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractChar2LongSortedMap
extends AbstractChar2LongMap
implements Char2LongSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractChar2LongSortedMap() {
    }

    @Override
    public CharSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public LongCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements LongIterator {
        protected final ObjectBidirectionalIterator<Char2LongMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Char2LongMap.Entry> i) {
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
            return new ValuesIterator(Char2LongSortedMaps.fastIterator(AbstractChar2LongSortedMap.this));
        }

        @Override
        public boolean contains(long k) {
            return AbstractChar2LongSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractChar2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2LongSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements CharBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Char2LongMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Char2LongMap.Entry> i) {
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
            return AbstractChar2LongSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractChar2LongSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2LongSortedMap.this.clear();
        }

        @Override
        public CharComparator comparator() {
            return AbstractChar2LongSortedMap.this.comparator();
        }

        @Override
        public char firstChar() {
            return AbstractChar2LongSortedMap.this.firstCharKey();
        }

        @Override
        public char lastChar() {
            return AbstractChar2LongSortedMap.this.lastCharKey();
        }

        @Override
        public CharSortedSet headSet(char to) {
            return AbstractChar2LongSortedMap.this.headMap(to).keySet();
        }

        @Override
        public CharSortedSet tailSet(char from) {
            return AbstractChar2LongSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public CharSortedSet subSet(char from, char to) {
            return AbstractChar2LongSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public CharBidirectionalIterator iterator(char from) {
            return new KeySetIterator(AbstractChar2LongSortedMap.this.char2LongEntrySet().iterator(new AbstractChar2LongMap.BasicEntry(from, 0L)));
        }

        @Override
        public CharBidirectionalIterator iterator() {
            return new KeySetIterator(Char2LongSortedMaps.fastIterator(AbstractChar2LongSortedMap.this));
        }
    }

}

