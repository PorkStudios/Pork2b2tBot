/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.unix.UnixChannelOption;
import java.net.InetAddress;
import java.util.Map;

public final class EpollChannelOption<T>
extends UnixChannelOption<T> {
    public static final ChannelOption<Boolean> TCP_CORK = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_CORK");
    public static final ChannelOption<Long> TCP_NOTSENT_LOWAT = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_NOTSENT_LOWAT");
    public static final ChannelOption<Integer> TCP_KEEPIDLE = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPIDLE");
    public static final ChannelOption<Integer> TCP_KEEPINTVL = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPINTVL");
    public static final ChannelOption<Integer> TCP_KEEPCNT = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPCNT");
    public static final ChannelOption<Integer> TCP_USER_TIMEOUT = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_USER_TIMEOUT");
    public static final ChannelOption<Boolean> IP_FREEBIND = EpollChannelOption.valueOf("IP_FREEBIND");
    public static final ChannelOption<Boolean> IP_TRANSPARENT = EpollChannelOption.valueOf("IP_TRANSPARENT");
    public static final ChannelOption<Integer> TCP_FASTOPEN = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_FASTOPEN");
    public static final ChannelOption<Boolean> TCP_FASTOPEN_CONNECT = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_FASTOPEN_CONNECT");
    public static final ChannelOption<Integer> TCP_DEFER_ACCEPT = ChannelOption.valueOf(EpollChannelOption.class, "TCP_DEFER_ACCEPT");
    public static final ChannelOption<Boolean> TCP_QUICKACK = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_QUICKACK");
    public static final ChannelOption<EpollMode> EPOLL_MODE = ChannelOption.valueOf(EpollChannelOption.class, "EPOLL_MODE");
    public static final ChannelOption<Map<InetAddress, byte[]>> TCP_MD5SIG = EpollChannelOption.valueOf("TCP_MD5SIG");

    private EpollChannelOption() {
    }
}

