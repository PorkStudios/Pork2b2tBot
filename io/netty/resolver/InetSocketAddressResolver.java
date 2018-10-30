/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.resolver.AbstractAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class InetSocketAddressResolver
extends AbstractAddressResolver<InetSocketAddress> {
    final NameResolver<InetAddress> nameResolver;

    public InetSocketAddressResolver(EventExecutor executor, NameResolver<InetAddress> nameResolver) {
        super(executor, InetSocketAddress.class);
        this.nameResolver = nameResolver;
    }

    @Override
    protected boolean doIsResolved(InetSocketAddress address) {
        return !address.isUnresolved();
    }

    @Override
    protected void doResolve(final InetSocketAddress unresolvedAddress, final Promise<InetSocketAddress> promise) throws Exception {
        this.nameResolver.resolve(unresolvedAddress.getHostName()).addListener((GenericFutureListener<Future<InetAddress>>)new FutureListener<InetAddress>(){

            @Override
            public void operationComplete(Future<InetAddress> future) throws Exception {
                if (future.isSuccess()) {
                    promise.setSuccess(new InetSocketAddress(future.getNow(), unresolvedAddress.getPort()));
                } else {
                    promise.setFailure(future.cause());
                }
            }
        });
    }

    @Override
    protected void doResolveAll(final InetSocketAddress unresolvedAddress, final Promise<List<InetSocketAddress>> promise) throws Exception {
        this.nameResolver.resolveAll(unresolvedAddress.getHostName()).addListener((GenericFutureListener<Future<List<InetAddress>>>)new FutureListener<List<InetAddress>>(){

            @Override
            public void operationComplete(Future<List<InetAddress>> future) throws Exception {
                if (future.isSuccess()) {
                    List<InetAddress> inetAddresses = future.getNow();
                    ArrayList<InetSocketAddress> socketAddresses = new ArrayList<InetSocketAddress>(inetAddresses.size());
                    for (InetAddress inetAddress : inetAddresses) {
                        socketAddresses.add(new InetSocketAddress(inetAddress, unresolvedAddress.getPort()));
                    }
                    promise.setSuccess(socketAddresses);
                } else {
                    promise.setFailure(future.cause());
                }
            }
        });
    }

    @Override
    public void close() {
        this.nameResolver.close();
    }

}

