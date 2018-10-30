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
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollSocketChannelConfig
extends EpollChannelConfig
implements SocketChannelConfig {
    private final EpollSocketChannel channel;
    private volatile boolean allowHalfClosure;

    EpollSocketChannelConfig(EpollSocketChannel channel) {
        super(channel);
        this.channel = channel;
        if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
            this.setTcpNoDelay(true);
        }
        this.calculateMaxBytesPerGatheringWrite();
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE, EpollChannelOption.TCP_CORK, EpollChannelOption.TCP_NOTSENT_LOWAT, EpollChannelOption.TCP_KEEPCNT, EpollChannelOption.TCP_KEEPIDLE, EpollChannelOption.TCP_KEEPINTVL, EpollChannelOption.TCP_MD5SIG, EpollChannelOption.TCP_QUICKACK, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.TCP_FASTOPEN_CONNECT);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_RCVBUF) {
            return this.getReceiveBufferSize();
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return this.getSendBufferSize();
        }
        if (option == ChannelOption.TCP_NODELAY) {
            return this.isTcpNoDelay();
        }
        if (option == ChannelOption.SO_KEEPALIVE) {
            return this.isKeepAlive();
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return this.isReuseAddress();
        }
        if (option == ChannelOption.SO_LINGER) {
            return this.getSoLinger();
        }
        if (option == ChannelOption.IP_TOS) {
            return this.getTrafficClass();
        }
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            return this.isAllowHalfClosure();
        }
        if (option == EpollChannelOption.TCP_CORK) {
            return this.isTcpCork();
        }
        if (option == EpollChannelOption.TCP_NOTSENT_LOWAT) {
            return this.getTcpNotSentLowAt();
        }
        if (option == EpollChannelOption.TCP_KEEPIDLE) {
            return this.getTcpKeepIdle();
        }
        if (option == EpollChannelOption.TCP_KEEPINTVL) {
            return this.getTcpKeepIntvl();
        }
        if (option == EpollChannelOption.TCP_KEEPCNT) {
            return this.getTcpKeepCnt();
        }
        if (option == EpollChannelOption.TCP_USER_TIMEOUT) {
            return this.getTcpUserTimeout();
        }
        if (option == EpollChannelOption.TCP_QUICKACK) {
            return this.isTcpQuickAck();
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            return this.isIpTransparent();
        }
        if (option == EpollChannelOption.TCP_FASTOPEN_CONNECT) {
            return this.isTcpFastOpenConnect();
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
        } else if (option == ChannelOption.TCP_NODELAY) {
            this.setTcpNoDelay((Boolean)value);
        } else if (option == ChannelOption.SO_KEEPALIVE) {
            this.setKeepAlive((Boolean)value);
        } else if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((Boolean)value);
        } else if (option == ChannelOption.SO_LINGER) {
            this.setSoLinger((Integer)value);
        } else if (option == ChannelOption.IP_TOS) {
            this.setTrafficClass((Integer)value);
        } else if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            this.setAllowHalfClosure((Boolean)value);
        } else if (option == EpollChannelOption.TCP_CORK) {
            this.setTcpCork((Boolean)value);
        } else if (option == EpollChannelOption.TCP_NOTSENT_LOWAT) {
            this.setTcpNotSentLowAt((Long)value);
        } else if (option == EpollChannelOption.TCP_KEEPIDLE) {
            this.setTcpKeepIdle((Integer)value);
        } else if (option == EpollChannelOption.TCP_KEEPCNT) {
            this.setTcpKeepCnt((Integer)value);
        } else if (option == EpollChannelOption.TCP_KEEPINTVL) {
            this.setTcpKeepIntvl((Integer)value);
        } else if (option == EpollChannelOption.TCP_USER_TIMEOUT) {
            this.setTcpUserTimeout((Integer)value);
        } else if (option == EpollChannelOption.IP_TRANSPARENT) {
            this.setIpTransparent((Boolean)value);
        } else if (option == EpollChannelOption.TCP_MD5SIG) {
            Map m = (Map)value;
            this.setTcpMd5Sig(m);
        } else if (option == EpollChannelOption.TCP_QUICKACK) {
            this.setTcpQuickAck((Boolean)value);
        } else if (option == EpollChannelOption.TCP_FASTOPEN_CONNECT) {
            this.setTcpFastOpenConnect((Boolean)value);
        } else {
            return super.setOption(option, value);
        }
        return true;
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
    public int getSendBufferSize() {
        try {
            return this.channel.socket.getSendBufferSize();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public int getSoLinger() {
        try {
            return this.channel.socket.getSoLinger();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public int getTrafficClass() {
        try {
            return this.channel.socket.getTrafficClass();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public boolean isKeepAlive() {
        try {
            return this.channel.socket.isKeepAlive();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
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
    public boolean isTcpNoDelay() {
        try {
            return this.channel.socket.isTcpNoDelay();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public boolean isTcpCork() {
        try {
            return this.channel.socket.isTcpCork();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public long getTcpNotSentLowAt() {
        try {
            return this.channel.socket.getTcpNotSentLowAt();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public int getTcpKeepIdle() {
        try {
            return this.channel.socket.getTcpKeepIdle();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public int getTcpKeepIntvl() {
        try {
            return this.channel.socket.getTcpKeepIntvl();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public int getTcpKeepCnt() {
        try {
            return this.channel.socket.getTcpKeepCnt();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public int getTcpUserTimeout() {
        try {
            return this.channel.socket.getTcpUserTimeout();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollSocketChannelConfig setKeepAlive(boolean keepAlive) {
        try {
            this.channel.socket.setKeepAlive(keepAlive);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public EpollSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            this.channel.socket.setReceiveBufferSize(receiveBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        try {
            this.channel.socket.setReuseAddress(reuseAddress);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollSocketChannelConfig setSendBufferSize(int sendBufferSize) {
        try {
            this.channel.socket.setSendBufferSize(sendBufferSize);
            this.calculateMaxBytesPerGatheringWrite();
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollSocketChannelConfig setSoLinger(int soLinger) {
        try {
            this.channel.socket.setSoLinger(soLinger);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollSocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
        try {
            this.channel.socket.setTcpNoDelay(tcpNoDelay);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpCork(boolean tcpCork) {
        try {
            this.channel.socket.setTcpCork(tcpCork);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpNotSentLowAt(long tcpNotSentLowAt) {
        try {
            this.channel.socket.setTcpNotSentLowAt(tcpNotSentLowAt);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public EpollSocketChannelConfig setTrafficClass(int trafficClass) {
        try {
            this.channel.socket.setTrafficClass(trafficClass);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpKeepIdle(int seconds) {
        try {
            this.channel.socket.setTcpKeepIdle(seconds);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpKeepIntvl(int seconds) {
        try {
            this.channel.socket.setTcpKeepIntvl(seconds);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Deprecated
    public EpollSocketChannelConfig setTcpKeepCntl(int probes) {
        return this.setTcpKeepCnt(probes);
    }

    public EpollSocketChannelConfig setTcpKeepCnt(int probes) {
        try {
            this.channel.socket.setTcpKeepCnt(probes);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpUserTimeout(int milliseconds) {
        try {
            this.channel.socket.setTcpUserTimeout(milliseconds);
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

    public EpollSocketChannelConfig setIpTransparent(boolean transparent) {
        try {
            this.channel.socket.setIpTransparent(transparent);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> keys) {
        try {
            this.channel.setTcpMd5Sig(keys);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpQuickAck(boolean quickAck) {
        try {
            this.channel.socket.setTcpQuickAck(quickAck);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public boolean isTcpQuickAck() {
        try {
            return this.channel.socket.isTcpQuickAck();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public EpollSocketChannelConfig setTcpFastOpenConnect(boolean fastOpenConnect) {
        try {
            this.channel.socket.setTcpFastOpenConnect(fastOpenConnect);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public boolean isTcpFastOpenConnect() {
        try {
            return this.channel.socket.isTcpFastOpenConnect();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }

    @Override
    public EpollSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
        return this;
    }

    @Override
    public EpollSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }

    @Deprecated
    @Override
    public EpollSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setEpollMode(EpollMode mode) {
        super.setEpollMode(mode);
        return this;
    }

    private void calculateMaxBytesPerGatheringWrite() {
        int newSendBufferSize = this.getSendBufferSize() << 1;
        if (newSendBufferSize > 0) {
            this.setMaxBytesPerGatheringWrite(this.getSendBufferSize() << 1);
        }
    }
}

