/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FlowController;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2NoMoreStreamIdsException;
import io.netty.handler.codec.http2.Http2RemoteFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.Http2StreamVisitor;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.UnaryPromiseNotifier;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DefaultHttp2Connection
implements Http2Connection {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHttp2Connection.class);
    final IntObjectMap<Http2Stream> streamMap = new IntObjectHashMap<Http2Stream>();
    final PropertyKeyRegistry propertyKeyRegistry = new PropertyKeyRegistry();
    final ConnectionStream connectionStream = new ConnectionStream();
    final DefaultEndpoint<Http2LocalFlowController> localEndpoint;
    final DefaultEndpoint<Http2RemoteFlowController> remoteEndpoint;
    final List<Http2Connection.Listener> listeners = new ArrayList<Http2Connection.Listener>(4);
    final ActiveStreams activeStreams = new ActiveStreams(this.listeners);
    Promise<Void> closePromise;

    public DefaultHttp2Connection(boolean server) {
        this(server, 100);
    }

    public DefaultHttp2Connection(boolean server, int maxReservedStreams) {
        this.localEndpoint = new DefaultEndpoint(server, server ? Integer.MAX_VALUE : maxReservedStreams);
        this.remoteEndpoint = new DefaultEndpoint(!server, maxReservedStreams);
        this.streamMap.put(this.connectionStream.id(), this.connectionStream);
    }

    final boolean isClosed() {
        return this.closePromise != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Future<Void> close(Promise<Void> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        if (this.closePromise != null) {
            if (this.closePromise != promise) {
                if (promise instanceof ChannelPromise && ((ChannelPromise)this.closePromise).isVoid()) {
                    this.closePromise = promise;
                } else {
                    this.closePromise.addListener(new UnaryPromiseNotifier<Void>(promise));
                }
            }
        } else {
            this.closePromise = promise;
        }
        if (this.isStreamMapEmpty()) {
            promise.trySuccess(null);
            return promise;
        }
        Iterator<IntObjectMap.PrimitiveEntry<Http2Stream>> itr = this.streamMap.entries().iterator();
        if (this.activeStreams.allowModifications()) {
            this.activeStreams.incrementPendingIterations();
            try {
                while (itr.hasNext()) {
                    DefaultStream stream = (DefaultStream)itr.next().value();
                    if (stream.id() == 0) continue;
                    stream.close(itr);
                }
            }
            finally {
                this.activeStreams.decrementPendingIterations();
            }
        } else {
            while (itr.hasNext()) {
                Http2Stream stream = itr.next().value();
                if (stream.id() == 0) continue;
                stream.close();
            }
        }
        return this.closePromise;
    }

    @Override
    public void addListener(Http2Connection.Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Http2Connection.Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public boolean isServer() {
        return this.localEndpoint.isServer();
    }

    @Override
    public Http2Stream connectionStream() {
        return this.connectionStream;
    }

    @Override
    public Http2Stream stream(int streamId) {
        return this.streamMap.get(streamId);
    }

    @Override
    public boolean streamMayHaveExisted(int streamId) {
        return this.remoteEndpoint.mayHaveCreatedStream(streamId) || this.localEndpoint.mayHaveCreatedStream(streamId);
    }

    @Override
    public int numActiveStreams() {
        return this.activeStreams.size();
    }

    @Override
    public Http2Stream forEachActiveStream(Http2StreamVisitor visitor) throws Http2Exception {
        return this.activeStreams.forEachActiveStream(visitor);
    }

    @Override
    public Http2Connection.Endpoint<Http2LocalFlowController> local() {
        return this.localEndpoint;
    }

    @Override
    public Http2Connection.Endpoint<Http2RemoteFlowController> remote() {
        return this.remoteEndpoint;
    }

    @Override
    public boolean goAwayReceived() {
        return this.localEndpoint.lastStreamKnownByPeer >= 0;
    }

    @Override
    public void goAwayReceived(final int lastKnownStream, long errorCode, ByteBuf debugData) {
        this.localEndpoint.lastStreamKnownByPeer(lastKnownStream);
        for (int i = 0; i < this.listeners.size(); ++i) {
            try {
                this.listeners.get(i).onGoAwayReceived(lastKnownStream, errorCode, debugData);
                continue;
            }
            catch (Throwable cause) {
                logger.error("Caught Throwable from listener onGoAwayReceived.", cause);
            }
        }
        try {
            this.forEachActiveStream(new Http2StreamVisitor(){

                @Override
                public boolean visit(Http2Stream stream) {
                    if (stream.id() > lastKnownStream && DefaultHttp2Connection.this.localEndpoint.isValidStreamId(stream.id())) {
                        stream.close();
                    }
                    return true;
                }
            });
        }
        catch (Http2Exception e) {
            PlatformDependent.throwException(e);
        }
    }

    @Override
    public boolean goAwaySent() {
        return this.remoteEndpoint.lastStreamKnownByPeer >= 0;
    }

    @Override
    public void goAwaySent(final int lastKnownStream, long errorCode, ByteBuf debugData) {
        this.remoteEndpoint.lastStreamKnownByPeer(lastKnownStream);
        for (int i = 0; i < this.listeners.size(); ++i) {
            try {
                this.listeners.get(i).onGoAwaySent(lastKnownStream, errorCode, debugData);
                continue;
            }
            catch (Throwable cause) {
                logger.error("Caught Throwable from listener onGoAwaySent.", cause);
            }
        }
        try {
            this.forEachActiveStream(new Http2StreamVisitor(){

                @Override
                public boolean visit(Http2Stream stream) {
                    if (stream.id() > lastKnownStream && DefaultHttp2Connection.this.remoteEndpoint.isValidStreamId(stream.id())) {
                        stream.close();
                    }
                    return true;
                }
            });
        }
        catch (Http2Exception e) {
            PlatformDependent.throwException(e);
        }
    }

    private boolean isStreamMapEmpty() {
        return this.streamMap.size() == 1;
    }

    void removeStream(DefaultStream stream, Iterator<?> itr) {
        boolean removed;
        if (itr == null) {
            removed = this.streamMap.remove(stream.id()) != null;
        } else {
            itr.remove();
            removed = true;
        }
        if (removed) {
            for (int i = 0; i < this.listeners.size(); ++i) {
                try {
                    this.listeners.get(i).onStreamRemoved(stream);
                    continue;
                }
                catch (Throwable cause) {
                    logger.error("Caught Throwable from listener onStreamRemoved.", cause);
                }
            }
            if (this.closePromise != null && this.isStreamMapEmpty()) {
                this.closePromise.trySuccess(null);
            }
        }
    }

    static Http2Stream.State activeState(int streamId, Http2Stream.State initialState, boolean isLocal, boolean halfClosed) throws Http2Exception {
        switch (initialState) {
            case IDLE: {
                return halfClosed ? (isLocal ? Http2Stream.State.HALF_CLOSED_LOCAL : Http2Stream.State.HALF_CLOSED_REMOTE) : Http2Stream.State.OPEN;
            }
            case RESERVED_LOCAL: {
                return Http2Stream.State.HALF_CLOSED_REMOTE;
            }
            case RESERVED_REMOTE: {
                return Http2Stream.State.HALF_CLOSED_LOCAL;
            }
        }
        throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, "Attempting to open a stream in an invalid state: " + (Object)((Object)initialState), new Object[0]);
    }

    void notifyHalfClosed(Http2Stream stream) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            try {
                this.listeners.get(i).onStreamHalfClosed(stream);
                continue;
            }
            catch (Throwable cause) {
                logger.error("Caught Throwable from listener onStreamHalfClosed.", cause);
            }
        }
    }

    void notifyClosed(Http2Stream stream) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            try {
                this.listeners.get(i).onStreamClosed(stream);
                continue;
            }
            catch (Throwable cause) {
                logger.error("Caught Throwable from listener onStreamClosed.", cause);
            }
        }
    }

    @Override
    public Http2Connection.PropertyKey newKey() {
        return this.propertyKeyRegistry.newKey();
    }

    final DefaultPropertyKey verifyKey(Http2Connection.PropertyKey key) {
        return ObjectUtil.checkNotNull((DefaultPropertyKey)key, "key").verifyConnection(this);
    }

    private final class PropertyKeyRegistry {
        final List<DefaultPropertyKey> keys = new ArrayList<DefaultPropertyKey>(4);

        private PropertyKeyRegistry() {
        }

        DefaultPropertyKey newKey() {
            DefaultPropertyKey key = new DefaultPropertyKey(this.keys.size());
            this.keys.add(key);
            return key;
        }

        int size() {
            return this.keys.size();
        }
    }

    final class DefaultPropertyKey
    implements Http2Connection.PropertyKey {
        final int index;

        DefaultPropertyKey(int index) {
            this.index = index;
        }

        DefaultPropertyKey verifyConnection(Http2Connection connection) {
            if (connection != DefaultHttp2Connection.this) {
                throw new IllegalArgumentException("Using a key that was not created by this connection");
            }
            return this;
        }
    }

    private final class ActiveStreams {
        private final List<Http2Connection.Listener> listeners;
        private final Queue<Event> pendingEvents = new ArrayDeque<Event>(4);
        private final Set<Http2Stream> streams = new LinkedHashSet<Http2Stream>();
        private int pendingIterations;

        public ActiveStreams(List<Http2Connection.Listener> listeners) {
            this.listeners = listeners;
        }

        public int size() {
            return this.streams.size();
        }

        public void activate(final DefaultStream stream) {
            if (this.allowModifications()) {
                this.addToActiveStreams(stream);
            } else {
                this.pendingEvents.add(new Event(){

                    @Override
                    public void process() {
                        ActiveStreams.this.addToActiveStreams(stream);
                    }
                });
            }
        }

        public void deactivate(final DefaultStream stream, final Iterator<?> itr) {
            if (this.allowModifications() || itr != null) {
                this.removeFromActiveStreams(stream, itr);
            } else {
                this.pendingEvents.add(new Event(){

                    @Override
                    public void process() {
                        ActiveStreams.this.removeFromActiveStreams(stream, itr);
                    }
                });
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Http2Stream forEachActiveStream(Http2StreamVisitor visitor) throws Http2Exception {
            this.incrementPendingIterations();
            try {
                for (Http2Stream stream : this.streams) {
                    if (visitor.visit(stream)) continue;
                    Http2Stream http2Stream = stream;
                    return http2Stream;
                }
                Iterator<Http2Stream> iterator = null;
                return iterator;
            }
            finally {
                this.decrementPendingIterations();
            }
        }

        void addToActiveStreams(DefaultStream stream) {
            if (this.streams.add(stream)) {
                ++stream.createdBy().numActiveStreams;
                for (int i = 0; i < this.listeners.size(); ++i) {
                    try {
                        this.listeners.get(i).onStreamActive(stream);
                        continue;
                    }
                    catch (Throwable cause) {
                        logger.error("Caught Throwable from listener onStreamActive.", cause);
                    }
                }
            }
        }

        void removeFromActiveStreams(DefaultStream stream, Iterator<?> itr) {
            if (this.streams.remove(stream)) {
                --stream.createdBy().numActiveStreams;
                DefaultHttp2Connection.this.notifyClosed(stream);
            }
            DefaultHttp2Connection.this.removeStream(stream, itr);
        }

        boolean allowModifications() {
            return this.pendingIterations == 0;
        }

        void incrementPendingIterations() {
            ++this.pendingIterations;
        }

        void decrementPendingIterations() {
            --this.pendingIterations;
            if (this.allowModifications()) {
                Event event;
                while ((event = this.pendingEvents.poll()) != null) {
                    try {
                        event.process();
                    }
                    catch (Throwable cause) {
                        logger.error("Caught Throwable while processing pending ActiveStreams$Event.", cause);
                    }
                }
            }
        }

    }

    static interface Event {
        public void process();
    }

    private final class DefaultEndpoint<F extends Http2FlowController>
    implements Http2Connection.Endpoint<F> {
        private final boolean server;
        private int nextStreamIdToCreate;
        private int nextReservationStreamId;
        private int lastStreamKnownByPeer = -1;
        private boolean pushToAllowed = true;
        private F flowController;
        private int maxStreams;
        private int maxActiveStreams;
        private final int maxReservedStreams;
        int numActiveStreams;
        int numStreams;

        DefaultEndpoint(boolean server, int maxReservedStreams) {
            this.server = server;
            if (server) {
                this.nextStreamIdToCreate = 2;
                this.nextReservationStreamId = 0;
            } else {
                this.nextStreamIdToCreate = 1;
                this.nextReservationStreamId = 1;
            }
            this.pushToAllowed = !server;
            this.maxActiveStreams = Integer.MAX_VALUE;
            this.maxReservedStreams = ObjectUtil.checkPositiveOrZero(maxReservedStreams, "maxReservedStreams");
            this.updateMaxStreams();
        }

        @Override
        public int incrementAndGetNextStreamId() {
            int n = this.nextReservationStreamId >= 0 ? (this.nextReservationStreamId = this.nextReservationStreamId + 2) : this.nextReservationStreamId;
            return n;
        }

        private void incrementExpectedStreamId(int streamId) {
            if (streamId > this.nextReservationStreamId && this.nextReservationStreamId >= 0) {
                this.nextReservationStreamId = streamId;
            }
            this.nextStreamIdToCreate = streamId + 2;
            ++this.numStreams;
        }

        @Override
        public boolean isValidStreamId(int streamId) {
            return streamId > 0 && this.server == ((streamId & 1) == 0);
        }

        @Override
        public boolean mayHaveCreatedStream(int streamId) {
            return this.isValidStreamId(streamId) && streamId <= this.lastStreamCreated();
        }

        @Override
        public boolean canOpenStream() {
            return this.numActiveStreams < this.maxActiveStreams;
        }

        @Override
        public DefaultStream createStream(int streamId, boolean halfClosed) throws Http2Exception {
            Http2Stream.State state = DefaultHttp2Connection.activeState(streamId, Http2Stream.State.IDLE, this.isLocal(), halfClosed);
            this.checkNewStreamAllowed(streamId, state);
            DefaultStream stream = new DefaultStream(streamId, state);
            this.incrementExpectedStreamId(streamId);
            this.addStream(stream);
            stream.activate();
            return stream;
        }

        @Override
        public boolean created(Http2Stream stream) {
            return stream instanceof DefaultStream && ((DefaultStream)stream).createdBy() == this;
        }

        @Override
        public boolean isServer() {
            return this.server;
        }

        @Override
        public DefaultStream reservePushStream(int streamId, Http2Stream parent) throws Http2Exception {
            if (parent == null) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Parent stream missing", new Object[0]);
            }
            if (this.isLocal() ? !parent.state().localSideOpen() : !parent.state().remoteSideOpen()) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d is not open for sending push promise", parent.id());
            }
            if (!this.opposite().allowPushTo()) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server push not allowed to opposite endpoint", new Object[0]);
            }
            Http2Stream.State state = this.isLocal() ? Http2Stream.State.RESERVED_LOCAL : Http2Stream.State.RESERVED_REMOTE;
            this.checkNewStreamAllowed(streamId, state);
            DefaultStream stream = new DefaultStream(streamId, state);
            this.incrementExpectedStreamId(streamId);
            this.addStream(stream);
            return stream;
        }

        private void addStream(DefaultStream stream) {
            DefaultHttp2Connection.this.streamMap.put(stream.id(), stream);
            for (int i = 0; i < DefaultHttp2Connection.this.listeners.size(); ++i) {
                try {
                    DefaultHttp2Connection.this.listeners.get(i).onStreamAdded(stream);
                    continue;
                }
                catch (Throwable cause) {
                    logger.error("Caught Throwable from listener onStreamAdded.", cause);
                }
            }
        }

        @Override
        public void allowPushTo(boolean allow) {
            if (allow && this.server) {
                throw new IllegalArgumentException("Servers do not allow push");
            }
            this.pushToAllowed = allow;
        }

        @Override
        public boolean allowPushTo() {
            return this.pushToAllowed;
        }

        @Override
        public int numActiveStreams() {
            return this.numActiveStreams;
        }

        @Override
        public int maxActiveStreams() {
            return this.maxActiveStreams;
        }

        @Override
        public void maxActiveStreams(int maxActiveStreams) {
            this.maxActiveStreams = maxActiveStreams;
            this.updateMaxStreams();
        }

        @Override
        public int lastStreamCreated() {
            return this.nextStreamIdToCreate > 1 ? this.nextStreamIdToCreate - 2 : 0;
        }

        @Override
        public int lastStreamKnownByPeer() {
            return this.lastStreamKnownByPeer;
        }

        private void lastStreamKnownByPeer(int lastKnownStream) {
            this.lastStreamKnownByPeer = lastKnownStream;
        }

        @Override
        public F flowController() {
            return this.flowController;
        }

        @Override
        public void flowController(F flowController) {
            this.flowController = (Http2FlowController)ObjectUtil.checkNotNull(flowController, "flowController");
        }

        @Override
        public Http2Connection.Endpoint<? extends Http2FlowController> opposite() {
            return this.isLocal() ? DefaultHttp2Connection.this.remoteEndpoint : DefaultHttp2Connection.this.localEndpoint;
        }

        private void updateMaxStreams() {
            this.maxStreams = (int)Math.min(Integer.MAX_VALUE, (long)this.maxActiveStreams + (long)this.maxReservedStreams);
        }

        private void checkNewStreamAllowed(int streamId, Http2Stream.State state) throws Http2Exception {
            boolean isReserved;
            assert (state != Http2Stream.State.IDLE);
            if (DefaultHttp2Connection.this.goAwayReceived() && streamId > DefaultHttp2Connection.this.localEndpoint.lastStreamKnownByPeer()) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Cannot create stream %d since this endpoint has received a GOAWAY frame with last stream id %d.", streamId, DefaultHttp2Connection.this.localEndpoint.lastStreamKnownByPeer());
            }
            if (!this.isValidStreamId(streamId)) {
                if (streamId < 0) {
                    throw new Http2NoMoreStreamIdsException();
                }
                Object[] arrobject = new Object[2];
                arrobject[0] = streamId;
                arrobject[1] = this.server ? "server" : "client";
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Request stream %d is not correct for %s connection", arrobject);
            }
            if (streamId < this.nextStreamIdToCreate) {
                throw Http2Exception.closedStreamError(Http2Error.PROTOCOL_ERROR, "Request stream %d is behind the next expected stream %d", streamId, this.nextStreamIdToCreate);
            }
            if (this.nextStreamIdToCreate <= 0) {
                throw Http2Exception.connectionError(Http2Error.REFUSED_STREAM, "Stream IDs are exhausted for this endpoint.", new Object[0]);
            }
            boolean bl = isReserved = state == Http2Stream.State.RESERVED_LOCAL || state == Http2Stream.State.RESERVED_REMOTE;
            if (!isReserved && !this.canOpenStream() || isReserved && this.numStreams >= this.maxStreams) {
                throw Http2Exception.streamError(streamId, Http2Error.REFUSED_STREAM, "Maximum active streams violated for this endpoint.", new Object[0]);
            }
            if (DefaultHttp2Connection.this.isClosed()) {
                throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "Attempted to create stream id %d after connection was closed", streamId);
            }
        }

        private boolean isLocal() {
            return this == DefaultHttp2Connection.this.localEndpoint;
        }
    }

    private final class ConnectionStream
    extends DefaultStream {
        ConnectionStream() {
            super(0, Http2Stream.State.IDLE);
        }

        @Override
        public boolean isResetSent() {
            return false;
        }

        @Override
        DefaultEndpoint<? extends Http2FlowController> createdBy() {
            return null;
        }

        @Override
        public Http2Stream resetSent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Http2Stream open(boolean halfClosed) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Http2Stream close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Http2Stream closeLocalSide() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Http2Stream closeRemoteSide() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Http2Stream headersSent(boolean isInformational) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isHeadersSent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Http2Stream pushPromiseSent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPushPromiseSent() {
            throw new UnsupportedOperationException();
        }
    }

    private class DefaultStream
    implements Http2Stream {
        private static final byte META_STATE_SENT_RST = 1;
        private static final byte META_STATE_SENT_HEADERS = 2;
        private static final byte META_STATE_SENT_TRAILERS = 4;
        private static final byte META_STATE_SENT_PUSHPROMISE = 8;
        private static final byte META_STATE_RECV_HEADERS = 16;
        private static final byte META_STATE_RECV_TRAILERS = 32;
        private final int id;
        private final PropertyMap properties = new PropertyMap();
        private Http2Stream.State state;
        private byte metaState;

        DefaultStream(int id, Http2Stream.State state) {
            this.id = id;
            this.state = state;
        }

        @Override
        public final int id() {
            return this.id;
        }

        @Override
        public final Http2Stream.State state() {
            return this.state;
        }

        @Override
        public boolean isResetSent() {
            return (this.metaState & 1) != 0;
        }

        @Override
        public Http2Stream resetSent() {
            this.metaState = (byte)(this.metaState | 1);
            return this;
        }

        @Override
        public Http2Stream headersSent(boolean isInformational) {
            if (!isInformational) {
                this.metaState = (byte)(this.metaState | (this.isHeadersSent() ? 4 : 2));
            }
            return this;
        }

        @Override
        public boolean isHeadersSent() {
            return (this.metaState & 2) != 0;
        }

        @Override
        public boolean isTrailersSent() {
            return (this.metaState & 4) != 0;
        }

        @Override
        public Http2Stream headersReceived(boolean isInformational) {
            if (!isInformational) {
                this.metaState = (byte)(this.metaState | (this.isHeadersReceived() ? 32 : 16));
            }
            return this;
        }

        @Override
        public boolean isHeadersReceived() {
            return (this.metaState & 16) != 0;
        }

        @Override
        public boolean isTrailersReceived() {
            return (this.metaState & 32) != 0;
        }

        @Override
        public Http2Stream pushPromiseSent() {
            this.metaState = (byte)(this.metaState | 8);
            return this;
        }

        @Override
        public boolean isPushPromiseSent() {
            return (this.metaState & 8) != 0;
        }

        @Override
        public final <V> V setProperty(Http2Connection.PropertyKey key, V value) {
            return this.properties.add(DefaultHttp2Connection.this.verifyKey(key), value);
        }

        @Override
        public final <V> V getProperty(Http2Connection.PropertyKey key) {
            return this.properties.get(DefaultHttp2Connection.this.verifyKey(key));
        }

        @Override
        public final <V> V removeProperty(Http2Connection.PropertyKey key) {
            return this.properties.remove(DefaultHttp2Connection.this.verifyKey(key));
        }

        @Override
        public Http2Stream open(boolean halfClosed) throws Http2Exception {
            this.state = DefaultHttp2Connection.activeState(this.id, this.state, this.isLocal(), halfClosed);
            if (!this.createdBy().canOpenStream()) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Maximum active streams violated for this endpoint.", new Object[0]);
            }
            this.activate();
            return this;
        }

        void activate() {
            DefaultHttp2Connection.this.activeStreams.activate(this);
        }

        Http2Stream close(Iterator<?> itr) {
            if (this.state == Http2Stream.State.CLOSED) {
                return this;
            }
            this.state = Http2Stream.State.CLOSED;
            --this.createdBy().numStreams;
            DefaultHttp2Connection.this.activeStreams.deactivate(this, itr);
            return this;
        }

        @Override
        public Http2Stream close() {
            return this.close(null);
        }

        @Override
        public Http2Stream closeLocalSide() {
            switch (this.state) {
                case OPEN: {
                    this.state = Http2Stream.State.HALF_CLOSED_LOCAL;
                    DefaultHttp2Connection.this.notifyHalfClosed(this);
                    break;
                }
                case HALF_CLOSED_LOCAL: {
                    break;
                }
                default: {
                    this.close();
                }
            }
            return this;
        }

        @Override
        public Http2Stream closeRemoteSide() {
            switch (this.state) {
                case OPEN: {
                    this.state = Http2Stream.State.HALF_CLOSED_REMOTE;
                    DefaultHttp2Connection.this.notifyHalfClosed(this);
                    break;
                }
                case HALF_CLOSED_REMOTE: {
                    break;
                }
                default: {
                    this.close();
                }
            }
            return this;
        }

        DefaultEndpoint<? extends Http2FlowController> createdBy() {
            return DefaultHttp2Connection.this.localEndpoint.isValidStreamId(this.id) ? DefaultHttp2Connection.this.localEndpoint : DefaultHttp2Connection.this.remoteEndpoint;
        }

        final boolean isLocal() {
            return DefaultHttp2Connection.this.localEndpoint.isValidStreamId(this.id);
        }

        private class PropertyMap {
            Object[] values = EmptyArrays.EMPTY_OBJECTS;

            private PropertyMap() {
            }

            <V> V add(DefaultPropertyKey key, V value) {
                this.resizeIfNecessary(key.index);
                Object prevValue = this.values[key.index];
                this.values[key.index] = value;
                return (V)prevValue;
            }

            <V> V get(DefaultPropertyKey key) {
                if (key.index >= this.values.length) {
                    return null;
                }
                return (V)this.values[key.index];
            }

            <V> V remove(DefaultPropertyKey key) {
                Object prevValue = null;
                if (key.index < this.values.length) {
                    prevValue = this.values[key.index];
                    this.values[key.index] = null;
                }
                return (V)prevValue;
            }

            void resizeIfNecessary(int index) {
                if (index >= this.values.length) {
                    this.values = Arrays.copyOf(this.values, DefaultHttp2Connection.this.propertyKeyRegistry.size());
                }
            }
        }

    }

}

