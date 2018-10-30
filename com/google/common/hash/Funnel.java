/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.hash.PrimitiveSink;
import java.io.Serializable;

@Beta
public interface Funnel<T>
extends Serializable {
    public void funnel(T var1, PrimitiveSink var2);
}

