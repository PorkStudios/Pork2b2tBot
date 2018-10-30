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
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.PrintStream;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.util.ChunkPos;
import net.daporkchop.toobeetooteebot.util.Config;

public class ListenerBlockChangePacket
implements IPacketListener<ServerBlockChangePacket> {
    @Override
    public void handlePacket(Session session, ServerBlockChangePacket pck) {
        if (Config.doServer) {
            int chunkX = pck.getRecord().getPosition().getX() >> 4;
            int chunkZ = pck.getRecord().getPosition().getZ() >> 4;
            int subchunkY = TooBeeTooTeeBot.ensureRange(pck.getRecord().getPosition().getY() >> 4, 0, 15);
            Column column = Caches.cachedChunks.get(ChunkPos.getChunkHashFromXZ(chunkX, chunkZ));
            if (column == null) {
                System.out.println("null chunk, this is probably a server bug");
                return;
            }
            Chunk subChunk = column.getChunks()[subchunkY];
            int subchunkRelativeY = Math.abs(pck.getRecord().getPosition().getY() - 16 * subchunkY);
            try {
                subChunk.getBlocks().set(Math.abs(Math.abs(pck.getRecord().getPosition().getX()) - Math.abs(Math.abs(pck.getRecord().getPosition().getX() >> 4)) * 16), TooBeeTooTeeBot.ensureRange(subchunkRelativeY, 0, 15), Math.abs(Math.abs(pck.getRecord().getPosition().getZ()) - Math.abs(Math.abs(pck.getRecord().getPosition().getZ() >> 4)) * 16), pck.getRecord().getBlock());
                column.getChunks()[subchunkY] = subChunk;
                Caches.cachedChunks.put(ChunkPos.getChunkHashFromXZ(chunkX, chunkZ), column);
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println("" + Math.abs(Math.abs(pck.getRecord().getPosition().getX()) - Math.abs(Math.abs(pck.getRecord().getPosition().getX() >> 4)) * 16) + " " + subchunkRelativeY + " " + Math.abs(Math.abs(pck.getRecord().getPosition().getZ()) - Math.abs(Math.abs(pck.getRecord().getPosition().getZ() >> 4)) * 16) + " " + (subchunkRelativeY << 8 | chunkZ << 4 | chunkX));
            }
            Caches.cachedChunks.put(ChunkPos.getChunkHashFromXZ(chunkX, chunkZ), column);
        }
    }
}

