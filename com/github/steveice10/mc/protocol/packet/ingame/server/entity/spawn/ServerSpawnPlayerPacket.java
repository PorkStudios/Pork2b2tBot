/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import java.util.UUID;

public class ServerSpawnPlayerPacket
extends MinecraftPacket {
    public int entityId;
    public UUID uuid;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public EntityMetadata[] metadata;

    public ServerSpawnPlayerPacket() {
    }

    public ServerSpawnPlayerPacket(int entityId, UUID uuid, double x, double y, double z, float yaw, float pitch, EntityMetadata[] metadata) {
        this.entityId = entityId;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.metadata = metadata;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUUID() {
        return this.uuid;
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

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public EntityMetadata[] getMetadata() {
        return this.metadata;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityId = in.readVarInt();
        this.uuid = in.readUUID();
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.yaw = (float)(in.readByte() * 360) / 256.0f;
        this.pitch = (float)(in.readByte() * 360) / 256.0f;
        this.metadata = NetUtil.readEntityMetadata(in);
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeUUID(this.uuid);
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeByte((byte)(this.yaw * 256.0f / 360.0f));
        out.writeByte((byte)(this.pitch * 256.0f / 360.0f));
        NetUtil.writeEntityMetadata(out, this.metadata);
    }
}

