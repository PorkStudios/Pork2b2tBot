/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseLinkedQueuePad2;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedQueueNode;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.util.Iterator;

abstract class BaseLinkedQueue<E>
extends BaseLinkedQueuePad2<E> {
    BaseLinkedQueue() {
    }

    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    protected final LinkedQueueNode<E> newNode() {
        return new LinkedQueueNode();
    }

    protected final LinkedQueueNode<E> newNode(E e) {
        return new LinkedQueueNode<E>(e);
    }

    @Override
    public final int size() {
        int size;
        LinkedQueueNode chaserNode = this.lvConsumerNode();
        LinkedQueueNode producerNode = this.lvProducerNode();
        for (size = 0; chaserNode != producerNode && chaserNode != null && size < Integer.MAX_VALUE; ++size) {
            LinkedQueueNode next = chaserNode.lvNext();
            if (next == chaserNode) {
                return size;
            }
            chaserNode = next;
        }
        return size;
    }

    @Override
    public final boolean isEmpty() {
        return this.lvConsumerNode() == this.lvProducerNode();
    }

    protected E getSingleConsumerNodeValue(LinkedQueueNode<E> currConsumerNode, LinkedQueueNode<E> nextNode) {
        E nextValue = nextNode.getAndNullValue();
        currConsumerNode.soNext(currConsumerNode);
        this.spConsumerNode(nextNode);
        return nextValue;
    }

    @Override
    public E relaxedPoll() {
        LinkedQueueNode currConsumerNode = this.lpConsumerNode();
        LinkedQueueNode nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
        }
        return null;
    }

    @Override
    public E relaxedPeek() {
        LinkedQueueNode nextNode = this.lpConsumerNode().lvNext();
        if (nextNode != null) {
            return nextNode.lpValue();
        }
        return null;
    }

    @Override
    public boolean relaxedOffer(E e) {
        return this.offer(e);
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        int drained;
        long result = 0L;
        while ((drained = this.drain(c, 4096)) == 4096 && (result += (long)drained) <= 2147479551L) {
        }
        return (int)result;
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
        LinkedQueueNode chaserNode = this.consumerNode;
        for (int i = 0; i < limit; ++i) {
            LinkedQueueNode nextNode = chaserNode.lvNext();
            if (nextNode == null) {
                return i;
            }
            Object nextValue = this.getSingleConsumerNodeValue(chaserNode, nextNode);
            chaserNode = nextNode;
            c.accept(nextValue);
        }
        return limit;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
        LinkedQueueNode chaserNode = this.consumerNode;
        int idleCounter = 0;
        while (exit.keepRunning()) {
            for (int i = 0; i < 4096; ++i) {
                LinkedQueueNode nextNode = chaserNode.lvNext();
                if (nextNode == null) {
                    idleCounter = wait.idle(idleCounter);
                    continue;
                }
                idleCounter = 0;
                Object nextValue = this.getSingleConsumerNodeValue(chaserNode, nextNode);
                chaserNode = nextNode;
                c.accept(nextValue);
            }
        }
    }

    @Override
    public int capacity() {
        return -1;
    }
}

