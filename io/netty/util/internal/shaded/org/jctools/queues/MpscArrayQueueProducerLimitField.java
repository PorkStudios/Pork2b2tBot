/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueMidPad;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class MpscArrayQueueProducerLimitField<E>
extends MpscArrayQueueMidPad<E> {
    private static final long P_LIMIT_OFFSET;
    private volatile long producerLimit;

    public MpscArrayQueueProducerLimitField(int capacity) {
        super(capacity);
        this.producerLimit = capacity;
    }

    protected final long lvProducerLimit() {
        return this.producerLimit;
    }

    protected final void soProducerLimit(long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, newValue);
    }

    static {
        try {
            P_LIMIT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(MpscArrayQueueProducerLimitField.class.getDeclaredField("producerLimit"));
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

