/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.ssl.ApplicationProtocolAccessor;
import io.netty.handler.ssl.ConscryptAlpnSslEngine;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

public class SslHandler
extends ByteToMessageDecoder
implements ChannelOutboundHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
    private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
    private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
    private static final SSLException SSLENGINE_CLOSED = ThrowableUtil.unknownStackTrace(new SSLException("SSLEngine closed already"), SslHandler.class, "wrap(...)");
    private static final SSLException HANDSHAKE_TIMED_OUT = ThrowableUtil.unknownStackTrace(new SSLException("handshake timed out"), SslHandler.class, "handshake(...)");
    private static final ClosedChannelException CHANNEL_CLOSED = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), SslHandler.class, "channelInactive(...)");
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private volatile ChannelHandlerContext ctx;
    private final SSLEngine engine;
    private final SslEngineType engineType;
    private final Executor delegatedTaskExecutor;
    private final boolean jdkCompatibilityMode;
    private final ByteBuffer[] singleBuffer = new ByteBuffer[1];
    private final boolean startTls;
    private boolean sentFirstMessage;
    private boolean flushedBeforeHandshake;
    private boolean readDuringHandshake;
    private boolean handshakeStarted;
    private SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
    private Promise<Channel> handshakePromise = new LazyChannelPromise();
    private final LazyChannelPromise sslClosePromise = new LazyChannelPromise();
    private boolean needsFlush;
    private boolean outboundClosed;
    private int packetLength;
    private boolean firedChannelRead;
    private volatile long handshakeTimeoutMillis = 10000L;
    private volatile long closeNotifyFlushTimeoutMillis = 3000L;
    private volatile long closeNotifyReadTimeoutMillis;
    volatile int wrapDataSize = 16384;

    public SslHandler(SSLEngine engine) {
        this(engine, false);
    }

    public SslHandler(SSLEngine engine, boolean startTls) {
        this(engine, startTls, ImmediateExecutor.INSTANCE);
    }

    @Deprecated
    public SslHandler(SSLEngine engine, Executor delegatedTaskExecutor) {
        this(engine, false, delegatedTaskExecutor);
    }

    @Deprecated
    public SslHandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor) {
        if (engine == null) {
            throw new NullPointerException("engine");
        }
        if (delegatedTaskExecutor == null) {
            throw new NullPointerException("delegatedTaskExecutor");
        }
        this.engine = engine;
        this.engineType = SslEngineType.forEngine(engine);
        this.delegatedTaskExecutor = delegatedTaskExecutor;
        this.startTls = startTls;
        this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode(engine);
        this.setCumulator(this.engineType.cumulator);
    }

    public long getHandshakeTimeoutMillis() {
        return this.handshakeTimeoutMillis;
    }

    public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        this.setHandshakeTimeoutMillis(unit.toMillis(handshakeTimeout));
    }

    public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis) {
        if (handshakeTimeoutMillis < 0L) {
            throw new IllegalArgumentException("handshakeTimeoutMillis: " + handshakeTimeoutMillis + " (expected: >= 0)");
        }
        this.handshakeTimeoutMillis = handshakeTimeoutMillis;
    }

    public final void setWrapDataSize(int wrapDataSize) {
        this.wrapDataSize = wrapDataSize;
    }

    @Deprecated
    public long getCloseNotifyTimeoutMillis() {
        return this.getCloseNotifyFlushTimeoutMillis();
    }

    @Deprecated
    public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) {
        this.setCloseNotifyFlushTimeout(closeNotifyTimeout, unit);
    }

    @Deprecated
    public void setCloseNotifyTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
        this.setCloseNotifyFlushTimeoutMillis(closeNotifyFlushTimeoutMillis);
    }

    public final long getCloseNotifyFlushTimeoutMillis() {
        return this.closeNotifyFlushTimeoutMillis;
    }

    public final void setCloseNotifyFlushTimeout(long closeNotifyFlushTimeout, TimeUnit unit) {
        this.setCloseNotifyFlushTimeoutMillis(unit.toMillis(closeNotifyFlushTimeout));
    }

    public final void setCloseNotifyFlushTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
        if (closeNotifyFlushTimeoutMillis < 0L) {
            throw new IllegalArgumentException("closeNotifyFlushTimeoutMillis: " + closeNotifyFlushTimeoutMillis + " (expected: >= 0)");
        }
        this.closeNotifyFlushTimeoutMillis = closeNotifyFlushTimeoutMillis;
    }

    public final long getCloseNotifyReadTimeoutMillis() {
        return this.closeNotifyReadTimeoutMillis;
    }

    public final void setCloseNotifyReadTimeout(long closeNotifyReadTimeout, TimeUnit unit) {
        this.setCloseNotifyReadTimeoutMillis(unit.toMillis(closeNotifyReadTimeout));
    }

    public final void setCloseNotifyReadTimeoutMillis(long closeNotifyReadTimeoutMillis) {
        if (closeNotifyReadTimeoutMillis < 0L) {
            throw new IllegalArgumentException("closeNotifyReadTimeoutMillis: " + closeNotifyReadTimeoutMillis + " (expected: >= 0)");
        }
        this.closeNotifyReadTimeoutMillis = closeNotifyReadTimeoutMillis;
    }

    public SSLEngine engine() {
        return this.engine;
    }

    public String applicationProtocol() {
        SSLEngine engine = this.engine();
        if (!(engine instanceof ApplicationProtocolAccessor)) {
            return null;
        }
        return ((ApplicationProtocolAccessor)((Object)engine)).getNegotiatedApplicationProtocol();
    }

    public Future<Channel> handshakeFuture() {
        return this.handshakePromise;
    }

    @Deprecated
    public ChannelFuture close() {
        return this.close(this.ctx.newPromise());
    }

    @Deprecated
    public ChannelFuture close(final ChannelPromise promise) {
        final ChannelHandlerContext ctx = this.ctx;
        ctx.executor().execute(new Runnable(){

            @Override
            public void run() {
                block2 : {
                    SslHandler.this.outboundClosed = true;
                    SslHandler.this.engine.closeOutbound();
                    try {
                        SslHandler.this.flush(ctx, promise);
                    }
                    catch (Exception e) {
                        if (promise.tryFailure(e)) break block2;
                        logger.warn("{} flush() raised a masked exception.", (Object)ctx.channel(), (Object)e);
                    }
                }
            }
        });
        return promise;
    }

    public Future<Channel> sslCloseFuture() {
        return this.sslClosePromise;
    }

    @Override
    public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        if (!this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.releaseAndFailAll(ctx, new ChannelException("Pending write on removal of SslHandler"));
        }
        this.pendingUnencryptedWrites = null;
        if (this.engine instanceof ReferenceCounted) {
            ((ReferenceCounted)((Object)this.engine)).release();
        }
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
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel(ctx, promise, true);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel(ctx, promise, false);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (!this.handshakePromise.isDone()) {
            this.readDuringHandshake = true;
        }
        ctx.read();
    }

    private static IllegalStateException newPendingWritesNullException() {
        return new IllegalStateException("pendingUnencryptedWrites is null, handlerRemoved0 called?");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(msg, ByteBuf.class);
            ReferenceCountUtil.safeRelease(msg);
            promise.setFailure(exception);
        } else if (this.pendingUnencryptedWrites == null) {
            ReferenceCountUtil.safeRelease(msg);
            promise.setFailure(SslHandler.newPendingWritesNullException());
        } else {
            this.pendingUnencryptedWrites.add((ByteBuf)msg, promise);
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.startTls && !this.sentFirstMessage) {
            this.sentFirstMessage = true;
            this.pendingUnencryptedWrites.writeAndRemoveAll(ctx);
            this.forceFlush(ctx);
            return;
        }
        try {
            this.wrapAndFlush(ctx);
        }
        catch (Throwable cause) {
            this.setHandshakeFailure(ctx, cause);
            PlatformDependent.throwException(cause);
        }
    }

    private void wrapAndFlush(ChannelHandlerContext ctx) throws SSLException {
        if (this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, ctx.newPromise());
        }
        if (!this.handshakePromise.isDone()) {
            this.flushedBeforeHandshake = true;
        }
        try {
            this.wrap(ctx, false);
        }
        finally {
            this.forceFlush(ctx);
        }
    }

    /*
     * Exception decompiling
     */
    private void wrap(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 8[CASE]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:416)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:468)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2960)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:818)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:196)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:141)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:95)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:372)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:867)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:768)
        // org.benf.cfr.reader.Main.doJar(Main.java:141)
        // org.benf.cfr.reader.Main.main(Main.java:242)
        throw new IllegalStateException("Decompilation failed");
    }

    private void finishWrap(ChannelHandlerContext ctx, ByteBuf out, ChannelPromise promise, boolean inUnwrap, boolean needUnwrap) {
        if (out == null) {
            out = Unpooled.EMPTY_BUFFER;
        } else if (!out.isReadable()) {
            out.release();
            out = Unpooled.EMPTY_BUFFER;
        }
        if (promise != null) {
            ctx.write(out, promise);
        } else {
            ctx.write(out);
        }
        if (inUnwrap) {
            this.needsFlush = true;
        }
        if (needUnwrap) {
            this.readIfNeeded(ctx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private boolean wrapNonAppData(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
        out = null;
        alloc = ctx.alloc();
        try {
            do {
                if (ctx.isRemoved() != false) return false;
                if (out == null) {
                    out = this.allocateOutNetBuf(ctx, 2048, 1);
                }
                if ((result = this.wrap(alloc, this.engine, Unpooled.EMPTY_BUFFER, (ByteBuf)out)).bytesProduced() > 0) {
                    ctx.write(out);
                    if (inUnwrap) {
                        this.needsFlush = true;
                    }
                    out = null;
                }
                switch (.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[result.getHandshakeStatus().ordinal()]) {
                    case 2: {
                        this.setHandshakeSuccess();
                        var6_6 = false;
                        return var6_6;
                    }
                    case 1: {
                        this.runDelegatedTasks();
                        ** break;
                    }
                    case 5: {
                        if (inUnwrap) {
                            var6_7 = false;
                            return var6_7;
                        }
                        this.unwrapNonAppData(ctx);
                        ** break;
                    }
                    case 4: {
                        ** break;
                    }
                    case 3: {
                        this.setHandshakeSuccessIfStillHandshaking();
                        if (!inUnwrap) {
                            this.unwrapNonAppData(ctx);
                        }
                        var6_8 = true;
                        return var6_8;
                    }
                }
                throw new IllegalStateException("Unknown handshake status: " + (Object)result.getHandshakeStatus());
lbl36: // 3 sources:
                if (result.bytesProduced() != 0) continue;
                return false;
            } while (result.bytesConsumed() != 0 || result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING);
            return false;
        }
        finally {
            if (out != null) {
                out.release();
            }
        }
    }

    private SSLEngineResult wrap(ByteBufAllocator alloc, SSLEngine engine, ByteBuf in, ByteBuf out) throws SSLException {
        ReferenceCounted newDirectIn = null;
        try {
            ByteBuffer[] in0;
            int readerIndex = in.readerIndex();
            int readableBytes = in.readableBytes();
            if (in.isDirect() || !this.engineType.wantsDirectBuffer) {
                if (!(in instanceof CompositeByteBuf) && in.nioBufferCount() == 1) {
                    in0 = this.singleBuffer;
                    in0[0] = in.internalNioBuffer(readerIndex, readableBytes);
                } else {
                    in0 = in.nioBuffers();
                }
            } else {
                newDirectIn = alloc.directBuffer(readableBytes);
                newDirectIn.writeBytes(in, readerIndex, readableBytes);
                in0 = this.singleBuffer;
                in0[0] = newDirectIn.internalNioBuffer(newDirectIn.readerIndex(), readableBytes);
            }
            do {
                ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
                SSLEngineResult result = engine.wrap(in0, out0);
                in.skipBytes(result.bytesConsumed());
                out.writerIndex(out.writerIndex() + result.bytesProduced());
                switch (result.getStatus()) {
                    case BUFFER_OVERFLOW: {
                        out.ensureWritable(engine.getSession().getPacketBufferSize());
                        break;
                    }
                    default: {
                        SSLEngineResult sSLEngineResult = result;
                        return sSLEngineResult;
                    }
                }
            } while (true);
        }
        finally {
            this.singleBuffer[0] = null;
            if (newDirectIn != null) {
                newDirectIn.release();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.setHandshakeFailure(ctx, CHANNEL_CLOSED, !this.outboundClosed, this.handshakeStarted);
        this.notifyClosePromise(CHANNEL_CLOSED);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.ignoreException(cause)) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", (Object)ctx.channel(), (Object)cause);
            }
            if (ctx.channel().isActive()) {
                ctx.close();
            }
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }

    private boolean ignoreException(Throwable t) {
        if (!(t instanceof SSLException) && t instanceof IOException && this.sslClosePromise.isDone()) {
            StackTraceElement[] elements;
            String message = t.getMessage();
            if (message != null && IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
                return true;
            }
            for (StackTraceElement element : elements = t.getStackTrace()) {
                String classname = element.getClassName();
                String methodname = element.getMethodName();
                if (classname.startsWith("io.netty.") || !"read".equals(methodname)) continue;
                if (IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
                    return true;
                }
                try {
                    Class<?> clazz = PlatformDependent.getClassLoader(this.getClass()).loadClass(classname);
                    if (SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class.isAssignableFrom(clazz)) {
                        return true;
                    }
                    if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(clazz.getSuperclass().getName())) {
                        return true;
                    }
                }
                catch (Throwable cause) {
                    logger.debug("Unexpected exception while loading class {} classname {}", this.getClass(), classname, cause);
                }
            }
        }
        return false;
    }

    public static boolean isEncrypted(ByteBuf buffer) {
        if (buffer.readableBytes() < 5) {
            throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
        }
        return SslUtils.getEncryptedPacketLength(buffer, buffer.readerIndex()) != -2;
    }

    private void decodeJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) throws NotSslRecordException {
        int packetLength = this.packetLength;
        if (packetLength > 0) {
            if (in.readableBytes() < packetLength) {
                return;
            }
        } else {
            int readableBytes = in.readableBytes();
            if (readableBytes < 5) {
                return;
            }
            packetLength = SslUtils.getEncryptedPacketLength(in, in.readerIndex());
            if (packetLength == -2) {
                NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
                in.skipBytes(in.readableBytes());
                this.setHandshakeFailure(ctx, e);
                throw e;
            }
            assert (packetLength > 0);
            if (packetLength > readableBytes) {
                this.packetLength = packetLength;
                return;
            }
        }
        this.packetLength = 0;
        try {
            int bytesConsumed = this.unwrap(ctx, in, in.readerIndex(), packetLength);
            assert (bytesConsumed == packetLength || this.engine.isInboundDone());
            in.skipBytes(bytesConsumed);
        }
        catch (Throwable cause) {
            this.handleUnwrapThrowable(ctx, cause);
        }
    }

    private void decodeNonJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) {
        try {
            in.skipBytes(this.unwrap(ctx, in, in.readerIndex(), in.readableBytes()));
        }
        catch (Throwable cause) {
            this.handleUnwrapThrowable(ctx, cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleUnwrapThrowable(ChannelHandlerContext ctx, Throwable cause) {
        try {
            this.wrapAndFlush(ctx);
        }
        catch (SSLException ex) {
            logger.debug("SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", ex);
        }
        finally {
            this.setHandshakeFailure(ctx, cause);
        }
        PlatformDependent.throwException(cause);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws SSLException {
        if (this.jdkCompatibilityMode) {
            this.decodeJdkCompatible(ctx, in);
        } else {
            this.decodeNonJdkCompatible(ctx, in);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.discardSomeReadBytes();
        this.flushIfNeeded(ctx);
        this.readIfNeeded(ctx);
        this.firedChannelRead = false;
        ctx.fireChannelReadComplete();
    }

    private void readIfNeeded(ChannelHandlerContext ctx) {
        if (!(ctx.channel().config().isAutoRead() || this.firedChannelRead && this.handshakePromise.isDone())) {
            ctx.read();
        }
    }

    private void flushIfNeeded(ChannelHandlerContext ctx) {
        if (this.needsFlush) {
            this.forceFlush(ctx);
        }
    }

    private void unwrapNonAppData(ChannelHandlerContext ctx) throws SSLException {
        this.unwrap(ctx, Unpooled.EMPTY_BUFFER, 0, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int unwrap(ChannelHandlerContext ctx, ByteBuf packet, int offset, int length) throws SSLException {
        int originalLength;
        originalLength = length;
        boolean wrapLater = false;
        boolean notifyClosure = false;
        int overflowReadableBytes = -1;
        ByteBuf decodeOut = this.allocate(ctx, length);
        try {
            block14 : while (!ctx.isRemoved()) {
                SSLEngineResult result = this.engineType.unwrap(this, packet, offset, length, decodeOut);
                SSLEngineResult.Status status = result.getStatus();
                SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                int produced = result.bytesProduced();
                int consumed = result.bytesConsumed();
                offset += consumed;
                length -= consumed;
                switch (status) {
                    case BUFFER_OVERFLOW: {
                        int readableBytes = decodeOut.readableBytes();
                        int previousOverflowReadableBytes = overflowReadableBytes;
                        overflowReadableBytes = readableBytes;
                        int bufferSize = this.engine.getSession().getApplicationBufferSize() - readableBytes;
                        if (readableBytes > 0) {
                            this.firedChannelRead = true;
                            ctx.fireChannelRead(decodeOut);
                            decodeOut = null;
                            if (bufferSize <= 0) {
                                bufferSize = this.engine.getSession().getApplicationBufferSize();
                            }
                        } else {
                            decodeOut.release();
                            decodeOut = null;
                        }
                        if (readableBytes == 0 && previousOverflowReadableBytes == 0) {
                            throw new IllegalStateException("Two consecutive overflows but no content was consumed. " + SSLSession.class.getSimpleName() + " getApplicationBufferSize: " + this.engine.getSession().getApplicationBufferSize() + " maybe too small.");
                        }
                        decodeOut = this.allocate(ctx, this.engineType.calculatePendingData(this, bufferSize));
                        continue block14;
                    }
                    case CLOSED: {
                        notifyClosure = true;
                        overflowReadableBytes = -1;
                        break;
                    }
                    default: {
                        overflowReadableBytes = -1;
                    }
                }
                switch (handshakeStatus) {
                    case NEED_UNWRAP: {
                        break;
                    }
                    case NEED_WRAP: {
                        if (!this.wrapNonAppData(ctx, true) || length != 0) break;
                        break block14;
                    }
                    case NEED_TASK: {
                        this.runDelegatedTasks();
                        break;
                    }
                    case FINISHED: {
                        this.setHandshakeSuccess();
                        wrapLater = true;
                        break;
                    }
                    case NOT_HANDSHAKING: {
                        if (this.setHandshakeSuccessIfStillHandshaking()) {
                            wrapLater = true;
                            continue block14;
                        }
                        if (this.flushedBeforeHandshake) {
                            this.flushedBeforeHandshake = false;
                            wrapLater = true;
                        }
                        if (length != 0) break;
                        break block14;
                    }
                    default: {
                        throw new IllegalStateException("unknown handshake status: " + (Object)((Object)handshakeStatus));
                    }
                }
                if (status != SSLEngineResult.Status.BUFFER_UNDERFLOW && (consumed != 0 || produced != 0)) continue;
                if (handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP) break;
                this.readIfNeeded(ctx);
                break;
            }
            if (wrapLater) {
                this.wrap(ctx, true);
            }
            if (notifyClosure) {
                this.notifyClosePromise(null);
            }
        }
        finally {
            if (decodeOut != null) {
                if (decodeOut.isReadable()) {
                    this.firedChannelRead = true;
                    ctx.fireChannelRead(decodeOut);
                } else {
                    decodeOut.release();
                }
            }
        }
        return originalLength - length;
    }

    private static ByteBuffer toByteBuffer(ByteBuf out, int index, int len) {
        return out.nioBufferCount() == 1 ? out.internalNioBuffer(index, len) : out.nioBuffer(index, len);
    }

    private void runDelegatedTasks() {
        if (this.delegatedTaskExecutor == ImmediateExecutor.INSTANCE) {
            Runnable task;
            while ((task = this.engine.getDelegatedTask()) != null) {
                task.run();
            }
        } else {
            Runnable task;
            final ArrayList<Runnable> tasks = new ArrayList<Runnable>(2);
            while ((task = this.engine.getDelegatedTask()) != null) {
                tasks.add(task);
            }
            if (tasks.isEmpty()) {
                return;
            }
            final CountDownLatch latch = new CountDownLatch(1);
            this.delegatedTaskExecutor.execute(new Runnable(){

                @Override
                public void run() {
                    try {
                        for (Runnable task : tasks) {
                            task.run();
                        }
                    }
                    catch (Exception e) {
                        SslHandler.this.ctx.fireExceptionCaught(e);
                    }
                    finally {
                        latch.countDown();
                    }
                }
            });
            boolean interrupted = false;
            while (latch.getCount() != 0L) {
                try {
                    latch.await();
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean setHandshakeSuccessIfStillHandshaking() {
        if (!this.handshakePromise.isDone()) {
            this.setHandshakeSuccess();
            return true;
        }
        return false;
    }

    private void setHandshakeSuccess() {
        this.handshakePromise.trySuccess(this.ctx.channel());
        if (logger.isDebugEnabled()) {
            logger.debug("{} HANDSHAKEN: {}", (Object)this.ctx.channel(), (Object)this.engine.getSession().getCipherSuite());
        }
        this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
        if (this.readDuringHandshake && !this.ctx.channel().config().isAutoRead()) {
            this.readDuringHandshake = false;
            this.ctx.read();
        }
    }

    private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause) {
        this.setHandshakeFailure(ctx, cause, true, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean closeInbound, boolean notify) {
        try {
            block6 : {
                this.engine.closeOutbound();
                if (closeInbound) {
                    try {
                        this.engine.closeInbound();
                    }
                    catch (SSLException e) {
                        String msg;
                        if (!logger.isDebugEnabled() || (msg = e.getMessage()) != null && msg.contains("possible truncation attack")) break block6;
                        logger.debug("{} SSLEngine.closeInbound() raised an exception.", (Object)ctx.channel(), (Object)e);
                    }
                }
            }
            this.notifyHandshakeFailure(cause, notify);
        }
        finally {
            this.releaseAndFailAll(cause);
        }
    }

    private void releaseAndFailAll(Throwable cause) {
        if (this.pendingUnencryptedWrites != null) {
            this.pendingUnencryptedWrites.releaseAndFailAll(this.ctx, cause);
        }
    }

    private void notifyHandshakeFailure(Throwable cause, boolean notify) {
        if (this.handshakePromise.tryFailure(cause)) {
            SslUtils.notifyHandshakeFailure(this.ctx, cause, notify);
        }
    }

    private void notifyClosePromise(Throwable cause) {
        if (cause == null) {
            if (this.sslClosePromise.trySuccess(this.ctx.channel())) {
                this.ctx.fireUserEventTriggered(SslCloseCompletionEvent.SUCCESS);
            }
        } else if (this.sslClosePromise.tryFailure(cause)) {
            this.ctx.fireUserEventTriggered(new SslCloseCompletionEvent(cause));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void closeOutboundAndChannel(ChannelHandlerContext ctx, ChannelPromise promise, boolean disconnect) throws Exception {
        if (!ctx.channel().isActive()) {
            if (disconnect) {
                ctx.disconnect(promise);
            } else {
                ctx.close(promise);
            }
            return;
        }
        this.outboundClosed = true;
        this.engine.closeOutbound();
        ChannelPromise closeNotifyPromise = ctx.newPromise();
        try {}
        catch (Throwable throwable) {
            this.safeClose(ctx, closeNotifyPromise, ctx.newPromise().addListener(new ChannelPromiseNotifier(false, promise)));
            throw throwable;
        }
        this.flush(ctx, closeNotifyPromise);
        this.safeClose(ctx, closeNotifyPromise, ctx.newPromise().addListener(new ChannelPromiseNotifier(false, promise)));
    }

    private void flush(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.pendingUnencryptedWrites != null) {
            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, promise);
        } else {
            promise.setFailure(SslHandler.newPendingWritesNullException());
        }
        this.flush(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.pendingUnencryptedWrites = new SslHandlerCoalescingBufferQueue(ctx.channel(), 16);
        if (ctx.channel().isActive()) {
            this.startHandshakeProcessing();
        }
    }

    private void startHandshakeProcessing() {
        this.handshakeStarted = true;
        if (this.engine.getUseClientMode()) {
            this.handshake(null);
        } else {
            this.applyHandshakeTimeout(null);
        }
    }

    public Future<Channel> renegotiate() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException();
        }
        return this.renegotiate(ctx.executor().newPromise());
    }

    public Future<Channel> renegotiate(final Promise<Channel> promise) {
        if (promise == null) {
            throw new NullPointerException("promise");
        }
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException();
        }
        EventExecutor executor = ctx.executor();
        if (!executor.inEventLoop()) {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    SslHandler.this.handshake(promise);
                }
            });
            return promise;
        }
        this.handshake(promise);
        return promise;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handshake(final Promise<Channel> newHandshakePromise) {
        Promise<Channel> p;
        if (newHandshakePromise != null) {
            Promise<Channel> oldHandshakePromise = this.handshakePromise;
            if (!oldHandshakePromise.isDone()) {
                oldHandshakePromise.addListener((GenericFutureListener<Future<Channel>>)new FutureListener<Channel>(){

                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        if (future.isSuccess()) {
                            newHandshakePromise.setSuccess(future.getNow());
                        } else {
                            newHandshakePromise.setFailure(future.cause());
                        }
                    }
                });
                return;
            }
            this.handshakePromise = p = newHandshakePromise;
        } else {
            if (this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                return;
            }
            p = this.handshakePromise;
            assert (!p.isDone());
        }
        ChannelHandlerContext ctx = this.ctx;
        try {
            this.engine.beginHandshake();
            this.wrapNonAppData(ctx, false);
        }
        catch (Throwable e) {
            this.setHandshakeFailure(ctx, e);
        }
        finally {
            this.forceFlush(ctx);
        }
        this.applyHandshakeTimeout(p);
    }

    private void applyHandshakeTimeout(Promise<Channel> p) {
        final Promise<Channel> promise = p == null ? this.handshakePromise : p;
        long handshakeTimeoutMillis = this.handshakeTimeoutMillis;
        if (handshakeTimeoutMillis <= 0L || promise.isDone()) {
            return;
        }
        final io.netty.util.concurrent.ScheduledFuture<?> timeoutFuture = this.ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (promise.isDone()) {
                    return;
                }
                try {
                    SslHandler.this.notifyHandshakeFailure(HANDSHAKE_TIMED_OUT, true);
                }
                finally {
                    SslHandler.this.releaseAndFailAll(HANDSHAKE_TIMED_OUT);
                }
            }
        }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        promise.addListener((GenericFutureListener<Future<Channel>>)new FutureListener<Channel>(){

            @Override
            public void operationComplete(Future<Channel> f) throws Exception {
                timeoutFuture.cancel(false);
            }
        });
    }

    private void forceFlush(ChannelHandlerContext ctx) {
        this.needsFlush = false;
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.startTls) {
            this.startHandshakeProcessing();
        }
        ctx.fireChannelActive();
    }

    private void safeClose(final ChannelHandlerContext ctx, final ChannelFuture flushFuture, final ChannelPromise promise) {
        long closeNotifyTimeout;
        if (!ctx.channel().isActive()) {
            ctx.close(promise);
            return;
        }
        final io.netty.util.concurrent.ScheduledFuture<?> timeoutFuture = !flushFuture.isDone() ? ((closeNotifyTimeout = this.closeNotifyFlushTimeoutMillis) > 0L ? ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (!flushFuture.isDone()) {
                    logger.warn("{} Last write attempt timed out; force-closing the connection.", (Object)ctx.channel());
                    SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                }
            }
        }, closeNotifyTimeout, TimeUnit.MILLISECONDS) : null) : null;
        flushFuture.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                long closeNotifyReadTimeout;
                if (timeoutFuture != null) {
                    timeoutFuture.cancel(false);
                }
                if ((closeNotifyReadTimeout = SslHandler.this.closeNotifyReadTimeoutMillis) <= 0L) {
                    SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                } else {
                    final io.netty.util.concurrent.ScheduledFuture<?> closeNotifyReadTimeoutFuture = !SslHandler.this.sslClosePromise.isDone() ? ctx.executor().schedule(new Runnable(){

                        @Override
                        public void run() {
                            if (!SslHandler.this.sslClosePromise.isDone()) {
                                logger.debug("{} did not receive close_notify in {}ms; force-closing the connection.", (Object)ctx.channel(), (Object)closeNotifyReadTimeout);
                                SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                            }
                        }
                    }, closeNotifyReadTimeout, TimeUnit.MILLISECONDS) : null;
                    SslHandler.this.sslClosePromise.addListener(new FutureListener<Channel>(){

                        @Override
                        public void operationComplete(Future<Channel> future) throws Exception {
                            if (closeNotifyReadTimeoutFuture != null) {
                                closeNotifyReadTimeoutFuture.cancel(false);
                            }
                            SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                        }
                    });
                }
            }

        });
    }

    private static void addCloseListener(ChannelFuture future, ChannelPromise promise) {
        future.addListener(new ChannelPromiseNotifier(false, promise));
    }

    private ByteBuf allocate(ChannelHandlerContext ctx, int capacity) {
        ByteBufAllocator alloc = ctx.alloc();
        if (this.engineType.wantsDirectBuffer) {
            return alloc.directBuffer(capacity);
        }
        return alloc.buffer(capacity);
    }

    private ByteBuf allocateOutNetBuf(ChannelHandlerContext ctx, int pendingBytes, int numComponents) {
        return this.allocate(ctx, this.engineType.calculateWrapBufferCapacity(this, pendingBytes, numComponents));
    }

    private static boolean attemptCopyToCumulation(ByteBuf cumulation, ByteBuf next, int wrapDataSize) {
        int inReadableBytes = next.readableBytes();
        int cumulationCapacity = cumulation.capacity();
        if (wrapDataSize - cumulation.readableBytes() >= inReadableBytes && (cumulation.isWritable(inReadableBytes) && cumulationCapacity >= wrapDataSize || cumulationCapacity < wrapDataSize && ByteBufUtil.ensureWritableSuccess(cumulation.ensureWritable(inReadableBytes, false)))) {
            cumulation.writeBytes(next);
            next.release();
            return true;
        }
        return false;
    }

    private final class LazyChannelPromise
    extends DefaultPromise<Channel> {
        private LazyChannelPromise() {
        }

        @Override
        protected EventExecutor executor() {
            if (SslHandler.this.ctx == null) {
                throw new IllegalStateException();
            }
            return SslHandler.this.ctx.executor();
        }

        @Override
        protected void checkDeadLock() {
            if (SslHandler.this.ctx == null) {
                return;
            }
            super.checkDeadLock();
        }
    }

    private final class SslHandlerCoalescingBufferQueue
    extends AbstractCoalescingBufferQueue {
        SslHandlerCoalescingBufferQueue(Channel channel, int initSize) {
            super(channel, initSize);
        }

        @Override
        protected ByteBuf compose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
            int wrapDataSize = SslHandler.this.wrapDataSize;
            if (cumulation instanceof CompositeByteBuf) {
                CompositeByteBuf composite = (CompositeByteBuf)cumulation;
                int numComponents = composite.numComponents();
                if (numComponents == 0 || !SslHandler.attemptCopyToCumulation(composite.internalComponent(numComponents - 1), next, wrapDataSize)) {
                    composite.addComponent(true, next);
                }
                return composite;
            }
            return SslHandler.attemptCopyToCumulation(cumulation, next, wrapDataSize) ? cumulation : this.copyAndCompose(alloc, cumulation, next);
        }

        @Override
        protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
            if (first instanceof CompositeByteBuf) {
                CompositeByteBuf composite = (CompositeByteBuf)first;
                first = allocator.directBuffer(composite.readableBytes());
                try {
                    first.writeBytes(composite);
                }
                catch (Throwable cause) {
                    first.release();
                    PlatformDependent.throwException(cause);
                }
                composite.release();
            }
            return first;
        }

        @Override
        protected ByteBuf removeEmptyValue() {
            return null;
        }
    }

    private static enum SslEngineType {
        TCNATIVE(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
                SSLEngineResult result;
                int nioBufferCount = in.nioBufferCount();
                int writerIndex = out.writerIndex();
                if (nioBufferCount > 1) {
                    ReferenceCountedOpenSslEngine opensslEngine = (ReferenceCountedOpenSslEngine)handler.engine;
                    try {
                        SslHandler.access$200((SslHandler)handler)[0] = SslHandler.toByteBuffer(out, writerIndex, out.writableBytes());
                        result = opensslEngine.unwrap(in.nioBuffers(readerIndex, len), handler.singleBuffer);
                    }
                    finally {
                        SslHandler.access$200((SslHandler)handler)[0] = null;
                    }
                } else {
                    result = handler.engine.unwrap(SslHandler.toByteBuffer(in, readerIndex, len), SslHandler.toByteBuffer(out, writerIndex, out.writableBytes()));
                }
                out.writerIndex(writerIndex + result.bytesProduced());
                return result;
            }

            @Override
            int getPacketBufferSize(SslHandler handler) {
                return ((ReferenceCountedOpenSslEngine)handler.engine).maxEncryptedPacketLength0();
            }

            @Override
            int calculateWrapBufferCapacity(SslHandler handler, int pendingBytes, int numComponents) {
                return ((ReferenceCountedOpenSslEngine)handler.engine).calculateMaxLengthForWrap(pendingBytes, numComponents);
            }

            @Override
            int calculatePendingData(SslHandler handler, int guess) {
                int sslPending = ((ReferenceCountedOpenSslEngine)handler.engine).sslPending();
                return sslPending > 0 ? sslPending : guess;
            }

            @Override
            boolean jdkCompatibilityMode(SSLEngine engine) {
                return ((ReferenceCountedOpenSslEngine)engine).jdkCompatibilityMode;
            }
        }
        ,
        CONSCRYPT(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
                SSLEngineResult result;
                int nioBufferCount = in.nioBufferCount();
                int writerIndex = out.writerIndex();
                if (nioBufferCount > 1) {
                    try {
                        SslHandler.access$200((SslHandler)handler)[0] = SslHandler.toByteBuffer(out, writerIndex, out.writableBytes());
                        result = ((ConscryptAlpnSslEngine)handler.engine).unwrap(in.nioBuffers(readerIndex, len), handler.singleBuffer);
                    }
                    finally {
                        SslHandler.access$200((SslHandler)handler)[0] = null;
                    }
                } else {
                    result = handler.engine.unwrap(SslHandler.toByteBuffer(in, readerIndex, len), SslHandler.toByteBuffer(out, writerIndex, out.writableBytes()));
                }
                out.writerIndex(writerIndex + result.bytesProduced());
                return result;
            }

            @Override
            int calculateWrapBufferCapacity(SslHandler handler, int pendingBytes, int numComponents) {
                return ((ConscryptAlpnSslEngine)handler.engine).calculateOutNetBufSize(pendingBytes, numComponents);
            }

            @Override
            int calculatePendingData(SslHandler handler, int guess) {
                return guess;
            }

            @Override
            boolean jdkCompatibilityMode(SSLEngine engine) {
                return true;
            }
        }
        ,
        JDK(false, ByteToMessageDecoder.MERGE_CUMULATOR){

            @Override
            SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
                int writerIndex = out.writerIndex();
                SSLEngineResult result = handler.engine.unwrap(SslHandler.toByteBuffer(in, readerIndex, len), SslHandler.toByteBuffer(out, writerIndex, out.writableBytes()));
                out.writerIndex(writerIndex + result.bytesProduced());
                return result;
            }

            @Override
            int calculateWrapBufferCapacity(SslHandler handler, int pendingBytes, int numComponents) {
                return handler.engine.getSession().getPacketBufferSize();
            }

            @Override
            int calculatePendingData(SslHandler handler, int guess) {
                return guess;
            }

            @Override
            boolean jdkCompatibilityMode(SSLEngine engine) {
                return true;
            }
        };
        
        final boolean wantsDirectBuffer;
        final ByteToMessageDecoder.Cumulator cumulator;

        static SslEngineType forEngine(SSLEngine engine) {
            return engine instanceof ReferenceCountedOpenSslEngine ? TCNATIVE : (engine instanceof ConscryptAlpnSslEngine ? CONSCRYPT : JDK);
        }

        private SslEngineType(boolean wantsDirectBuffer, ByteToMessageDecoder.Cumulator cumulator) {
            this.wantsDirectBuffer = wantsDirectBuffer;
            this.cumulator = cumulator;
        }

        int getPacketBufferSize(SslHandler handler) {
            return handler.engine.getSession().getPacketBufferSize();
        }

        abstract SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, int var4, ByteBuf var5) throws SSLException;

        abstract int calculateWrapBufferCapacity(SslHandler var1, int var2, int var3);

        abstract int calculatePendingData(SslHandler var1, int var2);

        abstract boolean jdkCompatibilityMode(SSLEngine var1);

    }

}

