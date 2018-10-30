/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.unix;

import io.netty.util.internal.EmptyArrays;

public final class PeerCredentials {
    private final int pid;
    private final int uid;
    private final int[] gids;

    /* varargs */ PeerCredentials(int p, int u, int ... gids) {
        this.pid = p;
        this.uid = u;
        this.gids = gids == null ? EmptyArrays.EMPTY_INTS : gids;
    }

    public int pid() {
        return this.pid;
    }

    public int uid() {
        return this.uid;
    }

    public int[] gids() {
        return (int[])this.gids.clone();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("UserCredentials[pid=").append(this.pid).append("; uid=").append(this.uid).append("; gids=[");
        if (this.gids.length > 0) {
            sb.append(this.gids[0]);
            for (int i = 1; i < this.gids.length; ++i) {
                sb.append(", ").append(this.gids[i]);
            }
        }
        sb.append(']');
        return sb.toString();
    }
}

