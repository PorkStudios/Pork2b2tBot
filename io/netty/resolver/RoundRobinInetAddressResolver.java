/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.resolver.InetNameResolver;
import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RoundRobinInetAddressResolver
extends InetNameResolver {
    private final NameResolver<InetAddress> nameResolver;

    public RoundRobinInetAddressResolver(EventExecutor executor, NameResolver<InetAddress> nameResolver) {
        super(executor);
        this.nameResolver = nameResolver;
    }

    @Override
    protected void doResolve(final String inetHost, final Promise<InetAddress> promise) throws Exception {
        this.nameResolver.resolveAll(inetHost).addListener((GenericFutureListener<Future<List<InetAddress>>>)new FutureListener<List<InetAddress>>(){

            @Override
            public void operationComplete(Future<List<InetAddress>> future) throws Exception {
                if (future.isSuccess()) {
                    List<InetAddress> inetAddresses = future.getNow();
                    int numAddresses = inetAddresses.size();
                    if (numAddresses > 0) {
                        promise.setSuccess(inetAddresses.get(RoundRobinInetAddressResolver.randomIndex(numAddresses)));
                    } else {
                        promise.setFailure(new UnknownHostException(inetHost));
                    }
                } else {
                    promise.setFailure(future.cause());
                }
            }
        });
    }

    @Override
    protected void doResolveAll(String inetHost, final Promise<List<InetAddress>> promise) throws Exception {
        this.nameResolver.resolveAll(inetHost).addListener((GenericFutureListener<Future<List<InetAddress>>>)new FutureListener<List<InetAddress>>(){

            @Override
            public void operationComplete(Future<List<InetAddress>> future) throws Exception {
                if (future.isSuccess()) {
                    List<InetAddress> inetAddresses = future.getNow();
                    if (!inetAddresses.isEmpty()) {
                        ArrayList<InetAddress> result = new ArrayList<InetAddress>(inetAddresses);
                        Collections.rotate(result, RoundRobinInetAddressResolver.randomIndex(inetAddresses.size()));
                        promise.setSuccess(result);
                    } else {
                        promise.setSuccess(inetAddresses);
                    }
                } else {
                    promise.setFailure(future.cause());
                }
            }
        });
    }

    private static int randomIndex(int numAddresses) {
        return numAddresses == 1 ? 0 : PlatformDependent.threadLocalRandom().nextInt(numAddresses);
    }

}

