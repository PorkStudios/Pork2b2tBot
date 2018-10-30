/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.cache.RemovalNotification;

@FunctionalInterface
@GwtCompatible
public interface RemovalListener<K, V> {
    public void onRemoval(RemovalNotification<K, V> var1);
}

