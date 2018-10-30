/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.client.window;

import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.window.CraftingBookDataType;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ClientCraftingBookDataPacket
extends MinecraftPacket {
    public CraftingBookDataType type;
    public int recipeId;
    public boolean craftingBookOpen;
    public boolean filterActive;

    public ClientCraftingBookDataPacket() {
    }

    public ClientCraftingBookDataPacket(int recipeId) {
        this.type = CraftingBookDataType.DISPLAYED_RECIPE;
        this.recipeId = recipeId;
    }

    public ClientCraftingBookDataPacket(boolean craftingBookOpen, boolean filterActive) {
        this.type = CraftingBookDataType.CRAFTING_BOOK_STATUS;
        this.craftingBookOpen = craftingBookOpen;
        this.filterActive = filterActive;
    }

    public CraftingBookDataType getType() {
        return this.type;
    }

    public void ensureType(CraftingBookDataType type, String what) {
        if (this.type != type) {
            throw new IllegalStateException(what + " is only set when type is " + (Object)((Object)type) + " but it is " + (Object)((Object)this.type));
        }
    }

    public int getRecipeId() {
        this.ensureType(CraftingBookDataType.DISPLAYED_RECIPE, "recipeId");
        return this.recipeId;
    }

    public boolean isCraftingBookOpen() {
        this.ensureType(CraftingBookDataType.CRAFTING_BOOK_STATUS, "craftingBookOpen");
        return this.craftingBookOpen;
    }

    public boolean isFilterActive() {
        this.ensureType(CraftingBookDataType.CRAFTING_BOOK_STATUS, "filterActive");
        return this.filterActive;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.type = MagicValues.key(CraftingBookDataType.class, in.readVarInt());
        switch (this.type) {
            case DISPLAYED_RECIPE: {
                this.recipeId = in.readInt();
                break;
            }
            case CRAFTING_BOOK_STATUS: {
                this.craftingBookOpen = in.readBoolean();
                this.filterActive = in.readBoolean();
                break;
            }
            default: {
                throw new IOException("Unknown crafting book data type: " + (Object)((Object)this.type));
            }
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(MagicValues.value(Integer.class, (Object)this.type));
        switch (this.type) {
            case DISPLAYED_RECIPE: {
                out.writeInt(this.recipeId);
                break;
            }
            case CRAFTING_BOOK_STATUS: {
                out.writeBoolean(this.craftingBookOpen);
                out.writeBoolean(this.filterActive);
                break;
            }
            default: {
                throw new IOException("Unknown crafting book data type: " + (Object)((Object)this.type));
            }
        }
    }

}

