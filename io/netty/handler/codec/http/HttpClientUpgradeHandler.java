/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class HttpClientUpgradeHandler
extends HttpObjectAggregator
implements ChannelOutboundHandler {
    private final SourceCodec sourceCodec;
    private final UpgradeCodec upgradeCodec;
    private boolean upgradeRequested;

    public HttpClientUpgradeHandler(SourceCodec sourceCodec, UpgradeCodec upgradeCodec, int maxContentLength) {
        super(maxContentLength);
        if (sourceCodec == null) {
            throw new NullPointerException("sourceCodec");
        }
        if (upgradeCodec == null) {
            throw new NullPointerException("upgradeCodec");
        }
        this.sourceCodec = sourceCodec;
        this.upgradeCodec = upgradeCodec;
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            ctx.write(msg, promise);
            return;
        }
        if (this.upgradeRequested) {
            promise.setFailure(new IllegalStateException("Attempting to write HTTP request with upgrade in progress"));
            return;
        }
        this.upgradeRequested = true;
        this.setUpgradeRequestHeaders(ctx, (HttpRequest)msg);
        ctx.write(msg, promise);
        ctx.fireUserEventTriggered((Object)UpgradeEvent.UPGRADE_ISSUED);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        FullHttpResponse response = null;
        try {
            HttpResponse rep;
            if (!this.upgradeRequested) {
                throw new IllegalStateException("Read HTTP response without requesting protocol switch");
            }
            if (msg instanceof HttpResponse && !HttpResponseStatus.SWITCHING_PROTOCOLS.equals((rep = (HttpResponse)msg).status())) {
                ctx.fireUserEventTriggered((Object)UpgradeEvent.UPGRADE_REJECTED);
                HttpClientUpgradeHandler.removeThisHandler(ctx);
                ctx.fireChannelRead(msg);
                return;
            }
            if (msg instanceof FullHttpResponse) {
                response = (FullHttpResponse)msg;
                response.retain();
                out.add(response);
            } else {
                super.decode(ctx, msg, out);
                if (out.isEmpty()) {
                    return;
                }
                assert (out.size() == 1);
                response = (FullHttpResponse)out.get(0);
            }
            String upgradeHeader = response.headers().get(HttpHeaderNames.UPGRADE);
            if (upgradeHeader != null && !AsciiString.contentEqualsIgnoreCase(this.upgradeCodec.protocol(), upgradeHeader)) {
                throw new IllegalStateException("Switching Protocols response with unexpected UPGRADE protocol: " + upgradeHeader);
            }
            this.sourceCodec.prepareUpgradeFrom(ctx);
            this.upgradeCodec.upgradeTo(ctx, response);
            ctx.fireUserEventTriggered((Object)UpgradeEvent.UPGRADE_SUCCESSFUL);
            this.sourceCodec.upgradeFrom(ctx);
            response.release();
            out.clear();
            HttpClientUpgradeHandler.removeThisHandler(ctx);
        }
        catch (Throwable t) {
            ReferenceCountUtil.release(response);
            ctx.fireExceptionCaught(t);
            HttpClientUpgradeHandler.removeThisHandler(ctx);
        }
    }

    private static void removeThisHandler(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(ctx.name());
    }

    private void setUpgradeRequestHeaders(ChannelHandlerContext ctx, HttpRequest request) {
        request.headers().set((CharSequence)HttpHeaderNames.UPGRADE, (Object)this.upgradeCodec.protocol());
        LinkedHashSet<CharSequence> connectionParts = new LinkedHashSet<CharSequence>(2);
        connectionParts.addAll(this.upgradeCodec.setUpgradeHeaders(ctx, request));
        StringBuilder builder = new StringBuilder();
        for (CharSequence part : connectionParts) {
            builder.append(part);
            builder.append(',');
        }
        builder.append(HttpHeaderValues.UPGRADE);
        request.headers().set((CharSequence)HttpHeaderNames.CONNECTION, (Object)builder.toString());
    }

    public static interface UpgradeCodec {
        public CharSequence protocol();

        public Collection<CharSequence> setUpgradeHeaders(ChannelHandlerContext var1, HttpRequest var2);

        public void upgradeTo(ChannelHandlerContext var1, FullHttpResponse var2) throws Exception;
    }

    public static interface SourceCodec {
        public void prepareUpgradeFrom(ChannelHandlerContext var1);

        public void upgradeFrom(ChannelHandlerContext var1);
    }

    public static enum UpgradeEvent {
        UPGRADE_ISSUED,
        UPGRADE_SUCCESSFUL,
        UPGRADE_REJECTED;
        

        private UpgradeEvent() {
        }
    }

}

