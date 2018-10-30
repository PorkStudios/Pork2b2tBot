/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.SslCompletionEvent;

public final class SslCloseCompletionEvent
extends SslCompletionEvent {
    public static final SslCloseCompletionEvent SUCCESS = new SslCloseCompletionEvent();

    private SslCloseCompletionEvent() {
    }

    public SslCloseCompletionEvent(Throwable cause) {
        super(cause);
    }
}

