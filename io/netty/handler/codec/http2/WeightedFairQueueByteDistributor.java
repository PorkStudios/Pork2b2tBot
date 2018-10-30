/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionAdapter;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.StreamByteDistributor;
import io.netty.util.collection.IntCollections;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.EmptyPriorityQueue;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import io.netty.util.internal.SystemPropertyUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class WeightedFairQueueByteDistributor
implements StreamByteDistributor {
    static final int INITIAL_CHILDREN_MAP_SIZE = Math.max(1, SystemPropertyUtil.getInt("io.netty.http2.childrenMapSize", 2));
    private static final int DEFAULT_MAX_STATE_ONLY_SIZE = 5;
    private final Http2Connection.PropertyKey stateKey;
    private final IntObjectMap<State> stateOnlyMap;
    private final PriorityQueue<State> stateOnlyRemovalQueue;
    private final Http2Connection connection;
    private final State connectionState;
    private int allocationQuantum = 1024;
    private final int maxStateOnlySize;

    public WeightedFairQueueByteDistributor(Http2Connection connection) {
        this(connection, 5);
    }

    public WeightedFairQueueByteDistributor(Http2Connection connection, int maxStateOnlySize) {
        if (maxStateOnlySize < 0) {
            throw new IllegalArgumentException("maxStateOnlySize: " + maxStateOnlySize + " (expected: >0)");
        }
        if (maxStateOnlySize == 0) {
            this.stateOnlyMap = IntCollections.emptyMap();
            this.stateOnlyRemovalQueue = EmptyPriorityQueue.instance();
        } else {
            this.stateOnlyMap = new IntObjectHashMap<State>(maxStateOnlySize);
            this.stateOnlyRemovalQueue = new DefaultPriorityQueue<State>(StateOnlyComparator.INSTANCE, maxStateOnlySize + 2);
        }
        this.maxStateOnlySize = maxStateOnlySize;
        this.connection = connection;
        this.stateKey = connection.newKey();
        Http2Stream connectionStream = connection.connectionStream();
        this.connectionState = new State(connectionStream, 16);
        connectionStream.setProperty(this.stateKey, this.connectionState);
        connection.addListener(new Http2ConnectionAdapter(){

            @Override
            public void onStreamAdded(Http2Stream stream) {
                State state = (State)WeightedFairQueueByteDistributor.this.stateOnlyMap.remove(stream.id());
                if (state == null) {
                    state = new State(stream);
                    ArrayList<ParentChangedEvent> events = new ArrayList<ParentChangedEvent>(1);
                    WeightedFairQueueByteDistributor.this.connectionState.takeChild(state, false, events);
                    WeightedFairQueueByteDistributor.this.notifyParentChanged(events);
                } else {
                    WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.removeTyped(state);
                    state.stream = stream;
                }
                switch (stream.state()) {
                    case RESERVED_REMOTE: 
                    case RESERVED_LOCAL: {
                        state.setStreamReservedOrActivated();
                        break;
                    }
                }
                stream.setProperty(WeightedFairQueueByteDistributor.this.stateKey, state);
            }

            @Override
            public void onStreamActive(Http2Stream stream) {
                WeightedFairQueueByteDistributor.this.state(stream).setStreamReservedOrActivated();
            }

            @Override
            public void onStreamClosed(Http2Stream stream) {
                WeightedFairQueueByteDistributor.this.state(stream).close();
            }

            @Override
            public void onStreamRemoved(Http2Stream stream) {
                State state = WeightedFairQueueByteDistributor.this.state(stream);
                state.stream = null;
                if (WeightedFairQueueByteDistributor.this.maxStateOnlySize == 0) {
                    state.parent.removeChild(state);
                    return;
                }
                if (WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.size() == WeightedFairQueueByteDistributor.this.maxStateOnlySize) {
                    State stateToRemove = (State)WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.peek();
                    if (StateOnlyComparator.INSTANCE.compare(stateToRemove, state) >= 0) {
                        state.parent.removeChild(state);
                        return;
                    }
                    WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.poll();
                    stateToRemove.parent.removeChild(stateToRemove);
                    WeightedFairQueueByteDistributor.this.stateOnlyMap.remove(stateToRemove.streamId);
                }
                WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.add(state);
                WeightedFairQueueByteDistributor.this.stateOnlyMap.put(state.streamId, state);
            }
        });
    }

    @Override
    public void updateStreamableBytes(StreamByteDistributor.StreamState state) {
        this.state(state.stream()).updateStreamableBytes(Http2CodecUtil.streamableBytes(state), state.hasFrame() && state.windowSize() >= 0);
    }

    @Override
    public void updateDependencyTree(int childStreamId, int parentStreamId, short weight, boolean exclusive) {
        ArrayList<ParentChangedEvent> events;
        State newParent;
        State state = this.state(childStreamId);
        if (state == null) {
            if (this.maxStateOnlySize == 0) {
                return;
            }
            state = new State(childStreamId);
            this.stateOnlyRemovalQueue.add(state);
            this.stateOnlyMap.put(childStreamId, state);
        }
        if ((newParent = this.state(parentStreamId)) == null) {
            if (this.maxStateOnlySize == 0) {
                return;
            }
            newParent = new State(parentStreamId);
            this.stateOnlyRemovalQueue.add(newParent);
            this.stateOnlyMap.put(parentStreamId, newParent);
            events = new ArrayList(1);
            this.connectionState.takeChild(newParent, false, events);
            this.notifyParentChanged(events);
        }
        if (state.activeCountForTree != 0 && state.parent != null) {
            state.parent.totalQueuedWeights += (long)(weight - state.weight);
        }
        state.weight = weight;
        if (newParent != state.parent || exclusive && newParent.children.size() != 1) {
            if (newParent.isDescendantOf(state)) {
                events = new ArrayList<ParentChangedEvent>(2 + (exclusive ? newParent.children.size() : 0));
                state.parent.takeChild(newParent, false, events);
            } else {
                events = new ArrayList(1 + (exclusive ? newParent.children.size() : 0));
            }
            newParent.takeChild(state, exclusive, events);
            this.notifyParentChanged(events);
        }
        while (this.stateOnlyRemovalQueue.size() > this.maxStateOnlySize) {
            State stateToRemove = this.stateOnlyRemovalQueue.poll();
            stateToRemove.parent.removeChild(stateToRemove);
            this.stateOnlyMap.remove(stateToRemove.streamId);
        }
    }

    @Override
    public boolean distribute(int maxBytes, StreamByteDistributor.Writer writer) throws Http2Exception {
        int oldIsActiveCountForTree;
        if (this.connectionState.activeCountForTree == 0) {
            return false;
        }
        do {
            oldIsActiveCountForTree = this.connectionState.activeCountForTree;
            maxBytes -= this.distributeToChildren(maxBytes, writer, this.connectionState);
        } while (this.connectionState.activeCountForTree != 0 && (maxBytes > 0 || oldIsActiveCountForTree != this.connectionState.activeCountForTree));
        return this.connectionState.activeCountForTree != 0;
    }

    public void allocationQuantum(int allocationQuantum) {
        if (allocationQuantum <= 0) {
            throw new IllegalArgumentException("allocationQuantum must be > 0");
        }
        this.allocationQuantum = allocationQuantum;
    }

    private int distribute(int maxBytes, StreamByteDistributor.Writer writer, State state) throws Http2Exception {
        if (state.isActive()) {
            int nsent = Math.min(maxBytes, state.streamableBytes);
            state.write(nsent, writer);
            if (nsent == 0 && maxBytes != 0) {
                state.updateStreamableBytes(state.streamableBytes, false);
            }
            return nsent;
        }
        return this.distributeToChildren(maxBytes, writer, state);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int distributeToChildren(int maxBytes, StreamByteDistributor.Writer writer, State state) throws Http2Exception {
        long oldTotalQueuedWeights = state.totalQueuedWeights;
        State childState = state.pollPseudoTimeQueue();
        State nextChildState = state.peekPseudoTimeQueue();
        childState.setDistributing();
        try {
            assert (nextChildState == null || nextChildState.pseudoTimeToWrite >= childState.pseudoTimeToWrite);
            int nsent = this.distribute(nextChildState == null ? maxBytes : Math.min(maxBytes, (int)Math.min((nextChildState.pseudoTimeToWrite - childState.pseudoTimeToWrite) * (long)childState.weight / oldTotalQueuedWeights + (long)this.allocationQuantum, Integer.MAX_VALUE)), writer, childState);
            state.pseudoTime += (long)nsent;
            childState.updatePseudoTime(state, nsent, oldTotalQueuedWeights);
            int n = nsent;
            return n;
        }
        finally {
            childState.unsetDistributing();
            if (childState.activeCountForTree != 0) {
                state.offerPseudoTimeQueue(childState);
            }
        }
    }

    private State state(Http2Stream stream) {
        return (State)stream.getProperty(this.stateKey);
    }

    private State state(int streamId) {
        Http2Stream stream = this.connection.stream(streamId);
        return stream != null ? this.state(stream) : this.stateOnlyMap.get(streamId);
    }

    int streamableBytes0(Http2Stream stream) {
        return this.state((Http2Stream)stream).streamableBytes;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    boolean isChild(int childId, int parentId, short weight) {
        State parent = this.state(parentId);
        if (!parent.children.containsKey(childId)) return false;
        State child = this.state(childId);
        if (child.parent != parent) return false;
        if (child.weight != weight) return false;
        return true;
    }

    int numChildren(int streamId) {
        State state = this.state(streamId);
        return state == null ? 0 : state.children.size();
    }

    void notifyParentChanged(List<ParentChangedEvent> events) {
        for (int i = 0; i < events.size(); ++i) {
            ParentChangedEvent event = events.get(i);
            this.stateOnlyRemovalQueue.priorityChanged(event.state);
            if (event.state.parent == null || event.state.activeCountForTree == 0) continue;
            event.state.parent.offerAndInitializePseudoTime(event.state);
            event.state.parent.activeCountChangeForTree(event.state.activeCountForTree);
        }
    }

    private static final class ParentChangedEvent {
        final State state;
        final State oldParent;

        ParentChangedEvent(State state, State oldParent) {
            this.state = state;
            this.oldParent = oldParent;
        }
    }

    private final class State
    implements PriorityQueueNode {
        private static final byte STATE_IS_ACTIVE = 1;
        private static final byte STATE_IS_DISTRIBUTING = 2;
        private static final byte STATE_STREAM_ACTIVATED = 4;
        Http2Stream stream;
        State parent;
        IntObjectMap<State> children = IntCollections.emptyMap();
        private final PriorityQueue<State> pseudoTimeQueue;
        final int streamId;
        int streamableBytes;
        int dependencyTreeDepth;
        int activeCountForTree;
        private int pseudoTimeQueueIndex = -1;
        private int stateOnlyQueueIndex = -1;
        long pseudoTimeToWrite;
        long pseudoTime;
        long totalQueuedWeights;
        private byte flags;
        short weight = (short)16;

        State(int streamId) {
            this(streamId, null, 0);
        }

        State(Http2Stream stream) {
            this(stream, 0);
        }

        State(Http2Stream stream, int initialSize) {
            this(stream.id(), stream, initialSize);
        }

        State(int streamId, Http2Stream stream, int initialSize) {
            this.stream = stream;
            this.streamId = streamId;
            this.pseudoTimeQueue = new DefaultPriorityQueue<State>(StatePseudoTimeComparator.INSTANCE, initialSize);
        }

        boolean isDescendantOf(State state) {
            State next = this.parent;
            while (next != null) {
                if (next == state) {
                    return true;
                }
                next = next.parent;
            }
            return false;
        }

        void takeChild(State child, boolean exclusive, List<ParentChangedEvent> events) {
            this.takeChild(null, child, exclusive, events);
        }

        void takeChild(Iterator<IntObjectMap.PrimitiveEntry<State>> childItr, State child, boolean exclusive, List<ParentChangedEvent> events) {
            State oldParent = child.parent;
            if (oldParent != this) {
                events.add(new ParentChangedEvent(child, oldParent));
                child.setParent(this);
                if (childItr != null) {
                    childItr.remove();
                } else if (oldParent != null) {
                    oldParent.children.remove(child.streamId);
                }
                this.initChildrenIfEmpty();
                State oldChild = this.children.put(child.streamId, child);
                assert (oldChild == null);
            }
            if (exclusive && !this.children.isEmpty()) {
                Iterator<IntObjectMap.PrimitiveEntry<State>> itr = this.removeAllChildrenExcept(child).entries().iterator();
                while (itr.hasNext()) {
                    child.takeChild(itr, itr.next().value(), false, events);
                }
            }
        }

        void removeChild(State child) {
            if (this.children.remove(child.streamId) != null) {
                ArrayList<ParentChangedEvent> events = new ArrayList<ParentChangedEvent>(1 + child.children.size());
                events.add(new ParentChangedEvent(child, child.parent));
                child.setParent(null);
                Iterator<IntObjectMap.PrimitiveEntry<State>> itr = child.children.entries().iterator();
                while (itr.hasNext()) {
                    this.takeChild(itr, itr.next().value(), false, events);
                }
                WeightedFairQueueByteDistributor.this.notifyParentChanged(events);
            }
        }

        private IntObjectMap<State> removeAllChildrenExcept(State stateToRetain) {
            stateToRetain = this.children.remove(stateToRetain.streamId);
            IntObjectMap<State> prevChildren = this.children;
            this.initChildren();
            if (stateToRetain != null) {
                this.children.put(stateToRetain.streamId, stateToRetain);
            }
            return prevChildren;
        }

        private void setParent(State newParent) {
            if (this.activeCountForTree != 0 && this.parent != null) {
                this.parent.removePseudoTimeQueue(this);
                this.parent.activeCountChangeForTree(- this.activeCountForTree);
            }
            this.parent = newParent;
            this.dependencyTreeDepth = newParent == null ? Integer.MAX_VALUE : newParent.dependencyTreeDepth + 1;
        }

        private void initChildrenIfEmpty() {
            if (this.children == IntCollections.emptyMap()) {
                this.initChildren();
            }
        }

        private void initChildren() {
            this.children = new IntObjectHashMap<State>(WeightedFairQueueByteDistributor.INITIAL_CHILDREN_MAP_SIZE);
        }

        void write(int numBytes, StreamByteDistributor.Writer writer) throws Http2Exception {
            assert (this.stream != null);
            try {
                writer.write(this.stream, numBytes);
            }
            catch (Throwable t) {
                throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, t, "byte distribution write error", new Object[0]);
            }
        }

        void activeCountChangeForTree(int increment) {
            assert (this.activeCountForTree + increment >= 0);
            this.activeCountForTree += increment;
            if (this.parent != null) {
                assert (this.activeCountForTree != increment || this.pseudoTimeQueueIndex == -1 || this.parent.pseudoTimeQueue.containsTyped(this));
                if (this.activeCountForTree == 0) {
                    this.parent.removePseudoTimeQueue(this);
                } else if (this.activeCountForTree == increment && !this.isDistributing()) {
                    this.parent.offerAndInitializePseudoTime(this);
                }
                this.parent.activeCountChangeForTree(increment);
            }
        }

        void updateStreamableBytes(int newStreamableBytes, boolean isActive) {
            if (this.isActive() != isActive) {
                if (isActive) {
                    this.activeCountChangeForTree(1);
                    this.setActive();
                } else {
                    this.activeCountChangeForTree(-1);
                    this.unsetActive();
                }
            }
            this.streamableBytes = newStreamableBytes;
        }

        void updatePseudoTime(State parentState, int nsent, long totalQueuedWeights) {
            assert (this.streamId != 0 && nsent >= 0);
            this.pseudoTimeToWrite = Math.min(this.pseudoTimeToWrite, parentState.pseudoTime) + (long)nsent * totalQueuedWeights / (long)this.weight;
        }

        void offerAndInitializePseudoTime(State state) {
            state.pseudoTimeToWrite = this.pseudoTime;
            this.offerPseudoTimeQueue(state);
        }

        void offerPseudoTimeQueue(State state) {
            this.pseudoTimeQueue.offer(state);
            this.totalQueuedWeights += (long)state.weight;
        }

        State pollPseudoTimeQueue() {
            State state = this.pseudoTimeQueue.poll();
            this.totalQueuedWeights -= (long)state.weight;
            return state;
        }

        void removePseudoTimeQueue(State state) {
            if (this.pseudoTimeQueue.removeTyped(state)) {
                this.totalQueuedWeights -= (long)state.weight;
            }
        }

        State peekPseudoTimeQueue() {
            return this.pseudoTimeQueue.peek();
        }

        void close() {
            this.updateStreamableBytes(0, false);
            this.stream = null;
        }

        boolean wasStreamReservedOrActivated() {
            return (this.flags & 4) != 0;
        }

        void setStreamReservedOrActivated() {
            this.flags = (byte)(this.flags | 4);
        }

        boolean isActive() {
            return (this.flags & 1) != 0;
        }

        private void setActive() {
            this.flags = (byte)(this.flags | 1);
        }

        private void unsetActive() {
            this.flags = (byte)(this.flags & -2);
        }

        boolean isDistributing() {
            return (this.flags & 2) != 0;
        }

        void setDistributing() {
            this.flags = (byte)(this.flags | 2);
        }

        void unsetDistributing() {
            this.flags = (byte)(this.flags & -3);
        }

        @Override
        public int priorityQueueIndex(DefaultPriorityQueue<?> queue) {
            return queue == WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue ? this.stateOnlyQueueIndex : this.pseudoTimeQueueIndex;
        }

        @Override
        public void priorityQueueIndex(DefaultPriorityQueue<?> queue, int i) {
            if (queue == WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue) {
                this.stateOnlyQueueIndex = i;
            } else {
                this.pseudoTimeQueueIndex = i;
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(256 * (this.activeCountForTree > 0 ? this.activeCountForTree : 1));
            this.toString(sb);
            return sb.toString();
        }

        private void toString(StringBuilder sb) {
            sb.append("{streamId ").append(this.streamId).append(" streamableBytes ").append(this.streamableBytes).append(" activeCountForTree ").append(this.activeCountForTree).append(" pseudoTimeQueueIndex ").append(this.pseudoTimeQueueIndex).append(" pseudoTimeToWrite ").append(this.pseudoTimeToWrite).append(" pseudoTime ").append(this.pseudoTime).append(" flags ").append(this.flags).append(" pseudoTimeQueue.size() ").append(this.pseudoTimeQueue.size()).append(" stateOnlyQueueIndex ").append(this.stateOnlyQueueIndex).append(" parent.streamId ").append(this.parent == null ? -1 : this.parent.streamId).append("} [");
            if (!this.pseudoTimeQueue.isEmpty()) {
                for (State s : this.pseudoTimeQueue) {
                    s.toString(sb);
                    sb.append(", ");
                }
                sb.setLength(sb.length() - 2);
            }
            sb.append(']');
        }
    }

    private static final class StatePseudoTimeComparator
    implements Comparator<State>,
    Serializable {
        private static final long serialVersionUID = -1437548640227161828L;
        static final StatePseudoTimeComparator INSTANCE = new StatePseudoTimeComparator();

        private StatePseudoTimeComparator() {
        }

        @Override
        public int compare(State o1, State o2) {
            return MathUtil.compare(o1.pseudoTimeToWrite, o2.pseudoTimeToWrite);
        }
    }

    private static final class StateOnlyComparator
    implements Comparator<State>,
    Serializable {
        private static final long serialVersionUID = -4806936913002105966L;
        static final StateOnlyComparator INSTANCE = new StateOnlyComparator();

        private StateOnlyComparator() {
        }

        @Override
        public int compare(State o1, State o2) {
            boolean o1Actived = o1.wasStreamReservedOrActivated();
            if (o1Actived != o2.wasStreamReservedOrActivated()) {
                return o1Actived ? -1 : 1;
            }
            int x = o2.dependencyTreeDepth - o1.dependencyTreeDepth;
            return x != 0 ? x : o1.streamId - o2.streamId;
        }
    }

}

