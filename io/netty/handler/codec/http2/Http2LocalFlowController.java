/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FlowController;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Stream;

public interface Http2LocalFlowController
extends Http2FlowController {
    public Http2LocalFlowController frameWriter(Http2FrameWriter var1);

    public void receiveFlowControlledFrame(Http2Stream var1, ByteBuf var2, int var3, boolean var4) throws Http2Exception;

    public boolean consumeBytes(Http2Stream var1, int var2) throws Http2Exception;

    public int unconsumedBytes(Http2Stream var1);

    public int initialWindowSize(Http2Stream var1);
}

