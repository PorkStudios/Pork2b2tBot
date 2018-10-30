/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharComparator;
import java.io.Serializable;
import java.util.Comparator;

public final class CharComparators {
    public static final CharComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final CharComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private CharComparators() {
    }

    public static CharComparator oppositeComparator(CharComparator c) {
        return new OppositeComparator(c);
    }

    public static CharComparator asCharComparator(final Comparator<? super Character> c) {
        if (c == null || c instanceof CharComparator) {
            return (CharComparator)c;
        }
        return new CharComparator(){

            @Override
            public int compare(char x, char y) {
                return c.compare(Character.valueOf(x), Character.valueOf(y));
            }

            @Override
            public int compare(Character x, Character y) {
                return c.compare(x, y);
            }
        };
    }

    protected static class OppositeComparator
    implements CharComparator,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final CharComparator comparator;

        protected OppositeComparator(CharComparator c) {
            this.comparator = c;
        }

        @Override
        public final int compare(char a, char b) {
            return this.comparator.compare(b, a);
        }
    }

    protected static class OppositeImplicitComparator
    implements CharComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(char a, char b) {
            return - Character.compare(a, b);
        }

        private Object readResolve() {
            return CharComparators.OPPOSITE_COMPARATOR;
        }
    }

    protected static class NaturalImplicitComparator
    implements CharComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(char a, char b) {
            return Character.compare(a, b);
        }

        private Object readResolve() {
            return CharComparators.NATURAL_COMPARATOR;
        }
    }

}

