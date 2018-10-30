/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.timeout;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IdleStateHandler
extends ChannelDuplexHandler {
    private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
    private final ChannelFutureListener writeListener = new ChannelFutureListener(){

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            IdleStateHandler.this.lastWriteTime = IdleStateHandler.this.ticksInNanos();
            IdleStateHandler.this.firstWriterIdleEvent = (IdleStateHandler.this.firstAllIdleEvent = true);
        }
    };
    private final boolean observeOutput;
    private final long readerIdleTimeNanos;
    private final long writerIdleTimeNanos;
    private final long allIdleTimeNanos;
    private ScheduledFuture<?> readerIdleTimeout;
    private long lastReadTime;
    private boolean firstReaderIdleEvent = true;
    private ScheduledFuture<?> writerIdleTimeout;
    private long lastWriteTime;
    private boolean firstWriterIdleEvent = true;
    private ScheduledFuture<?> allIdleTimeout;
    private boolean firstAllIdleEvent = true;
    private byte state;
    private boolean reading;
    private long lastChangeCheckTimeStamp;
    private int lastMessageHashCode;
    private long lastPendingWriteBytes;

    public IdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        this(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS);
    }

    public IdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        this(false, readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    public IdleStateHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        this.observeOutput = observeOutput;
        this.readerIdleTimeNanos = readerIdleTime <= 0L ? 0L : Math.max(unit.toNanos(readerIdleTime), MIN_TIMEOUT_NANOS);
        this.writerIdleTimeNanos = writerIdleTime <= 0L ? 0L : Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS);
        this.allIdleTimeNanos = allIdleTime <= 0L ? 0L : Math.max(unit.toNanos(allIdleTime), MIN_TIMEOUT_NANOS);
    }

    public long getReaderIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.readerIdleTimeNanos);
    }

    public long getWriterIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.writerIdleTimeNanos);
    }

    public long getAllIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.allIdleTimeNanos);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive() && ctx.channel().isRegistered()) {
            this.initialize(ctx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            this.initialize(ctx);
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.initialize(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
            this.reading = true;
            this.firstAllIdleEvent = true;
            this.firstReaderIdleEvent = true;
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if ((this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) && this.reading) {
            this.lastReadTime = this.ticksInNanos();
            this.reading = false;
        }
        ctx.fireChannelReadComplete();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.writerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
            ctx.write(msg, promise.unvoid()).addListener(this.writeListener);
        } else {
            ctx.write(msg, promise);
        }
    }

    private void initialize(ChannelHandlerContext ctx) {
        switch (this.state) {
            case 1: 
            case 2: {
                return;
            }
        }
        this.state = 1;
        this.initOutputChanged(ctx);
        this.lastReadTime = this.lastWriteTime = this.ticksInNanos();
        if (this.readerIdleTimeNanos > 0L) {
            this.readerIdleTimeout = this.schedule(ctx, new ReaderIdleTimeoutTask(ctx), this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
        }
        if (this.writerIdleTimeNanos > 0L) {
            this.writerIdleTimeout = this.schedule(ctx, new WriterIdleTimeoutTask(ctx), this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
        }
        if (this.allIdleTimeNanos > 0L) {
            this.allIdleTimeout = this.schedule(ctx, new AllIdleTimeoutTask(ctx), this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
        }
    }

    long ticksInNanos() {
        return System.nanoTime();
    }

    ScheduledFuture<?> schedule(ChannelHandlerContext ctx, Runnable task, long delay, TimeUnit unit) {
        return ctx.executor().schedule(task, delay, unit);
    }

    private void destroy() {
        this.state = (byte)2;
        if (this.readerIdleTimeout != null) {
            this.readerIdleTimeout.cancel(false);
            this.readerIdleTimeout = null;
        }
        if (this.writerIdleTimeout != null) {
            this.writerIdleTimeout.cancel(false);
            this.writerIdleTimeout = null;
        }
        if (this.allIdleTimeout != null) {
            this.allIdleTimeout.cancel(false);
            this.allIdleTimeout = null;
        }
    }

    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }

    protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
        switch (state) {
            case ALL_IDLE: {
                return first ? IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT : IdleStateEvent.ALL_IDLE_STATE_EVENT;
            }
            case READER_IDLE: {
                return first ? IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT : IdleStateEvent.READER_IDLE_STATE_EVENT;
            }
            case WRITER_IDLE: {
                return first ? IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT : IdleStateEvent.WRITER_IDLE_STATE_EVENT;
            }
        }
        throw new IllegalArgumentException("Unhandled: state=" + (Object)((Object)state) + ", first=" + first);
    }

    private void initOutputChanged(ChannelHandlerContext ctx) {
        ChannelOutboundBuffer buf;
        Channel channel;
        Channel.Unsafe unsafe;
        if (this.observeOutput && (buf = (unsafe = (channel = ctx.channel()).unsafe()).outboundBuffer()) != null) {
            this.lastMessageHashCode = System.identityHashCode(buf.current());
            this.lastPendingWriteBytes = buf.totalPendingWriteBytes();
        }
    }

    private boolean hasOutputChanged(ChannelHandlerContext ctx, boolean first) {
        if (this.observeOutput) {
            ChannelOutboundBuffer buf;
            Channel.Unsafe unsafe;
            Channel channel;
            if (this.lastChangeCheckTimeStamp != this.lastWriteTime) {
                this.lastChangeCheckTimeStamp = this.lastWriteTime;
                if (!first) {
                    return true;
                }
            }
            if ((buf = (unsafe = (channel = ctx.channel()).unsafe()).outboundBuffer()) != null) {
                int messageHashCode = System.identityHashCode(buf.current());
                long pendingWriteBytes = buf.totalPendingWriteBytes();
                if (messageHashCode != this.lastMessageHashCode || pendingWriteBytes != this.lastPendingWriteBytes) {
                    this.lastMessageHashCode = messageHashCode;
                    this.lastPendingWriteBytes = pendingWriteBytes;
                    if (!first) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final class AllIdleTimeoutTask
    extends AbstractIdleTask {
        AllIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void run(ChannelHandlerContext ctx) {
            long nextDelay = IdleStateHandler.this.allIdleTimeNanos;
            if (!IdleStateHandler.this.reading) {
                nextDelay -= IdleStateHandler.this.ticksInNanos() - Math.max(IdleStateHandler.this.lastReadTime, IdleStateHandler.this.lastWriteTime);
            }
            if (nextDelay <= 0L) {
                IdleStateHandler.this.allIdleTimeout = IdleStateHandler.this.schedule(ctx, this, IdleStateHandler.this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = IdleStateHandler.this.firstAllIdleEvent;
                IdleStateHandler.this.firstAllIdleEvent = false;
                try {
                    if (IdleStateHandler.this.hasOutputChanged(ctx, first)) {
                        return;
                    }
                    IdleStateEvent event = IdleStateHandler.this.newIdleStateEvent(IdleState.ALL_IDLE, first);
                    IdleStateHandler.this.channelIdle(ctx, event);
                }
                catch (Throwable t) {
                    ctx.fireExceptionCaught(t);
                }
            } else {
                IdleStateHandler.this.allIdleTimeout = IdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }
        }
    }

    private final class WriterIdleTimeoutTask
    extends AbstractIdleTask {
        WriterIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void run(ChannelHandlerContext ctx) {
            long lastWriteTime = IdleStateHandler.this.lastWriteTime;
            long nextDelay = IdleStateHandler.this.writerIdleTimeNanos - (IdleStateHandler.this.ticksInNanos() - lastWriteTime);
            if (nextDelay <= 0L) {
                IdleStateHandler.this.writerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, IdleStateHandler.this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = IdleStateHandler.this.firstWriterIdleEvent;
                IdleStateHandler.this.firstWriterIdleEvent = false;
                try {
                    if (IdleStateHandler.this.hasOutputChanged(ctx, first)) {
                        return;
                    }
                    IdleStateEvent event = IdleStateHandler.this.newIdleStateEvent(IdleState.WRITER_IDLE, first);
                    IdleStateHandler.this.channelIdle(ctx, event);
                }
                catch (Throwable t) {
                    ctx.fireExceptionCaught(t);
                }
            } else {
                IdleStateHandler.this.writerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }
        }
    }

    private final class ReaderIdleTimeoutTask
    extends AbstractIdleTask {
        ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void run(ChannelHandlerContext ctx) {
            long nextDelay = IdleStateHandler.this.readerIdleTimeNanos;
            if (!IdleStateHandler.this.reading) {
                nextDelay -= IdleStateHandler.this.ticksInNanos() - IdleStateHandler.this.lastReadTime;
            }
            if (nextDelay <= 0L) {
                IdleStateHandler.this.readerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, IdleStateHandler.this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = IdleStateHandler.this.firstReaderIdleEvent;
                IdleStateHandler.this.firstReaderIdleEvent = false;
                try {
                    IdleStateEvent event = IdleStateHandler.this.newIdleStateEvent(IdleState.READER_IDLE, first);
                    IdleStateHandler.this.channelIdle(ctx, event);
                }
                catch (Throwable t) {
                    ctx.fireExceptionCaught(t);
                }
            } else {
                IdleStateHandler.this.readerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }
        }
    }

    private static abstract class AbstractIdleTask
    implements Runnable {
        private final ChannelHandlerContext ctx;

        AbstractIdleTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (!this.ctx.channel().isOpen()) {
                return;
            }
            this.run(this.ctx);
        }

        protected abstract void run(ChannelHandlerContext var1);
    }

}

