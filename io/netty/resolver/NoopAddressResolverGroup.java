/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.NoopAddressResolver;
import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;

public final class NoopAddressResolverGroup
extends AddressResolverGroup<SocketAddress> {
    public static final NoopAddressResolverGroup INSTANCE = new NoopAddressResolverGroup();

    private NoopAddressResolverGroup() {
    }

    @Override
    protected AddressResolver<SocketAddress> newResolver(EventExecutor executor) throws Exception {
        return new NoopAddressResolver(executor);
    }
}

