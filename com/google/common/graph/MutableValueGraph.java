/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.ValueGraph;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
public interface MutableValueGraph<N, V>
extends ValueGraph<N, V> {
    @CanIgnoreReturnValue
    public boolean addNode(N var1);

    @CanIgnoreReturnValue
    public V putEdgeValue(N var1, N var2, V var3);

    @CanIgnoreReturnValue
    public boolean removeNode(N var1);

    @CanIgnoreReturnValue
    public V removeEdge(N var1, N var2);
}

