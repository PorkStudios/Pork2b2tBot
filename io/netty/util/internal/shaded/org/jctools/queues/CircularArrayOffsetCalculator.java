/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public final class CircularArrayOffsetCalculator {
    public static <E> E[] allocate(int capacity) {
        return new Object[capacity];
    }

    public static long calcElementOffset(long index, long mask) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((index & mask) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
    }
}

