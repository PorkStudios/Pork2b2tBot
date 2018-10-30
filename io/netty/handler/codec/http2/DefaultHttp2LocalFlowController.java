/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionAdapter;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.Http2StreamVisitor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

public class DefaultHttp2LocalFlowController
implements Http2LocalFlowController {
    public static final float DEFAULT_WINDOW_UPDATE_RATIO = 0.5f;
    private final Http2Connection connection;
    private final Http2Connection.PropertyKey stateKey;
    private Http2FrameWriter frameWriter;
    private ChannelHandlerContext ctx;
    private float windowUpdateRatio;
    private int initialWindowSize = 65535;
    private static final FlowState REDUCED_FLOW_STATE = new FlowState(){

        @Override
        public int windowSize() {
            return 0;
        }

        @Override
        public int initialWindowSize() {
            return 0;
        }

        @Override
        public void window(int initialWindowSize) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void incrementInitialStreamWindow(int delta) {
        }

        @Override
        public boolean writeWindowUpdateIfNeeded() throws Http2Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean consumeBytes(int numBytes) throws Http2Exception {
            return false;
        }

        @Override
        public int unconsumedBytes() {
            return 0;
        }

        @Override
        public float windowUpdateRatio() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void windowUpdateRatio(float ratio) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void receiveFlowControlledFrame(int dataLength) throws Http2Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void incrementFlowControlWindows(int delta) throws Http2Exception {
        }

        @Override
        public void endOfStream(boolean endOfStream) {
            throw new UnsupportedOperationException();
        }
    };

    public DefaultHttp2LocalFlowController(Http2Connection connection) {
        this(connection, 0.5f, false);
    }

    public DefaultHttp2LocalFlowController(Http2Connection connection, float windowUpdateRatio, boolean autoRefillConnectionWindow) {
        this.connection = ObjectUtil.checkNotNull(connection, "connection");
        this.windowUpdateRatio(windowUpdateRatio);
        this.stateKey = connection.newKey();
        DefaultState connectionState = autoRefillConnectionWindow ? new AutoRefillState(connection.connectionStream(), this.initialWindowSize) : new DefaultState(connection.connectionStream(), this.initialWindowSize);
        connection.connectionStream().setProperty(this.stateKey, connectionState);
        connection.addListener(new Http2ConnectionAdapter(){

            @Override
            public void onStreamAdded(Http2Stream stream) {
                stream.setProperty(DefaultHttp2LocalFlowController.this.stateKey, REDUCED_FLOW_STATE);
            }

            @Override
            public void onStreamActive(Http2Stream stream) {
                stream.setProperty(DefaultHttp2LocalFlowController.this.stateKey, new DefaultState(stream, DefaultHttp2LocalFlowController.this.initialWindowSize));
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void onStreamClosed(Http2Stream stream) {
                try {
                    FlowState state = DefaultHttp2LocalFlowController.this.state(stream);
                    int unconsumedBytes = state.unconsumedBytes();
                    if (DefaultHttp2LocalFlowController.this.ctx != null && unconsumedBytes > 0) {
                        DefaultHttp2LocalFlowController.this.connectionState().consumeBytes(unconsumedBytes);
                        state.consumeBytes(unconsumedBytes);
                    }
                }
                catch (Http2Exception e) {
                    PlatformDependent.throwException(e);
                }
                finally {
                    stream.setProperty(DefaultHttp2LocalFlowController.this.stateKey, REDUCED_FLOW_STATE);
                }
            }
        });
    }

    @Override
    public DefaultHttp2LocalFlowController frameWriter(Http2FrameWriter frameWriter) {
        this.frameWriter = ObjectUtil.checkNotNull(frameWriter, "frameWriter");
        return this;
    }

    @Override
    public void channelHandlerContext(ChannelHandlerContext ctx) {
        this.ctx = ObjectUtil.checkNotNull(ctx, "ctx");
    }

    @Override
    public void initialWindowSize(int newWindowSize) throws Http2Exception {
        assert (this.ctx == null || this.ctx.executor().inEventLoop());
        int delta = newWindowSize - this.initialWindowSize;
        this.initialWindowSize = newWindowSize;
        WindowUpdateVisitor visitor = new WindowUpdateVisitor(delta);
        this.connection.forEachActiveStream(visitor);
        visitor.throwIfError();
    }

    @Override
    public int initialWindowSize() {
        return this.initialWindowSize;
    }

    @Override
    public int windowSize(Http2Stream stream) {
        return this.state(stream).windowSize();
    }

    @Override
    public int initialWindowSize(Http2Stream stream) {
        return this.state(stream).initialWindowSize();
    }

    @Override
    public void incrementWindowSize(Http2Stream stream, int delta) throws Http2Exception {
        assert (this.ctx != null && this.ctx.executor().inEventLoop());
        FlowState state = this.state(stream);
        state.incrementInitialStreamWindow(delta);
        state.writeWindowUpdateIfNeeded();
    }

    @Override
    public boolean consumeBytes(Http2Stream stream, int numBytes) throws Http2Exception {
        assert (this.ctx != null && this.ctx.executor().inEventLoop());
        if (numBytes < 0) {
            throw new IllegalArgumentException("numBytes must not be negative");
        }
        if (numBytes == 0) {
            return false;
        }
        if (stream != null && !DefaultHttp2LocalFlowController.isClosed(stream)) {
            if (stream.id() == 0) {
                throw new UnsupportedOperationException("Returning bytes for the connection window is not supported");
            }
            boolean windowUpdateSent = this.connectionState().consumeBytes(numBytes);
            return windowUpdateSent |= this.state(stream).consumeBytes(numBytes);
        }
        return false;
    }

    @Override
    public int unconsumedBytes(Http2Stream stream) {
        return this.state(stream).unconsumedBytes();
    }

    private static void checkValidRatio(float ratio) {
        if (Double.compare(ratio, 0.0) <= 0 || Double.compare(ratio, 1.0) >= 0) {
            throw new IllegalArgumentException("Invalid ratio: " + ratio);
        }
    }

    public void windowUpdateRatio(float ratio) {
        assert (this.ctx == null || this.ctx.executor().inEventLoop());
        DefaultHttp2LocalFlowController.checkValidRatio(ratio);
        this.windowUpdateRatio = ratio;
    }

    public float windowUpdateRatio() {
        return this.windowUpdateRatio;
    }

    public void windowUpdateRatio(Http2Stream stream, float ratio) throws Http2Exception {
        assert (this.ctx != null && this.ctx.executor().inEventLoop());
        DefaultHttp2LocalFlowController.checkValidRatio(ratio);
        FlowState state = this.state(stream);
        state.windowUpdateRatio(ratio);
        state.writeWindowUpdateIfNeeded();
    }

    public float windowUpdateRatio(Http2Stream stream) throws Http2Exception {
        return this.state(stream).windowUpdateRatio();
    }

    @Override
    public void receiveFlowControlledFrame(Http2Stream stream, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        assert (this.ctx != null && this.ctx.executor().inEventLoop());
        int dataLength = data.readableBytes() + padding;
        FlowState connectionState = this.connectionState();
        connectionState.receiveFlowControlledFrame(dataLength);
        if (stream != null && !DefaultHttp2LocalFlowController.isClosed(stream)) {
            FlowState state = this.state(stream);
            state.endOfStream(endOfStream);
            state.receiveFlowControlledFrame(dataLength);
        } else if (dataLength > 0) {
            connectionState.consumeBytes(dataLength);
        }
    }

    private FlowState connectionState() {
        return (FlowState)this.connection.connectionStream().getProperty(this.stateKey);
    }

    private FlowState state(Http2Stream stream) {
        return (FlowState)stream.getProperty(this.stateKey);
    }

    private static boolean isClosed(Http2Stream stream) {
        return stream.state() == Http2Stream.State.CLOSED;
    }

    private final class WindowUpdateVisitor
    implements Http2StreamVisitor {
        private Http2Exception.CompositeStreamException compositeException;
        private final int delta;

        public WindowUpdateVisitor(int delta) {
            this.delta = delta;
        }

        @Override
        public boolean visit(Http2Stream stream) throws Http2Exception {
            try {
                FlowState state = DefaultHttp2LocalFlowController.this.state(stream);
                state.incrementFlowControlWindows(this.delta);
                state.incrementInitialStreamWindow(this.delta);
            }
            catch (Http2Exception.StreamException e) {
                if (this.compositeException == null) {
                    this.compositeException = new Http2Exception.CompositeStreamException(e.error(), 4);
                }
                this.compositeException.add(e);
            }
            return true;
        }

        public void throwIfError() throws Http2Exception.CompositeStreamException {
            if (this.compositeException != null) {
                throw this.compositeException;
            }
        }
    }

    private static interface FlowState {
        public int windowSize();

        public int initialWindowSize();

        public void window(int var1);

        public void incrementInitialStreamWindow(int var1);

        public boolean writeWindowUpdateIfNeeded() throws Http2Exception;

        public boolean consumeBytes(int var1) throws Http2Exception;

        public int unconsumedBytes();

        public float windowUpdateRatio();

        public void windowUpdateRatio(float var1);

        public void receiveFlowControlledFrame(int var1) throws Http2Exception;

        public void incrementFlowControlWindows(int var1) throws Http2Exception;

        public void endOfStream(boolean var1);
    }

    private class DefaultState
    implements FlowState {
        private final Http2Stream stream;
        private int window;
        private int processedWindow;
        private int initialStreamWindowSize;
        private float streamWindowUpdateRatio;
        private int lowerBound;
        private boolean endOfStream;

        public DefaultState(Http2Stream stream, int initialWindowSize) {
            this.stream = stream;
            this.window(initialWindowSize);
            this.streamWindowUpdateRatio = DefaultHttp2LocalFlowController.this.windowUpdateRatio;
        }

        @Override
        public void window(int initialWindowSize) {
            assert (DefaultHttp2LocalFlowController.this.ctx == null || DefaultHttp2LocalFlowController.this.ctx.executor().inEventLoop());
            this.processedWindow = this.initialStreamWindowSize = initialWindowSize;
            this.window = this.initialStreamWindowSize;
        }

        @Override
        public int windowSize() {
            return this.window;
        }

        @Override
        public int initialWindowSize() {
            return this.initialStreamWindowSize;
        }

        @Override
        public void endOfStream(boolean endOfStream) {
            this.endOfStream = endOfStream;
        }

        @Override
        public float windowUpdateRatio() {
            return this.streamWindowUpdateRatio;
        }

        @Override
        public void windowUpdateRatio(float ratio) {
            assert (DefaultHttp2LocalFlowController.this.ctx == null || DefaultHttp2LocalFlowController.this.ctx.executor().inEventLoop());
            this.streamWindowUpdateRatio = ratio;
        }

        @Override
        public void incrementInitialStreamWindow(int delta) {
            int newValue = (int)Math.min(Integer.MAX_VALUE, Math.max(0L, (long)this.initialStreamWindowSize + (long)delta));
            delta = newValue - this.initialStreamWindowSize;
            this.initialStreamWindowSize += delta;
        }

        @Override
        public void incrementFlowControlWindows(int delta) throws Http2Exception {
            if (delta > 0 && this.window > Integer.MAX_VALUE - delta) {
                throw Http2Exception.streamError(this.stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Flow control window overflowed for stream: %d", this.stream.id());
            }
            this.window += delta;
            this.processedWindow += delta;
            this.lowerBound = delta < 0 ? delta : 0;
        }

        @Override
        public void receiveFlowControlledFrame(int dataLength) throws Http2Exception {
            assert (dataLength >= 0);
            this.window -= dataLength;
            if (this.window < this.lowerBound) {
                throw Http2Exception.streamError(this.stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Flow control window exceeded for stream: %d", this.stream.id());
            }
        }

        private void returnProcessedBytes(int delta) throws Http2Exception {
            if (this.processedWindow - delta < this.window) {
                throw Http2Exception.streamError(this.stream.id(), Http2Error.INTERNAL_ERROR, "Attempting to return too many bytes for stream %d", this.stream.id());
            }
            this.processedWindow -= delta;
        }

        @Override
        public boolean consumeBytes(int numBytes) throws Http2Exception {
            this.returnProcessedBytes(numBytes);
            return this.writeWindowUpdateIfNeeded();
        }

        @Override
        public int unconsumedBytes() {
            return this.processedWindow - this.window;
        }

        @Override
        public boolean writeWindowUpdateIfNeeded() throws Http2Exception {
            if (this.endOfStream || this.initialStreamWindowSize <= 0) {
                return false;
            }
            int threshold = (int)((float)this.initialStreamWindowSize * this.streamWindowUpdateRatio);
            if (this.processedWindow <= threshold) {
                this.writeWindowUpdate();
                return true;
            }
            return false;
        }

        private void writeWindowUpdate() throws Http2Exception {
            int deltaWindowSize = this.initialStreamWindowSize - this.processedWindow;
            try {
                this.incrementFlowControlWindows(deltaWindowSize);
            }
            catch (Throwable t) {
                throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, t, "Attempting to return too many bytes for stream %d", this.stream.id());
            }
            DefaultHttp2LocalFlowController.this.frameWriter.writeWindowUpdate(DefaultHttp2LocalFlowController.this.ctx, this.stream.id(), deltaWindowSize, DefaultHttp2LocalFlowController.this.ctx.newPromise());
        }
    }

    private final class AutoRefillState
    extends DefaultState {
        public AutoRefillState(Http2Stream stream, int initialWindowSize) {
            super(stream, initialWindowSize);
        }

        @Override
        public void receiveFlowControlledFrame(int dataLength) throws Http2Exception {
            super.receiveFlowControlledFrame(dataLength);
            super.consumeBytes(dataLength);
        }

        @Override
        public boolean consumeBytes(int numBytes) throws Http2Exception {
            return false;
        }
    }

}

