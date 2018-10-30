/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server.entity;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ServerEntityVelocityPacket
extends MinecraftPacket {
    public int entityId;
    public double motX;
    public double motY;
    public double motZ;

    public ServerEntityVelocityPacket() {
    }

    public ServerEntityVelocityPacket(int entityId, double motX, double motY, double motZ) {
        this.entityId = entityId;
        this.motX = motX;
        this.motY = motY;
        this.motZ = motZ;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public double getMotionX() {
        return this.motX;
    }

    public double getMotionY() {
        return this.motY;
    }

    public double getMotionZ() {
        return this.motZ;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityId = in.readVarInt();
        this.motX = (double)in.readShort() / 8000.0;
        this.motY = (double)in.readShort() / 8000.0;
        this.motZ = (double)in.readShort() / 8000.0;
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeShort((int)(this.motX * 8000.0));
        out.writeShort((int)(this.motY * 8000.0));
        out.writeShort((int)(this.motZ * 8000.0));
    }
}

