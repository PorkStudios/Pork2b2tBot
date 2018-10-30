/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortComparator;
import java.io.Serializable;
import java.util.Comparator;

public final class ShortComparators {
    public static final ShortComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final ShortComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private ShortComparators() {
    }

    public static ShortComparator oppositeComparator(ShortComparator c) {
        return new OppositeComparator(c);
    }

    public static ShortComparator asShortComparator(final Comparator<? super Short> c) {
        if (c == null || c instanceof ShortComparator) {
            return (ShortComparator)c;
        }
        return new ShortComparator(){

            @Override
            public int compare(short x, short y) {
                return c.compare(x, y);
            }

            @Override
            public int compare(Short x, Short y) {
                return c.compare(x, y);
            }
        };
    }

    protected static class OppositeComparator
    implements ShortComparator,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final ShortComparator comparator;

        protected OppositeComparator(ShortComparator c) {
            this.comparator = c;
        }

        @Override
        public final int compare(short a, short b) {
            return this.comparator.compare(b, a);
        }
    }

    protected static class OppositeImplicitComparator
    implements ShortComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(short a, short b) {
            return - Short.compare(a, b);
        }

        private Object readResolve() {
            return ShortComparators.OPPOSITE_COMPARATOR;
        }
    }

    protected static class NaturalImplicitComparator
    implements ShortComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(short a, short b) {
            return Short.compare(a, b);
        }

        private Object readResolve() {
            return ShortComparators.NATURAL_COMPARATOR;
        }
    }

}

