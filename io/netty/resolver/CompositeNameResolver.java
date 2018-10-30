/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.List;

public final class CompositeNameResolver<T>
extends SimpleNameResolver<T> {
    private final NameResolver<T>[] resolvers;

    public /* varargs */ CompositeNameResolver(EventExecutor executor, NameResolver<T> ... resolvers) {
        super(executor);
        ObjectUtil.checkNotNull(resolvers, "resolvers");
        for (int i = 0; i < resolvers.length; ++i) {
            if (resolvers[i] != null) continue;
            throw new NullPointerException("resolvers[" + i + ']');
        }
        if (resolvers.length < 2) {
            throw new IllegalArgumentException("resolvers: " + Arrays.asList(resolvers) + " (expected: at least 2 resolvers)");
        }
        this.resolvers = (NameResolver[])resolvers.clone();
    }

    @Override
    protected void doResolve(String inetHost, Promise<T> promise) throws Exception {
        this.doResolveRec(inetHost, promise, 0, null);
    }

    private void doResolveRec(final String inetHost, final Promise<T> promise, final int resolverIndex, Throwable lastFailure) throws Exception {
        if (resolverIndex >= this.resolvers.length) {
            promise.setFailure(lastFailure);
        } else {
            NameResolver<T> resolver = this.resolvers[resolverIndex];
            resolver.resolve(inetHost).addListener(new FutureListener<T>(){

                @Override
                public void operationComplete(Future<T> future) throws Exception {
                    if (future.isSuccess()) {
                        promise.setSuccess(future.getNow());
                    } else {
                        CompositeNameResolver.this.doResolveRec(inetHost, promise, resolverIndex + 1, future.cause());
                    }
                }
            });
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<T>> promise) throws Exception {
        this.doResolveAllRec(inetHost, promise, 0, null);
    }

    private void doResolveAllRec(final String inetHost, final Promise<List<T>> promise, final int resolverIndex, Throwable lastFailure) throws Exception {
        if (resolverIndex >= this.resolvers.length) {
            promise.setFailure(lastFailure);
        } else {
            NameResolver<T> resolver = this.resolvers[resolverIndex];
            resolver.resolveAll(inetHost).addListener(new FutureListener<List<T>>(){

                @Override
                public void operationComplete(Future<List<T>> future) throws Exception {
                    if (future.isSuccess()) {
                        promise.setSuccess(future.getNow());
                    } else {
                        CompositeNameResolver.this.doResolveAllRec(inetHost, promise, resolverIndex + 1, future.cause());
                    }
                }
            });
        }
    }

}

