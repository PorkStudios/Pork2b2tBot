/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ChunkedWriteHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
    private final Queue<PendingWrite> queue = new ArrayDeque<PendingWrite>();
    private volatile ChannelHandlerContext ctx;
    private PendingWrite currentWrite;

    public ChunkedWriteHandler() {
    }

    @Deprecated
    public ChunkedWriteHandler(int maxPendingWrites) {
        if (maxPendingWrites <= 0) {
            throw new IllegalArgumentException("maxPendingWrites: " + maxPendingWrites + " (expected: > 0)");
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    public void resumeTransfer() {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            return;
        }
        if (ctx.executor().inEventLoop()) {
            try {
                this.doFlush(ctx);
            }
            catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Unexpected exception while sending chunks.", e);
                }
            }
        } else {
            ctx.executor().execute(new Runnable(){

                @Override
                public void run() {
                    block2 : {
                        try {
                            ChunkedWriteHandler.this.doFlush(ctx);
                        }
                        catch (Exception e) {
                            if (!logger.isWarnEnabled()) break block2;
                            logger.warn("Unexpected exception while sending chunks.", e);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.queue.add(new PendingWrite(msg, promise));
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        this.doFlush(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.doFlush(ctx);
        ctx.fireChannelInactive();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            this.doFlush(ctx);
        }
        ctx.fireChannelWritabilityChanged();
    }

    private void discard(Throwable cause) {
        do {
            PendingWrite currentWrite = this.currentWrite;
            if (this.currentWrite == null) {
                currentWrite = this.queue.poll();
            } else {
                this.currentWrite = null;
            }
            if (currentWrite == null) break;
            Object message = currentWrite.msg;
            if (message instanceof ChunkedInput) {
                ChunkedInput in = (ChunkedInput)message;
                try {
                    if (!in.isEndOfInput()) {
                        if (cause == null) {
                            cause = new ClosedChannelException();
                        }
                        currentWrite.fail(cause);
                    } else {
                        currentWrite.success(in.length());
                    }
                    ChunkedWriteHandler.closeInput(in);
                }
                catch (Exception e) {
                    currentWrite.fail(e);
                    logger.warn(ChunkedInput.class.getSimpleName() + ".isEndOfInput() failed", e);
                    ChunkedWriteHandler.closeInput(in);
                }
                continue;
            }
            if (cause == null) {
                cause = new ClosedChannelException();
            }
            currentWrite.fail(cause);
        } while (true);
    }

    private void doFlush(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        if (!channel.isActive()) {
            this.discard(null);
            return;
        }
        boolean requiresFlush = true;
        ByteBufAllocator allocator = ctx.alloc();
        while (channel.isWritable()) {
            if (this.currentWrite == null) {
                this.currentWrite = this.queue.poll();
            }
            if (this.currentWrite == null) break;
            final PendingWrite currentWrite = this.currentWrite;
            final Object pendingMessage = currentWrite.msg;
            if (pendingMessage instanceof ChunkedInput) {
                boolean endOfInput;
                boolean suspend;
                final ChunkedInput chunks = (ChunkedInput)pendingMessage;
                ByteBuf message = null;
                try {
                    message = (ByteBuf)chunks.readChunk(allocator);
                    endOfInput = chunks.isEndOfInput();
                    suspend = message == null ? !endOfInput : false;
                }
                catch (Throwable t) {
                    this.currentWrite = null;
                    if (message != null) {
                        ReferenceCountUtil.release(message);
                    }
                    currentWrite.fail(t);
                    ChunkedWriteHandler.closeInput(chunks);
                    break;
                }
                if (suspend) break;
                if (message == null) {
                    message = Unpooled.EMPTY_BUFFER;
                }
                ChannelFuture f = ctx.write(message);
                if (endOfInput) {
                    this.currentWrite = null;
                    f.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            currentWrite.progress(chunks.progress(), chunks.length());
                            currentWrite.success(chunks.length());
                            ChunkedWriteHandler.closeInput(chunks);
                        }
                    });
                } else if (channel.isWritable()) {
                    f.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                ChunkedWriteHandler.closeInput((ChunkedInput)pendingMessage);
                                currentWrite.fail(future.cause());
                            } else {
                                currentWrite.progress(chunks.progress(), chunks.length());
                            }
                        }
                    });
                } else {
                    f.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                ChunkedWriteHandler.closeInput((ChunkedInput)pendingMessage);
                                currentWrite.fail(future.cause());
                            } else {
                                currentWrite.progress(chunks.progress(), chunks.length());
                                if (channel.isWritable()) {
                                    ChunkedWriteHandler.this.resumeTransfer();
                                }
                            }
                        }
                    });
                }
                ctx.flush();
                requiresFlush = false;
            } else {
                ctx.write(pendingMessage, currentWrite.promise);
                this.currentWrite = null;
                requiresFlush = true;
            }
            if (channel.isActive()) continue;
            this.discard(new ClosedChannelException());
            break;
        }
        if (requiresFlush) {
            ctx.flush();
        }
    }

    static void closeInput(ChunkedInput<?> chunks) {
        block2 : {
            try {
                chunks.close();
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn("Failed to close a chunked input.", t);
            }
        }
    }

    private static final class PendingWrite {
        final Object msg;
        final ChannelPromise promise;

        PendingWrite(Object msg, ChannelPromise promise) {
            this.msg = msg;
            this.promise = promise;
        }

        void fail(Throwable cause) {
            ReferenceCountUtil.release(this.msg);
            this.promise.tryFailure(cause);
        }

        void success(long total) {
            if (this.promise.isDone()) {
                return;
            }
            if (this.promise instanceof ChannelProgressivePromise) {
                ((ChannelProgressivePromise)this.promise).tryProgress(total, total);
            }
            this.promise.trySuccess();
        }

        void progress(long progress, long total) {
            if (this.promise instanceof ChannelProgressivePromise) {
                ((ChannelProgressivePromise)this.promise).tryProgress(progress, total);
            }
        }
    }

}

