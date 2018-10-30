/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.unix;

import io.netty.channel.ServerChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.UnixChannel;

public interface ServerDomainSocketChannel
extends ServerChannel,
UnixChannel {
    @Override
    public DomainSocketAddress remoteAddress();

    @Override
    public DomainSocketAddress localAddress();
}

