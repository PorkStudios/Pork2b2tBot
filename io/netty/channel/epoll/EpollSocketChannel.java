/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollStreamChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannelConfig;
import io.netty.channel.epoll.EpollTcpInfo;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.TcpMd5Util;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;

public final class EpollSocketChannel
extends AbstractEpollStreamChannel
implements SocketChannel {
    private final EpollSocketChannelConfig config = new EpollSocketChannelConfig(this);
    private volatile Collection<InetAddress> tcpMd5SigAddresses = Collections.emptyList();

    public EpollSocketChannel() {
        super(LinuxSocket.newSocketStream(), false);
    }

    public EpollSocketChannel(int fd) {
        super(fd);
    }

    EpollSocketChannel(LinuxSocket fd, boolean active) {
        super(fd, active);
    }

    EpollSocketChannel(Channel parent, LinuxSocket fd, InetSocketAddress remoteAddress) {
        super(parent, fd, remoteAddress);
        if (parent instanceof EpollServerSocketChannel) {
            this.tcpMd5SigAddresses = ((EpollServerSocketChannel)parent).tcpMd5SigAddresses();
        }
    }

    public EpollTcpInfo tcpInfo() {
        return this.tcpInfo(new EpollTcpInfo());
    }

    public EpollTcpInfo tcpInfo(EpollTcpInfo info) {
        try {
            this.socket.getTcpInfo(info);
            return info;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
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
    public EpollSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollSocketChannelUnsafe();
    }

    void setTcpMd5Sig(Map<InetAddress, byte[]> keys) throws IOException {
        this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, keys);
    }

    private final class EpollSocketChannelUnsafe
    extends AbstractEpollStreamChannel.EpollStreamUnsafe {
        private EpollSocketChannelUnsafe() {
            super(EpollSocketChannel.this);
        }

        @Override
        protected Executor prepareToClose() {
            try {
                if (EpollSocketChannel.this.isOpen() && EpollSocketChannel.this.config().getSoLinger() > 0) {
                    ((EpollEventLoop)EpollSocketChannel.this.eventLoop()).remove(EpollSocketChannel.this);
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

