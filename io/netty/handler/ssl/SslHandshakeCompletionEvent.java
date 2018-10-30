/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.SslCompletionEvent;

public final class SslHandshakeCompletionEvent
extends SslCompletionEvent {
    public static final SslHandshakeCompletionEvent SUCCESS = new SslHandshakeCompletionEvent();

    private SslHandshakeCompletionEvent() {
    }

    public SslHandshakeCompletionEvent(Throwable cause) {
        super(cause);
    }
}

