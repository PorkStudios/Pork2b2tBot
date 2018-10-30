/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.util.internal.ObjectUtil;

public final class Http2FrameStreamException
extends Exception {
    private static final long serialVersionUID = -4407186173493887044L;
    private final Http2Error error;
    private final Http2FrameStream stream;

    public Http2FrameStreamException(Http2FrameStream stream, Http2Error error, Throwable cause) {
        super(cause.getMessage(), cause);
        this.stream = ObjectUtil.checkNotNull(stream, "stream");
        this.error = ObjectUtil.checkNotNull(error, "error");
    }

    public Http2Error error() {
        return this.error;
    }

    public Http2FrameStream stream() {
        return this.stream;
    }
}

