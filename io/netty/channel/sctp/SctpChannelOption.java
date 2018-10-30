/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import io.netty.channel.ChannelOption;
import java.net.SocketAddress;

public final class SctpChannelOption<T>
extends ChannelOption<T> {
    public static final ChannelOption<Boolean> SCTP_DISABLE_FRAGMENTS = SctpChannelOption.valueOf(SctpChannelOption.class, "SCTP_DISABLE_FRAGMENTS");
    public static final ChannelOption<Boolean> SCTP_EXPLICIT_COMPLETE = SctpChannelOption.valueOf(SctpChannelOption.class, "SCTP_EXPLICIT_COMPLETE");
    public static final ChannelOption<Integer> SCTP_FRAGMENT_INTERLEAVE = SctpChannelOption.valueOf(SctpChannelOption.class, "SCTP_FRAGMENT_INTERLEAVE");
    public static final ChannelOption<SctpStandardSocketOptions.InitMaxStreams> SCTP_INIT_MAXSTREAMS = SctpChannelOption.valueOf(SctpChannelOption.class, "SCTP_INIT_MAXSTREAMS");
    public static final ChannelOption<Boolean> SCTP_NODELAY = SctpChannelOption.valueOf(SctpChannelOption.class, "SCTP_NODELAY");
    public static final ChannelOption<SocketAddress> SCTP_PRIMARY_ADDR = SctpChannelOption.valueOf(SctpChannelOption.class, "SCTP_PRIMARY_ADDR");
    public static final ChannelOption<SocketAddress> SCTP_SET_PEER_PRIMARY_ADDR = SctpChannelOption.valueOf(SctpChannelOption.class, "SCTP_SET_PEER_PRIMARY_ADDR");

    private SctpChannelOption() {
        super(null);
    }
}

