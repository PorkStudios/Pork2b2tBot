/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueuePad1;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class BaseMpscLinkedArrayQueueProducerFields<E>
extends BaseMpscLinkedArrayQueuePad1<E> {
    private static final long P_INDEX_OFFSET;
    protected long producerIndex;

    BaseMpscLinkedArrayQueueProducerFields() {
    }

    @Override
    public final long lvProducerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
    }

    final void soProducerIndex(long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, newValue);
    }

    final boolean casProducerIndex(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
    }

    static {
        try {
            Field iField = BaseMpscLinkedArrayQueueProducerFields.class.getDeclaredField("producerIndex");
            P_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(iField);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

