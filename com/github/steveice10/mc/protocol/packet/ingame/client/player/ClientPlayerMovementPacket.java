/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.client.player;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ClientPlayerMovementPacket
extends MinecraftPacket {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public boolean pos = false;
    public boolean rot = false;

    public ClientPlayerMovementPacket() {
    }

    public ClientPlayerMovementPacket(boolean onGround) {
        this.onGround = onGround;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public void read(NetInput in) throws IOException {
        if (this.pos) {
            this.x = in.readDouble();
            this.y = in.readDouble();
            this.z = in.readDouble();
        }
        if (this.rot) {
            this.yaw = in.readFloat();
            this.pitch = in.readFloat();
        }
        this.onGround = in.readBoolean();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        if (this.pos) {
            out.writeDouble(this.x);
            out.writeDouble(this.y);
            out.writeDouble(this.z);
        }
        if (this.rot) {
            out.writeFloat(this.yaw);
            out.writeFloat(this.pitch);
        }
        out.writeBoolean(this.onGround);
    }
}

