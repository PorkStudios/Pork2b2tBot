/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingWriteQueue;
import io.netty.handler.proxy.ProxyConnectException;
import io.netty.handler.proxy.ProxyConnectionEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ConnectionPendingException;
import java.util.concurrent.TimeUnit;

public abstract class ProxyHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyHandler.class);
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000L;
    static final String AUTH_NONE = "none";
    private final SocketAddress proxyAddress;
    private volatile SocketAddress destinationAddress;
    private volatile long connectTimeoutMillis = 10000L;
    private volatile ChannelHandlerContext ctx;
    private PendingWriteQueue pendingWrites;
    private boolean finished;
    private boolean suppressChannelReadComplete;
    private boolean flushedPrematurely;
    private final LazyChannelPromise connectPromise = new LazyChannelPromise();
    private ScheduledFuture<?> connectTimeoutFuture;
    private final ChannelFutureListener writeListener = new ChannelFutureListener(){

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                ProxyHandler.this.setConnectFailure(future.cause());
            }
        }
    };

    protected ProxyHandler(SocketAddress proxyAddress) {
        if (proxyAddress == null) {
            throw new NullPointerException("proxyAddress");
        }
        this.proxyAddress = proxyAddress;
    }

    public abstract String protocol();

    public abstract String authScheme();

    public final <T extends SocketAddress> T proxyAddress() {
        return (T)this.proxyAddress;
    }

    public final <T extends SocketAddress> T destinationAddress() {
        return (T)this.destinationAddress;
    }

    public final boolean isConnected() {
        return this.connectPromise.isSuccess();
    }

    public final Future<Channel> connectFuture() {
        return this.connectPromise;
    }

    public final long connectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    public final void setConnectTimeoutMillis(long connectTimeoutMillis) {
        if (connectTimeoutMillis <= 0L) {
            connectTimeoutMillis = 0L;
        }
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    @Override
    public final void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.addCodec(ctx);
        if (ctx.channel().isActive()) {
            this.sendInitialMessage(ctx);
        }
    }

    protected abstract void addCodec(ChannelHandlerContext var1) throws Exception;

    protected abstract void removeEncoder(ChannelHandlerContext var1) throws Exception;

    protected abstract void removeDecoder(ChannelHandlerContext var1) throws Exception;

    @Override
    public final void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (this.destinationAddress != null) {
            promise.setFailure(new ConnectionPendingException());
            return;
        }
        this.destinationAddress = remoteAddress;
        ctx.connect(this.proxyAddress, localAddress, promise);
    }

    @Override
    public final void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.sendInitialMessage(ctx);
        ctx.fireChannelActive();
    }

    private void sendInitialMessage(ChannelHandlerContext ctx) throws Exception {
        Object initialMessage;
        long connectTimeoutMillis = this.connectTimeoutMillis;
        if (connectTimeoutMillis > 0L) {
            this.connectTimeoutFuture = ctx.executor().schedule(new Runnable(){

                @Override
                public void run() {
                    if (!ProxyHandler.this.connectPromise.isDone()) {
                        ProxyHandler.this.setConnectFailure(new ProxyConnectException(ProxyHandler.this.exceptionMessage("timeout")));
                    }
                }
            }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
        }
        if ((initialMessage = this.newInitialMessage(ctx)) != null) {
            this.sendToProxyServer(initialMessage);
        }
        ProxyHandler.readIfNeeded(ctx);
    }

    protected abstract Object newInitialMessage(ChannelHandlerContext var1) throws Exception;

    protected final void sendToProxyServer(Object msg) {
        this.ctx.writeAndFlush(msg).addListener(this.writeListener);
    }

    @Override
    public final void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.finished) {
            ctx.fireChannelInactive();
        } else {
            this.setConnectFailure(new ProxyConnectException(this.exceptionMessage("disconnected")));
        }
    }

    @Override
    public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.finished) {
            ctx.fireExceptionCaught(cause);
        } else {
            this.setConnectFailure(cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.finished) {
            this.suppressChannelReadComplete = false;
            ctx.fireChannelRead(msg);
        } else {
            this.suppressChannelReadComplete = true;
            Throwable cause = null;
            try {
                boolean done = this.handleResponse(ctx, msg);
                if (done) {
                    this.setConnectSuccess();
                }
            }
            catch (Throwable t) {
                cause = t;
            }
            finally {
                ReferenceCountUtil.release(msg);
                if (cause != null) {
                    this.setConnectFailure(cause);
                }
            }
        }
    }

    protected abstract boolean handleResponse(ChannelHandlerContext var1, Object var2) throws Exception;

    private void setConnectSuccess() {
        this.finished = true;
        this.cancelConnectTimeoutFuture();
        if (!this.connectPromise.isDone()) {
            boolean removedCodec = true;
            removedCodec &= this.safeRemoveEncoder();
            this.ctx.fireUserEventTriggered(new ProxyConnectionEvent(this.protocol(), this.authScheme(), this.proxyAddress, this.destinationAddress));
            if (removedCodec &= this.safeRemoveDecoder()) {
                this.writePendingWrites();
                if (this.flushedPrematurely) {
                    this.ctx.flush();
                }
                this.connectPromise.trySuccess(this.ctx.channel());
            } else {
                ProxyConnectException cause = new ProxyConnectException("failed to remove all codec handlers added by the proxy handler; bug?");
                this.failPendingWritesAndClose(cause);
            }
        }
    }

    private boolean safeRemoveDecoder() {
        try {
            this.removeDecoder(this.ctx);
            return true;
        }
        catch (Exception e) {
            logger.warn("Failed to remove proxy decoders:", e);
            return false;
        }
    }

    private boolean safeRemoveEncoder() {
        try {
            this.removeEncoder(this.ctx);
            return true;
        }
        catch (Exception e) {
            logger.warn("Failed to remove proxy encoders:", e);
            return false;
        }
    }

    private void setConnectFailure(Throwable cause) {
        this.finished = true;
        this.cancelConnectTimeoutFuture();
        if (!this.connectPromise.isDone()) {
            if (!(cause instanceof ProxyConnectException)) {
                cause = new ProxyConnectException(this.exceptionMessage(cause.toString()), cause);
            }
            this.safeRemoveDecoder();
            this.safeRemoveEncoder();
            this.failPendingWritesAndClose(cause);
        }
    }

    private void failPendingWritesAndClose(Throwable cause) {
        this.failPendingWrites(cause);
        this.connectPromise.tryFailure(cause);
        this.ctx.fireExceptionCaught(cause);
        this.ctx.close();
    }

    private void cancelConnectTimeoutFuture() {
        if (this.connectTimeoutFuture != null) {
            this.connectTimeoutFuture.cancel(false);
            this.connectTimeoutFuture = null;
        }
    }

    protected final String exceptionMessage(String msg) {
        if (msg == null) {
            msg = "";
        }
        StringBuilder buf = new StringBuilder(128 + msg.length()).append(this.protocol()).append(", ").append(this.authScheme()).append(", ").append(this.proxyAddress).append(" => ").append(this.destinationAddress);
        if (!msg.isEmpty()) {
            buf.append(", ").append(msg);
        }
        return buf.toString();
    }

    @Override
    public final void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.suppressChannelReadComplete) {
            this.suppressChannelReadComplete = false;
            ProxyHandler.readIfNeeded(ctx);
        } else {
            ctx.fireChannelReadComplete();
        }
    }

    @Override
    public final void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.finished) {
            this.writePendingWrites();
            ctx.write(msg, promise);
        } else {
            this.addPendingWrite(ctx, msg, promise);
        }
    }

    @Override
    public final void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.finished) {
            this.writePendingWrites();
            ctx.flush();
        } else {
            this.flushedPrematurely = true;
        }
    }

    private static void readIfNeeded(ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }

    private void writePendingWrites() {
        if (this.pendingWrites != null) {
            this.pendingWrites.removeAndWriteAll();
            this.pendingWrites = null;
        }
    }

    private void failPendingWrites(Throwable cause) {
        if (this.pendingWrites != null) {
            this.pendingWrites.removeAndFailAll(cause);
            this.pendingWrites = null;
        }
    }

    private void addPendingWrite(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        PendingWriteQueue pendingWrites = this.pendingWrites;
        if (pendingWrites == null) {
            this.pendingWrites = pendingWrites = new PendingWriteQueue(ctx);
        }
        pendingWrites.add(msg, promise);
    }

    private final class LazyChannelPromise
    extends DefaultPromise<Channel> {
        private LazyChannelPromise() {
        }

        @Override
        protected EventExecutor executor() {
            if (ProxyHandler.this.ctx == null) {
                throw new IllegalStateException();
            }
            return ProxyHandler.this.ctx.executor();
        }
    }

}

