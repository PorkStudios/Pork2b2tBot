/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server.entity;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ServerEntityDestroyPacket
extends MinecraftPacket {
    public int[] entityIds;

    public ServerEntityDestroyPacket() {
    }

    public /* varargs */ ServerEntityDestroyPacket(int ... entityIds) {
        this.entityIds = entityIds;
    }

    public int[] getEntityIds() {
        return this.entityIds;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityIds = new int[in.readVarInt()];
        for (int index = 0; index < this.entityIds.length; ++index) {
            this.entityIds[index] = in.readVarInt();
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.entityIds.length);
        for (int entityId : this.entityIds) {
            out.writeVarInt(entityId);
        }
    }
}

