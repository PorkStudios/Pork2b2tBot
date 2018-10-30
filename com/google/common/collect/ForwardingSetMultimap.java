/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSetMultimap<K, V>
extends ForwardingMultimap<K, V>
implements SetMultimap<K, V> {
    @Override
    protected abstract SetMultimap<K, V> delegate();

    @Override
    public Set<Map.Entry<K, V>> entries() {
        return this.delegate().entries();
    }

    @Override
    public Set<V> get(@Nullable K key) {
        return this.delegate().get((Object)key);
    }

    @CanIgnoreReturnValue
    @Override
    public Set<V> removeAll(@Nullable Object key) {
        return this.delegate().removeAll(key);
    }

    @CanIgnoreReturnValue
    @Override
    public Set<V> replaceValues(K key, Iterable<? extends V> values) {
        return this.delegate().replaceValues((Object)key, (Iterable)values);
    }
}

