/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ServerSetCooldownPacket
extends MinecraftPacket {
    public int itemId;
    public int cooldownTicks;

    public ServerSetCooldownPacket() {
    }

    public ServerSetCooldownPacket(int itemId, int cooldownTicks) {
        this.itemId = itemId;
        this.cooldownTicks = cooldownTicks;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.itemId = in.readVarInt();
        this.cooldownTicks = in.readVarInt();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.itemId);
        out.writeVarInt(this.cooldownTicks);
    }
}

