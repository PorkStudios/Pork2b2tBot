/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.NoSuchElementException;

@GwtCompatible
public abstract class AbstractIterator<T>
extends UnmodifiableIterator<T> {
    private State state = State.NOT_READY;
    private T next;

    protected AbstractIterator() {
    }

    protected abstract T computeNext();

    @CanIgnoreReturnValue
    protected final T endOfData() {
        this.state = State.DONE;
        return null;
    }

    @CanIgnoreReturnValue
    @Override
    public final boolean hasNext() {
        Preconditions.checkState(this.state != State.FAILED);
        switch (this.state) {
            case DONE: {
                return false;
            }
            case READY: {
                return true;
            }
        }
        return this.tryToComputeNext();
    }

    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        this.next = this.computeNext();
        if (this.state != State.DONE) {
            this.state = State.READY;
            return true;
        }
        return false;
    }

    @CanIgnoreReturnValue
    @Override
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.state = State.NOT_READY;
        T result = this.next;
        this.next = null;
        return result;
    }

    public final T peek() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.next;
    }

    private static enum State {
        READY,
        NOT_READY,
        DONE,
        FAILED;
        

        private State() {
        }
    }

}

