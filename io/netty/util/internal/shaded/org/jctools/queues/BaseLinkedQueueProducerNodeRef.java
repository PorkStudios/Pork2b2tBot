/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseLinkedQueuePad0;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedQueueNode;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class BaseLinkedQueueProducerNodeRef<E>
extends BaseLinkedQueuePad0<E> {
    protected static final long P_NODE_OFFSET;
    protected LinkedQueueNode<E> producerNode;

    BaseLinkedQueueProducerNodeRef() {
    }

    protected final void spProducerNode(LinkedQueueNode<E> newValue) {
        this.producerNode = newValue;
    }

    protected final LinkedQueueNode<E> lvProducerNode() {
        return (LinkedQueueNode)UnsafeAccess.UNSAFE.getObjectVolatile(this, P_NODE_OFFSET);
    }

    protected final boolean casProducerNode(LinkedQueueNode<E> expect, LinkedQueueNode<E> newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapObject(this, P_NODE_OFFSET, expect, newValue);
    }

    protected final LinkedQueueNode<E> lpProducerNode() {
        return this.producerNode;
    }

    static {
        try {
            Field pNodeField = BaseLinkedQueueProducerNodeRef.class.getDeclaredField("producerNode");
            P_NODE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(pNodeField);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

