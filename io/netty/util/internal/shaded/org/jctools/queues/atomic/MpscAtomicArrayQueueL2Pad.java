/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueueProducerLimitField;

abstract class MpscAtomicArrayQueueL2Pad<E>
extends MpscAtomicArrayQueueProducerLimitField<E> {
    long p00;
    long p01;
    long p02;
    long p03;
    long p04;
    long p05;
    long p06;
    long p07;
    long p10;
    long p11;
    long p12;
    long p13;
    long p14;
    long p15;
    long p16;

    public MpscAtomicArrayQueueL2Pad(int capacity) {
        super(capacity);
    }
}

