/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.kqueue.AbstractKQueueServerChannel;
import io.netty.channel.kqueue.BsdSocket;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.kqueue.KQueueEventLoop;
import io.netty.channel.kqueue.KQueueServerSocketChannelConfig;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.unix.NativeInetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class KQueueServerSocketChannel
extends AbstractKQueueServerChannel
implements ServerSocketChannel {
    private final KQueueServerSocketChannelConfig config = new KQueueServerSocketChannelConfig(this);

    public KQueueServerSocketChannel() {
        super(BsdSocket.newSocketStream(), false);
    }

    public KQueueServerSocketChannel(int fd) {
        this(new BsdSocket(fd));
    }

    KQueueServerSocketChannel(BsdSocket fd) {
        super(fd);
    }

    KQueueServerSocketChannel(BsdSocket fd, boolean active) {
        super(fd, active);
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof KQueueEventLoop;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        this.socket.listen(this.config.getBacklog());
        this.active = true;
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
    public KQueueServerSocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected Channel newChildChannel(int fd, byte[] address, int offset, int len) throws Exception {
        return new KQueueSocketChannel(this, new BsdSocket(fd), NativeInetAddress.address(address, offset, len));
    }
}

