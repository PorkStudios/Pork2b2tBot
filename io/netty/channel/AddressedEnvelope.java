/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.util.ReferenceCounted;
import java.net.SocketAddress;

public interface AddressedEnvelope<M, A extends SocketAddress>
extends ReferenceCounted {
    public M content();

    public A sender();

    public A recipient();

    @Override
    public AddressedEnvelope<M, A> retain();

    @Override
    public AddressedEnvelope<M, A> retain(int var1);

    @Override
    public AddressedEnvelope<M, A> touch();

    @Override
    public AddressedEnvelope<M, A> touch(Object var1);
}

