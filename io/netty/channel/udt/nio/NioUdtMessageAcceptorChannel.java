/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  com.barchart.udt.TypeUDT
 *  com.barchart.udt.nio.SocketChannelUDT
 */
package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.channel.Channel;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtAcceptorChannel;
import io.netty.channel.udt.nio.NioUdtMessageConnectorChannel;

@Deprecated
public class NioUdtMessageAcceptorChannel
extends NioUdtAcceptorChannel {
    public NioUdtMessageAcceptorChannel() {
        super(TypeUDT.DATAGRAM);
    }

    @Override
    protected UdtChannel newConnectorChannel(SocketChannelUDT channelUDT) {
        return new NioUdtMessageConnectorChannel(this, channelUDT);
    }
}

