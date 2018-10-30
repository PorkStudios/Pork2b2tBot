/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongComparator;
import java.io.Serializable;
import java.util.Comparator;

public final class LongComparators {
    public static final LongComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final LongComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private LongComparators() {
    }

    public static LongComparator oppositeComparator(LongComparator c) {
        return new OppositeComparator(c);
    }

    public static LongComparator asLongComparator(final Comparator<? super Long> c) {
        if (c == null || c instanceof LongComparator) {
            return (LongComparator)c;
        }
        return new LongComparator(){

            @Override
            public int compare(long x, long y) {
                return c.compare(x, y);
            }

            @Override
            public int compare(Long x, Long y) {
                return c.compare(x, y);
            }
        };
    }

    protected static class OppositeComparator
    implements LongComparator,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final LongComparator comparator;

        protected OppositeComparator(LongComparator c) {
            this.comparator = c;
        }

        @Override
        public final int compare(long a, long b) {
            return this.comparator.compare(b, a);
        }
    }

    protected static class OppositeImplicitComparator
    implements LongComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(long a, long b) {
            return - Long.compare(a, b);
        }

        private Object readResolve() {
            return LongComparators.OPPOSITE_COMPARATOR;
        }
    }

    protected static class NaturalImplicitComparator
    implements LongComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(long a, long b) {
            return Long.compare(a, b);
        }

        private Object readResolve() {
            return LongComparators.NATURAL_COMPARATOR;
        }
    }

}

