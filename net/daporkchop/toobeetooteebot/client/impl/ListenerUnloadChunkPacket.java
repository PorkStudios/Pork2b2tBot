/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.util.ChunkPos;
import net.daporkchop.toobeetooteebot.util.Config;

public class ListenerUnloadChunkPacket
implements IPacketListener<ServerUnloadChunkPacket> {
    @Override
    public void handlePacket(Session session, ServerUnloadChunkPacket pck) {
        if (Config.doServer) {
            Caches.cachedChunks.remove(ChunkPos.getChunkHashFromXZ(pck.getX(), pck.getZ()));
        }
    }
}

