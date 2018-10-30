/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.kqueue.AbstractKQueueChannel;
import io.netty.channel.kqueue.AbstractKQueueStreamChannel;
import io.netty.channel.kqueue.BsdSocket;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.kqueue.KQueueEventLoop;
import io.netty.channel.kqueue.KQueueSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executor;

public final class KQueueSocketChannel
extends AbstractKQueueStreamChannel
implements SocketChannel {
    private final KQueueSocketChannelConfig config = new KQueueSocketChannelConfig(this);

    public KQueueSocketChannel() {
        super(null, BsdSocket.newSocketStream(), false);
    }

    public KQueueSocketChannel(int fd) {
        super(new BsdSocket(fd));
    }

    KQueueSocketChannel(Channel parent, BsdSocket fd, InetSocketAddress remoteAddress) {
        super(parent, fd, remoteAddress);
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public KQueueSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
        return new KQueueSocketChannelUnsafe();
    }

    private final class KQueueSocketChannelUnsafe
    extends AbstractKQueueStreamChannel.KQueueStreamUnsafe {
        private KQueueSocketChannelUnsafe() {
        }

        @Override
        protected Executor prepareToClose() {
            try {
                if (KQueueSocketChannel.this.isOpen() && KQueueSocketChannel.this.config().getSoLinger() > 0) {
                    ((KQueueEventLoop)KQueueSocketChannel.this.eventLoop()).remove(KQueueSocketChannel.this);
                    return GlobalEventExecutor.INSTANCE;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return null;
        }
    }

}

