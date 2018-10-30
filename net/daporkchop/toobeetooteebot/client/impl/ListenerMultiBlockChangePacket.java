/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.PrintStream;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.util.ChunkPos;
import net.daporkchop.toobeetooteebot.util.Config;

public class ListenerMultiBlockChangePacket
implements IPacketListener<ServerMultiBlockChangePacket> {
    @Override
    public void handlePacket(Session session, ServerMultiBlockChangePacket pck) {
        if (Config.doServer) {
            int chunkZ;
            int chunkX = pck.getRecords()[0].getPosition().getX() >> 4;
            Column column = Caches.cachedChunks.get(ChunkPos.getChunkHashFromXZ(chunkX, chunkZ = pck.getRecords()[0].getPosition().getZ() >> 4));
            if (column == null) {
                System.out.println("null chunk multi, this is probably a server bug");
                return;
            }
            for (BlockChangeRecord record : pck.getRecords()) {
                int relativeChunkX = Math.abs(Math.abs(record.getPosition().getX()) - Math.abs(Math.abs(record.getPosition().getX() >> 4)) * 16);
                int relativeChunkZ = Math.abs(Math.abs(record.getPosition().getZ()) - Math.abs(Math.abs(record.getPosition().getZ() >> 4)) * 16);
                int subchunkY = TooBeeTooTeeBot.ensureRange(record.getPosition().getY() >> 4, 0, 15);
                Chunk subChunk = column.getChunks()[subchunkY];
                int subchunkRelativeY = Math.abs(record.getPosition().getY() - 16 * subchunkY);
                try {
                    subChunk.getBlocks().set(relativeChunkX, TooBeeTooTeeBot.ensureRange(subchunkRelativeY, 0, 15), relativeChunkZ, record.getBlock());
                    column.getChunks()[subchunkY] = subChunk;
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println("" + relativeChunkX + " " + subchunkRelativeY + " " + relativeChunkZ + " " + (subchunkRelativeY << 8 | relativeChunkZ << 4 | relativeChunkX));
                }
            }
            Caches.cachedChunks.put(ChunkPos.getChunkHashFromXZ(chunkX, chunkZ), column);
        }
    }
}

