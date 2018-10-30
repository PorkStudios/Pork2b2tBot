/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollServerChannelConfig;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.socket.ServerSocketChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollServerSocketChannelConfig
extends EpollServerChannelConfig
implements ServerSocketChannelConfig {
    EpollServerSocketChannelConfig(EpollServerSocketChannel channel) {
        super(channel);
        this.setReuseAddress(true);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_FREEBIND, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.TCP_DEFER_ACCEPT);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == EpollChannelOption.SO_REUSEPORT) {
            return this.isReusePort();
        }
        if (option == EpollChannelOption.IP_FREEBIND) {
            return this.isFreeBind();
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            return this.isIpTransparent();
        }
        if (option == EpollChannelOption.TCP_DEFER_ACCEPT) {
            return this.getTcpDeferAccept();
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == EpollChannelOption.SO_REUSEPORT) {
            this.setReusePort((Boolean)value);
        } else if (option == EpollChannelOption.IP_FREEBIND) {
            this.setFreeBind((Boolean)value);
        } else if (option == EpollChannelOption.IP_TRANSPARENT) {
            this.setIpTransparent((Boolean)value);
        } else if (option == EpollChannelOption.TCP_MD5SIG) {
            Map m = (Map)value;
            this.setTcpMd5Sig(m);
        } else if (option == EpollChannelOption.TCP_DEFER_ACCEPT) {
            this.setTcpDeferAccept((Integer)value);
        } else {
            return super.setOption(option, value);
        }
        return true;
    }

    @Override
    public EpollServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress(reuseAddress);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        super.setReceiveBufferSize(receiveBufferSize);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setBacklog(int backlog) {
        super.setBacklog(backlog);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    public EpollServerSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> keys) {
        try {
            ((EpollServerSocketChannel)this.channel).setTcpMd5Sig(keys);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public boolean isReusePort() {
        try {
            return this.channel.socket.isReusePort();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollServerSocketChannelConfig setReusePort(boolean reusePort) {
        try {
            this.channel.socket.setReusePort(reusePort);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public boolean isFreeBind() {
        try {
            return this.channel.socket.isIpFreeBind();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollServerSocketChannelConfig setFreeBind(boolean freeBind) {
        try {
            this.channel.socket.setIpFreeBind(freeBind);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public boolean isIpTransparent() {
        try {
            return this.channel.socket.isIpTransparent();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollServerSocketChannelConfig setIpTransparent(boolean transparent) {
        try {
            this.channel.socket.setIpTransparent(transparent);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollServerSocketChannelConfig setTcpDeferAccept(int deferAccept) {
        try {
            this.channel.socket.setTcpDeferAccept(deferAccept);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public int getTcpDeferAccept() {
        try {
            return this.channel.socket.getTcpDeferAccept();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }
}

