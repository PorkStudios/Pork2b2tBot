/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;

@Beta
public interface SuccessorsFunction<N> {
    public Iterable<? extends N> successors(N var1);
}

