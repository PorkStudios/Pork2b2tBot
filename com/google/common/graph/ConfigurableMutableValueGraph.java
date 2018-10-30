/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.ConfigurableValueGraph;
import com.google.common.graph.DirectedGraphConnections;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.Graphs;
import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.UndirectedGraphConnections;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;

final class ConfigurableMutableValueGraph<N, V>
extends ConfigurableValueGraph<N, V>
implements MutableValueGraph<N, V> {
    ConfigurableMutableValueGraph(AbstractGraphBuilder<? super N> builder) {
        super(builder);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean addNode(N node) {
        Preconditions.checkNotNull(node, "node");
        if (this.containsNode(node)) {
            return false;
        }
        this.addNodeInternal(node);
        return true;
    }

    @CanIgnoreReturnValue
    private GraphConnections<N, V> addNodeInternal(N node) {
        GraphConnections<N, V> connections = this.newConnections();
        Preconditions.checkState(this.nodeConnections.put(node, connections) == null);
        return connections;
    }

    @CanIgnoreReturnValue
    @Override
    public V putEdgeValue(N nodeU, N nodeV, V value) {
        GraphConnections<N, V> connectionsU;
        Preconditions.checkNotNull(nodeU, "nodeU");
        Preconditions.checkNotNull(nodeV, "nodeV");
        Preconditions.checkNotNull(value, "value");
        if (!this.allowsSelfLoops()) {
            Preconditions.checkArgument(!nodeU.equals(nodeV), "Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.", nodeU);
        }
        if ((connectionsU = (GraphConnections<N, V>)this.nodeConnections.get(nodeU)) == null) {
            connectionsU = this.addNodeInternal(nodeU);
        }
        V previousValue = connectionsU.addSuccessor(nodeV, value);
        GraphConnections<N, V> connectionsV = (GraphConnections<N, V>)this.nodeConnections.get(nodeV);
        if (connectionsV == null) {
            connectionsV = this.addNodeInternal(nodeV);
        }
        connectionsV.addPredecessor(nodeU, value);
        if (previousValue == null) {
            Graphs.checkPositive(++this.edgeCount);
        }
        return previousValue;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeNode(N node) {
        Preconditions.checkNotNull(node, "node");
        GraphConnections connections = (GraphConnections)this.nodeConnections.get(node);
        if (connections == null) {
            return false;
        }
        if (this.allowsSelfLoops() && connections.removeSuccessor(node) != null) {
            connections.removePredecessor(node);
            --this.edgeCount;
        }
        for (Object successor : connections.successors()) {
            ((GraphConnections)this.nodeConnections.getWithoutCaching(successor)).removePredecessor(node);
            --this.edgeCount;
        }
        if (this.isDirected()) {
            for (Object predecessor : connections.predecessors()) {
                Preconditions.checkState(((GraphConnections)this.nodeConnections.getWithoutCaching(predecessor)).removeSuccessor(node) != null);
                --this.edgeCount;
            }
        }
        this.nodeConnections.remove(node);
        Graphs.checkNonNegative(this.edgeCount);
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public V removeEdge(N nodeU, N nodeV) {
        Preconditions.checkNotNull(nodeU, "nodeU");
        Preconditions.checkNotNull(nodeV, "nodeV");
        GraphConnections connectionsU = (GraphConnections)this.nodeConnections.get(nodeU);
        GraphConnections connectionsV = (GraphConnections)this.nodeConnections.get(nodeV);
        if (connectionsU == null || connectionsV == null) {
            return null;
        }
        Object previousValue = connectionsU.removeSuccessor(nodeV);
        if (previousValue != null) {
            connectionsV.removePredecessor(nodeU);
            Graphs.checkNonNegative(--this.edgeCount);
        }
        return previousValue;
    }

    private GraphConnections<N, V> newConnections() {
        return this.isDirected() ? DirectedGraphConnections.of() : UndirectedGraphConnections.of();
    }
}

