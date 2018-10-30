/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.cookie.Cookie;

@Deprecated
public final class ClientCookieEncoder {
    @Deprecated
    public static String encode(String name, String value) {
        return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(name, value);
    }

    @Deprecated
    public static String encode(io.netty.handler.codec.http.Cookie cookie) {
        return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode((Cookie)cookie);
    }

    @Deprecated
    public static /* varargs */ String encode(io.netty.handler.codec.http.Cookie ... cookies) {
        return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(cookies);
    }

    @Deprecated
    public static String encode(Iterable<io.netty.handler.codec.http.Cookie> cookies) {
        return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(cookies);
    }

    private ClientCookieEncoder() {
    }
}

