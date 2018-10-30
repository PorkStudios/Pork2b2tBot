/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.unix;

import java.net.InetSocketAddress;

public final class DatagramSocketAddress
extends InetSocketAddress {
    private static final long serialVersionUID = 3094819287843178401L;
    private final int receivedAmount;

    DatagramSocketAddress(String addr, int port, int receivedAmount) {
        super(addr, port);
        this.receivedAmount = receivedAmount;
    }

    public int receivedAmount() {
        return this.receivedAmount;
    }
}

