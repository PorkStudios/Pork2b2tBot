/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpSocketOption;
import com.sun.nio.sctp.SctpStandardSocketOptions;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.sctp.SctpChannelConfig;
import io.netty.channel.sctp.SctpChannelOption;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.util.Map;

public class DefaultSctpChannelConfig
extends DefaultChannelConfig
implements SctpChannelConfig {
    private final com.sun.nio.sctp.SctpChannel javaChannel;

    public DefaultSctpChannelConfig(SctpChannel channel, com.sun.nio.sctp.SctpChannel javaChannel) {
        super(channel);
        if (javaChannel == null) {
            throw new NullPointerException("javaChannel");
        }
        this.javaChannel = javaChannel;
        if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
            try {
                this.setSctpNoDelay(true);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, SctpChannelOption.SCTP_NODELAY, SctpChannelOption.SCTP_INIT_MAXSTREAMS);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_RCVBUF) {
            return this.getReceiveBufferSize();
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return this.getSendBufferSize();
        }
        if (option == SctpChannelOption.SCTP_NODELAY) {
            return this.isSctpNoDelay();
        }
        if (option == SctpChannelOption.SCTP_INIT_MAXSTREAMS) {
            return (T)this.getInitMaxStreams();
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer)value);
        } else if (option == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((Integer)value);
        } else if (option == SctpChannelOption.SCTP_NODELAY) {
            this.setSctpNoDelay((Boolean)value);
        } else if (option == SctpChannelOption.SCTP_INIT_MAXSTREAMS) {
            this.setInitMaxStreams((SctpStandardSocketOptions.InitMaxStreams)value);
        } else {
            return super.setOption(option, value);
        }
        return true;
    }

    @Override
    public boolean isSctpNoDelay() {
        try {
            return this.javaChannel.getOption(SctpStandardSocketOptions.SCTP_NODELAY);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public SctpChannelConfig setSctpNoDelay(boolean sctpNoDelay) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_NODELAY, sctpNoDelay);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
        return this;
    }

    @Override
    public int getSendBufferSize() {
        try {
            return this.javaChannel.getOption(SctpStandardSocketOptions.SO_SNDBUF);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public SctpChannelConfig setSendBufferSize(int sendBufferSize) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SO_SNDBUF, sendBufferSize);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
        return this;
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return this.javaChannel.getOption(SctpStandardSocketOptions.SO_RCVBUF);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public SctpChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SO_RCVBUF, receiveBufferSize);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
        return this;
    }

    @Override
    public SctpStandardSocketOptions.InitMaxStreams getInitMaxStreams() {
        try {
            return this.javaChannel.getOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public SctpChannelConfig setInitMaxStreams(SctpStandardSocketOptions.InitMaxStreams initMaxStreams) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS, initMaxStreams);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
        return this;
    }

    @Override
    public SctpChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public SctpChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public SctpChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public SctpChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public SctpChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public SctpChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    public SctpChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }

    @Override
    public SctpChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Override
    public SctpChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public SctpChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public SctpChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
}

