/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

final class LinkedArrayQueueUtil {
    private LinkedArrayQueueUtil() {
    }

    static int length(Object[] buf) {
        return buf.length;
    }

    static long modifiedCalcElementOffset(long index, long mask) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((index & mask) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT - 1);
    }

    static long nextArrayOffset(Object[] curr) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((long)(LinkedArrayQueueUtil.length(curr) - 1) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
    }
}

