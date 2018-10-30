/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.rxtx;

import io.netty.channel.ChannelOption;
import io.netty.channel.rxtx.RxtxChannelConfig;

@Deprecated
public final class RxtxChannelOption<T>
extends ChannelOption<T> {
    public static final ChannelOption<Integer> BAUD_RATE = RxtxChannelOption.valueOf(RxtxChannelOption.class, "BAUD_RATE");
    public static final ChannelOption<Boolean> DTR = RxtxChannelOption.valueOf(RxtxChannelOption.class, "DTR");
    public static final ChannelOption<Boolean> RTS = RxtxChannelOption.valueOf(RxtxChannelOption.class, "RTS");
    public static final ChannelOption<RxtxChannelConfig.Stopbits> STOP_BITS = RxtxChannelOption.valueOf(RxtxChannelOption.class, "STOP_BITS");
    public static final ChannelOption<RxtxChannelConfig.Databits> DATA_BITS = RxtxChannelOption.valueOf(RxtxChannelOption.class, "DATA_BITS");
    public static final ChannelOption<RxtxChannelConfig.Paritybit> PARITY_BIT = RxtxChannelOption.valueOf(RxtxChannelOption.class, "PARITY_BIT");
    public static final ChannelOption<Integer> WAIT_TIME = RxtxChannelOption.valueOf(RxtxChannelOption.class, "WAIT_TIME");
    public static final ChannelOption<Integer> READ_TIMEOUT = RxtxChannelOption.valueOf(RxtxChannelOption.class, "READ_TIMEOUT");

    private RxtxChannelOption() {
        super(null);
    }
}

