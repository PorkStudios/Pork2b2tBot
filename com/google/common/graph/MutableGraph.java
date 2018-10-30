/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.Graph;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
public interface MutableGraph<N>
extends Graph<N> {
    @CanIgnoreReturnValue
    public boolean addNode(N var1);

    @CanIgnoreReturnValue
    public boolean putEdge(N var1, N var2);

    @CanIgnoreReturnValue
    public boolean removeNode(N var1);

    @CanIgnoreReturnValue
    public boolean removeEdge(N var1, N var2);
}

