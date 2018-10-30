/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCounted
implements ReferenceCounted {
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCounted> refCntUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCounted.class, "refCnt");
    private volatile int refCnt = 1;

    @Override
    public final int refCnt() {
        return this.refCnt;
    }

    protected final void setRefCnt(int refCnt) {
        refCntUpdater.set(this, refCnt);
    }

    @Override
    public ReferenceCounted retain() {
        return this.retain0(1);
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return this.retain0(ObjectUtil.checkPositive(increment, "increment"));
    }

    private ReferenceCounted retain0(int increment) {
        int oldRef = refCntUpdater.getAndAdd(this, increment);
        if (oldRef <= 0 || oldRef + increment < oldRef) {
            refCntUpdater.getAndAdd(this, - increment);
            throw new IllegalReferenceCountException(oldRef, increment);
        }
        return this;
    }

    @Override
    public ReferenceCounted touch() {
        return this.touch(null);
    }

    @Override
    public boolean release() {
        return this.release0(1);
    }

    @Override
    public boolean release(int decrement) {
        return this.release0(ObjectUtil.checkPositive(decrement, "decrement"));
    }

    private boolean release0(int decrement) {
        int oldRef = refCntUpdater.getAndAdd(this, - decrement);
        if (oldRef == decrement) {
            this.deallocate();
            return true;
        }
        if (oldRef < decrement || oldRef - decrement > oldRef) {
            refCntUpdater.getAndAdd(this, decrement);
            throw new IllegalReferenceCountException(oldRef, decrement);
        }
        return false;
    }

    protected abstract void deallocate();
}

