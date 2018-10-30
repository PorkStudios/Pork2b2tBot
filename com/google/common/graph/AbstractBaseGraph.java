/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.BaseGraph;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.EndpointPairIterator;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

abstract class AbstractBaseGraph<N>
implements BaseGraph<N> {
    AbstractBaseGraph() {
    }

    protected long edgeCount() {
        long degreeSum = 0L;
        for (Object node : this.nodes()) {
            degreeSum += (long)this.degree(node);
        }
        Preconditions.checkState((degreeSum & 1L) == 0L);
        return degreeSum >>> 1;
    }

    @Override
    public Set<EndpointPair<N>> edges() {
        return new AbstractSet<EndpointPair<N>>(){

            @Override
            public UnmodifiableIterator<EndpointPair<N>> iterator() {
                return EndpointPairIterator.of(AbstractBaseGraph.this);
            }

            @Override
            public int size() {
                return Ints.saturatedCast(AbstractBaseGraph.this.edgeCount());
            }

            @Override
            public boolean contains(@Nullable Object obj) {
                if (!(obj instanceof EndpointPair)) {
                    return false;
                }
                EndpointPair endpointPair = (EndpointPair)obj;
                return AbstractBaseGraph.this.isDirected() == endpointPair.isOrdered() && AbstractBaseGraph.this.nodes().contains(endpointPair.nodeU()) && AbstractBaseGraph.this.successors(endpointPair.nodeU()).contains(endpointPair.nodeV());
            }
        };
    }

    @Override
    public int degree(N node) {
        if (this.isDirected()) {
            return IntMath.saturatedAdd(this.predecessors((Object)node).size(), this.successors((Object)node).size());
        }
        Set<N> neighbors = this.adjacentNodes(node);
        int selfLoopCount = this.allowsSelfLoops() && neighbors.contains(node) ? 1 : 0;
        return IntMath.saturatedAdd(neighbors.size(), selfLoopCount);
    }

    @Override
    public int inDegree(N node) {
        return this.isDirected() ? this.predecessors((Object)node).size() : this.degree(node);
    }

    @Override
    public int outDegree(N node) {
        return this.isDirected() ? this.successors((Object)node).size() : this.degree(node);
    }

    @Override
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
        Preconditions.checkNotNull(nodeU);
        Preconditions.checkNotNull(nodeV);
        return this.nodes().contains(nodeU) && this.successors((Object)nodeU).contains(nodeV);
    }

}

