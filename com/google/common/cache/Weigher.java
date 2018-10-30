/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface Weigher<K, V> {
    public int weigh(K var1, V var2);
}

