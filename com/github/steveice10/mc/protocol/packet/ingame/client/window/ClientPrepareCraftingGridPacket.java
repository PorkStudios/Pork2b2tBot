/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.client.window;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ClientPrepareCraftingGridPacket
extends MinecraftPacket {
    public int windowId;
    public int recipeId;
    public boolean makeAll;

    public ClientPrepareCraftingGridPacket() {
    }

    public ClientPrepareCraftingGridPacket(int windowId, int recipeId, boolean makeAll) {
        this.windowId = windowId;
        this.recipeId = recipeId;
        this.makeAll = makeAll;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getRecipeId() {
        return this.recipeId;
    }

    public boolean doesMakeAll() {
        return this.makeAll;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.windowId = in.readByte();
        this.recipeId = in.readVarInt();
        this.makeAll = in.readBoolean();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(this.windowId);
        out.writeVarInt(this.recipeId);
        out.writeBoolean(this.makeAll);
    }
}

