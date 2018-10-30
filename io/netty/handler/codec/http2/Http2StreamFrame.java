/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameStream;

public interface Http2StreamFrame
extends Http2Frame {
    public Http2StreamFrame stream(Http2FrameStream var1);

    public Http2FrameStream stream();
}

