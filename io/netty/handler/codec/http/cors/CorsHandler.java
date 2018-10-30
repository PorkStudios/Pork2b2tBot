/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cors;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Set;

public class CorsHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
    private static final String ANY_ORIGIN = "*";
    private static final String NULL_ORIGIN = "null";
    private final CorsConfig config;
    private HttpRequest request;

    public CorsHandler(CorsConfig config) {
        this.config = ObjectUtil.checkNotNull(config, "config");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.config.isCorsSupportEnabled() && msg instanceof HttpRequest) {
            this.request = (HttpRequest)msg;
            if (CorsHandler.isPreflightRequest(this.request)) {
                this.handlePreflight(ctx, this.request);
                return;
            }
            if (this.config.isShortCircuit() && !this.validateOrigin()) {
                CorsHandler.forbidden(ctx, this.request);
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }

    private void handlePreflight(ChannelHandlerContext ctx, HttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, true, true);
        if (this.setOrigin(response)) {
            this.setAllowMethods(response);
            this.setAllowHeaders(response);
            this.setAllowCredentials(response);
            this.setMaxAge(response);
            this.setPreflightHeaders(response);
        }
        if (!response.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
            response.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
        }
        ReferenceCountUtil.release(request);
        CorsHandler.respond(ctx, request, response);
    }

    private void setPreflightHeaders(HttpResponse response) {
        response.headers().add(this.config.preflightResponseHeaders());
    }

    private boolean setOrigin(HttpResponse response) {
        String origin = this.request.headers().get(HttpHeaderNames.ORIGIN);
        if (origin != null) {
            if (NULL_ORIGIN.equals(origin) && this.config.isNullOriginAllowed()) {
                CorsHandler.setNullOrigin(response);
                return true;
            }
            if (this.config.isAnyOriginSupported()) {
                if (this.config.isCredentialsAllowed()) {
                    this.echoRequestOrigin(response);
                    CorsHandler.setVaryHeader(response);
                } else {
                    CorsHandler.setAnyOrigin(response);
                }
                return true;
            }
            if (this.config.origins().contains(origin)) {
                CorsHandler.setOrigin(response, origin);
                CorsHandler.setVaryHeader(response);
                return true;
            }
            logger.debug("Request origin [{}]] was not among the configured origins [{}]", (Object)origin, (Object)this.config.origins());
        }
        return false;
    }

    private boolean validateOrigin() {
        if (this.config.isAnyOriginSupported()) {
            return true;
        }
        String origin = this.request.headers().get(HttpHeaderNames.ORIGIN);
        if (origin == null) {
            return true;
        }
        if (NULL_ORIGIN.equals(origin) && this.config.isNullOriginAllowed()) {
            return true;
        }
        return this.config.origins().contains(origin);
    }

    private void echoRequestOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, this.request.headers().get(HttpHeaderNames.ORIGIN));
    }

    private static void setVaryHeader(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.VARY, (Object)HttpHeaderNames.ORIGIN);
    }

    private static void setAnyOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, ANY_ORIGIN);
    }

    private static void setNullOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, NULL_ORIGIN);
    }

    private static void setOrigin(HttpResponse response, String origin) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, (Object)origin);
    }

    private void setAllowCredentials(HttpResponse response) {
        if (this.config.isCredentialsAllowed() && !response.headers().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN).equals(ANY_ORIGIN)) {
            response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, (Object)"true");
        }
    }

    private static boolean isPreflightRequest(HttpRequest request) {
        HttpHeaders headers = request.headers();
        return request.method().equals(HttpMethod.OPTIONS) && headers.contains(HttpHeaderNames.ORIGIN) && headers.contains(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
    }

    private void setExposeHeaders(HttpResponse response) {
        if (!this.config.exposedHeaders().isEmpty()) {
            response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, this.config.exposedHeaders());
        }
    }

    private void setAllowMethods(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, this.config.allowedRequestMethods());
    }

    private void setAllowHeaders(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, this.config.allowedRequestHeaders());
    }

    private void setMaxAge(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, (Object)this.config.maxAge());
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        HttpResponse response;
        if (this.config.isCorsSupportEnabled() && msg instanceof HttpResponse && this.setOrigin(response = (HttpResponse)msg)) {
            this.setAllowCredentials(response);
            this.setExposeHeaders(response);
        }
        ctx.writeAndFlush(msg, promise);
    }

    private static void forbidden(ChannelHandlerContext ctx, HttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN);
        response.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
        ReferenceCountUtil.release(request);
        CorsHandler.respond(ctx, request, response);
    }

    private static void respond(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setKeepAlive(response, keepAlive);
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}

