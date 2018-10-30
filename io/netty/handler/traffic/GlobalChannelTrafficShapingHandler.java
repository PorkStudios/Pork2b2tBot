/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.GlobalChannelTrafficCounter;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class GlobalChannelTrafficShapingHandler
extends AbstractTrafficShapingHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalChannelTrafficShapingHandler.class);
    final ConcurrentMap<Integer, PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
    private final AtomicLong queuesSize = new AtomicLong();
    private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
    private final AtomicLong cumulativeReadBytes = new AtomicLong();
    volatile long maxGlobalWriteSize = 419430400L;
    private volatile long writeChannelLimit;
    private volatile long readChannelLimit;
    private static final float DEFAULT_DEVIATION = 0.1f;
    private static final float MAX_DEVIATION = 0.4f;
    private static final float DEFAULT_SLOWDOWN = 0.4f;
    private static final float DEFAULT_ACCELERATION = -0.1f;
    private volatile float maxDeviation;
    private volatile float accelerationFactor;
    private volatile float slowDownFactor;
    private volatile boolean readDeviationActive;
    private volatile boolean writeDeviationActive;

    void createGlobalTrafficCounter(ScheduledExecutorService executor) {
        this.setMaxDeviation(0.1f, 0.4f, -0.1f);
        if (executor == null) {
            throw new IllegalArgumentException("Executor must not be null");
        }
        GlobalChannelTrafficCounter tc = new GlobalChannelTrafficCounter(this, executor, "GlobalChannelTC", this.checkInterval);
        this.setTrafficCounter(tc);
        tc.start();
    }

    @Override
    protected int userDefinedWritabilityIndex() {
        return 3;
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval, long maxTime) {
        super(writeGlobalLimit, readGlobalLimit, checkInterval, maxTime);
        this.createGlobalTrafficCounter(executor);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval) {
        super(writeGlobalLimit, readGlobalLimit, checkInterval);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter(executor);
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit) {
        super(writeGlobalLimit, readGlobalLimit);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter(executor);
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
        super(checkInterval);
        this.createGlobalTrafficCounter(executor);
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor) {
        this.createGlobalTrafficCounter(executor);
    }

    public float maxDeviation() {
        return this.maxDeviation;
    }

    public float accelerationFactor() {
        return this.accelerationFactor;
    }

    public float slowDownFactor() {
        return this.slowDownFactor;
    }

    public void setMaxDeviation(float maxDeviation, float slowDownFactor, float accelerationFactor) {
        if (maxDeviation > 0.4f) {
            throw new IllegalArgumentException("maxDeviation must be <= 0.4");
        }
        if (slowDownFactor < 0.0f) {
            throw new IllegalArgumentException("slowDownFactor must be >= 0");
        }
        if (accelerationFactor > 0.0f) {
            throw new IllegalArgumentException("accelerationFactor must be <= 0");
        }
        this.maxDeviation = maxDeviation;
        this.accelerationFactor = 1.0f + accelerationFactor;
        this.slowDownFactor = 1.0f + slowDownFactor;
    }

    private void computeDeviationCumulativeBytes() {
        long maxWrittenBytes = 0L;
        long maxReadBytes = 0L;
        long minWrittenBytes = Long.MAX_VALUE;
        long minReadBytes = Long.MAX_VALUE;
        for (PerChannel perChannel : this.channelQueues.values()) {
            long value = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
            if (maxWrittenBytes < value) {
                maxWrittenBytes = value;
            }
            if (minWrittenBytes > value) {
                minWrittenBytes = value;
            }
            if (maxReadBytes < (value = perChannel.channelTrafficCounter.cumulativeReadBytes())) {
                maxReadBytes = value;
            }
            if (minReadBytes <= value) continue;
            minReadBytes = value;
        }
        boolean multiple = this.channelQueues.size() > 1;
        this.readDeviationActive = multiple && minReadBytes < maxReadBytes / 2L;
        this.writeDeviationActive = multiple && minWrittenBytes < maxWrittenBytes / 2L;
        this.cumulativeWrittenBytes.set(maxWrittenBytes);
        this.cumulativeReadBytes.set(maxReadBytes);
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        this.computeDeviationCumulativeBytes();
        super.doAccounting(counter);
    }

    private long computeBalancedWait(float maxLocal, float maxGlobal, long wait) {
        if (maxGlobal == 0.0f) {
            return wait;
        }
        float ratio = maxLocal / maxGlobal;
        if (ratio > this.maxDeviation) {
            if (ratio < 1.0f - this.maxDeviation) {
                return wait;
            }
            ratio = this.slowDownFactor;
            if (wait < 10L) {
                wait = 10L;
            }
        } else {
            ratio = this.accelerationFactor;
        }
        return (long)((float)wait * ratio);
    }

    public long getMaxGlobalWriteSize() {
        return this.maxGlobalWriteSize;
    }

    public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
        if (maxGlobalWriteSize <= 0L) {
            throw new IllegalArgumentException("maxGlobalWriteSize must be positive");
        }
        this.maxGlobalWriteSize = maxGlobalWriteSize;
    }

    public long queuesSize() {
        return this.queuesSize.get();
    }

    public void configureChannel(long newWriteLimit, long newReadLimit) {
        this.writeChannelLimit = newWriteLimit;
        this.readChannelLimit = newReadLimit;
        long now = TrafficCounter.milliSecondFromNano();
        for (PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }

    public long getWriteChannelLimit() {
        return this.writeChannelLimit;
    }

    public void setWriteChannelLimit(long writeLimit) {
        this.writeChannelLimit = writeLimit;
        long now = TrafficCounter.milliSecondFromNano();
        for (PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }

    public long getReadChannelLimit() {
        return this.readChannelLimit;
    }

    public void setReadChannelLimit(long readLimit) {
        this.readChannelLimit = readLimit;
        long now = TrafficCounter.milliSecondFromNano();
        for (PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }

    public final void release() {
        this.trafficCounter.stop();
    }

    private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Integer key = channel.hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel == null) {
            perChannel = new PerChannel();
            perChannel.messagesQueue = new ArrayDeque();
            perChannel.channelTrafficCounter = new TrafficCounter(this, null, "ChannelTC" + ctx.channel().hashCode(), this.checkInterval);
            perChannel.queueSize = 0L;
            perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
            this.channelQueues.put(key, perChannel);
        }
        return perChannel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.getOrSetPerChannel(ctx);
        this.trafficCounter.resetCumulativeTime();
        super.handlerAdded(ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.trafficCounter.resetCumulativeTime();
        Channel channel = ctx.channel();
        Integer key = channel.hashCode();
        PerChannel perChannel = this.channelQueues.remove(key);
        if (perChannel != null) {
            PerChannel perChannel2 = perChannel;
            synchronized (perChannel2) {
                if (channel.isActive()) {
                    for (ToSend toSend : perChannel.messagesQueue) {
                        long size = this.calculateSize(toSend.toSend);
                        this.trafficCounter.bytesRealWriteFlowControl(size);
                        perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
                        perChannel.queueSize -= size;
                        this.queuesSize.addAndGet(- size);
                        ctx.write(toSend.toSend, toSend.promise);
                    }
                } else {
                    this.queuesSize.addAndGet(- perChannel.queueSize);
                    for (ToSend toSend : perChannel.messagesQueue) {
                        if (!(toSend.toSend instanceof ByteBuf)) continue;
                        ((ByteBuf)toSend.toSend).release();
                    }
                }
                perChannel.messagesQueue.clear();
            }
        }
        this.releaseWriteSuspended(ctx);
        this.releaseReadSuspended(ctx);
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long size = this.calculateSize(msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L) {
            long waitGlobal = this.trafficCounter.readTimeToWait(size, this.getReadLimit(), this.maxTime, now);
            Integer key = ctx.channel().hashCode();
            PerChannel perChannel = this.channelQueues.get(key);
            long wait = 0L;
            if (perChannel != null) {
                wait = perChannel.channelTrafficCounter.readTimeToWait(size, this.readChannelLimit, this.maxTime, now);
                if (this.readDeviationActive) {
                    long maxLocalRead = perChannel.channelTrafficCounter.cumulativeReadBytes();
                    long maxGlobalRead = this.cumulativeReadBytes.get();
                    if (maxLocalRead <= 0L) {
                        maxLocalRead = 0L;
                    }
                    if (maxGlobalRead < maxLocalRead) {
                        maxGlobalRead = maxLocalRead;
                    }
                    wait = this.computeBalancedWait(maxLocalRead, maxGlobalRead, wait);
                }
            }
            if (wait < waitGlobal) {
                wait = waitGlobal;
            }
            if ((wait = this.checkWaitReadTime(ctx, wait, now)) >= 10L) {
                ChannelConfig config = ctx.channel().config();
                if (logger.isDebugEnabled()) {
                    logger.debug("Read Suspend: " + wait + ':' + config.isAutoRead() + ':' + GlobalChannelTrafficShapingHandler.isHandlerActive(ctx));
                }
                if (config.isAutoRead() && GlobalChannelTrafficShapingHandler.isHandlerActive(ctx)) {
                    config.setAutoRead(false);
                    ctx.attr(READ_SUSPENDED).set(true);
                    Attribute<Runnable> attr = ctx.attr(REOPEN_TASK);
                    Runnable reopenTask = (Runnable)attr.get();
                    if (reopenTask == null) {
                        reopenTask = new AbstractTrafficShapingHandler.ReopenReadTimerTask(ctx);
                        attr.set(reopenTask);
                    }
                    ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Suspend final status => " + config.isAutoRead() + ':' + GlobalChannelTrafficShapingHandler.isHandlerActive(ctx) + " will reopened at: " + wait);
                    }
                }
            }
        }
        this.informReadOperation(ctx, now);
        ctx.fireChannelRead(msg);
    }

    @Override
    protected long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        Integer key = ctx.channel().hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null && wait > this.maxTime && now + wait - perChannel.lastReadTimestamp > this.maxTime) {
            wait = this.maxTime;
        }
        return wait;
    }

    @Override
    protected void informReadOperation(ChannelHandlerContext ctx, long now) {
        Integer key = ctx.channel().hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null) {
            perChannel.lastReadTimestamp = now;
        }
    }

    protected long maximumCumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }

    protected long maximumCumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }

    public Collection<TrafficCounter> channelTrafficCounters() {
        return new AbstractCollection<TrafficCounter>(){

            @Override
            public Iterator<TrafficCounter> iterator() {
                return new Iterator<TrafficCounter>(){
                    final Iterator<PerChannel> iter;
                    {
                        this.iter = GlobalChannelTrafficShapingHandler.this.channelQueues.values().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }

                    @Override
                    public TrafficCounter next() {
                        return this.iter.next().channelTrafficCounter;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                return GlobalChannelTrafficShapingHandler.this.channelQueues.size();
            }

        };
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        long size = this.calculateSize(msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L) {
            long waitGlobal = this.trafficCounter.writeTimeToWait(size, this.getWriteLimit(), this.maxTime, now);
            Integer key = ctx.channel().hashCode();
            PerChannel perChannel = this.channelQueues.get(key);
            long wait = 0L;
            if (perChannel != null) {
                wait = perChannel.channelTrafficCounter.writeTimeToWait(size, this.writeChannelLimit, this.maxTime, now);
                if (this.writeDeviationActive) {
                    long maxLocalWrite = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
                    long maxGlobalWrite = this.cumulativeWrittenBytes.get();
                    if (maxLocalWrite <= 0L) {
                        maxLocalWrite = 0L;
                    }
                    if (maxGlobalWrite < maxLocalWrite) {
                        maxGlobalWrite = maxLocalWrite;
                    }
                    wait = this.computeBalancedWait(maxLocalWrite, maxGlobalWrite, wait);
                }
            }
            if (wait < waitGlobal) {
                wait = waitGlobal;
            }
            if (wait >= 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':' + GlobalChannelTrafficShapingHandler.isHandlerActive(ctx));
                }
                this.submitWrite(ctx, msg, size, wait, now, promise);
                return;
            }
        }
        this.submitWrite(ctx, msg, size, 0L, now, promise);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void submitWrite(final ChannelHandlerContext ctx, Object msg, long size, long writedelay, long now, ChannelPromise promise) {
        Channel channel = ctx.channel();
        Integer key = channel.hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel == null) {
            perChannel = this.getOrSetPerChannel(ctx);
        }
        long delay = writedelay;
        boolean globalSizeExceeded = false;
        PerChannel perChannel2 = perChannel;
        synchronized (perChannel2) {
            if (writedelay == 0L && perChannel.messagesQueue.isEmpty()) {
                this.trafficCounter.bytesRealWriteFlowControl(size);
                perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
                ctx.write(msg, promise);
                perChannel.lastWriteTimestamp = now;
                return;
            }
            if (delay > this.maxTime && now + delay - perChannel.lastWriteTimestamp > this.maxTime) {
                delay = this.maxTime;
            }
            ToSend newToSend = new ToSend(delay + now, msg, size, promise);
            perChannel.messagesQueue.addLast(newToSend);
            perChannel.queueSize += size;
            this.queuesSize.addAndGet(size);
            this.checkWriteSuspend(ctx, delay, perChannel.queueSize);
            if (this.queuesSize.get() > this.maxGlobalWriteSize) {
                globalSizeExceeded = true;
            }
        }
        if (globalSizeExceeded) {
            this.setUserDefinedWritability(ctx, false);
        }
        final long futureNow = newToSend.relativeTimeAction;
        final PerChannel forSchedule = perChannel;
        ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                GlobalChannelTrafficShapingHandler.this.sendAllValid(ctx, forSchedule, futureNow);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendAllValid(ChannelHandlerContext ctx, PerChannel perChannel, long now) {
        PerChannel perChannel2 = perChannel;
        synchronized (perChannel2) {
            ToSend newToSend = perChannel.messagesQueue.pollFirst();
            while (newToSend != null) {
                long size;
                if (newToSend.relativeTimeAction <= now) {
                    size = newToSend.size;
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                    perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
                    perChannel.queueSize -= size;
                } else {
                    perChannel.messagesQueue.addFirst(newToSend);
                    break;
                }
                this.queuesSize.addAndGet(- size);
                ctx.write(newToSend.toSend, newToSend.promise);
                perChannel.lastWriteTimestamp = now;
                newToSend = perChannel.messagesQueue.pollFirst();
            }
            if (perChannel.messagesQueue.isEmpty()) {
                this.releaseWriteSuspended(ctx);
            }
        }
        ctx.flush();
    }

    @Override
    public String toString() {
        return new StringBuilder(340).append(super.toString()).append(" Write Channel Limit: ").append(this.writeChannelLimit).append(" Read Channel Limit: ").append(this.readChannelLimit).toString();
    }

    private static final class ToSend {
        final long relativeTimeAction;
        final Object toSend;
        final ChannelPromise promise;
        final long size;

        private ToSend(long delay, Object toSend, long size, ChannelPromise promise) {
            this.relativeTimeAction = delay;
            this.toSend = toSend;
            this.size = size;
            this.promise = promise;
        }
    }

    static final class PerChannel {
        ArrayDeque<ToSend> messagesQueue;
        TrafficCounter channelTrafficCounter;
        long queueSize;
        long lastWriteTimestamp;
        long lastReadTimestamp;

        PerChannel() {
        }
    }

}

