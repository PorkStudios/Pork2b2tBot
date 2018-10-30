/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.chars.AbstractChar2ByteMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSortedSet;
import it.unimi.dsi.fastutil.chars.Char2ByteMap;
import it.unimi.dsi.fastutil.chars.Char2ByteSortedMap;
import it.unimi.dsi.fastutil.chars.Char2ByteSortedMaps;
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

public abstract class AbstractChar2ByteSortedMap
extends AbstractChar2ByteMap
implements Char2ByteSortedMap {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractChar2ByteSortedMap() {
    }

    @Override
    public CharSortedSet keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected static class ValuesIterator
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Char2ByteMap.Entry> i;

        public ValuesIterator(ObjectBidirectionalIterator<Char2ByteMap.Entry> i) {
            this.i = i;
        }

        @Override
        public byte nextByte() {
            return this.i.next().getByteValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected class ValuesCollection
    extends AbstractByteCollection {
        protected ValuesCollection() {
        }

        @Override
        public ByteIterator iterator() {
            return new ValuesIterator(Char2ByteSortedMaps.fastIterator(AbstractChar2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractChar2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractChar2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2ByteSortedMap.this.clear();
        }
    }

    protected static class KeySetIterator
    implements CharBidirectionalIterator {
        protected final ObjectBidirectionalIterator<Char2ByteMap.Entry> i;

        public KeySetIterator(ObjectBidirectionalIterator<Char2ByteMap.Entry> i) {
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
            return AbstractChar2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractChar2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractChar2ByteSortedMap.this.clear();
        }

        @Override
        public CharComparator comparator() {
            return AbstractChar2ByteSortedMap.this.comparator();
        }

        @Override
        public char firstChar() {
            return AbstractChar2ByteSortedMap.this.firstCharKey();
        }

        @Override
        public char lastChar() {
            return AbstractChar2ByteSortedMap.this.lastCharKey();
        }

        @Override
        public CharSortedSet headSet(char to) {
            return AbstractChar2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public CharSortedSet tailSet(char from) {
            return AbstractChar2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public CharSortedSet subSet(char from, char to) {
            return AbstractChar2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public CharBidirectionalIterator iterator(char from) {
            return new KeySetIterator(AbstractChar2ByteSortedMap.this.char2ByteEntrySet().iterator(new AbstractChar2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public CharBidirectionalIterator iterator() {
            return new KeySetIterator(Char2ByteSortedMaps.fastIterator(AbstractChar2ByteSortedMap.this));
        }
    }

}

