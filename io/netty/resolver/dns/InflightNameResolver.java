/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

final class InflightNameResolver<T>
implements NameResolver<T> {
    private final EventExecutor executor;
    private final NameResolver<T> delegate;
    private final ConcurrentMap<String, Promise<T>> resolvesInProgress;
    private final ConcurrentMap<String, Promise<List<T>>> resolveAllsInProgress;

    InflightNameResolver(EventExecutor executor, NameResolver<T> delegate, ConcurrentMap<String, Promise<T>> resolvesInProgress, ConcurrentMap<String, Promise<List<T>>> resolveAllsInProgress) {
        this.executor = ObjectUtil.checkNotNull(executor, "executor");
        this.delegate = ObjectUtil.checkNotNull(delegate, "delegate");
        this.resolvesInProgress = ObjectUtil.checkNotNull(resolvesInProgress, "resolvesInProgress");
        this.resolveAllsInProgress = ObjectUtil.checkNotNull(resolveAllsInProgress, "resolveAllsInProgress");
    }

    @Override
    public Future<T> resolve(String inetHost) {
        return this.resolve(inetHost, this.executor.newPromise());
    }

    @Override
    public Future<List<T>> resolveAll(String inetHost) {
        return this.resolveAll(inetHost, this.executor.newPromise());
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public Promise<T> resolve(String inetHost, Promise<T> promise) {
        return this.resolve(this.resolvesInProgress, inetHost, promise, false);
    }

    @Override
    public Promise<List<T>> resolveAll(String inetHost, Promise<List<T>> promise) {
        return this.resolve(this.resolveAllsInProgress, inetHost, promise, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private <U> Promise<U> resolve(ConcurrentMap<String, Promise<U>> resolveMap, String inetHost, final Promise<U> promise, boolean resolveAll) {
        block10 : {
            Promise earlyPromise = resolveMap.putIfAbsent(inetHost, promise);
            if (earlyPromise != null) {
                if (earlyPromise.isDone()) {
                    InflightNameResolver.transferResult(earlyPromise, promise);
                    return promise;
                } else {
                    earlyPromise.addListener(new FutureListener<U>(){

                        @Override
                        public void operationComplete(Future<U> f) throws Exception {
                            InflightNameResolver.transferResult(f, promise);
                        }
                    });
                }
                return promise;
            }
            try {
                if (resolveAll) {
                    Promise castPromise = promise;
                    this.delegate.resolveAll(inetHost, castPromise);
                    break block10;
                }
                Promise castPromise = promise;
                this.delegate.resolve(inetHost, castPromise);
            }
            catch (Throwable throwable) {
                if (promise.isDone()) {
                    resolveMap.remove(inetHost);
                    throw throwable;
                } else {
                    promise.addListener(new FutureListener<U>(resolveMap, inetHost){
                        final /* synthetic */ ConcurrentMap val$resolveMap;
                        final /* synthetic */ String val$inetHost;
                        {
                            this.val$resolveMap = concurrentMap;
                            this.val$inetHost = string;
                        }

                        @Override
                        public void operationComplete(Future<U> f) throws Exception {
                            this.val$resolveMap.remove(this.val$inetHost);
                        }
                    });
                }
                throw throwable;
            }
        }
        if (promise.isDone()) {
            resolveMap.remove(inetHost);
            return promise;
        } else {
            promise.addListener(new /* invalid duplicate definition of identical inner class */);
        }
        return promise;
    }

    private static <T> void transferResult(Future<T> src, Promise<T> dst) {
        if (src.isSuccess()) {
            dst.trySuccess(src.getNow());
        } else {
            dst.tryFailure(src.cause());
        }
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + this.delegate + ')';
    }

}

