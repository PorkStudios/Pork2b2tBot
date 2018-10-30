/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.resolver.AbstractAddressResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;

public class NoopAddressResolver
extends AbstractAddressResolver<SocketAddress> {
    public NoopAddressResolver(EventExecutor executor) {
        super(executor);
    }

    @Override
    protected boolean doIsResolved(SocketAddress address) {
        return true;
    }

    @Override
    protected void doResolve(SocketAddress unresolvedAddress, Promise<SocketAddress> promise) throws Exception {
        promise.setSuccess(unresolvedAddress);
    }

    @Override
    protected void doResolveAll(SocketAddress unresolvedAddress, Promise<List<SocketAddress>> promise) throws Exception {
        promise.setSuccess(Collections.singletonList(unresolvedAddress));
    }
}

