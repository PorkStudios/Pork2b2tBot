/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.DefaultNameResolver;
import io.netty.util.concurrent.EventExecutor;
import java.net.InetSocketAddress;

public final class DefaultAddressResolverGroup
extends AddressResolverGroup<InetSocketAddress> {
    public static final DefaultAddressResolverGroup INSTANCE = new DefaultAddressResolverGroup();

    private DefaultAddressResolverGroup() {
    }

    @Override
    protected AddressResolver<InetSocketAddress> newResolver(EventExecutor executor) throws Exception {
        return new DefaultNameResolver(executor).asAddressResolver();
    }
}

