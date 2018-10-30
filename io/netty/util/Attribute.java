/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.AttributeKey;

public interface Attribute<T> {
    public AttributeKey<T> key();

    public T get();

    public void set(T var1);

    public T getAndSet(T var1);

    public T setIfAbsent(T var1);

    @Deprecated
    public T getAndRemove();

    public boolean compareAndSet(T var1, T var2);

    @Deprecated
    public void remove();
}

