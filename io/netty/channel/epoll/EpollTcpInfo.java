/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

public final class EpollTcpInfo {
    final long[] info = new long[32];

    public int state() {
        return (int)this.info[0];
    }

    public int caState() {
        return (int)this.info[1];
    }

    public int retransmits() {
        return (int)this.info[2];
    }

    public int probes() {
        return (int)this.info[3];
    }

    public int backoff() {
        return (int)this.info[4];
    }

    public int options() {
        return (int)this.info[5];
    }

    public int sndWscale() {
        return (int)this.info[6];
    }

    public int rcvWscale() {
        return (int)this.info[7];
    }

    public long rto() {
        return this.info[8];
    }

    public long ato() {
        return this.info[9];
    }

    public long sndMss() {
        return this.info[10];
    }

    public long rcvMss() {
        return this.info[11];
    }

    public long unacked() {
        return this.info[12];
    }

    public long sacked() {
        return this.info[13];
    }

    public long lost() {
        return this.info[14];
    }

    public long retrans() {
        return this.info[15];
    }

    public long fackets() {
        return this.info[16];
    }

    public long lastDataSent() {
        return this.info[17];
    }

    public long lastAckSent() {
        return this.info[18];
    }

    public long lastDataRecv() {
        return this.info[19];
    }

    public long lastAckRecv() {
        return this.info[20];
    }

    public long pmtu() {
        return this.info[21];
    }

    public long rcvSsthresh() {
        return this.info[22];
    }

    public long rtt() {
        return this.info[23];
    }

    public long rttvar() {
        return this.info[24];
    }

    public long sndSsthresh() {
        return this.info[25];
    }

    public long sndCwnd() {
        return this.info[26];
    }

    public long advmss() {
        return this.info[27];
    }

    public long reordering() {
        return this.info[28];
    }

    public long rcvRtt() {
        return this.info[29];
    }

    public long rcvSpace() {
        return this.info[30];
    }

    public long totalRetrans() {
        return this.info[31];
    }
}

