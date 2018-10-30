/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.status.client;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class StatusPingPacket
extends MinecraftPacket {
    public long time;

    public StatusPingPacket() {
    }

    public StatusPingPacket(long time) {
        this.time = time;
    }

    public long getPingTime() {
        return this.time;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.time = in.readLong();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeLong(this.time);
    }
}

