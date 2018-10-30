/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public interface AsyncMapping<IN, OUT> {
    public Future<OUT> map(IN var1, Promise<OUT> var2);
}

