/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.util;

import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Rotation;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.io.stream.StreamNetInput;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class NetUtil {
    public static final int POSITION_X_SIZE = 38;
    public static final int POSITION_Y_SIZE = 26;
    public static final int POSITION_Z_SIZE = 38;
    public static final int POSITION_Y_SHIFT = 4095;
    public static final int POSITION_WRITE_SHIFT = 67108863;

    public static CompoundTag readNBT(NetInput in) throws IOException {
        byte b = in.readByte();
        if (b == 0) {
            return null;
        }
        return (CompoundTag)NBTIO.readTag(new NetInputStream(in, b));
    }

    public static void writeNBT(NetOutput out, CompoundTag tag) throws IOException {
        if (tag == null) {
            out.writeByte(0);
        } else {
            NBTIO.writeTag(new NetOutputStream(out), (Tag)tag);
        }
    }

    public static BlockState readBlockState(NetInput in) throws IOException {
        int rawId = in.readVarInt();
        return new BlockState(rawId >> 4, rawId & 15);
    }

    public static void writeBlockState(NetOutput out, BlockState blockState) throws IOException {
        out.writeVarInt(blockState.getId() << 4 | blockState.getData() & 15);
    }

    public static ItemStack readItem(NetInput in) throws IOException {
        short item = in.readShort();
        if (item < 0) {
            return null;
        }
        return new ItemStack(item, in.readByte(), in.readShort(), NetUtil.readNBT(in));
    }

    public static void writeItem(NetOutput out, ItemStack item) throws IOException {
        if (item == null) {
            out.writeShort(-1);
        } else {
            out.writeShort(item.getId());
            out.writeByte(item.getAmount());
            out.writeShort(item.getData());
            NetUtil.writeNBT(out, item.getNBT());
        }
    }

    public static Position readPosition(NetInput in) throws IOException {
        long val = in.readLong();
        int x = (int)(val >> 38);
        int y = (int)(val >> 26 & 4095L);
        int z = (int)(val << 38 >> 38);
        return new Position(x, y, z);
    }

    public static void writePosition(NetOutput out, Position pos) throws IOException {
        long x = pos.getX() & 67108863;
        long y = pos.getY() & 4095;
        long z = pos.getZ() & 67108863;
        out.writeLong(x << 38 | y << 26 | z);
    }

    public static Rotation readRotation(NetInput in) throws IOException {
        return new Rotation(in.readFloat(), in.readFloat(), in.readFloat());
    }

    public static void writeRotation(NetOutput out, Rotation rot) throws IOException {
        out.writeFloat(rot.getPitch());
        out.writeFloat(rot.getYaw());
        out.writeFloat(rot.getRoll());
    }

    public static EntityMetadata[] readEntityMetadata(NetInput in) throws IOException {
        int id;
        ArrayList<EntityMetadata> ret = new ArrayList<EntityMetadata>();
        while ((id = in.readUnsignedByte()) != 255) {
            int typeId = in.readVarInt();
            MetadataType type = MagicValues.key(MetadataType.class, typeId);
            Object value = null;
            switch (type) {
                case BYTE: {
                    value = in.readByte();
                    break;
                }
                case INT: {
                    value = in.readVarInt();
                    break;
                }
                case FLOAT: {
                    value = Float.valueOf(in.readFloat());
                    break;
                }
                case STRING: {
                    value = in.readString();
                    break;
                }
                case CHAT: {
                    value = Message.fromString(in.readString());
                    break;
                }
                case ITEM: {
                    value = NetUtil.readItem(in);
                    break;
                }
                case BOOLEAN: {
                    value = in.readBoolean();
                    break;
                }
                case ROTATION: {
                    value = NetUtil.readRotation(in);
                    break;
                }
                case POSITION: {
                    value = NetUtil.readPosition(in);
                    break;
                }
                case OPTIONAL_POSITION: {
                    boolean positionPresent = in.readBoolean();
                    if (!positionPresent) break;
                    value = NetUtil.readPosition(in);
                    break;
                }
                case BLOCK_FACE: {
                    value = MagicValues.key(BlockFace.class, in.readVarInt());
                    break;
                }
                case OPTIONAL_UUID: {
                    boolean uuidPresent = in.readBoolean();
                    if (!uuidPresent) break;
                    value = in.readUUID();
                    break;
                }
                case BLOCK_STATE: {
                    value = NetUtil.readBlockState(in);
                    break;
                }
                case NBT_TAG: {
                    value = NetUtil.readNBT(in);
                    break;
                }
                default: {
                    throw new IOException("Unknown metadata type id: " + typeId);
                }
            }
            ret.add(new EntityMetadata(id, type, value));
        }
        return ret.toArray(new EntityMetadata[ret.size()]);
    }

    public static void writeEntityMetadata(NetOutput out, EntityMetadata[] metadata) throws IOException {
        block16 : for (EntityMetadata meta : metadata) {
            out.writeByte(meta.getId());
            out.writeVarInt(MagicValues.value(Integer.class, (Object)meta.getType()));
            switch (meta.getType()) {
                case BYTE: {
                    out.writeByte(((Byte)meta.getValue()).byteValue());
                    continue block16;
                }
                case INT: {
                    out.writeVarInt((Integer)meta.getValue());
                    continue block16;
                }
                case FLOAT: {
                    out.writeFloat(((Float)meta.getValue()).floatValue());
                    continue block16;
                }
                case STRING: {
                    out.writeString((String)meta.getValue());
                    continue block16;
                }
                case CHAT: {
                    out.writeString(((Message)meta.getValue()).toJsonString());
                    continue block16;
                }
                case ITEM: {
                    NetUtil.writeItem(out, (ItemStack)meta.getValue());
                    continue block16;
                }
                case BOOLEAN: {
                    out.writeBoolean((Boolean)meta.getValue());
                    continue block16;
                }
                case ROTATION: {
                    NetUtil.writeRotation(out, (Rotation)meta.getValue());
                    continue block16;
                }
                case POSITION: {
                    NetUtil.writePosition(out, (Position)meta.getValue());
                    continue block16;
                }
                case OPTIONAL_POSITION: {
                    out.writeBoolean(meta.getValue() != null);
                    if (meta.getValue() == null) continue block16;
                    NetUtil.writePosition(out, (Position)meta.getValue());
                    continue block16;
                }
                case BLOCK_FACE: {
                    out.writeVarInt(MagicValues.value(Integer.class, meta.getValue()));
                    continue block16;
                }
                case OPTIONAL_UUID: {
                    out.writeBoolean(meta.getValue() != null);
                    if (meta.getValue() == null) continue block16;
                    out.writeUUID((UUID)meta.getValue());
                    continue block16;
                }
                case BLOCK_STATE: {
                    NetUtil.writeBlockState(out, (BlockState)meta.getValue());
                    continue block16;
                }
                case NBT_TAG: {
                    NetUtil.writeNBT(out, (CompoundTag)meta.getValue());
                    continue block16;
                }
                default: {
                    throw new IOException("Unknown metadata type: " + (Object)((Object)meta.getType()));
                }
            }
        }
        out.writeByte(255);
    }

    public static Column readColumn(byte[] data, int x, int z, boolean fullChunk, boolean hasSkylight, int mask, CompoundTag[] tileEntities) throws IOException {
        StreamNetInput in = new StreamNetInput(new ByteArrayInputStream(data));
        Throwable ex = null;
        Column column = null;
        try {
            Chunk[] chunks = new Chunk[16];
            for (int index = 0; index < chunks.length; ++index) {
                if ((mask & 1 << index) == 0) continue;
                BlockStorage blocks = new BlockStorage(in);
                NibbleArray3d blocklight = new NibbleArray3d(in, 2048);
                NibbleArray3d skylight = hasSkylight ? new NibbleArray3d(in, 2048) : null;
                chunks[index] = new Chunk(blocks, blocklight, skylight);
            }
            byte[] biomeData = null;
            if (fullChunk) {
                biomeData = in.readBytes(256);
            }
            column = new Column(x, z, chunks, biomeData, tileEntities);
        }
        catch (Throwable e) {
            ex = e;
        }
        if (!(in.available() <= 0 && ex == null || hasSkylight)) {
            return NetUtil.readColumn(data, x, z, fullChunk, true, mask, tileEntities);
        }
        if (ex != null) {
            throw new IOException("Failed to read chunk data.", ex);
        }
        return column;
    }

    public static int writeColumn(NetOutput out, Column column, boolean fullChunk, boolean hasSkylight) throws IOException {
        int mask = 0;
        Chunk[] chunks = column.getChunks();
        for (int index = 0; index < chunks.length; ++index) {
            Chunk chunk = chunks[index];
            if (chunk == null || fullChunk && chunk.isEmpty()) continue;
            mask |= 1 << index;
            chunk.getBlocks().write(out);
            chunk.getBlockLight().write(out);
            if (!hasSkylight) continue;
            chunk.getSkyLight().write(out);
        }
        if (fullChunk) {
            out.writeBytes(column.getBiomeData());
        }
        return mask;
    }

    public static class NetOutputStream
    extends OutputStream {
        public NetOutput out;

        public NetOutputStream(NetOutput out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            this.out.writeByte(b);
        }
    }

    public static class NetInputStream
    extends InputStream {
        public NetInput in;
        public boolean readFirst;
        public byte firstByte;

        public NetInputStream(NetInput in, byte firstByte) {
            this.in = in;
            this.firstByte = firstByte;
        }

        @Override
        public int read() throws IOException {
            if (!this.readFirst) {
                this.readFirst = true;
                return this.firstByte;
            }
            return this.in.readUnsignedByte();
        }
    }

}

