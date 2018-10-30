/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5Message;
import java.util.List;

public interface Socks5InitialRequest
extends Socks5Message {
    public List<Socks5AuthMethod> authMethods();
}

