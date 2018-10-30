/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTrafficShapingHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
    public static final long DEFAULT_CHECK_INTERVAL = 1000L;
    public static final long DEFAULT_MAX_TIME = 15000L;
    static final long DEFAULT_MAX_SIZE = 0x400000L;
    static final long MINIMAL_WAIT = 10L;
    protected TrafficCounter trafficCounter;
    private volatile long writeLimit;
    private volatile long readLimit;
    protected volatile long maxTime = 15000L;
    protected volatile long checkInterval = 1000L;
    static final AttributeKey<Boolean> READ_SUSPENDED = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".READ_SUSPENDED");
    static final AttributeKey<Runnable> REOPEN_TASK = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".REOPEN_TASK");
    volatile long maxWriteDelay = 4000L;
    volatile long maxWriteSize = 0x400000L;
    final int userDefinedWritabilityIndex;
    static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
    static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
    static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;

    void setTrafficCounter(TrafficCounter newTrafficCounter) {
        this.trafficCounter = newTrafficCounter;
    }

    protected int userDefinedWritabilityIndex() {
        return 1;
    }

    protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
        if (maxTime <= 0L) {
            throw new IllegalArgumentException("maxTime must be positive");
        }
        this.userDefinedWritabilityIndex = this.userDefinedWritabilityIndex();
        this.writeLimit = writeLimit;
        this.readLimit = readLimit;
        this.checkInterval = checkInterval;
        this.maxTime = maxTime;
    }

    protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval) {
        this(writeLimit, readLimit, checkInterval, 15000L);
    }

    protected AbstractTrafficShapingHandler(long writeLimit, long readLimit) {
        this(writeLimit, readLimit, 1000L, 15000L);
    }

    protected AbstractTrafficShapingHandler() {
        this(0L, 0L, 1000L, 15000L);
    }

    protected AbstractTrafficShapingHandler(long checkInterval) {
        this(0L, 0L, checkInterval, 15000L);
    }

    public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval) {
        this.configure(newWriteLimit, newReadLimit);
        this.configure(newCheckInterval);
    }

    public void configure(long newWriteLimit, long newReadLimit) {
        this.writeLimit = newWriteLimit;
        this.readLimit = newReadLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    public void configure(long newCheckInterval) {
        this.checkInterval = newCheckInterval;
        if (this.trafficCounter != null) {
            this.trafficCounter.configure(this.checkInterval);
        }
    }

    public long getWriteLimit() {
        return this.writeLimit;
    }

    public void setWriteLimit(long writeLimit) {
        this.writeLimit = writeLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    public long getReadLimit() {
        return this.readLimit;
    }

    public void setReadLimit(long readLimit) {
        this.readLimit = readLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    public long getCheckInterval() {
        return this.checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
        if (this.trafficCounter != null) {
            this.trafficCounter.configure(checkInterval);
        }
    }

    public void setMaxTimeWait(long maxTime) {
        if (maxTime <= 0L) {
            throw new IllegalArgumentException("maxTime must be positive");
        }
        this.maxTime = maxTime;
    }

    public long getMaxTimeWait() {
        return this.maxTime;
    }

    public long getMaxWriteDelay() {
        return this.maxWriteDelay;
    }

    public void setMaxWriteDelay(long maxWriteDelay) {
        if (maxWriteDelay <= 0L) {
            throw new IllegalArgumentException("maxWriteDelay must be positive");
        }
        this.maxWriteDelay = maxWriteDelay;
    }

    public long getMaxWriteSize() {
        return this.maxWriteSize;
    }

    public void setMaxWriteSize(long maxWriteSize) {
        this.maxWriteSize = maxWriteSize;
    }

    protected void doAccounting(TrafficCounter counter) {
    }

    void releaseReadSuspended(ChannelHandlerContext ctx) {
        ctx.attr(READ_SUSPENDED).set(false);
        ctx.channel().config().setAutoRead(true);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long size = this.calculateSize(msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L) {
            long wait = this.trafficCounter.readTimeToWait(size, this.readLimit, this.maxTime, now);
            if ((wait = this.checkWaitReadTime(ctx, wait, now)) >= 10L) {
                ChannelConfig config = ctx.channel().config();
                if (logger.isDebugEnabled()) {
                    logger.debug("Read suspend: " + wait + ':' + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(ctx));
                }
                if (config.isAutoRead() && AbstractTrafficShapingHandler.isHandlerActive(ctx)) {
                    config.setAutoRead(false);
                    ctx.attr(READ_SUSPENDED).set(true);
                    Attribute<Runnable> attr = ctx.attr(REOPEN_TASK);
                    Runnable reopenTask = attr.get();
                    if (reopenTask == null) {
                        reopenTask = new ReopenReadTimerTask(ctx);
                        attr.set(reopenTask);
                    }
                    ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Suspend final status => " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(ctx) + " will reopened at: " + wait);
                    }
                }
            }
        }
        this.informReadOperation(ctx, now);
        ctx.fireChannelRead(msg);
    }

    long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        return wait;
    }

    void informReadOperation(ChannelHandlerContext ctx, long now) {
    }

    protected static boolean isHandlerActive(ChannelHandlerContext ctx) {
        Boolean suspended = ctx.attr(READ_SUSPENDED).get();
        return suspended == null || Boolean.FALSE.equals(suspended);
    }

    @Override
    public void read(ChannelHandlerContext ctx) {
        if (AbstractTrafficShapingHandler.isHandlerActive(ctx)) {
            ctx.read();
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        long wait;
        long size = this.calculateSize(msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L && (wait = this.trafficCounter.writeTimeToWait(size, this.writeLimit, this.maxTime, now)) >= 10L) {
            if (logger.isDebugEnabled()) {
                logger.debug("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(ctx));
            }
            this.submitWrite(ctx, msg, size, wait, now, promise);
            return;
        }
        this.submitWrite(ctx, msg, size, 0L, now, promise);
    }

    @Deprecated
    protected void submitWrite(ChannelHandlerContext ctx, Object msg, long delay, ChannelPromise promise) {
        this.submitWrite(ctx, msg, this.calculateSize(msg), delay, TrafficCounter.milliSecondFromNano(), promise);
    }

    abstract void submitWrite(ChannelHandlerContext var1, Object var2, long var3, long var5, long var7, ChannelPromise var9);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.setUserDefinedWritability(ctx, true);
        super.channelRegistered(ctx);
    }

    void setUserDefinedWritability(ChannelHandlerContext ctx, boolean writable) {
        ChannelOutboundBuffer cob = ctx.channel().unsafe().outboundBuffer();
        if (cob != null) {
            cob.setUserDefinedWritability(this.userDefinedWritabilityIndex, writable);
        }
    }

    void checkWriteSuspend(ChannelHandlerContext ctx, long delay, long queueSize) {
        if (queueSize > this.maxWriteSize || delay > this.maxWriteDelay) {
            this.setUserDefinedWritability(ctx, false);
        }
    }

    void releaseWriteSuspended(ChannelHandlerContext ctx) {
        this.setUserDefinedWritability(ctx, true);
    }

    public TrafficCounter trafficCounter() {
        return this.trafficCounter;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(290).append("TrafficShaping with Write Limit: ").append(this.writeLimit).append(" Read Limit: ").append(this.readLimit).append(" CheckInterval: ").append(this.checkInterval).append(" maxDelay: ").append(this.maxWriteDelay).append(" maxSize: ").append(this.maxWriteSize).append(" and Counter: ");
        if (this.trafficCounter != null) {
            builder.append(this.trafficCounter);
        } else {
            builder.append("none");
        }
        return builder.toString();
    }

    protected long calculateSize(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf)msg).readableBytes();
        }
        if (msg instanceof ByteBufHolder) {
            return ((ByteBufHolder)msg).content().readableBytes();
        }
        return -1L;
    }

    static final class ReopenReadTimerTask
    implements Runnable {
        final ChannelHandlerContext ctx;

        ReopenReadTimerTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            ChannelConfig config = this.ctx.channel().config();
            if (!config.isAutoRead() && AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Not unsuspend: " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
                }
                this.ctx.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(false);
            } else {
                if (logger.isDebugEnabled()) {
                    if (config.isAutoRead() && !AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
                        logger.debug("Unsuspend: " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
                    } else {
                        logger.debug("Normal unsuspend: " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
                    }
                }
                this.ctx.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(false);
                config.setAutoRead(true);
                this.ctx.channel().read();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Unsuspend final status => " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
            }
        }
    }

}

