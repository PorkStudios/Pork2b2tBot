/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.chars.AbstractChar2BooleanMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSortedSet;
import it.unimi.dsi.fastutil.chars.Char2BooleanMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanSortedMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanSortedMaps;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractChar2BooleanSortedMap
extends AbstractChar2BooleanMap
implements Char2BooleanSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractChar2BooleanSortedMap() {
    }

    @Override
    public CharSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Char2BooleanMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Char2BooleanMap.Entry> i) {
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
            return new ValuesIterator(Char2BooleanSortedMaps.fastIterator(AbstractChar2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractChar2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractChar2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2BooleanSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements CharBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Char2BooleanMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Char2BooleanMap.Entry> i) {
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
            return AbstractChar2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractChar2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2BooleanSortedMap.this.clear();
        }

        @Override
        public CharComparator comparator() {
            return AbstractChar2BooleanSortedMap.this.comparator();
        }

        @Override
        public char firstChar() {
            return AbstractChar2BooleanSortedMap.this.firstCharKey();
        }

        @Override
        public char lastChar() {
            return AbstractChar2BooleanSortedMap.this.lastCharKey();
        }

        @Override
        public CharSortedSet headSet(char to) {
            return AbstractChar2BooleanSortedMap.this.headMap(to).keySet();
        }

        @Override
        public CharSortedSet tailSet(char from) {
            return AbstractChar2BooleanSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public CharSortedSet subSet(char from, char to) {
            return AbstractChar2BooleanSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public CharBidirectionalIterator iterator(char from) {
            return new KeySetIterator(AbstractChar2BooleanSortedMap.this.char2BooleanEntrySet().iterator(new AbstractChar2BooleanMap.BasicEntry(from, false)));
        }

        @Override
        public CharBidirectionalIterator iterator() {
            return new KeySetIterator(Char2BooleanSortedMaps.fastIterator(AbstractChar2BooleanSortedMap.this));
        }
    }

}

