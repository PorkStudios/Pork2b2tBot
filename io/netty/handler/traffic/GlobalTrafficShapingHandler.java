/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class GlobalTrafficShapingHandler
extends AbstractTrafficShapingHandler {
    private final ConcurrentMap<Integer, PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
    private final AtomicLong queuesSize = new AtomicLong();
    long maxGlobalWriteSize = 419430400L;

    void createGlobalTrafficCounter(ScheduledExecutorService executor) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        TrafficCounter tc = new TrafficCounter(this, executor, "GlobalTC", this.checkInterval);
        this.setTrafficCounter(tc);
        tc.start();
    }

    @Override
    protected int userDefinedWritabilityIndex() {
        return 2;
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(writeLimit, readLimit, checkInterval, maxTime);
        this.createGlobalTrafficCounter(executor);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval) {
        super(writeLimit, readLimit, checkInterval);
        this.createGlobalTrafficCounter(executor);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit) {
        super(writeLimit, readLimit);
        this.createGlobalTrafficCounter(executor);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
        super(checkInterval);
        this.createGlobalTrafficCounter(executor);
    }

    public GlobalTrafficShapingHandler(EventExecutor executor) {
        this.createGlobalTrafficCounter(executor);
    }

    public long getMaxGlobalWriteSize() {
        return this.maxGlobalWriteSize;
    }

    public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
        this.maxGlobalWriteSize = maxGlobalWriteSize;
    }

    public long queuesSize() {
        return this.queuesSize.get();
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
            perChannel.queueSize = 0L;
            perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
            this.channelQueues.put(key, perChannel);
        }
        return perChannel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.getOrSetPerChannel(ctx);
        super.handlerAdded(ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
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
    long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        Integer key = ctx.channel().hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null && wait > this.maxTime && now + wait - perChannel.lastReadTimestamp > this.maxTime) {
            wait = this.maxTime;
        }
        return wait;
    }

    @Override
    void informReadOperation(ChannelHandlerContext ctx, long now) {
        Integer key = ctx.channel().hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null) {
            perChannel.lastReadTimestamp = now;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void submitWrite(final ChannelHandlerContext ctx, Object msg, long size, long writedelay, long now, ChannelPromise promise) {
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
                GlobalTrafficShapingHandler.this.sendAllValid(ctx, forSchedule, futureNow);
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

    private static final class ToSend {
        final long relativeTimeAction;
        final Object toSend;
        final long size;
        final ChannelPromise promise;

        private ToSend(long delay, Object toSend, long size, ChannelPromise promise) {
            this.relativeTimeAction = delay;
            this.toSend = toSend;
            this.size = size;
            this.promise = promise;
        }
    }

    private static final class PerChannel {
        ArrayDeque<ToSend> messagesQueue;
        long queueSize;
        long lastWriteTimestamp;
        long lastReadTimestamp;

        private PerChannel() {
        }
    }

}

