/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.kqueue.AbstractKQueueChannel;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.DomainSocketReadMode;
import io.netty.channel.unix.UnixChannelOption;
import java.util.Map;

public final class KQueueDomainSocketChannelConfig
extends KQueueChannelConfig
implements DomainSocketChannelConfig {
    private volatile DomainSocketReadMode mode = DomainSocketReadMode.BYTES;

    KQueueDomainSocketChannelConfig(AbstractKQueueChannel channel) {
        super(channel);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), UnixChannelOption.DOMAIN_SOCKET_READ_MODE);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
            return (T)((Object)this.getReadMode());
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option != UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
            return super.setOption(option, value);
        }
        this.setReadMode((DomainSocketReadMode)((Object)value));
        return true;
    }

    @Override
    public KQueueDomainSocketChannelConfig setRcvAllocTransportProvidesGuess(boolean transportProvidesGuess) {
        super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
        return this;
    }

    @Deprecated
    @Override
    public KQueueDomainSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    @Deprecated
    @Override
    public KQueueDomainSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public KQueueDomainSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    public KQueueDomainSocketChannelConfig setReadMode(DomainSocketReadMode mode) {
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

