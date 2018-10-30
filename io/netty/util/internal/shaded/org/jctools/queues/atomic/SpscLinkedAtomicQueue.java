/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.BaseLinkedAtomicQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedQueueAtomicNode;

public class SpscLinkedAtomicQueue<E>
extends BaseLinkedAtomicQueue<E> {
    public SpscLinkedAtomicQueue() {
        LinkedQueueAtomicNode node = this.newNode();
        this.spProducerNode(node);
        this.spConsumerNode(node);
        node.soNext(null);
    }

    @Override
    public boolean offer(E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        LinkedQueueAtomicNode<E> nextNode = this.newNode(e);
        this.lpProducerNode().soNext(nextNode);
        this.spProducerNode(nextNode);
        return true;
    }

    @Override
    public E poll() {
        return (E)this.relaxedPoll();
    }

    @Override
    public E peek() {
        return (E)this.relaxedPeek();
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s) {
        long result = 0L;
        do {
            this.fill(s, 4096);
        } while ((result += 4096L) <= 2147479551L);
        return (int)result;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
        LinkedQueueAtomicNode<E> tail;
        if (limit == 0) {
            return 0;
        }
        LinkedQueueAtomicNode<E> head = tail = this.newNode(s.get());
        for (int i = 1; i < limit; ++i) {
            LinkedQueueAtomicNode<E> temp = this.newNode(s.get());
            tail.soNext(temp);
            tail = temp;
        }
        LinkedQueueAtomicNode<E> oldPNode = this.lpProducerNode();
        oldPNode.soNext(head);
        this.spProducerNode(tail);
        return limit;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
        LinkedQueueAtomicNode<E> chaserNode = this.producerNode;
        while (exit.keepRunning()) {
            for (int i = 0; i < 4096; ++i) {
                LinkedQueueAtomicNode<E> nextNode = this.newNode(s.get());
                chaserNode.soNext(nextNode);
                this.producerNode = chaserNode = nextNode;
            }
        }
    }
}

