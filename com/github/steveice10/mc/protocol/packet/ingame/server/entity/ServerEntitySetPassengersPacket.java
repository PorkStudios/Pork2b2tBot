/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server.entity;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ServerEntitySetPassengersPacket
extends MinecraftPacket {
    public int entityId;
    public int[] passengerIds;

    public ServerEntitySetPassengersPacket() {
    }

    public /* varargs */ ServerEntitySetPassengersPacket(int entityId, int ... passengerIds) {
        this.entityId = entityId;
        this.passengerIds = passengerIds;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int[] getPassengerIds() {
        return this.passengerIds;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityId = in.readVarInt();
        this.passengerIds = new int[in.readVarInt()];
        for (int index = 0; index < this.passengerIds.length; ++index) {
            this.passengerIds[index] = in.readVarInt();
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.passengerIds.length);
        for (int entityId : this.passengerIds) {
            out.writeVarInt(entityId);
        }
    }
}

