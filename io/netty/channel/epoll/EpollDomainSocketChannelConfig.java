/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.DomainSocketReadMode;
import java.util.Map;

public final class EpollDomainSocketChannelConfig
extends EpollChannelConfig
implements DomainSocketChannelConfig {
    private volatile DomainSocketReadMode mode = DomainSocketReadMode.BYTES;

    EpollDomainSocketChannelConfig(AbstractEpollChannel channel) {
        super(channel);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), EpollChannelOption.DOMAIN_SOCKET_READ_MODE);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == EpollChannelOption.DOMAIN_SOCKET_READ_MODE) {
            return (T)((Object)this.getReadMode());
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option != EpollChannelOption.DOMAIN_SOCKET_READ_MODE) {
            return super.setOption(option, value);
        }
        this.setReadMode((DomainSocketReadMode)((Object)value));
        return true;
    }

    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setEpollMode(EpollMode mode) {
        super.setEpollMode(mode);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setReadMode(DomainSocketReadMode mode) {
        if (mode == null) {
            throw new NullPointerException("mode");
        }
        this.mode = mode;
        return this;
    }

    @Override
    public DomainSocketReadMode getReadMode() {
        return this.mode;
    }
}

