/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2HeadersDecoder;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionPrefaceAndSettingsFrameWrittenEvent;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersDecoder;
import io.netty.handler.codec.http2.Http2InboundFrameLogger;
import io.netty.handler.codec.http2.Http2LifecycleManager;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2OutboundFrameLogger;
import io.netty.handler.codec.http2.Http2RemoteFlowController;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.ReadOnlyHttp2Headers;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Http2ConnectionHandler
extends ByteToMessageDecoder
implements Http2LifecycleManager,
ChannelOutboundHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http2ConnectionHandler.class);
    private static final Http2Headers HEADERS_TOO_LARGE_HEADERS = ReadOnlyHttp2Headers.serverHeaders(false, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.codeAsText(), new AsciiString[0]);
    private static final ByteBuf HTTP_1_X_BUF = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[]{72, 84, 84, 80, 47, 49, 46})).asReadOnly();
    private final Http2ConnectionDecoder decoder;
    private final Http2ConnectionEncoder encoder;
    private final Http2Settings initialSettings;
    private ChannelFutureListener closeListener;
    private BaseDecoder byteDecoder;
    private long gracefulShutdownTimeoutMillis;

    protected Http2ConnectionHandler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        this.initialSettings = ObjectUtil.checkNotNull(initialSettings, "initialSettings");
        this.decoder = ObjectUtil.checkNotNull(decoder, "decoder");
        this.encoder = ObjectUtil.checkNotNull(encoder, "encoder");
        if (encoder.connection() != decoder.connection()) {
            throw new IllegalArgumentException("Encoder and Decoder do not share the same connection object");
        }
    }

    Http2ConnectionHandler(boolean server, Http2FrameWriter frameWriter, Http2FrameLogger frameLogger, Http2Settings initialSettings) {
        this.initialSettings = ObjectUtil.checkNotNull(initialSettings, "initialSettings");
        DefaultHttp2Connection connection = new DefaultHttp2Connection(server);
        Long maxHeaderListSize = initialSettings.maxHeaderListSize();
        Http2FrameReader frameReader = new DefaultHttp2FrameReader(maxHeaderListSize == null ? new DefaultHttp2HeadersDecoder(true) : new DefaultHttp2HeadersDecoder(true, maxHeaderListSize));
        if (frameLogger != null) {
            frameWriter = new Http2OutboundFrameLogger(frameWriter, frameLogger);
            frameReader = new Http2InboundFrameLogger(frameReader, frameLogger);
        }
        this.encoder = new DefaultHttp2ConnectionEncoder(connection, frameWriter);
        this.decoder = new DefaultHttp2ConnectionDecoder(connection, this.encoder, frameReader);
    }

    public long gracefulShutdownTimeoutMillis() {
        return this.gracefulShutdownTimeoutMillis;
    }

    public void gracefulShutdownTimeoutMillis(long gracefulShutdownTimeoutMillis) {
        if (gracefulShutdownTimeoutMillis < -1L) {
            throw new IllegalArgumentException("gracefulShutdownTimeoutMillis: " + gracefulShutdownTimeoutMillis + " (expected: -1 for indefinite or >= 0)");
        }
        this.gracefulShutdownTimeoutMillis = gracefulShutdownTimeoutMillis;
    }

    public Http2Connection connection() {
        return this.encoder.connection();
    }

    public Http2ConnectionDecoder decoder() {
        return this.decoder;
    }

    public Http2ConnectionEncoder encoder() {
        return this.encoder;
    }

    private boolean prefaceSent() {
        return this.byteDecoder != null && this.byteDecoder.prefaceSent();
    }

    public void onHttpClientUpgrade() throws Http2Exception {
        if (this.connection().isServer()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Client-side HTTP upgrade requested for a server", new Object[0]);
        }
        if (!this.prefaceSent()) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "HTTP upgrade must occur after preface was sent", new Object[0]);
        }
        if (this.decoder.prefaceReceived()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP upgrade must occur before HTTP/2 preface is received", new Object[0]);
        }
        this.connection().local().createStream(1, true);
    }

    public void onHttpServerUpgrade(Http2Settings settings) throws Http2Exception {
        if (!this.connection().isServer()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server-side HTTP upgrade requested for a client", new Object[0]);
        }
        if (!this.prefaceSent()) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "HTTP upgrade must occur after preface was sent", new Object[0]);
        }
        if (this.decoder.prefaceReceived()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP upgrade must occur before HTTP/2 preface is received", new Object[0]);
        }
        this.encoder.remoteSettings(settings);
        this.connection().remote().createStream(1, true);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) {
        try {
            this.encoder.flowController().writePendingBytes();
            ctx.flush();
        }
        catch (Http2Exception e) {
            this.onError(ctx, e);
        }
        catch (Throwable cause) {
            this.onError(ctx, Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, cause, "Error flushing", new Object[0]));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.encoder.lifecycleManager(this);
        this.decoder.lifecycleManager(this);
        this.encoder.flowController().channelHandlerContext(ctx);
        this.decoder.flowController().channelHandlerContext(ctx);
        this.byteDecoder = new PrefaceDecoder(ctx);
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        if (this.byteDecoder != null) {
            this.byteDecoder.handlerRemoved(ctx);
            this.byteDecoder = null;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.byteDecoder == null) {
            this.byteDecoder = new PrefaceDecoder(ctx);
        }
        this.byteDecoder.channelActive(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (this.byteDecoder != null) {
            this.byteDecoder.channelInactive(ctx);
            this.byteDecoder = null;
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        try {
            if (ctx.channel().isWritable()) {
                this.flush(ctx);
            }
            this.encoder.flowController().channelWritabilityChanged();
        }
        finally {
            super.channelWritabilityChanged(ctx);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.byteDecoder.decode(ctx, in, out);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        promise = promise.unvoid();
        if (!ctx.channel().isActive()) {
            ctx.close(promise);
            return;
        }
        ChannelFuture future = this.connection().goAwaySent() ? ctx.write(Unpooled.EMPTY_BUFFER) : this.goAway(ctx, null);
        ctx.flush();
        this.doGracefulShutdown(ctx, future, promise);
    }

    private void doGracefulShutdown(ChannelHandlerContext ctx, ChannelFuture future, ChannelPromise promise) {
        if (this.isGracefulShutdownComplete()) {
            future.addListener(new ClosingChannelFutureListener(ctx, promise));
        } else {
            this.closeListener = this.gracefulShutdownTimeoutMillis < 0L ? new ClosingChannelFutureListener(ctx, promise) : new ClosingChannelFutureListener(ctx, promise, this.gracefulShutdownTimeoutMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        try {
            this.channelReadComplete0(ctx);
        }
        finally {
            this.flush(ctx);
        }
    }

    void channelReadComplete0(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (Http2CodecUtil.getEmbeddedHttp2Exception(cause) != null) {
            this.onError(ctx, cause);
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    @Override
    public void closeStreamLocal(Http2Stream stream, ChannelFuture future) {
        switch (stream.state()) {
            case HALF_CLOSED_LOCAL: 
            case OPEN: {
                stream.closeLocalSide();
                break;
            }
            default: {
                this.closeStream(stream, future);
            }
        }
    }

    @Override
    public void closeStreamRemote(Http2Stream stream, ChannelFuture future) {
        switch (stream.state()) {
            case OPEN: 
            case HALF_CLOSED_REMOTE: {
                stream.closeRemoteSide();
                break;
            }
            default: {
                this.closeStream(stream, future);
            }
        }
    }

    @Override
    public void closeStream(Http2Stream stream, ChannelFuture future) {
        stream.close();
        if (future.isDone()) {
            this.checkCloseConnection(future);
        } else {
            future.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Http2ConnectionHandler.this.checkCloseConnection(future);
                }
            });
        }
    }

    @Override
    public void onError(ChannelHandlerContext ctx, Throwable cause) {
        Http2Exception embedded = Http2CodecUtil.getEmbeddedHttp2Exception(cause);
        if (Http2Exception.isStreamError(embedded)) {
            this.onStreamError(ctx, cause, (Http2Exception.StreamException)embedded);
        } else if (embedded instanceof Http2Exception.CompositeStreamException) {
            Http2Exception.CompositeStreamException compositException = (Http2Exception.CompositeStreamException)embedded;
            for (Http2Exception.StreamException streamException : compositException) {
                this.onStreamError(ctx, cause, streamException);
            }
        } else {
            this.onConnectionError(ctx, cause, embedded);
        }
        ctx.flush();
    }

    protected boolean isGracefulShutdownComplete() {
        return this.connection().numActiveStreams() == 0;
    }

    protected void onConnectionError(ChannelHandlerContext ctx, Throwable cause, Http2Exception http2Ex) {
        if (http2Ex == null) {
            http2Ex = new Http2Exception(Http2Error.INTERNAL_ERROR, cause.getMessage(), cause);
        }
        ChannelPromise promise = ctx.newPromise();
        ChannelFuture future = this.goAway(ctx, http2Ex);
        switch (http2Ex.shutdownHint()) {
            case GRACEFUL_SHUTDOWN: {
                this.doGracefulShutdown(ctx, future, promise);
                break;
            }
            default: {
                future.addListener(new ClosingChannelFutureListener(ctx, promise));
            }
        }
    }

    protected void onStreamError(ChannelHandlerContext ctx, Throwable cause, Http2Exception.StreamException http2Ex) {
        int streamId = http2Ex.streamId();
        Http2Stream stream = this.connection().stream(streamId);
        if (http2Ex instanceof Http2Exception.HeaderListSizeException && ((Http2Exception.HeaderListSizeException)http2Ex).duringDecode() && this.connection().isServer()) {
            if (stream == null) {
                try {
                    stream = this.encoder.connection().remote().createStream(streamId, true);
                }
                catch (Http2Exception e) {
                    this.resetUnknownStream(ctx, streamId, http2Ex.error().code(), ctx.newPromise());
                    return;
                }
            }
            if (stream != null && !stream.isHeadersSent()) {
                try {
                    this.handleServerHeaderDecodeSizeError(ctx, stream);
                }
                catch (Throwable cause2) {
                    this.onError(ctx, Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, cause2, "Error DecodeSizeError", new Object[0]));
                }
            }
        }
        if (stream == null) {
            this.resetUnknownStream(ctx, streamId, http2Ex.error().code(), ctx.newPromise());
        } else {
            this.resetStream(ctx, stream, http2Ex.error().code(), ctx.newPromise());
        }
    }

    protected void handleServerHeaderDecodeSizeError(ChannelHandlerContext ctx, Http2Stream stream) {
        this.encoder().writeHeaders(ctx, stream.id(), HEADERS_TOO_LARGE_HEADERS, 0, true, ctx.newPromise());
    }

    protected Http2FrameWriter frameWriter() {
        return this.encoder().frameWriter();
    }

    private ChannelFuture resetUnknownStream(final ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise) {
        ChannelFuture future = this.frameWriter().writeRstStream(ctx, streamId, errorCode, promise);
        if (future.isDone()) {
            this.closeConnectionOnError(ctx, future);
        } else {
            future.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Http2ConnectionHandler.this.closeConnectionOnError(ctx, future);
                }
            });
        }
        return future;
    }

    @Override
    public ChannelFuture resetStream(ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise) {
        Http2Stream stream = this.connection().stream(streamId);
        if (stream == null) {
            return this.resetUnknownStream(ctx, streamId, errorCode, promise.unvoid());
        }
        return this.resetStream(ctx, stream, errorCode, promise);
    }

    private ChannelFuture resetStream(final ChannelHandlerContext ctx, final Http2Stream stream, long errorCode, ChannelPromise promise) {
        promise = promise.unvoid();
        if (stream.isResetSent()) {
            return promise.setSuccess();
        }
        ChannelFuture future = stream.state() == Http2Stream.State.IDLE || this.connection().local().created(stream) && !stream.isHeadersSent() && !stream.isPushPromiseSent() ? promise.setSuccess() : this.frameWriter().writeRstStream(ctx, stream.id(), errorCode, promise);
        stream.resetSent();
        if (future.isDone()) {
            this.processRstStreamWriteResult(ctx, stream, future);
        } else {
            future.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Http2ConnectionHandler.this.processRstStreamWriteResult(ctx, stream, future);
                }
            });
        }
        return future;
    }

    @Override
    public ChannelFuture goAway(final ChannelHandlerContext ctx, final int lastStreamId, final long errorCode, final ByteBuf debugData, ChannelPromise promise) {
        try {
            promise = promise.unvoid();
            Http2Connection connection = this.connection();
            if (this.connection().goAwaySent()) {
                if (lastStreamId == this.connection().remote().lastStreamKnownByPeer()) {
                    debugData.release();
                    return promise.setSuccess();
                }
                if (lastStreamId > connection.remote().lastStreamKnownByPeer()) {
                    throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Last stream identifier must not increase between sending multiple GOAWAY frames (was '%d', is '%d').", connection.remote().lastStreamKnownByPeer(), lastStreamId);
                }
            }
            connection.goAwaySent(lastStreamId, errorCode, debugData);
            debugData.retain();
            ChannelFuture future = this.frameWriter().writeGoAway(ctx, lastStreamId, errorCode, debugData, promise);
            if (future.isDone()) {
                Http2ConnectionHandler.processGoAwayWriteResult(ctx, lastStreamId, errorCode, debugData, future);
            } else {
                future.addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        Http2ConnectionHandler.processGoAwayWriteResult(ctx, lastStreamId, errorCode, debugData, future);
                    }
                });
            }
            return future;
        }
        catch (Throwable cause) {
            debugData.release();
            return promise.setFailure(cause);
        }
    }

    private void checkCloseConnection(ChannelFuture future) {
        if (this.closeListener != null && this.isGracefulShutdownComplete()) {
            ChannelFutureListener closeListener = this.closeListener;
            this.closeListener = null;
            try {
                closeListener.operationComplete(future);
            }
            catch (Exception e) {
                throw new IllegalStateException("Close listener threw an unexpected exception", e);
            }
        }
    }

    private ChannelFuture goAway(ChannelHandlerContext ctx, Http2Exception cause) {
        long errorCode = cause != null ? cause.error().code() : Http2Error.NO_ERROR.code();
        int lastKnownStream = this.connection().remote().lastStreamCreated();
        return this.goAway(ctx, lastKnownStream, errorCode, Http2CodecUtil.toByteBuf(ctx, cause), ctx.newPromise());
    }

    private void processRstStreamWriteResult(ChannelHandlerContext ctx, Http2Stream stream, ChannelFuture future) {
        if (future.isSuccess()) {
            this.closeStream(stream, future);
        } else {
            this.onConnectionError(ctx, future.cause(), null);
        }
    }

    private void closeConnectionOnError(ChannelHandlerContext ctx, ChannelFuture future) {
        if (!future.isSuccess()) {
            this.onConnectionError(ctx, future.cause(), null);
        }
    }

    private static ByteBuf clientPrefaceString(Http2Connection connection) {
        return connection.isServer() ? Http2CodecUtil.connectionPrefaceBuf() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processGoAwayWriteResult(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData, ChannelFuture future) {
        try {
            if (future.isSuccess()) {
                if (errorCode != Http2Error.NO_ERROR.code()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("{} Sent GOAWAY: lastStreamId '{}', errorCode '{}', debugData '{}'. Forcing shutdown of the connection.", ctx.channel(), lastStreamId, errorCode, debugData.toString(CharsetUtil.UTF_8), future.cause());
                    }
                    ctx.close();
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("{} Sending GOAWAY failed: lastStreamId '{}', errorCode '{}', debugData '{}'. Forcing shutdown of the connection.", ctx.channel(), lastStreamId, errorCode, debugData.toString(CharsetUtil.UTF_8), future.cause());
                }
                ctx.close();
            }
        }
        finally {
            debugData.release();
        }
    }

    private static final class ClosingChannelFutureListener
    implements ChannelFutureListener {
        private final ChannelHandlerContext ctx;
        private final ChannelPromise promise;
        private final ScheduledFuture<?> timeoutTask;

        ClosingChannelFutureListener(ChannelHandlerContext ctx, ChannelPromise promise) {
            this.ctx = ctx;
            this.promise = promise;
            this.timeoutTask = null;
        }

        ClosingChannelFutureListener(final ChannelHandlerContext ctx, final ChannelPromise promise, long timeout, TimeUnit unit) {
            this.ctx = ctx;
            this.promise = promise;
            this.timeoutTask = ctx.executor().schedule(new Runnable(){

                @Override
                public void run() {
                    ctx.close(promise);
                }
            }, timeout, unit);
        }

        @Override
        public void operationComplete(ChannelFuture sentGoAwayFuture) throws Exception {
            if (this.timeoutTask != null) {
                this.timeoutTask.cancel(false);
            }
            this.ctx.close(this.promise);
        }

    }

    private final class FrameDecoder
    extends BaseDecoder {
        private FrameDecoder() {
            super();
        }

        @Override
        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            try {
                Http2ConnectionHandler.this.decoder.decodeFrame(ctx, in, out);
            }
            catch (Throwable e) {
                Http2ConnectionHandler.this.onError(ctx, e);
            }
        }
    }

    private final class PrefaceDecoder
    extends BaseDecoder {
        private ByteBuf clientPrefaceString;
        private boolean prefaceSent;

        public PrefaceDecoder(ChannelHandlerContext ctx) throws Exception {
            super();
            this.clientPrefaceString = Http2ConnectionHandler.clientPrefaceString(Http2ConnectionHandler.this.encoder.connection());
            this.sendPreface(ctx);
        }

        @Override
        public boolean prefaceSent() {
            return this.prefaceSent;
        }

        @Override
        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            try {
                if (ctx.channel().isActive() && this.readClientPrefaceString(in) && this.verifyFirstFrameIsSettings(in)) {
                    Http2ConnectionHandler.this.byteDecoder = new FrameDecoder();
                    Http2ConnectionHandler.this.byteDecoder.decode(ctx, in, out);
                }
            }
            catch (Throwable e) {
                Http2ConnectionHandler.this.onError(ctx, e);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            this.sendPreface(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            this.cleanup();
            super.channelInactive(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            this.cleanup();
        }

        private void cleanup() {
            if (this.clientPrefaceString != null) {
                this.clientPrefaceString.release();
                this.clientPrefaceString = null;
            }
        }

        private boolean readClientPrefaceString(ByteBuf in) throws Http2Exception {
            if (this.clientPrefaceString == null) {
                return true;
            }
            int prefaceRemaining = this.clientPrefaceString.readableBytes();
            int bytesRead = Math.min(in.readableBytes(), prefaceRemaining);
            if (bytesRead == 0 || !ByteBufUtil.equals(in, in.readerIndex(), this.clientPrefaceString, this.clientPrefaceString.readerIndex(), bytesRead)) {
                int maxSearch = 1024;
                int http1Index = ByteBufUtil.indexOf(HTTP_1_X_BUF, in.slice(in.readerIndex(), Math.min(in.readableBytes(), maxSearch)));
                if (http1Index != -1) {
                    String chunk = in.toString(in.readerIndex(), http1Index - in.readerIndex(), CharsetUtil.US_ASCII);
                    throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Unexpected HTTP/1.x request: %s", chunk);
                }
                String receivedBytes = ByteBufUtil.hexDump(in, in.readerIndex(), Math.min(in.readableBytes(), this.clientPrefaceString.readableBytes()));
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP/2 client preface string missing or corrupt. Hex dump for received bytes: %s", receivedBytes);
            }
            in.skipBytes(bytesRead);
            this.clientPrefaceString.skipBytes(bytesRead);
            if (!this.clientPrefaceString.isReadable()) {
                this.clientPrefaceString.release();
                this.clientPrefaceString = null;
                return true;
            }
            return false;
        }

        private boolean verifyFirstFrameIsSettings(ByteBuf in) throws Http2Exception {
            if (in.readableBytes() < 5) {
                return false;
            }
            short frameType = in.getUnsignedByte(in.readerIndex() + 3);
            short flags = in.getUnsignedByte(in.readerIndex() + 4);
            if (frameType != 4 || (flags & 1) != 0) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "First received frame was not SETTINGS. Hex dump for first 5 bytes: %s", ByteBufUtil.hexDump(in, in.readerIndex(), 5));
            }
            return true;
        }

        private void sendPreface(ChannelHandlerContext ctx) throws Exception {
            boolean isClient;
            if (this.prefaceSent || !ctx.channel().isActive()) {
                return;
            }
            this.prefaceSent = true;
            boolean bl = isClient = !Http2ConnectionHandler.this.connection().isServer();
            if (isClient) {
                ctx.write(Http2CodecUtil.connectionPrefaceBuf()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
            Http2ConnectionHandler.this.encoder.writeSettings(ctx, Http2ConnectionHandler.this.initialSettings, ctx.newPromise()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            if (isClient) {
                Http2ConnectionHandler.this.userEventTriggered(ctx, Http2ConnectionPrefaceAndSettingsFrameWrittenEvent.INSTANCE);
            }
        }
    }

    private abstract class BaseDecoder {
        private BaseDecoder() {
        }

        public abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        }

        public void channelActive(ChannelHandlerContext ctx) throws Exception {
        }

        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Http2ConnectionHandler.this.encoder().close();
            Http2ConnectionHandler.this.decoder().close();
            Http2ConnectionHandler.this.connection().close(ctx.voidPromise());
        }

        public boolean prefaceSent() {
            return true;
        }
    }

}

