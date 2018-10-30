/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.io.Serializable;
import java.util.Comparator;

public final class ByteComparators {
    public static final ByteComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final ByteComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private ByteComparators() {
    }

    public static ByteComparator oppositeComparator(ByteComparator c) {
        return new OppositeComparator(c);
    }

    public static ByteComparator asByteComparator(final Comparator<? super Byte> c) {
        if (c == null || c instanceof ByteComparator) {
            return (ByteComparator)c;
        }
        return new ByteComparator(){

            @Override
            public int compare(byte x, byte y) {
                return c.compare(x, y);
            }

            @Override
            public int compare(Byte x, Byte y) {
                return c.compare(x, y);
            }
        };
    }

    protected static class OppositeComparator
    implements ByteComparator,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final ByteComparator comparator;

        protected OppositeComparator(ByteComparator c) {
            this.comparator = c;
        }

        @Override
        public final int compare(byte a, byte b) {
            return this.comparator.compare(b, a);
        }
    }

    protected static class OppositeImplicitComparator
    implements ByteComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(byte a, byte b) {
            return - Byte.compare(a, b);
        }

        private Object readResolve() {
            return ByteComparators.OPPOSITE_COMPARATOR;
        }
    }

    protected static class NaturalImplicitComparator
    implements ByteComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(byte a, byte b) {
            return Byte.compare(a, b);
        }

        private Object readResolve() {
            return ByteComparators.NATURAL_COMPARATOR;
        }
    }

}

