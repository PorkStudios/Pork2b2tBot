/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.AbstractValueGraph;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.Graphs;
import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MapRetrievalCache;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

class ConfigurableValueGraph<N, V>
extends AbstractValueGraph<N, V> {
    private final boolean isDirected;
    private final boolean allowsSelfLoops;
    private final ElementOrder<N> nodeOrder;
    protected final MapIteratorCache<N, GraphConnections<N, V>> nodeConnections;
    protected long edgeCount;

    ConfigurableValueGraph(AbstractGraphBuilder<? super N> builder) {
        this(builder, builder.nodeOrder.createMap(builder.expectedNodeCount.or(10)), 0L);
    }

    ConfigurableValueGraph(AbstractGraphBuilder<? super N> builder, Map<N, GraphConnections<N, V>> nodeConnections, long edgeCount) {
        this.isDirected = builder.directed;
        this.allowsSelfLoops = builder.allowsSelfLoops;
        this.nodeOrder = builder.nodeOrder.cast();
        this.nodeConnections = nodeConnections instanceof TreeMap ? new MapRetrievalCache<N, GraphConnections<N, V>>(nodeConnections) : new MapIteratorCache<N, GraphConnections<N, V>>(nodeConnections);
        this.edgeCount = Graphs.checkNonNegative(edgeCount);
    }

    @Override
    public Set<N> nodes() {
        return this.nodeConnections.unmodifiableKeySet();
    }

    @Override
    public boolean isDirected() {
        return this.isDirected;
    }

    @Override
    public boolean allowsSelfLoops() {
        return this.allowsSelfLoops;
    }

    @Override
    public ElementOrder<N> nodeOrder() {
        return this.nodeOrder;
    }

    @Override
    public Set<N> adjacentNodes(N node) {
        return this.checkedConnections(node).adjacentNodes();
    }

    @Override
    public Set<N> predecessors(N node) {
        return this.checkedConnections(node).predecessors();
    }

    @Override
    public Set<N> successors(N node) {
        return this.checkedConnections(node).successors();
    }

    @Override
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
        Preconditions.checkNotNull(nodeU);
        Preconditions.checkNotNull(nodeV);
        GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
        return connectionsU != null && connectionsU.successors().contains(nodeV);
    }

    @Nullable
    @Override
    public V edgeValueOrDefault(N nodeU, N nodeV, @Nullable V defaultValue) {
        Preconditions.checkNotNull(nodeU);
        Preconditions.checkNotNull(nodeV);
        GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
        return connectionsU == null ? defaultValue : connectionsU.value(nodeV);
    }

    @Override
    protected long edgeCount() {
        return this.edgeCount;
    }

    protected final GraphConnections<N, V> checkedConnections(N node) {
        GraphConnections<N, V> connections = this.nodeConnections.get(node);
        if (connections == null) {
            Preconditions.checkNotNull(node);
            throw new IllegalArgumentException("Node " + node + " is not an element of this graph.");
        }
        return connections;
    }

    protected final boolean containsNode(@Nullable N node) {
        return this.nodeConnections.containsKey(node);
    }
}

