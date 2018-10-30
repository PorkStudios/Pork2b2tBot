/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.util.AsciiString;

public final class WebSocketScheme {
    public static final WebSocketScheme WS = new WebSocketScheme(80, "ws");
    public static final WebSocketScheme WSS = new WebSocketScheme(443, "wss");
    private final int port;
    private final AsciiString name;

    private WebSocketScheme(int port, String name) {
        this.port = port;
        this.name = AsciiString.cached(name);
    }

    public AsciiString name() {
        return this.name;
    }

    public int port() {
        return this.port;
    }

    public boolean equals(Object o) {
        if (!(o instanceof WebSocketScheme)) {
            return false;
        }
        WebSocketScheme other = (WebSocketScheme)o;
        return other.port() == this.port && other.name().equals(this.name);
    }

    public int hashCode() {
        return this.port * 31 + this.name.hashCode();
    }

    public String toString() {
        return this.name.toString();
    }
}

