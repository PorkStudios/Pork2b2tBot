/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueL2Pad;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class MpscArrayQueueConsumerIndexField<E>
extends MpscArrayQueueL2Pad<E> {
    private static final long C_INDEX_OFFSET;
    protected long consumerIndex;

    public MpscArrayQueueConsumerIndexField(int capacity) {
        super(capacity);
    }

    protected final long lpConsumerIndex() {
        return this.consumerIndex;
    }

    @Override
    public final long lvConsumerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
    }

    protected void soConsumerIndex(long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, newValue);
    }

    static {
        try {
            C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(MpscArrayQueueConsumerIndexField.class.getDeclaredField("consumerIndex"));
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

