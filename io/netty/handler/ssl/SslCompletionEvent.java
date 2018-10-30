/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;

public abstract class SslCompletionEvent {
    private final Throwable cause;

    SslCompletionEvent() {
        this.cause = null;
    }

    SslCompletionEvent(Throwable cause) {
        this.cause = ObjectUtil.checkNotNull(cause, "cause");
    }

    public final boolean isSuccess() {
        return this.cause == null;
    }

    public final Throwable cause() {
        return this.cause;
    }

    public String toString() {
        Throwable cause = this.cause();
        return cause == null ? this.getClass().getSimpleName() + "(SUCCESS)" : this.getClass().getSimpleName() + '(' + cause + ')';
    }
}

