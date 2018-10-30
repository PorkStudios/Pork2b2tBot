/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Stream;

public interface Http2FlowController {
    public void channelHandlerContext(ChannelHandlerContext var1) throws Http2Exception;

    public void initialWindowSize(int var1) throws Http2Exception;

    public int initialWindowSize();

    public int windowSize(Http2Stream var1);

    public void incrementWindowSize(Http2Stream var1, int var2) throws Http2Exception;
}

