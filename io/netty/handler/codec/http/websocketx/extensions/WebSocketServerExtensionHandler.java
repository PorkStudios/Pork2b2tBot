/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionUtil;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WebSocketServerExtensionHandler
extends ChannelDuplexHandler {
    private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
    private List<WebSocketServerExtension> validExtensions;

    public /* varargs */ WebSocketServerExtensionHandler(WebSocketServerExtensionHandshaker ... extensionHandshakers) {
        if (extensionHandshakers == null) {
            throw new NullPointerException("extensionHandshakers");
        }
        if (extensionHandshakers.length == 0) {
            throw new IllegalArgumentException("extensionHandshakers must contains at least one handshaker");
        }
        this.extensionHandshakers = Arrays.asList(extensionHandshakers);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest request;
        String extensionsHeader;
        if (msg instanceof HttpRequest && WebSocketExtensionUtil.isWebsocketUpgrade((request = (HttpRequest)msg).headers()) && (extensionsHeader = request.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS)) != null) {
            List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions(extensionsHeader);
            int rsv = 0;
            for (WebSocketExtensionData extensionData : extensions) {
                Iterator<WebSocketServerExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
                WebSocketExtension validExtension = null;
                while (validExtension == null && extensionHandshakersIterator.hasNext()) {
                    WebSocketServerExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
                    validExtension = extensionHandshaker.handshakeExtension(extensionData);
                }
                if (validExtension == null || (validExtension.rsv() & rsv) != 0) continue;
                if (this.validExtensions == null) {
                    this.validExtensions = new ArrayList<WebSocketServerExtension>(1);
                }
                rsv |= validExtension.rsv();
                this.validExtensions.add((WebSocketServerExtension)validExtension);
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof HttpResponse && WebSocketExtensionUtil.isWebsocketUpgrade(((HttpResponse)msg).headers()) && this.validExtensions != null) {
            HttpResponse response = (HttpResponse)msg;
            String headerValue = response.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            for (WebSocketServerExtension extension : this.validExtensions) {
                WebSocketExtensionData extensionData = extension.newReponseData();
                headerValue = WebSocketExtensionUtil.appendExtension(headerValue, extensionData.name(), extensionData.parameters());
            }
            promise.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        for (WebSocketServerExtension extension : WebSocketServerExtensionHandler.this.validExtensions) {
                            WebSocketExtensionDecoder decoder = extension.newExtensionDecoder();
                            WebSocketExtensionEncoder encoder = extension.newExtensionEncoder();
                            ctx.pipeline().addAfter(ctx.name(), decoder.getClass().getName(), decoder);
                            ctx.pipeline().addAfter(ctx.name(), encoder.getClass().getName(), encoder);
                        }
                    }
                    ctx.pipeline().remove(ctx.name());
                }
            });
            if (headerValue != null) {
                response.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, (Object)headerValue);
            }
        }
        super.write(ctx, msg, promise);
    }

}

