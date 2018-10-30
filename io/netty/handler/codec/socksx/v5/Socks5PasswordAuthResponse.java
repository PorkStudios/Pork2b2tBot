/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.socksx.v5.Socks5Message;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;

public interface Socks5PasswordAuthResponse
extends Socks5Message {
    public Socks5PasswordAuthStatus status();
}

