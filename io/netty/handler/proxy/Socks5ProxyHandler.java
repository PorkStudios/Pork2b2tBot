/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5ClientEncoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequest;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import io.netty.handler.proxy.ProxyConnectException;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;

public final class Socks5ProxyHandler
extends ProxyHandler {
    private static final String PROTOCOL = "socks5";
    private static final String AUTH_PASSWORD = "password";
    private static final Socks5InitialRequest INIT_REQUEST_NO_AUTH = new DefaultSocks5InitialRequest(Collections.singletonList(Socks5AuthMethod.NO_AUTH));
    private static final Socks5InitialRequest INIT_REQUEST_PASSWORD = new DefaultSocks5InitialRequest(Arrays.asList(Socks5AuthMethod.NO_AUTH, Socks5AuthMethod.PASSWORD));
    private final String username;
    private final String password;
    private String decoderName;
    private String encoderName;

    public Socks5ProxyHandler(SocketAddress proxyAddress) {
        this(proxyAddress, null, null);
    }

    public Socks5ProxyHandler(SocketAddress proxyAddress, String username, String password) {
        super(proxyAddress);
        if (username != null && username.isEmpty()) {
            username = null;
        }
        if (password != null && password.isEmpty()) {
            password = null;
        }
        this.username = username;
        this.password = password;
    }

    @Override
    public String protocol() {
        return PROTOCOL;
    }

    @Override
    public String authScheme() {
        return this.socksAuthMethod() == Socks5AuthMethod.PASSWORD ? AUTH_PASSWORD : "none";
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
        Socks5InitialResponseDecoder decoder = new Socks5InitialResponseDecoder();
        p.addBefore(name, null, decoder);
        this.decoderName = p.context(decoder).name();
        this.encoderName = this.decoderName + ".encoder";
        p.addBefore(name, this.encoderName, Socks5ClientEncoder.DEFAULT);
    }

    @Override
    protected void removeEncoder(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().remove(this.encoderName);
    }

    @Override
    protected void removeDecoder(ChannelHandlerContext ctx) throws Exception {
        ChannelPipeline p = ctx.pipeline();
        if (p.context(this.decoderName) != null) {
            p.remove(this.decoderName);
        }
    }

    @Override
    protected Object newInitialMessage(ChannelHandlerContext ctx) throws Exception {
        return this.socksAuthMethod() == Socks5AuthMethod.PASSWORD ? INIT_REQUEST_PASSWORD : INIT_REQUEST_NO_AUTH;
    }

    @Override
    protected boolean handleResponse(ChannelHandlerContext ctx, Object response) throws Exception {
        if (response instanceof Socks5InitialResponse) {
            Socks5InitialResponse res = (Socks5InitialResponse)response;
            Socks5AuthMethod authMethod = this.socksAuthMethod();
            if (res.authMethod() != Socks5AuthMethod.NO_AUTH && res.authMethod() != authMethod) {
                throw new ProxyConnectException(this.exceptionMessage("unexpected authMethod: " + res.authMethod()));
            }
            if (authMethod == Socks5AuthMethod.NO_AUTH) {
                this.sendConnectCommand(ctx);
            } else if (authMethod == Socks5AuthMethod.PASSWORD) {
                ctx.pipeline().replace(this.decoderName, this.decoderName, (ChannelHandler)new Socks5PasswordAuthResponseDecoder());
                this.sendToProxyServer(new DefaultSocks5PasswordAuthRequest(this.username != null ? this.username : "", this.password != null ? this.password : ""));
            } else {
                throw new Error();
            }
            return false;
        }
        if (response instanceof Socks5PasswordAuthResponse) {
            Socks5PasswordAuthResponse res = (Socks5PasswordAuthResponse)response;
            if (res.status() != Socks5PasswordAuthStatus.SUCCESS) {
                throw new ProxyConnectException(this.exceptionMessage("authStatus: " + res.status()));
            }
            this.sendConnectCommand(ctx);
            return false;
        }
        Socks5CommandResponse res = (Socks5CommandResponse)response;
        if (res.status() != Socks5CommandStatus.SUCCESS) {
            throw new ProxyConnectException(this.exceptionMessage("status: " + res.status()));
        }
        return true;
    }

    private Socks5AuthMethod socksAuthMethod() {
        Socks5AuthMethod authMethod = this.username == null && this.password == null ? Socks5AuthMethod.NO_AUTH : Socks5AuthMethod.PASSWORD;
        return authMethod;
    }

    private void sendConnectCommand(ChannelHandlerContext ctx) throws Exception {
        String rhost;
        Socks5AddressType addrType;
        InetSocketAddress raddr = (InetSocketAddress)this.destinationAddress();
        if (raddr.isUnresolved()) {
            addrType = Socks5AddressType.DOMAIN;
            rhost = raddr.getHostString();
        } else {
            rhost = raddr.getAddress().getHostAddress();
            if (NetUtil.isValidIpV4Address(rhost)) {
                addrType = Socks5AddressType.IPv4;
            } else if (NetUtil.isValidIpV6Address(rhost)) {
                addrType = Socks5AddressType.IPv6;
            } else {
                throw new ProxyConnectException(this.exceptionMessage("unknown address type: " + StringUtil.simpleClassName(rhost)));
            }
        }
        ctx.pipeline().replace(this.decoderName, this.decoderName, (ChannelHandler)new Socks5CommandResponseDecoder());
        this.sendToProxyServer(new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT, addrType, rhost, raddr.getPort()));
    }
}

