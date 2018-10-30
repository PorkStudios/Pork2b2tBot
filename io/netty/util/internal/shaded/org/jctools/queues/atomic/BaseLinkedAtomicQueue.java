/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.BaseLinkedAtomicQueuePad2;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedQueueAtomicNode;
import java.util.Iterator;

abstract class BaseLinkedAtomicQueue<E>
extends BaseLinkedAtomicQueuePad2<E> {
    BaseLinkedAtomicQueue() {
    }

    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    protected final LinkedQueueAtomicNode<E> newNode() {
        return new LinkedQueueAtomicNode();
    }

    protected final LinkedQueueAtomicNode<E> newNode(E e) {
        return new LinkedQueueAtomicNode<E>(e);
    }

    @Override
    public final int size() {
        int size;
        LinkedQueueAtomicNode chaserNode = this.lvConsumerNode();
        LinkedQueueAtomicNode producerNode = this.lvProducerNode();
        for (size = 0; chaserNode != producerNode && chaserNode != null && size < Integer.MAX_VALUE; ++size) {
            LinkedQueueAtomicNode next = chaserNode.lvNext();
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

    protected E getSingleConsumerNodeValue(LinkedQueueAtomicNode<E> currConsumerNode, LinkedQueueAtomicNode<E> nextNode) {
        E nextValue = nextNode.getAndNullValue();
        currConsumerNode.soNext(currConsumerNode);
        this.spConsumerNode(nextNode);
        return nextValue;
    }

    @Override
    public E relaxedPoll() {
        LinkedQueueAtomicNode currConsumerNode = this.lpConsumerNode();
        LinkedQueueAtomicNode nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
        }
        return null;
    }

    @Override
    public E relaxedPeek() {
        LinkedQueueAtomicNode nextNode = this.lpConsumerNode().lvNext();
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
        LinkedQueueAtomicNode chaserNode = this.consumerNode;
        for (int i = 0; i < limit; ++i) {
            LinkedQueueAtomicNode nextNode = chaserNode.lvNext();
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
        LinkedQueueAtomicNode chaserNode = this.consumerNode;
        int idleCounter = 0;
        while (exit.keepRunning()) {
            for (int i = 0; i < 4096; ++i) {
                LinkedQueueAtomicNode nextNode = chaserNode.lvNext();
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

