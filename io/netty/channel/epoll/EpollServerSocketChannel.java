/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.AbstractEpollServerChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollServerSocketChannelConfig;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.TcpMd5Util;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.unix.NativeInetAddress;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class EpollServerSocketChannel
extends AbstractEpollServerChannel
implements ServerSocketChannel {
    private final EpollServerSocketChannelConfig config = new EpollServerSocketChannelConfig(this);
    private volatile Collection<InetAddress> tcpMd5SigAddresses = Collections.emptyList();

    public EpollServerSocketChannel() {
        super(LinuxSocket.newSocketStream(), false);
    }

    public EpollServerSocketChannel(int fd) {
        this(new LinuxSocket(fd));
    }

    EpollServerSocketChannel(LinuxSocket fd) {
        super(fd);
    }

    EpollServerSocketChannel(LinuxSocket fd, boolean active) {
        super(fd, active);
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        if (Native.IS_SUPPORTING_TCP_FASTOPEN && this.config.getTcpFastopen() > 0) {
            this.socket.setTcpFastOpen(this.config.getTcpFastopen());
        }
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
    public EpollServerSocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected Channel newChildChannel(int fd, byte[] address, int offset, int len) throws Exception {
        return new EpollSocketChannel(this, new LinuxSocket(fd), NativeInetAddress.address(address, offset, len));
    }

    Collection<InetAddress> tcpMd5SigAddresses() {
        return this.tcpMd5SigAddresses;
    }

    void setTcpMd5Sig(Map<InetAddress, byte[]> keys) throws IOException {
        this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, keys);
    }
}

