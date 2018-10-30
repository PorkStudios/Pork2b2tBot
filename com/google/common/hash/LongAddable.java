/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.hash;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface LongAddable {
    public void increment();

    public void add(long var1);

    public long sum();
}

