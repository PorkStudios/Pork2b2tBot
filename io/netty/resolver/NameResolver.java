/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.io.Closeable;
import java.util.List;

public interface NameResolver<T>
extends Closeable {
    public Future<T> resolve(String var1);

    public Future<T> resolve(String var1, Promise<T> var2);

    public Future<List<T>> resolveAll(String var1);

    public Future<List<T>> resolveAll(String var1, Promise<List<T>> var2);

    @Override
    public void close();
}

