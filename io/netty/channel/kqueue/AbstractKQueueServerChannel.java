/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.ServerChannel;
import io.netty.channel.kqueue.AbstractKQueueChannel;
import io.netty.channel.kqueue.BsdSocket;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.kqueue.KQueueEventLoop;
import io.netty.channel.kqueue.KQueueRecvByteAllocatorHandle;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class AbstractKQueueServerChannel
extends AbstractKQueueChannel
implements ServerChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);

    AbstractKQueueServerChannel(BsdSocket fd) {
        this(fd, AbstractKQueueServerChannel.isSoErrorZero(fd));
    }

    AbstractKQueueServerChannel(BsdSocket fd, boolean active) {
        super(null, fd, active);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof KQueueEventLoop;
    }

    @Override
    protected InetSocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
        return new KQueueServerSocketUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    abstract Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception;

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    final class KQueueServerSocketUnsafe
    extends AbstractKQueueChannel.AbstractKQueueUnsafe {
        private final byte[] acceptedAddress;

        KQueueServerSocketUnsafe() {
            super(AbstractKQueueServerChannel.this);
            this.acceptedAddress = new byte[26];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void readReady(KQueueRecvByteAllocatorHandle allocHandle) {
            assert (AbstractKQueueServerChannel.this.eventLoop().inEventLoop());
            KQueueChannelConfig config = AbstractKQueueServerChannel.this.config();
            if (AbstractKQueueServerChannel.this.shouldBreakReadReady(config)) {
                this.clearReadFilter0();
                return;
            }
            ChannelPipeline pipeline = AbstractKQueueServerChannel.this.pipeline();
            allocHandle.reset(config);
            allocHandle.attemptedBytesRead(1);
            this.readReadyBefore();
            Throwable exception = null;
            try {
                try {
                    do {
                        int acceptFd;
                        if ((acceptFd = AbstractKQueueServerChannel.this.socket.accept(this.acceptedAddress)) == -1) {
                            allocHandle.lastBytesRead(-1);
                            break;
                        }
                        allocHandle.lastBytesRead(1);
                        allocHandle.incMessagesRead(1);
                        this.readPending = false;
                        pipeline.fireChannelRead(AbstractKQueueServerChannel.this.newChildChannel(acceptFd, this.acceptedAddress, 1, this.acceptedAddress[0]));
                    } while (allocHandle.continueReading());
                }
                catch (Throwable t) {
                    exception = t;
                }
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    pipeline.fireExceptionCaught(exception);
                }
            }
            finally {
                this.readReadyFinally(config);
            }
        }
    }

}

