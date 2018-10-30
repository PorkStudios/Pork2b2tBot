/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server.world;

import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.value.BlockValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.BlockValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.ChestValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.ChestValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.GenericBlockValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.GenericBlockValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.MobSpawnerValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.MobSpawnerValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.NoteBlockValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.NoteBlockValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.PistonValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.PistonValueType;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;

public class ServerBlockValuePacket
extends MinecraftPacket {
    public static final int NOTE_BLOCK = 25;
    public static final int STICKY_PISTON = 29;
    public static final int PISTON = 33;
    public static final int MOB_SPAWNER = 52;
    public static final int CHEST = 54;
    public static final int ENDER_CHEST = 130;
    public static final int TRAPPED_CHEST = 146;
    public static final int SHULKER_BOX_LOWER = 219;
    public static final int SHULKER_BOX_HIGHER = 234;
    public Position position;
    public BlockValueType type;
    public BlockValue value;
    public int blockId;

    public ServerBlockValuePacket() {
    }

    public ServerBlockValuePacket(Position position, BlockValueType type, BlockValue value, int blockId) {
        this.position = position;
        this.type = type;
        this.value = value;
        this.blockId = blockId;
    }

    public Position getPosition() {
        return this.position;
    }

    public BlockValueType getType() {
        return this.type;
    }

    public BlockValue getValue() {
        return this.value;
    }

    public int getBlockId() {
        return this.blockId;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.position = NetUtil.readPosition(in);
        int type = in.readUnsignedByte();
        int value = in.readUnsignedByte();
        this.blockId = in.readVarInt() & 4095;
        if (this.blockId == 25) {
            this.type = MagicValues.key(NoteBlockValueType.class, type);
            this.value = new NoteBlockValue(value);
        } else if (this.blockId == 29 || this.blockId == 33) {
            this.type = MagicValues.key(PistonValueType.class, type);
            this.value = MagicValues.key(PistonValue.class, value);
        } else if (this.blockId == 52) {
            this.type = MagicValues.key(MobSpawnerValueType.class, type);
            this.value = new MobSpawnerValue();
        } else if (this.blockId == 54 || this.blockId == 130 || this.blockId == 146 || this.blockId >= 219 && this.blockId <= 234) {
            this.type = MagicValues.key(ChestValueType.class, type);
            this.value = new ChestValue(value);
        } else {
            this.type = MagicValues.key(GenericBlockValueType.class, type);
            this.value = new GenericBlockValue(value);
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        NetUtil.writePosition(out, this.position);
        int type = 0;
        if (this.type instanceof NoteBlockValueType) {
            type = MagicValues.value(Integer.class, this.type);
        } else if (this.type instanceof PistonValueType) {
            type = MagicValues.value(Integer.class, this.type);
        } else if (this.type instanceof MobSpawnerValueType) {
            type = MagicValues.value(Integer.class, this.type);
        } else if (this.type instanceof ChestValueType) {
            type = MagicValues.value(Integer.class, this.type);
        } else if (this.type instanceof GenericBlockValueType) {
            type = MagicValues.value(Integer.class, this.type);
        }
        out.writeByte(type);
        int val = 0;
        if (this.value instanceof NoteBlockValue) {
            val = ((NoteBlockValue)this.value).getPitch();
        } else if (this.value instanceof PistonValue) {
            val = MagicValues.value(Integer.class, this.value);
        } else if (this.value instanceof MobSpawnerValue) {
            val = 0;
        } else if (this.value instanceof ChestValue) {
            val = ((ChestValue)this.value).getViewers();
        } else if (this.value instanceof GenericBlockValue) {
            val = ((GenericBlockValue)this.value).getValue();
        }
        out.writeByte(val);
        out.writeVarInt(this.blockId & 4095);
    }
}

