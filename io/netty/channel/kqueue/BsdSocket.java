/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.DefaultFileRegion;
import io.netty.channel.kqueue.AcceptFilter;
import io.netty.channel.kqueue.Native;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.ThrowableUtil;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;

final class BsdSocket
extends Socket {
    private static final Errors.NativeIoException SENDFILE_CONNECTION_RESET_EXCEPTION;
    private static final ClosedChannelException SENDFILE_CLOSED_CHANNEL_EXCEPTION;
    private static final int APPLE_SND_LOW_AT_MAX = 131072;
    private static final int FREEBSD_SND_LOW_AT_MAX = 32768;
    static final int BSD_SND_LOW_AT_MAX;

    BsdSocket(int fd) {
        super(fd);
    }

    void setAcceptFilter(AcceptFilter acceptFilter) throws IOException {
        BsdSocket.setAcceptFilter(this.intValue(), acceptFilter.filterName(), acceptFilter.filterArgs());
    }

    void setTcpNoPush(boolean tcpNoPush) throws IOException {
        BsdSocket.setTcpNoPush(this.intValue(), tcpNoPush ? 1 : 0);
    }

    void setSndLowAt(int lowAt) throws IOException {
        BsdSocket.setSndLowAt(this.intValue(), lowAt);
    }

    boolean isTcpNoPush() throws IOException {
        return BsdSocket.getTcpNoPush(this.intValue()) != 0;
    }

    int getSndLowAt() throws IOException {
        return BsdSocket.getSndLowAt(this.intValue());
    }

    AcceptFilter getAcceptFilter() throws IOException {
        String[] result = BsdSocket.getAcceptFilter(this.intValue());
        return result == null ? AcceptFilter.PLATFORM_UNSUPPORTED : new AcceptFilter(result[0], result[1]);
    }

    PeerCredentials getPeerCredentials() throws IOException {
        return BsdSocket.getPeerCredentials(this.intValue());
    }

    long sendFile(DefaultFileRegion src, long baseOffset, long offset, long length) throws IOException {
        src.open();
        long res = BsdSocket.sendFile(this.intValue(), src, baseOffset, offset, length);
        if (res >= 0L) {
            return res;
        }
        return Errors.ioResult("sendfile", (int)res, SENDFILE_CONNECTION_RESET_EXCEPTION, SENDFILE_CLOSED_CHANNEL_EXCEPTION);
    }

    public static BsdSocket newSocketStream() {
        return new BsdSocket(BsdSocket.newSocketStream0());
    }

    public static BsdSocket newSocketDgram() {
        return new BsdSocket(BsdSocket.newSocketDgram0());
    }

    public static BsdSocket newSocketDomain() {
        return new BsdSocket(BsdSocket.newSocketDomain0());
    }

    private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

    private static native String[] getAcceptFilter(int var0) throws IOException;

    private static native int getTcpNoPush(int var0) throws IOException;

    private static native int getSndLowAt(int var0) throws IOException;

    private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

    private static native void setAcceptFilter(int var0, String var1, String var2) throws IOException;

    private static native void setTcpNoPush(int var0, int var1) throws IOException;

    private static native void setSndLowAt(int var0, int var1) throws IOException;

    static {
        SENDFILE_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Native.class, "sendfile(..)");
        BSD_SND_LOW_AT_MAX = Math.min(131072, 32768);
        SENDFILE_CONNECTION_RESET_EXCEPTION = Errors.newConnectionResetException("syscall:sendfile", Errors.ERRNO_EPIPE_NEGATIVE);
    }
}

