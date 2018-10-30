/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server.entity;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ServerEntityMovementPacket
extends MinecraftPacket {
    public int entityId;
    public double moveX;
    public double moveY;
    public double moveZ;
    public float yaw;
    public float pitch;
    public boolean pos = false;
    public boolean rot = false;
    public boolean onGround;

    public ServerEntityMovementPacket() {
    }

    public ServerEntityMovementPacket(int entityId, boolean onGround) {
        this.entityId = entityId;
        this.onGround = onGround;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public double getMovementX() {
        return this.moveX;
    }

    public double getMovementY() {
        return this.moveY;
    }

    public double getMovementZ() {
        return this.moveZ;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityId = in.readVarInt();
        if (this.pos) {
            this.moveX = (double)in.readShort() / 4096.0;
            this.moveY = (double)in.readShort() / 4096.0;
            this.moveZ = (double)in.readShort() / 4096.0;
        }
        if (this.rot) {
            this.yaw = (float)(in.readByte() * 360) / 256.0f;
            this.pitch = (float)(in.readByte() * 360) / 256.0f;
        }
        if (this.pos || this.rot) {
            this.onGround = in.readBoolean();
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.entityId);
        if (this.pos) {
            out.writeShort((int)(this.moveX * 4096.0));
            out.writeShort((int)(this.moveY * 4096.0));
            out.writeShort((int)(this.moveZ * 4096.0));
        }
        if (this.rot) {
            out.writeByte((byte)(this.yaw * 256.0f / 360.0f));
            out.writeByte((byte)(this.pitch * 256.0f / 360.0f));
        }
        if (this.pos || this.rot) {
            out.writeBoolean(this.onGround);
        }
    }

    public static class ServerEntityRotationPacket
    extends ServerEntityMovementPacket {
        public ServerEntityRotationPacket() {
            this.rot = true;
        }

        public ServerEntityRotationPacket(int entityId, float yaw, float pitch, boolean onGround) {
            super(entityId, onGround);
            this.rot = true;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    public static class ServerEntityPositionRotationPacket
    extends ServerEntityMovementPacket {
        public ServerEntityPositionRotationPacket() {
            this.pos = true;
            this.rot = true;
        }

        public ServerEntityPositionRotationPacket(int entityId, double moveX, double moveY, double moveZ, float yaw, float pitch, boolean onGround) {
            super(entityId, onGround);
            this.pos = true;
            this.rot = true;
            this.moveX = moveX;
            this.moveY = moveY;
            this.moveZ = moveZ;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    public static class ServerEntityPositionPacket
    extends ServerEntityMovementPacket {
        public ServerEntityPositionPacket() {
            this.pos = true;
        }

        public ServerEntityPositionPacket(int entityId, double moveX, double moveY, double moveZ, boolean onGround) {
            super(entityId, onGround);
            this.pos = true;
            this.moveX = moveX;
            this.moveY = moveY;
            this.moveZ = moveZ;
        }
    }

}

