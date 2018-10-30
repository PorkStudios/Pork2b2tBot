/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

final class LinkedQueueNode<E> {
    private static final long NEXT_OFFSET;
    private E value;
    private volatile LinkedQueueNode<E> next;

    LinkedQueueNode() {
        this(null);
    }

    LinkedQueueNode(E val) {
        this.spValue(val);
    }

    public E getAndNullValue() {
        E temp = this.lpValue();
        this.spValue(null);
        return temp;
    }

    public E lpValue() {
        return this.value;
    }

    public void spValue(E newValue) {
        this.value = newValue;
    }

    public void soNext(LinkedQueueNode<E> n) {
        UnsafeAccess.UNSAFE.putOrderedObject(this, NEXT_OFFSET, n);
    }

    public LinkedQueueNode<E> lvNext() {
        return this.next;
    }

    static {
        try {
            NEXT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(LinkedQueueNode.class.getDeclaredField("next"));
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

