/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.ChannelOption;
import io.netty.channel.kqueue.AcceptFilter;
import io.netty.channel.unix.UnixChannelOption;

public final class KQueueChannelOption<T>
extends UnixChannelOption<T> {
    public static final ChannelOption<Integer> SO_SNDLOWAT = KQueueChannelOption.valueOf(KQueueChannelOption.class, "SO_SNDLOWAT");
    public static final ChannelOption<Boolean> TCP_NOPUSH = KQueueChannelOption.valueOf(KQueueChannelOption.class, "TCP_NOPUSH");
    public static final ChannelOption<AcceptFilter> SO_ACCEPTFILTER = KQueueChannelOption.valueOf(KQueueChannelOption.class, "SO_ACCEPTFILTER");
    public static final ChannelOption<Boolean> RCV_ALLOC_TRANSPORT_PROVIDES_GUESS = KQueueChannelOption.valueOf(KQueueChannelOption.class, "RCV_ALLOC_TRANSPORT_PROVIDES_GUESS");

    private KQueueChannelOption() {
    }
}

