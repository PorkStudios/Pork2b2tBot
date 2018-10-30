/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import java.util.Deque;

public class SimpleChannelPool
implements ChannelPool {
    private static final AttributeKey<SimpleChannelPool> POOL_KEY = AttributeKey.newInstance("channelPool");
    private static final IllegalStateException FULL_EXCEPTION = ThrowableUtil.unknownStackTrace(new IllegalStateException("ChannelPool full"), SimpleChannelPool.class, "releaseAndOffer(...)");
    private final Deque<Channel> deque = PlatformDependent.newConcurrentDeque();
    private final ChannelPoolHandler handler;
    private final ChannelHealthChecker healthCheck;
    private final Bootstrap bootstrap;
    private final boolean releaseHealthCheck;
    private final boolean lastRecentUsed;

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
        this(bootstrap, handler, ChannelHealthChecker.ACTIVE);
    }

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck) {
        this(bootstrap, handler, healthCheck, true);
    }

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck) {
        this(bootstrap, handler, healthCheck, releaseHealthCheck, true);
    }

    public SimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck, boolean lastRecentUsed) {
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        this.healthCheck = ObjectUtil.checkNotNull(healthCheck, "healthCheck");
        this.releaseHealthCheck = releaseHealthCheck;
        this.bootstrap = ObjectUtil.checkNotNull(bootstrap, "bootstrap").clone();
        this.bootstrap.handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel ch) throws Exception {
                assert (ch.eventLoop().inEventLoop());
                handler.channelCreated(ch);
            }
        });
        this.lastRecentUsed = lastRecentUsed;
    }

    protected Bootstrap bootstrap() {
        return this.bootstrap;
    }

    protected ChannelPoolHandler handler() {
        return this.handler;
    }

    protected ChannelHealthChecker healthChecker() {
        return this.healthCheck;
    }

    protected boolean releaseHealthCheck() {
        return this.releaseHealthCheck;
    }

    @Override
    public final Future<Channel> acquire() {
        return this.acquire(this.bootstrap.config().group().next().newPromise());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        return this.acquireHealthyFromPoolOrNew(promise);
    }

    private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
        try {
            final Channel ch = this.pollChannel();
            if (ch == null) {
                Bootstrap bs = this.bootstrap.clone();
                bs.attr(POOL_KEY, this);
                ChannelFuture f = this.connectChannel(bs);
                if (f.isDone()) {
                    this.notifyConnect(f, promise);
                } else {
                    f.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            SimpleChannelPool.this.notifyConnect(future, promise);
                        }
                    });
                }
                return promise;
            }
            EventLoop loop = ch.eventLoop();
            if (loop.inEventLoop()) {
                this.doHealthCheck(ch, promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        SimpleChannelPool.this.doHealthCheck(ch, promise);
                    }
                });
            }
        }
        catch (Throwable cause) {
            promise.tryFailure(cause);
        }
        return promise;
    }

    private void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
        if (future.isSuccess()) {
            Channel channel = future.channel();
            if (!promise.trySuccess(channel)) {
                this.release(channel);
            }
        } else {
            promise.tryFailure(future.cause());
        }
    }

    private void doHealthCheck(final Channel ch, final Promise<Channel> promise) {
        assert (ch.eventLoop().inEventLoop());
        Future<Boolean> f = this.healthCheck.isHealthy(ch);
        if (f.isDone()) {
            this.notifyHealthCheck(f, ch, promise);
        } else {
            f.addListener((GenericFutureListener<Future<Boolean>>)new FutureListener<Boolean>(){

                @Override
                public void operationComplete(Future<Boolean> future) throws Exception {
                    SimpleChannelPool.this.notifyHealthCheck(future, ch, promise);
                }
            });
        }
    }

    private void notifyHealthCheck(Future<Boolean> future, Channel ch, Promise<Channel> promise) {
        assert (ch.eventLoop().inEventLoop());
        if (future.isSuccess()) {
            if (future.getNow().booleanValue()) {
                try {
                    ch.attr(POOL_KEY).set(this);
                    this.handler.channelAcquired(ch);
                    promise.setSuccess(ch);
                }
                catch (Throwable cause) {
                    SimpleChannelPool.closeAndFail(ch, cause, promise);
                }
            } else {
                SimpleChannelPool.closeChannel(ch);
                this.acquireHealthyFromPoolOrNew(promise);
            }
        } else {
            SimpleChannelPool.closeChannel(ch);
            this.acquireHealthyFromPoolOrNew(promise);
        }
    }

    protected ChannelFuture connectChannel(Bootstrap bs) {
        return bs.connect();
    }

    @Override
    public final Future<Void> release(Channel channel) {
        return this.release(channel, channel.eventLoop().newPromise());
    }

    @Override
    public Future<Void> release(final Channel channel, final Promise<Void> promise) {
        ObjectUtil.checkNotNull(channel, "channel");
        ObjectUtil.checkNotNull(promise, "promise");
        try {
            EventLoop loop = channel.eventLoop();
            if (loop.inEventLoop()) {
                this.doReleaseChannel(channel, promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        SimpleChannelPool.this.doReleaseChannel(channel, promise);
                    }
                });
            }
        }
        catch (Throwable cause) {
            SimpleChannelPool.closeAndFail(channel, cause, promise);
        }
        return promise;
    }

    private void doReleaseChannel(Channel channel, Promise<Void> promise) {
        assert (channel.eventLoop().inEventLoop());
        if (channel.attr(POOL_KEY).getAndSet(null) != this) {
            SimpleChannelPool.closeAndFail(channel, new IllegalArgumentException("Channel " + channel + " was not acquired from this ChannelPool"), promise);
        } else {
            try {
                if (this.releaseHealthCheck) {
                    this.doHealthCheckOnRelease(channel, promise);
                } else {
                    this.releaseAndOffer(channel, promise);
                }
            }
            catch (Throwable cause) {
                SimpleChannelPool.closeAndFail(channel, cause, promise);
            }
        }
    }

    private void doHealthCheckOnRelease(final Channel channel, final Promise<Void> promise) throws Exception {
        final Future<Boolean> f = this.healthCheck.isHealthy(channel);
        if (f.isDone()) {
            this.releaseAndOfferIfHealthy(channel, promise, f);
        } else {
            f.addListener((GenericFutureListener<Future<Boolean>>)new FutureListener<Boolean>(){

                @Override
                public void operationComplete(Future<Boolean> future) throws Exception {
                    SimpleChannelPool.this.releaseAndOfferIfHealthy(channel, promise, f);
                }
            });
        }
    }

    private void releaseAndOfferIfHealthy(Channel channel, Promise<Void> promise, Future<Boolean> future) throws Exception {
        if (future.getNow().booleanValue()) {
            this.releaseAndOffer(channel, promise);
        } else {
            this.handler.channelReleased(channel);
            promise.setSuccess(null);
        }
    }

    private void releaseAndOffer(Channel channel, Promise<Void> promise) throws Exception {
        if (this.offerChannel(channel)) {
            this.handler.channelReleased(channel);
            promise.setSuccess(null);
        } else {
            SimpleChannelPool.closeAndFail(channel, FULL_EXCEPTION, promise);
        }
    }

    private static void closeChannel(Channel channel) {
        channel.attr(POOL_KEY).getAndSet(null);
        channel.close();
    }

    private static void closeAndFail(Channel channel, Throwable cause, Promise<?> promise) {
        SimpleChannelPool.closeChannel(channel);
        promise.tryFailure(cause);
    }

    protected Channel pollChannel() {
        return this.lastRecentUsed ? this.deque.pollLast() : this.deque.pollFirst();
    }

    protected boolean offerChannel(Channel channel) {
        return this.deque.offer(channel);
    }

    @Override
    public void close() {
        Channel channel;
        while ((channel = this.pollChannel()) != null) {
            channel.close();
        }
    }

}

