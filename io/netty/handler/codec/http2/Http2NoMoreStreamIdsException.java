/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;

public class Http2NoMoreStreamIdsException
extends Http2Exception {
    private static final long serialVersionUID = -7756236161274851110L;
    private static final String ERROR_MESSAGE = "No more streams can be created on this connection";

    public Http2NoMoreStreamIdsException() {
        super(Http2Error.PROTOCOL_ERROR, ERROR_MESSAGE, Http2Exception.ShutdownHint.GRACEFUL_SHUTDOWN);
    }

    public Http2NoMoreStreamIdsException(Throwable cause) {
        super(Http2Error.PROTOCOL_ERROR, ERROR_MESSAGE, cause, Http2Exception.ShutdownHint.GRACEFUL_SHUTDOWN);
    }
}

