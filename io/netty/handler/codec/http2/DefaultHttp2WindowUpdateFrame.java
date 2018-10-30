/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.AbstractHttp2StreamFrame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.handler.codec.http2.Http2WindowUpdateFrame;

public class DefaultHttp2WindowUpdateFrame
extends AbstractHttp2StreamFrame
implements Http2WindowUpdateFrame {
    private final int windowUpdateIncrement;

    public DefaultHttp2WindowUpdateFrame(int windowUpdateIncrement) {
        this.windowUpdateIncrement = windowUpdateIncrement;
    }

    @Override
    public DefaultHttp2WindowUpdateFrame stream(Http2FrameStream stream) {
        super.stream(stream);
        return this;
    }

    @Override
    public String name() {
        return "WINDOW_UPDATE";
    }

    @Override
    public int windowSizeIncrement() {
        return this.windowUpdateIncrement;
    }
}

