/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.PredecessorsFunction;
import com.google.common.graph.SuccessorsFunction;
import java.util.Set;

interface BaseGraph<N>
extends SuccessorsFunction<N>,
PredecessorsFunction<N> {
    public Set<N> nodes();

    public Set<EndpointPair<N>> edges();

    public boolean isDirected();

    public boolean allowsSelfLoops();

    public ElementOrder<N> nodeOrder();

    public Set<N> adjacentNodes(N var1);

    @Override
    public Set<N> predecessors(N var1);

    @Override
    public Set<N> successors(N var1);

    public int degree(N var1);

    public int inDegree(N var1);

    public int outDegree(N var1);

    public boolean hasEdgeConnecting(N var1, N var2);
}

