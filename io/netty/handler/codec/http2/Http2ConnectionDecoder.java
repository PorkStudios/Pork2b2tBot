/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2LifecycleManager;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Settings;
import java.io.Closeable;
import java.util.List;

public interface Http2ConnectionDecoder
extends Closeable {
    public void lifecycleManager(Http2LifecycleManager var1);

    public Http2Connection connection();

    public Http2LocalFlowController flowController();

    public void frameListener(Http2FrameListener var1);

    public Http2FrameListener frameListener();

    public void decodeFrame(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Http2Exception;

    public Http2Settings localSettings();

    public boolean prefaceReceived();

    @Override
    public void close();
}

