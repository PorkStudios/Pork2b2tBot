/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.proxy.ProxyConnectException;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

public final class HttpProxyHandler
extends ProxyHandler {
    private static final String PROTOCOL = "http";
    private static final String AUTH_BASIC = "basic";
    private final HttpClientCodec codec = new HttpClientCodec();
    private final String username;
    private final String password;
    private final CharSequence authorization;
    private HttpResponseStatus status;
    private HttpHeaders headers;

    public HttpProxyHandler(SocketAddress proxyAddress) {
        this(proxyAddress, null);
    }

    public HttpProxyHandler(SocketAddress proxyAddress, HttpHeaders headers) {
        super(proxyAddress);
        this.username = null;
        this.password = null;
        this.authorization = null;
        this.headers = headers;
    }

    public HttpProxyHandler(SocketAddress proxyAddress, String username, String password) {
        this(proxyAddress, username, password, null);
    }

    public HttpProxyHandler(SocketAddress proxyAddress, String username, String password, HttpHeaders headers) {
        super(proxyAddress);
        if (username == null) {
            throw new NullPointerException("username");
        }
        if (password == null) {
            throw new NullPointerException("password");
        }
        this.username = username;
        this.password = password;
        ByteBuf authz = Unpooled.copiedBuffer(username + ':' + password, CharsetUtil.UTF_8);
        ByteBuf authzBase64 = Base64.encode(authz, false);
        this.authorization = new AsciiString("Basic " + authzBase64.toString(CharsetUtil.US_ASCII));
        authz.release();
        authzBase64.release();
        this.headers = headers;
    }

    @Override
    public String protocol() {
        return PROTOCOL;
    }

    @Override
    public String authScheme() {
        return this.authorization != null ? AUTH_BASIC : "none";
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    @Override
    protected void addCodec(ChannelHandlerContext ctx) throws Exception {
        ChannelPipeline p = ctx.pipeline();
        String name = ctx.name();
        p.addBefore(name, null, this.codec);
    }

    @Override
    protected void removeEncoder(ChannelHandlerContext ctx) throws Exception {
        this.codec.removeOutboundHandler();
    }

    @Override
    protected void removeDecoder(ChannelHandlerContext ctx) throws Exception {
        this.codec.removeInboundHandler();
    }

    @Override
    protected Object newInitialMessage(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress raddr = (InetSocketAddress)this.destinationAddress();
        String host = NetUtil.toSocketAddressString(raddr);
        DefaultFullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, host, Unpooled.EMPTY_BUFFER, false);
        req.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)host);
        if (this.authorization != null) {
            req.headers().set((CharSequence)HttpHeaderNames.PROXY_AUTHORIZATION, (Object)this.authorization);
        }
        if (this.headers != null) {
            req.headers().add(this.headers);
        }
        return req;
    }

    @Override
    protected boolean handleResponse(ChannelHandlerContext ctx, Object response) throws Exception {
        boolean finished;
        if (response instanceof HttpResponse) {
            if (this.status != null) {
                throw new ProxyConnectException(this.exceptionMessage("too many responses"));
            }
            this.status = ((HttpResponse)response).status();
        }
        if (finished = response instanceof LastHttpContent) {
            if (this.status == null) {
                throw new ProxyConnectException(this.exceptionMessage("missing response"));
            }
            if (this.status.code() != 200) {
                throw new ProxyConnectException(this.exceptionMessage("status: " + this.status));
            }
        }
        return finished;
    }
}

