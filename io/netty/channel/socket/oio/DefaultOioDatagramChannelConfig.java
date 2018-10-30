/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.channel.socket.oio.OioDatagramChannelConfig;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

final class DefaultOioDatagramChannelConfig
extends DefaultDatagramChannelConfig
implements OioDatagramChannelConfig {
    DefaultOioDatagramChannelConfig(DatagramChannel channel, DatagramSocket javaSocket) {
        super(channel, javaSocket);
        this.setAllocator(new PreferHeapByteBufAllocator(this.getAllocator()));
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_TIMEOUT);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_TIMEOUT) {
            return this.getSoTimeout();
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option != ChannelOption.SO_TIMEOUT) {
            return super.setOption(option, value);
        }
        this.setSoTimeout((Integer)value);
        return true;
    }

    @Override
    public OioDatagramChannelConfig setSoTimeout(int timeout) {
        try {
            this.javaSocket().setSoTimeout(timeout);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
        return this;
    }

    @Override
    public int getSoTimeout() {
        try {
            return this.javaSocket().getSoTimeout();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public OioDatagramChannelConfig setBroadcast(boolean broadcast) {
        super.setBroadcast(broadcast);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setInterface(InetAddress interfaceAddress) {
        super.setInterface(interfaceAddress);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        super.setLoopbackModeDisabled(loopbackModeDisabled);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
        super.setNetworkInterface(networkInterface);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress(reuseAddress);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        super.setReceiveBufferSize(receiveBufferSize);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
        super.setSendBufferSize(sendBufferSize);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setTimeToLive(int ttl) {
        super.setTimeToLive(ttl);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setTrafficClass(int trafficClass) {
        super.setTrafficClass(trafficClass);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
}

