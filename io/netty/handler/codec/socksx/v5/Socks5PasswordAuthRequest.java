/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.socksx.v5.Socks5Message;

public interface Socks5PasswordAuthRequest
extends Socks5Message {
    public String username();

    public String password();
}

