/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.proxy;

import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public final class ProxyConnectionEvent {
    private final String protocol;
    private final String authScheme;
    private final SocketAddress proxyAddress;
    private final SocketAddress destinationAddress;
    private String strVal;

    public ProxyConnectionEvent(String protocol, String authScheme, SocketAddress proxyAddress, SocketAddress destinationAddress) {
        if (protocol == null) {
            throw new NullPointerException("protocol");
        }
        if (authScheme == null) {
            throw new NullPointerException("authScheme");
        }
        if (proxyAddress == null) {
            throw new NullPointerException("proxyAddress");
        }
        if (destinationAddress == null) {
            throw new NullPointerException("destinationAddress");
        }
        this.protocol = protocol;
        this.authScheme = authScheme;
        this.proxyAddress = proxyAddress;
        this.destinationAddress = destinationAddress;
    }

    public String protocol() {
        return this.protocol;
    }

    public String authScheme() {
        return this.authScheme;
    }

    public <T extends SocketAddress> T proxyAddress() {
        return (T)this.proxyAddress;
    }

    public <T extends SocketAddress> T destinationAddress() {
        return (T)this.destinationAddress;
    }

    public String toString() {
        if (this.strVal != null) {
            return this.strVal;
        }
        StringBuilder buf = new StringBuilder(128).append(StringUtil.simpleClassName(this)).append('(').append(this.protocol).append(", ").append(this.authScheme).append(", ").append(this.proxyAddress).append(" => ").append(this.destinationAddress).append(')');
        this.strVal = buf.toString();
        return this.strVal;
    }
}

