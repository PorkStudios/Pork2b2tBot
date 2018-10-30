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
import io.netty.channel.kqueue.BsdSocket;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.util.Map;

public class KQueueServerChannelConfig
extends KQueueChannelConfig
implements ServerSocketChannelConfig {
    protected final AbstractKQueueChannel channel;
    private volatile int backlog = NetUtil.SOMAXCONN;

    KQueueServerChannelConfig(AbstractKQueueChannel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG);
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
    public KQueueServerChannelConfig setReuseAddress(boolean reuseAddress) {
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
    public KQueueServerChannelConfig setReceiveBufferSize(int receiveBufferSize) {
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
    public KQueueServerChannelConfig setBacklog(int backlog) {
        if (backlog < 0) {
            throw new IllegalArgumentException("backlog: " + backlog);
        }
        this.backlog = backlog;
        return this;
    }

    @Override
    public KQueueServerChannelConfig setRcvAllocTransportProvidesGuess(boolean transportProvidesGuess) {
        super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
        return this;
    }

    @Override
    public KQueueServerChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public KQueueServerChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public KQueueServerChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public KQueueServerChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public KQueueServerChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public KQueueServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public KQueueServerChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Deprecated
    @Override
    public KQueueServerChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public KQueueServerChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public KQueueServerChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public KQueueServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
}

