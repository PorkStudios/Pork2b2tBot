/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.resolver.AddressResolver;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.util.concurrent.EventExecutor;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class InetNameResolver
extends SimpleNameResolver<InetAddress> {
    private volatile AddressResolver<InetSocketAddress> addressResolver;

    protected InetNameResolver(EventExecutor executor) {
        super(executor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AddressResolver<InetSocketAddress> asAddressResolver() {
        InetSocketAddressResolver result = this.addressResolver;
        if (result == null) {
            InetNameResolver inetNameResolver = this;
            synchronized (inetNameResolver) {
                result = this.addressResolver;
                if (result == null) {
                    this.addressResolver = result = new InetSocketAddressResolver(this.executor(), this);
                }
            }
        }
        return result;
    }
}

