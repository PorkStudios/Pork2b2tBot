/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2MultiplexCodec;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Http2ServerUpgradeCodec
implements HttpServerUpgradeHandler.UpgradeCodec {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http2ServerUpgradeCodec.class);
    private static final List<CharSequence> REQUIRED_UPGRADE_HEADERS = Collections.singletonList(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
    private static final ChannelHandler[] EMPTY_HANDLERS = new ChannelHandler[0];
    private final String handlerName;
    private final Http2ConnectionHandler connectionHandler;
    private final ChannelHandler[] handlers;
    private final Http2FrameReader frameReader;
    private Http2Settings settings;

    public Http2ServerUpgradeCodec(Http2ConnectionHandler connectionHandler) {
        this(null, connectionHandler, EMPTY_HANDLERS);
    }

    public Http2ServerUpgradeCodec(Http2MultiplexCodec http2Codec) {
        this(null, http2Codec, EMPTY_HANDLERS);
    }

    public Http2ServerUpgradeCodec(String handlerName, Http2ConnectionHandler connectionHandler) {
        this(handlerName, connectionHandler, EMPTY_HANDLERS);
    }

    public Http2ServerUpgradeCodec(String handlerName, Http2MultiplexCodec http2Codec) {
        this(handlerName, http2Codec, EMPTY_HANDLERS);
    }

    public /* varargs */ Http2ServerUpgradeCodec(Http2FrameCodec http2Codec, ChannelHandler ... handlers) {
        this(null, http2Codec, handlers);
    }

    private /* varargs */ Http2ServerUpgradeCodec(String handlerName, Http2ConnectionHandler connectionHandler, ChannelHandler ... handlers) {
        this.handlerName = handlerName;
        this.connectionHandler = connectionHandler;
        this.handlers = handlers;
        this.frameReader = new DefaultHttp2FrameReader();
    }

    @Override
    public Collection<CharSequence> requiredUpgradeHeaders() {
        return REQUIRED_UPGRADE_HEADERS;
    }

    @Override
    public boolean prepareUpgradeResponse(ChannelHandlerContext ctx, FullHttpRequest upgradeRequest, HttpHeaders headers) {
        try {
            List<String> upgradeHeaders = upgradeRequest.headers().getAll(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
            if (upgradeHeaders.isEmpty() || upgradeHeaders.size() > 1) {
                throw new IllegalArgumentException("There must be 1 and only 1 " + Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER + " header.");
            }
            this.settings = this.decodeSettingsHeader(ctx, upgradeHeaders.get(0));
            return true;
        }
        catch (Throwable cause) {
            logger.info("Error during upgrade to HTTP/2", cause);
            return false;
        }
    }

    @Override
    public void upgradeTo(ChannelHandlerContext ctx, FullHttpRequest upgradeRequest) {
        try {
            ctx.pipeline().addAfter(ctx.name(), this.handlerName, this.connectionHandler);
            this.connectionHandler.onHttpServerUpgrade(this.settings);
        }
        catch (Http2Exception e) {
            ctx.fireExceptionCaught(e);
            ctx.close();
            return;
        }
        if (this.handlers != null) {
            String name = ctx.pipeline().context(this.connectionHandler).name();
            for (int i = this.handlers.length - 1; i >= 0; --i) {
                ctx.pipeline().addAfter(name, null, this.handlers[i]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Http2Settings decodeSettingsHeader(ChannelHandlerContext ctx, CharSequence settingsHeader) throws Http2Exception {
        ByteBuf header = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(settingsHeader), CharsetUtil.UTF_8);
        try {
            ByteBuf payload = Base64.decode(header, Base64Dialect.URL_SAFE);
            ByteBuf frame = Http2ServerUpgradeCodec.createSettingsFrame(ctx, payload);
            Http2Settings http2Settings = this.decodeSettings(ctx, frame);
            return http2Settings;
        }
        finally {
            header.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Http2Settings decodeSettings(ChannelHandlerContext ctx, ByteBuf frame) throws Http2Exception {
        try {
            final Http2Settings decodedSettings = new Http2Settings();
            this.frameReader.readFrame(ctx, frame, new Http2FrameAdapter(){

                @Override
                public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) {
                    decodedSettings.copyFrom(settings);
                }
            });
            Http2Settings http2Settings = decodedSettings;
            return http2Settings;
        }
        finally {
            frame.release();
        }
    }

    private static ByteBuf createSettingsFrame(ChannelHandlerContext ctx, ByteBuf payload) {
        ByteBuf frame = ctx.alloc().buffer(9 + payload.readableBytes());
        Http2CodecUtil.writeFrameHeader(frame, payload.readableBytes(), (byte)4, new Http2Flags(), 0);
        frame.writeBytes(payload);
        payload.release();
        return frame;
    }

}

