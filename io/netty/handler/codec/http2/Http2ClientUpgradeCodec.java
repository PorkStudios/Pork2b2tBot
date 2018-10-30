/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientUpgradeHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.collection.CharObjectMap;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Http2ClientUpgradeCodec
implements HttpClientUpgradeHandler.UpgradeCodec {
    private static final List<CharSequence> UPGRADE_HEADERS = Collections.singletonList(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
    private final String handlerName;
    private final Http2ConnectionHandler connectionHandler;
    private final ChannelHandler upgradeToHandler;

    public Http2ClientUpgradeCodec(Http2FrameCodec frameCodec, ChannelHandler upgradeToHandler) {
        this(null, frameCodec, upgradeToHandler);
    }

    public Http2ClientUpgradeCodec(String handlerName, Http2FrameCodec frameCodec, ChannelHandler upgradeToHandler) {
        this(handlerName, (Http2ConnectionHandler)frameCodec, upgradeToHandler);
    }

    public Http2ClientUpgradeCodec(Http2ConnectionHandler connectionHandler) {
        this((String)null, connectionHandler);
    }

    public Http2ClientUpgradeCodec(String handlerName, Http2ConnectionHandler connectionHandler) {
        this(handlerName, connectionHandler, (ChannelHandler)connectionHandler);
    }

    private Http2ClientUpgradeCodec(String handlerName, Http2ConnectionHandler connectionHandler, ChannelHandler upgradeToHandler) {
        this.handlerName = handlerName;
        this.connectionHandler = ObjectUtil.checkNotNull(connectionHandler, "connectionHandler");
        this.upgradeToHandler = ObjectUtil.checkNotNull(upgradeToHandler, "upgradeToHandler");
    }

    @Override
    public CharSequence protocol() {
        return Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME;
    }

    @Override
    public Collection<CharSequence> setUpgradeHeaders(ChannelHandlerContext ctx, HttpRequest upgradeRequest) {
        CharSequence settingsValue = this.getSettingsHeaderValue(ctx);
        upgradeRequest.headers().set(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER, (Object)settingsValue);
        return UPGRADE_HEADERS;
    }

    @Override
    public void upgradeTo(ChannelHandlerContext ctx, FullHttpResponse upgradeResponse) throws Exception {
        ctx.pipeline().addAfter(ctx.name(), this.handlerName, this.upgradeToHandler);
        this.connectionHandler.onHttpClientUpgrade();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CharSequence getSettingsHeaderValue(ChannelHandlerContext ctx) {
        String string;
        ByteBuf buf = null;
        ByteBuf encodedBuf = null;
        try {
            Http2Settings settings = this.connectionHandler.decoder().localSettings();
            int payloadLength = 6 * settings.size();
            buf = ctx.alloc().buffer(payloadLength);
            for (CharObjectMap.PrimitiveEntry entry : settings.entries()) {
                buf.writeChar(entry.key());
                buf.writeInt(((Long)entry.value()).intValue());
            }
            encodedBuf = Base64.encode(buf, Base64Dialect.URL_SAFE);
            string = encodedBuf.toString(CharsetUtil.UTF_8);
        }
        catch (Throwable throwable) {
            ReferenceCountUtil.release(buf);
            ReferenceCountUtil.release(encodedBuf);
            throw throwable;
        }
        ReferenceCountUtil.release(buf);
        ReferenceCountUtil.release(encodedBuf);
        return string;
    }
}

