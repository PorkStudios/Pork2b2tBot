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
import io.netty.channel.udt.nio.NioUdtByteConnectorChannel;
import io.netty.channel.udt.nio.NioUdtProvider;

@Deprecated
public class NioUdtByteRendezvousChannel
extends NioUdtByteConnectorChannel {
    public NioUdtByteRendezvousChannel() {
        super((SocketChannelUDT)NioUdtProvider.newRendezvousChannelUDT(TypeUDT.STREAM));
    }
}

