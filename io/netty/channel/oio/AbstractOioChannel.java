/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import java.net.SocketAddress;

public abstract class AbstractOioChannel
extends AbstractChannel {
    protected static final int SO_TIMEOUT = 1000;
    boolean readPending;
    private final Runnable readTask = new Runnable(){

        @Override
        public void run() {
            AbstractOioChannel.this.doRead();
        }
    };
    private final Runnable clearReadPendingRunnable = new Runnable(){

        @Override
        public void run() {
            AbstractOioChannel.this.readPending = false;
        }
    };

    protected AbstractOioChannel(Channel parent) {
        super(parent);
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new DefaultOioUnsafe();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof ThreadPerChannelEventLoop;
    }

    protected abstract void doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

    @Override
    protected void doBeginRead() throws Exception {
        if (this.readPending) {
            return;
        }
        this.readPending = true;
        this.eventLoop().execute(this.readTask);
    }

    protected abstract void doRead();

    @Deprecated
    protected boolean isReadPending() {
        return this.readPending;
    }

    @Deprecated
    protected void setReadPending(final boolean readPending) {
        if (this.isRegistered()) {
            EventLoop eventLoop = this.eventLoop();
            if (eventLoop.inEventLoop()) {
                this.readPending = readPending;
            } else {
                eventLoop.execute(new Runnable(){

                    @Override
                    public void run() {
                        AbstractOioChannel.this.readPending = readPending;
                    }
                });
            }
        } else {
            this.readPending = readPending;
        }
    }

    protected final void clearReadPending() {
        if (this.isRegistered()) {
            EventLoop eventLoop = this.eventLoop();
            if (eventLoop.inEventLoop()) {
                this.readPending = false;
            } else {
                eventLoop.execute(this.clearReadPendingRunnable);
            }
        } else {
            this.readPending = false;
        }
    }

    private final class DefaultOioUnsafe
    extends AbstractChannel.AbstractUnsafe {
        private DefaultOioUnsafe() {
        }

        @Override
        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                boolean wasActive = AbstractOioChannel.this.isActive();
                AbstractOioChannel.this.doConnect(remoteAddress, localAddress);
                boolean active = AbstractOioChannel.this.isActive();
                this.safeSetSuccess(promise);
                if (!wasActive && active) {
                    AbstractOioChannel.this.pipeline().fireChannelActive();
                }
            }
            catch (Throwable t) {
                this.safeSetFailure(promise, this.annotateConnectException(t, remoteAddress));
                this.closeIfClosed();
            }
        }
    }

}

