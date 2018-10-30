/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.client.window;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ClientCreativeInventoryActionPacket
extends MinecraftPacket {
    public int slot;
    public ItemStack clicked;

    public ClientCreativeInventoryActionPacket() {
    }

    public ClientCreativeInventoryActionPacket(int slot, ItemStack clicked) {
        this.slot = slot;
        this.clicked = clicked;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getClickedItem() {
        return this.clicked;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.slot = in.readShort();
        this.clicked = NetUtil.readItem(in);
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeShort(this.slot);
        NetUtil.writeItem(out, this.clicked);
    }
}

