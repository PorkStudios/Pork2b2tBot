/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2LifecycleManager;
import io.netty.handler.codec.http2.Http2RemoteFlowController;
import io.netty.handler.codec.http2.Http2Settings;

public interface Http2ConnectionEncoder
extends Http2FrameWriter {
    public void lifecycleManager(Http2LifecycleManager var1);

    public Http2Connection connection();

    public Http2RemoteFlowController flowController();

    public Http2FrameWriter frameWriter();

    public Http2Settings pollSentSettings();

    public void remoteSettings(Http2Settings var1) throws Http2Exception;

    @Override
    public ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6);
}

