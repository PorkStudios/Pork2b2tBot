/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible
public abstract class ForwardingConcurrentMap<K, V>
extends ForwardingMap<K, V>
implements ConcurrentMap<K, V> {
    protected ForwardingConcurrentMap() {
    }

    @Override
    protected abstract ConcurrentMap<K, V> delegate();

    @CanIgnoreReturnValue
    @Override
    public V putIfAbsent(K key, V value) {
        return this.delegate().putIfAbsent(key, value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean remove(Object key, Object value) {
        return this.delegate().remove(key, value);
    }

    @CanIgnoreReturnValue
    @Override
    public V replace(K key, V value) {
        return this.delegate().replace(key, value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return this.delegate().replace(key, oldValue, newValue);
    }
}

