/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server.window;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ServerPreparedCraftingGridPacket
extends MinecraftPacket {
    public int windowId;
    public int recipeId;

    public ServerPreparedCraftingGridPacket() {
    }

    public ServerPreparedCraftingGridPacket(int windowId, int recipeId) {
        this.windowId = windowId;
        this.recipeId = recipeId;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getRecipeId() {
        return this.recipeId;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.windowId = in.readByte();
        this.recipeId = in.readVarInt();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(this.windowId);
        out.writeVarInt(this.recipeId);
    }
}

