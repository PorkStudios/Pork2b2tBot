/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ShortMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSortedSet;
import it.unimi.dsi.fastutil.chars.Char2ShortMap;
import it.unimi.dsi.fastutil.chars.Char2ShortSortedMap;
import it.unimi.dsi.fastutil.chars.Char2ShortSortedMaps;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractChar2ShortSortedMap
extends AbstractChar2ShortMap
implements Char2ShortSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractChar2ShortSortedMap() {
    }

    @Override
    public CharSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ShortCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ShortIterator {
        protected final ObjectBidirectionalIterator<Char2ShortMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Char2ShortMap.Entry> i) {
            this.i = i;
        }

        @Override
        public short nextShort() {
            return this.i.next().getShortValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractShortCollection {
        protected ValuesCollection() {
        }

        @Override
        public ShortIterator iterator() {
            return new ValuesIterator(Char2ShortSortedMaps.fastIterator(AbstractChar2ShortSortedMap.this));
        }

        @Override
        public boolean contains(short k) {
            return AbstractChar2ShortSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractChar2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2ShortSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements CharBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Char2ShortMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Char2ShortMap.Entry> i) {
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
            return AbstractChar2ShortSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractChar2ShortSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2ShortSortedMap.this.clear();
        }

        @Override
        public CharComparator comparator() {
            return AbstractChar2ShortSortedMap.this.comparator();
        }

        @Override
        public char firstChar() {
            return AbstractChar2ShortSortedMap.this.firstCharKey();
        }

        @Override
        public char lastChar() {
            return AbstractChar2ShortSortedMap.this.lastCharKey();
        }

        @Override
        public CharSortedSet headSet(char to) {
            return AbstractChar2ShortSortedMap.this.headMap(to).keySet();
        }

        @Override
        public CharSortedSet tailSet(char from) {
            return AbstractChar2ShortSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public CharSortedSet subSet(char from, char to) {
            return AbstractChar2ShortSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public CharBidirectionalIterator iterator(char from) {
            return new KeySetIterator(AbstractChar2ShortSortedMap.this.char2ShortEntrySet().iterator(new AbstractChar2ShortMap.BasicEntry(from, 0)));
        }

        @Override
        public CharBidirectionalIterator iterator() {
            return new KeySetIterator(Char2ShortSortedMaps.fastIterator(AbstractChar2ShortSortedMap.this));
        }
    }

}

