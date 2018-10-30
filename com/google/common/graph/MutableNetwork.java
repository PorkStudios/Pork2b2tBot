/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.Network;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
public interface MutableNetwork<N, E>
extends Network<N, E> {
    @CanIgnoreReturnValue
    public boolean addNode(N var1);

    @CanIgnoreReturnValue
    public boolean addEdge(N var1, N var2, E var3);

    @CanIgnoreReturnValue
    public boolean removeNode(N var1);

    @CanIgnoreReturnValue
    public boolean removeEdge(E var1);
}

