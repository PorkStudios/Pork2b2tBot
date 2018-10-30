/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannelConfig;
import java.net.InetAddress;
import java.net.NetworkInterface;

public interface OioDatagramChannelConfig
extends DatagramChannelConfig {
    public OioDatagramChannelConfig setSoTimeout(int var1);

    public int getSoTimeout();

    @Override
    public OioDatagramChannelConfig setSendBufferSize(int var1);

    @Override
    public OioDatagramChannelConfig setReceiveBufferSize(int var1);

    @Override
    public OioDatagramChannelConfig setTrafficClass(int var1);

    @Override
    public OioDatagramChannelConfig setReuseAddress(boolean var1);

    @Override
    public OioDatagramChannelConfig setBroadcast(boolean var1);

    @Override
    public OioDatagramChannelConfig setLoopbackModeDisabled(boolean var1);

    @Override
    public OioDatagramChannelConfig setTimeToLive(int var1);

    @Override
    public OioDatagramChannelConfig setInterface(InetAddress var1);

    @Override
    public OioDatagramChannelConfig setNetworkInterface(NetworkInterface var1);

    @Override
    public OioDatagramChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public OioDatagramChannelConfig setWriteSpinCount(int var1);

    @Override
    public OioDatagramChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public OioDatagramChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public OioDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public OioDatagramChannelConfig setAutoRead(boolean var1);

    @Override
    public OioDatagramChannelConfig setAutoClose(boolean var1);

    @Override
    public OioDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

    @Override
    public OioDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

    @Override
    public OioDatagramChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public OioDatagramChannelConfig setWriteBufferLowWaterMark(int var1);
}

