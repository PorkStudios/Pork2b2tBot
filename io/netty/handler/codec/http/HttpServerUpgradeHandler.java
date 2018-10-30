/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpServerUpgradeHandler
extends HttpObjectAggregator {
    private final SourceCodec sourceCodec;
    private final UpgradeCodecFactory upgradeCodecFactory;
    private boolean handlingUpgrade;

    public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory) {
        this(sourceCodec, upgradeCodecFactory, 0);
    }

    public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory, int maxContentLength) {
        super(maxContentLength);
        this.sourceCodec = ObjectUtil.checkNotNull(sourceCodec, "sourceCodec");
        this.upgradeCodecFactory = ObjectUtil.checkNotNull(upgradeCodecFactory, "upgradeCodecFactory");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        FullHttpRequest fullRequest;
        this.handlingUpgrade |= HttpServerUpgradeHandler.isUpgradeRequest(msg);
        if (!this.handlingUpgrade) {
            ReferenceCountUtil.retain(msg);
            out.add(msg);
            return;
        }
        if (msg instanceof FullHttpRequest) {
            fullRequest = (FullHttpRequest)msg;
            ReferenceCountUtil.retain(msg);
            out.add(msg);
        } else {
            super.decode(ctx, msg, out);
            if (out.isEmpty()) {
                return;
            }
            assert (out.size() == 1);
            this.handlingUpgrade = false;
            fullRequest = (FullHttpRequest)out.get(0);
        }
        if (this.upgrade(ctx, fullRequest)) {
            out.clear();
        }
    }

    private static boolean isUpgradeRequest(HttpObject msg) {
        return msg instanceof HttpRequest && ((HttpRequest)msg).headers().get(HttpHeaderNames.UPGRADE) != null;
    }

    private boolean upgrade(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        List<CharSequence> requestedProtocols = HttpServerUpgradeHandler.splitHeader(request.headers().get(HttpHeaderNames.UPGRADE));
        int numRequestedProtocols = requestedProtocols.size();
        UpgradeCodec upgradeCodec = null;
        CharSequence upgradeProtocol = null;
        for (int i = 0; i < numRequestedProtocols; ++i) {
            CharSequence p = requestedProtocols.get(i);
            UpgradeCodec c = this.upgradeCodecFactory.newUpgradeCodec(p);
            if (c == null) continue;
            upgradeProtocol = p;
            upgradeCodec = c;
            break;
        }
        if (upgradeCodec == null) {
            return false;
        }
        String connectionHeader = request.headers().get(HttpHeaderNames.CONNECTION);
        if (connectionHeader == null) {
            return false;
        }
        Collection<CharSequence> requiredHeaders = upgradeCodec.requiredUpgradeHeaders();
        List<CharSequence> values = HttpServerUpgradeHandler.splitHeader(connectionHeader);
        if (!AsciiString.containsContentEqualsIgnoreCase(values, HttpHeaderNames.UPGRADE) || !AsciiString.containsAllContentEqualsIgnoreCase(values, requiredHeaders)) {
            return false;
        }
        for (CharSequence requiredHeader : requiredHeaders) {
            if (request.headers().contains(requiredHeader)) continue;
            return false;
        }
        FullHttpResponse upgradeResponse = HttpServerUpgradeHandler.createUpgradeResponse(upgradeProtocol);
        if (!upgradeCodec.prepareUpgradeResponse(ctx, request, upgradeResponse.headers())) {
            return false;
        }
        final UpgradeEvent event = new UpgradeEvent(upgradeProtocol, request);
        final UpgradeCodec finalUpgradeCodec = upgradeCodec;
        ctx.writeAndFlush(upgradeResponse).addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                try {
                    if (future.isSuccess()) {
                        HttpServerUpgradeHandler.this.sourceCodec.upgradeFrom(ctx);
                        finalUpgradeCodec.upgradeTo(ctx, request);
                        ctx.fireUserEventTriggered(event.retain());
                        ctx.pipeline().remove(HttpServerUpgradeHandler.this);
                    } else {
                        future.channel().close();
                    }
                }
                finally {
                    event.release();
                }
            }
        });
        return true;
    }

    private static FullHttpResponse createUpgradeResponse(CharSequence upgradeProtocol) {
        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, Unpooled.EMPTY_BUFFER, false);
        res.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
        res.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)upgradeProtocol);
        return res;
    }

    private static List<CharSequence> splitHeader(CharSequence header) {
        StringBuilder builder = new StringBuilder(header.length());
        ArrayList<CharSequence> protocols = new ArrayList<CharSequence>(4);
        for (int i = 0; i < header.length(); ++i) {
            char c = header.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (c == ',') {
                protocols.add(builder.toString());
                builder.setLength(0);
                continue;
            }
            builder.append(c);
        }
        if (builder.length() > 0) {
            protocols.add(builder.toString());
        }
        return protocols;
    }

    public static final class UpgradeEvent
    implements ReferenceCounted {
        private final CharSequence protocol;
        private final FullHttpRequest upgradeRequest;

        UpgradeEvent(CharSequence protocol, FullHttpRequest upgradeRequest) {
            this.protocol = protocol;
            this.upgradeRequest = upgradeRequest;
        }

        public CharSequence protocol() {
            return this.protocol;
        }

        public FullHttpRequest upgradeRequest() {
            return this.upgradeRequest;
        }

        @Override
        public int refCnt() {
            return this.upgradeRequest.refCnt();
        }

        @Override
        public UpgradeEvent retain() {
            this.upgradeRequest.retain();
            return this;
        }

        @Override
        public UpgradeEvent retain(int increment) {
            this.upgradeRequest.retain(increment);
            return this;
        }

        @Override
        public UpgradeEvent touch() {
            this.upgradeRequest.touch();
            return this;
        }

        @Override
        public UpgradeEvent touch(Object hint) {
            this.upgradeRequest.touch(hint);
            return this;
        }

        @Override
        public boolean release() {
            return this.upgradeRequest.release();
        }

        @Override
        public boolean release(int decrement) {
            return this.upgradeRequest.release(decrement);
        }

        public String toString() {
            return "UpgradeEvent [protocol=" + this.protocol + ", upgradeRequest=" + this.upgradeRequest + ']';
        }
    }

    public static interface UpgradeCodecFactory {
        public UpgradeCodec newUpgradeCodec(CharSequence var1);
    }

    public static interface UpgradeCodec {
        public Collection<CharSequence> requiredUpgradeHeaders();

        public boolean prepareUpgradeResponse(ChannelHandlerContext var1, FullHttpRequest var2, HttpHeaders var3);

        public void upgradeTo(ChannelHandlerContext var1, FullHttpRequest var2);
    }

    public static interface SourceCodec {
        public void upgradeFrom(ChannelHandlerContext var1);
    }

}

