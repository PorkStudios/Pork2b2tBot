/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.kqueue.AbstractKQueueChannel;
import io.netty.channel.kqueue.AcceptFilter;
import io.netty.channel.kqueue.BsdSocket;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.kqueue.KQueueChannelOption;
import io.netty.channel.kqueue.KQueueServerChannelConfig;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.unix.UnixChannelOption;
import java.io.IOException;
import java.util.Map;

public class KQueueServerSocketChannelConfig
extends KQueueServerChannelConfig
implements ServerSocketChannelConfig {
    KQueueServerSocketChannelConfig(KQueueServerSocketChannel channel) {
        super(channel);
        this.setReuseAddress(true);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), UnixChannelOption.SO_REUSEPORT, KQueueChannelOption.SO_ACCEPTFILTER);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == UnixChannelOption.SO_REUSEPORT) {
            return this.isReusePort();
        }
        if (option == KQueueChannelOption.SO_ACCEPTFILTER) {
            return (T)this.getAcceptFilter();
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == UnixChannelOption.SO_REUSEPORT) {
            this.setReusePort((Boolean)value);
        } else if (option == KQueueChannelOption.SO_ACCEPTFILTER) {
            this.setAcceptFilter((AcceptFilter)value);
        } else {
            return super.setOption(option, value);
        }
        return true;
    }

    public KQueueServerSocketChannelConfig setReusePort(boolean reusePort) {
        try {
            this.channel.socket.setReusePort(reusePort);
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

    public KQueueServerSocketChannelConfig setAcceptFilter(AcceptFilter acceptFilter) {
        try {
            this.channel.socket.setAcceptFilter(acceptFilter);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public AcceptFilter getAcceptFilter() {
        try {
            return this.channel.socket.getAcceptFilter();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public KQueueServerSocketChannelConfig setRcvAllocTransportProvidesGuess(boolean transportProvidesGuess) {
        super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress(reuseAddress);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        super.setReceiveBufferSize(receiveBufferSize);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setBacklog(int backlog) {
        super.setBacklog(backlog);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public KQueueServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Deprecated
    @Override
    public KQueueServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public KQueueServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public KQueueServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
}

