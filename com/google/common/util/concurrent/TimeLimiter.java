/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public interface TimeLimiter {
    public <T> T newProxy(T var1, Class<T> var2, long var3, TimeUnit var5);

    public <T> T callWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException, ExecutionException;

    public <T> T callUninterruptiblyWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, ExecutionException;

    public void runWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException;

    public void runUninterruptiblyWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException;
}

