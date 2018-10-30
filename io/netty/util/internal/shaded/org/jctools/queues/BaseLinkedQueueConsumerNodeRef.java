/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseLinkedQueuePad1;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedQueueNode;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class BaseLinkedQueueConsumerNodeRef<E>
extends BaseLinkedQueuePad1<E> {
    protected static final long C_NODE_OFFSET;
    protected LinkedQueueNode<E> consumerNode;

    BaseLinkedQueueConsumerNodeRef() {
    }

    protected final void spConsumerNode(LinkedQueueNode<E> newValue) {
        this.consumerNode = newValue;
    }

    protected final LinkedQueueNode<E> lvConsumerNode() {
        return (LinkedQueueNode)UnsafeAccess.UNSAFE.getObjectVolatile(this, C_NODE_OFFSET);
    }

    protected final LinkedQueueNode<E> lpConsumerNode() {
        return this.consumerNode;
    }

    static {
        try {
            Field cNodeField = BaseLinkedQueueConsumerNodeRef.class.getDeclaredField("consumerNode");
            C_NODE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(cNodeField);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

