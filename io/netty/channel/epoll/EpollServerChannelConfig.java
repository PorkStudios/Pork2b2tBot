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
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.util.Map;

public class EpollServerChannelConfig
extends EpollChannelConfig
implements ServerSocketChannelConfig {
    protected final AbstractEpollChannel channel;
    private volatile int backlog = NetUtil.SOMAXCONN;
    private volatile int pendingFastOpenRequestsThreshold;

    EpollServerChannelConfig(AbstractEpollChannel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG, EpollChannelOption.TCP_FASTOPEN);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_RCVBUF) {
            return this.getReceiveBufferSize();
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return this.isReuseAddress();
        }
        if (option == ChannelOption.SO_BACKLOG) {
            return this.getBacklog();
        }
        if (option == EpollChannelOption.TCP_FASTOPEN) {
            return this.getTcpFastopen();
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer)value);
        } else if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((Boolean)value);
        } else if (option == ChannelOption.SO_BACKLOG) {
            this.setBacklog((Integer)value);
        } else if (option == EpollChannelOption.TCP_FASTOPEN) {
            this.setTcpFastopen((Integer)value);
        } else {
            return super.setOption(option, value);
        }
        return true;
    }

    @Override
    public boolean isReuseAddress() {
        try {
            return this.channel.socket.isReuseAddress();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollServerChannelConfig setReuseAddress(boolean reuseAddress) {
        try {
            this.channel.socket.setReuseAddress(reuseAddress);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return this.channel.socket.getReceiveBufferSize();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollServerChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            this.channel.socket.setReceiveBufferSize(receiveBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public int getBacklog() {
        return this.backlog;
    }

    @Override
    public EpollServerChannelConfig setBacklog(int backlog) {
        if (backlog < 0) {
            throw new IllegalArgumentException("backlog: " + backlog);
        }
        this.backlog = backlog;
        return this;
    }

    public int getTcpFastopen() {
        return this.pendingFastOpenRequestsThreshold;
    }

    public EpollServerChannelConfig setTcpFastopen(int pendingFastOpenRequestsThreshold) {
        if (this.pendingFastOpenRequestsThreshold < 0) {
            throw new IllegalArgumentException("pendingFastOpenRequestsThreshold: " + pendingFastOpenRequestsThreshold);
        }
        this.pendingFastOpenRequestsThreshold = pendingFastOpenRequestsThreshold;
        return this;
    }

    @Override
    public EpollServerChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public EpollServerChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollServerChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public EpollServerChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public EpollServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public EpollServerChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollServerChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    @Override
    public EpollServerChannelConfig setEpollMode(EpollMode mode) {
        super.setEpollMode(mode);
        return this;
    }
}

