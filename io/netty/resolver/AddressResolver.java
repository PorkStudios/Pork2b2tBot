/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.List;

public interface AddressResolver<T extends SocketAddress>
extends Closeable {
    public boolean isSupported(SocketAddress var1);

    public boolean isResolved(SocketAddress var1);

    public Future<T> resolve(SocketAddress var1);

    public Future<T> resolve(SocketAddress var1, Promise<T> var2);

    public Future<List<T>> resolveAll(SocketAddress var1);

    public Future<List<T>> resolveAll(SocketAddress var1, Promise<List<T>> var2);

    @Override
    public void close();
}

