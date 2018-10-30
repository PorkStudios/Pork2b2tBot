/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2LifecycleManager;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class DecoratingHttp2ConnectionDecoder
implements Http2ConnectionDecoder {
    private final Http2ConnectionDecoder delegate;

    public DecoratingHttp2ConnectionDecoder(Http2ConnectionDecoder delegate) {
        this.delegate = ObjectUtil.checkNotNull(delegate, "delegate");
    }

    @Override
    public void lifecycleManager(Http2LifecycleManager lifecycleManager) {
        this.delegate.lifecycleManager(lifecycleManager);
    }

    @Override
    public Http2Connection connection() {
        return this.delegate.connection();
    }

    @Override
    public Http2LocalFlowController flowController() {
        return this.delegate.flowController();
    }

    @Override
    public void frameListener(Http2FrameListener listener) {
        this.delegate.frameListener(listener);
    }

    @Override
    public Http2FrameListener frameListener() {
        return this.delegate.frameListener();
    }

    @Override
    public void decodeFrame(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Http2Exception {
        this.delegate.decodeFrame(ctx, in, out);
    }

    @Override
    public Http2Settings localSettings() {
        return this.delegate.localSettings();
    }

    @Override
    public boolean prefaceReceived() {
        return this.delegate.prefaceReceived();
    }

    @Override
    public void close() {
        this.delegate.close();
    }
}

