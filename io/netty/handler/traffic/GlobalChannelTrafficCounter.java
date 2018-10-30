/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.traffic;

import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class GlobalChannelTrafficCounter
extends TrafficCounter {
    public GlobalChannelTrafficCounter(GlobalChannelTrafficShapingHandler trafficShapingHandler, ScheduledExecutorService executor, String name, long checkInterval) {
        super(trafficShapingHandler, executor, name, checkInterval);
        if (executor == null) {
            throw new IllegalArgumentException("Executor must not be null");
        }
    }

    @Override
    public synchronized void start() {
        if (this.monitorActive) {
            return;
        }
        this.lastTime.set(GlobalChannelTrafficCounter.milliSecondFromNano());
        long localCheckInterval = this.checkInterval.get();
        if (localCheckInterval > 0L) {
            this.monitorActive = true;
            this.monitor = new MixedTrafficMonitoringTask((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler, this);
            this.scheduledFuture = this.executor.schedule(this.monitor, localCheckInterval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public synchronized void stop() {
        if (!this.monitorActive) {
            return;
        }
        this.monitorActive = false;
        this.resetAccounting(GlobalChannelTrafficCounter.milliSecondFromNano());
        this.trafficShapingHandler.doAccounting(this);
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
    }

    @Override
    public void resetCumulativeTime() {
        for (GlobalChannelTrafficShapingHandler.PerChannel perChannel : ((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler).channelQueues.values()) {
            perChannel.channelTrafficCounter.resetCumulativeTime();
        }
        super.resetCumulativeTime();
    }

    private static class MixedTrafficMonitoringTask
    implements Runnable {
        private final GlobalChannelTrafficShapingHandler trafficShapingHandler1;
        private final TrafficCounter counter;

        MixedTrafficMonitoringTask(GlobalChannelTrafficShapingHandler trafficShapingHandler, TrafficCounter counter) {
            this.trafficShapingHandler1 = trafficShapingHandler;
            this.counter = counter;
        }

        @Override
        public void run() {
            if (!this.counter.monitorActive) {
                return;
            }
            long newLastTime = TrafficCounter.milliSecondFromNano();
            this.counter.resetAccounting(newLastTime);
            for (GlobalChannelTrafficShapingHandler.PerChannel perChannel : this.trafficShapingHandler1.channelQueues.values()) {
                perChannel.channelTrafficCounter.resetAccounting(newLastTime);
            }
            this.trafficShapingHandler1.doAccounting(this.counter);
            this.counter.scheduledFuture = this.counter.executor.schedule(this, this.counter.checkInterval.get(), TimeUnit.MILLISECONDS);
        }
    }

}

