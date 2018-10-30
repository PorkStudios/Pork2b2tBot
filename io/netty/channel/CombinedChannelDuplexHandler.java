/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundInvoker;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler>
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CombinedChannelDuplexHandler.class);
    private DelegatingChannelHandlerContext inboundCtx;
    private DelegatingChannelHandlerContext outboundCtx;
    private volatile boolean handlerAdded;
    private I inboundHandler;
    private O outboundHandler;

    protected CombinedChannelDuplexHandler() {
        this.ensureNotSharable();
    }

    public CombinedChannelDuplexHandler(I inboundHandler, O outboundHandler) {
        this.ensureNotSharable();
        this.init(inboundHandler, outboundHandler);
    }

    protected final void init(I inboundHandler, O outboundHandler) {
        this.validate(inboundHandler, outboundHandler);
        this.inboundHandler = inboundHandler;
        this.outboundHandler = outboundHandler;
    }

    private void validate(I inboundHandler, O outboundHandler) {
        if (this.inboundHandler != null) {
            throw new IllegalStateException("init() can not be invoked if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with non-default constructor.");
        }
        if (inboundHandler == null) {
            throw new NullPointerException("inboundHandler");
        }
        if (outboundHandler == null) {
            throw new NullPointerException("outboundHandler");
        }
        if (inboundHandler instanceof ChannelOutboundHandler) {
            throw new IllegalArgumentException("inboundHandler must not implement " + ChannelOutboundHandler.class.getSimpleName() + " to get combined.");
        }
        if (outboundHandler instanceof ChannelInboundHandler) {
            throw new IllegalArgumentException("outboundHandler must not implement " + ChannelInboundHandler.class.getSimpleName() + " to get combined.");
        }
    }

    protected final I inboundHandler() {
        return this.inboundHandler;
    }

    protected final O outboundHandler() {
        return this.outboundHandler;
    }

    private void checkAdded() {
        if (!this.handlerAdded) {
            throw new IllegalStateException("handler not added to pipeline yet");
        }
    }

    public final void removeInboundHandler() {
        this.checkAdded();
        this.inboundCtx.remove();
    }

    public final void removeOutboundHandler() {
        this.checkAdded();
        this.outboundCtx.remove();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (this.inboundHandler == null) {
            throw new IllegalStateException("init() must be invoked before being added to a " + ChannelPipeline.class.getSimpleName() + " if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with the default constructor.");
        }
        this.outboundCtx = new DelegatingChannelHandlerContext(ctx, (ChannelHandler)this.outboundHandler);
        this.inboundCtx = new DelegatingChannelHandlerContext(ctx, (ChannelHandler)this.inboundHandler){

            @Override
            public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
                if (!CombinedChannelDuplexHandler.access$000((CombinedChannelDuplexHandler)CombinedChannelDuplexHandler.this).removed) {
                    try {
                        CombinedChannelDuplexHandler.this.outboundHandler.exceptionCaught(CombinedChannelDuplexHandler.this.outboundCtx, cause);
                    }
                    catch (Throwable error) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)ThrowableUtil.stackTraceToString(error), (Object)cause);
                        } else if (logger.isWarnEnabled()) {
                            logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)error, (Object)cause);
                        }
                    }
                } else {
                    super.fireExceptionCaught(cause);
                }
                return this;
            }
        };
        this.handlerAdded = true;
        try {
            this.inboundHandler.handlerAdded(this.inboundCtx);
        }
        finally {
            this.outboundHandler.handlerAdded(this.outboundCtx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            this.inboundCtx.remove();
        }
        finally {
            this.outboundCtx.remove();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelRegistered(this.inboundCtx);
        } else {
            this.inboundCtx.fireChannelRegistered();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelUnregistered(this.inboundCtx);
        } else {
            this.inboundCtx.fireChannelUnregistered();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelActive(this.inboundCtx);
        } else {
            this.inboundCtx.fireChannelActive();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelInactive(this.inboundCtx);
        } else {
            this.inboundCtx.fireChannelInactive();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.exceptionCaught(this.inboundCtx, cause);
        } else {
            this.inboundCtx.fireExceptionCaught(cause);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.userEventTriggered(this.inboundCtx, evt);
        } else {
            this.inboundCtx.fireUserEventTriggered(evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelRead(this.inboundCtx, msg);
        } else {
            this.inboundCtx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelReadComplete(this.inboundCtx);
        } else {
            this.inboundCtx.fireChannelReadComplete();
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.inboundCtx.ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelWritabilityChanged(this.inboundCtx);
        } else {
            this.inboundCtx.fireChannelWritabilityChanged();
        }
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.bind(this.outboundCtx, localAddress, promise);
        } else {
            this.outboundCtx.bind(localAddress, promise);
        }
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.connect(this.outboundCtx, remoteAddress, localAddress, promise);
        } else {
            this.outboundCtx.connect(localAddress, promise);
        }
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.disconnect(this.outboundCtx, promise);
        } else {
            this.outboundCtx.disconnect(promise);
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.close(this.outboundCtx, promise);
        } else {
            this.outboundCtx.close(promise);
        }
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.deregister(this.outboundCtx, promise);
        } else {
            this.outboundCtx.deregister(promise);
        }
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.read(this.outboundCtx);
        } else {
            this.outboundCtx.read();
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.write(this.outboundCtx, msg, promise);
        } else {
            this.outboundCtx.write(msg, promise);
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == this.outboundCtx.ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.flush(this.outboundCtx);
        } else {
            this.outboundCtx.flush();
        }
    }

    private static class DelegatingChannelHandlerContext
    implements ChannelHandlerContext {
        private final ChannelHandlerContext ctx;
        private final ChannelHandler handler;
        boolean removed;

        DelegatingChannelHandlerContext(ChannelHandlerContext ctx, ChannelHandler handler) {
            this.ctx = ctx;
            this.handler = handler;
        }

        @Override
        public Channel channel() {
            return this.ctx.channel();
        }

        @Override
        public EventExecutor executor() {
            return this.ctx.executor();
        }

        @Override
        public String name() {
            return this.ctx.name();
        }

        @Override
        public ChannelHandler handler() {
            return this.ctx.handler();
        }

        @Override
        public boolean isRemoved() {
            return this.removed || this.ctx.isRemoved();
        }

        @Override
        public ChannelHandlerContext fireChannelRegistered() {
            this.ctx.fireChannelRegistered();
            return this;
        }

        @Override
        public ChannelHandlerContext fireChannelUnregistered() {
            this.ctx.fireChannelUnregistered();
            return this;
        }

        @Override
        public ChannelHandlerContext fireChannelActive() {
            this.ctx.fireChannelActive();
            return this;
        }

        @Override
        public ChannelHandlerContext fireChannelInactive() {
            this.ctx.fireChannelInactive();
            return this;
        }

        @Override
        public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
            this.ctx.fireExceptionCaught(cause);
            return this;
        }

        @Override
        public ChannelHandlerContext fireUserEventTriggered(Object event) {
            this.ctx.fireUserEventTriggered(event);
            return this;
        }

        @Override
        public ChannelHandlerContext fireChannelRead(Object msg) {
            this.ctx.fireChannelRead(msg);
            return this;
        }

        @Override
        public ChannelHandlerContext fireChannelReadComplete() {
            this.ctx.fireChannelReadComplete();
            return this;
        }

        @Override
        public ChannelHandlerContext fireChannelWritabilityChanged() {
            this.ctx.fireChannelWritabilityChanged();
            return this;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {
            return this.ctx.bind(localAddress);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {
            return this.ctx.connect(remoteAddress);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return this.ctx.connect(remoteAddress, localAddress);
        }

        @Override
        public ChannelFuture disconnect() {
            return this.ctx.disconnect();
        }

        @Override
        public ChannelFuture close() {
            return this.ctx.close();
        }

        @Override
        public ChannelFuture deregister() {
            return this.ctx.deregister();
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return this.ctx.bind(localAddress, promise);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            return this.ctx.connect(remoteAddress, promise);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            return this.ctx.connect(remoteAddress, localAddress, promise);
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            return this.ctx.disconnect(promise);
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            return this.ctx.close(promise);
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            return this.ctx.deregister(promise);
        }

        @Override
        public ChannelHandlerContext read() {
            this.ctx.read();
            return this;
        }

        @Override
        public ChannelFuture write(Object msg) {
            return this.ctx.write(msg);
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return this.ctx.write(msg, promise);
        }

        @Override
        public ChannelHandlerContext flush() {
            this.ctx.flush();
            return this;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            return this.ctx.writeAndFlush(msg, promise);
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            return this.ctx.writeAndFlush(msg);
        }

        @Override
        public ChannelPipeline pipeline() {
            return this.ctx.pipeline();
        }

        @Override
        public ByteBufAllocator alloc() {
            return this.ctx.alloc();
        }

        @Override
        public ChannelPromise newPromise() {
            return this.ctx.newPromise();
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return this.ctx.newProgressivePromise();
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return this.ctx.newSucceededFuture();
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            return this.ctx.newFailedFuture(cause);
        }

        @Override
        public ChannelPromise voidPromise() {
            return this.ctx.voidPromise();
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return this.ctx.attr(key);
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> key) {
            return this.ctx.hasAttr(key);
        }

        final void remove() {
            EventExecutor executor = this.executor();
            if (executor.inEventLoop()) {
                this.remove0();
            } else {
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DelegatingChannelHandlerContext.this.remove0();
                    }
                });
            }
        }

        private void remove0() {
            if (!this.removed) {
                this.removed = true;
                try {
                    this.handler.handlerRemoved(this);
                }
                catch (Throwable cause) {
                    this.fireExceptionCaught(new ChannelPipelineException(this.handler.getClass().getName() + ".handlerRemoved() has thrown an exception.", cause));
                }
            }
        }

    }

}

