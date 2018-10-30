/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.BaseGraph;
import com.google.common.graph.ConfigurableMutableValueGraph;
import com.google.common.graph.ForwardingGraph;
import com.google.common.graph.GraphConstants;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;

final class ConfigurableMutableGraph<N>
extends ForwardingGraph<N>
implements MutableGraph<N> {
    private final MutableValueGraph<N, GraphConstants.Presence> backingValueGraph;

    ConfigurableMutableGraph(AbstractGraphBuilder<? super N> builder) {
        this.backingValueGraph = new ConfigurableMutableValueGraph<N, GraphConstants.Presence>(builder);
    }

    @Override
    protected BaseGraph<N> delegate() {
        return this.backingValueGraph;
    }

    @Override
    public boolean addNode(N node) {
        return this.backingValueGraph.addNode(node);
    }

    @Override
    public boolean putEdge(N nodeU, N nodeV) {
        return this.backingValueGraph.putEdgeValue(nodeU, nodeV, GraphConstants.Presence.EDGE_EXISTS) == null;
    }

    @Override
    public boolean removeNode(N node) {
        return this.backingValueGraph.removeNode(node);
    }

    @Override
    public boolean removeEdge(N nodeU, N nodeV) {
        return this.backingValueGraph.removeEdge(nodeU, nodeV) != null;
    }
}

