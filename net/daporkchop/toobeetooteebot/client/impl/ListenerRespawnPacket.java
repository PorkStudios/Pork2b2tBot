/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Predicate;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPlayer;

public class ListenerRespawnPacket
implements IPacketListener<ServerRespawnPacket> {
    @Override
    public void handlePacket(Session session, ServerRespawnPacket pck) {
        Caches.dimension = pck.getDimension();
        Caches.gameMode = pck.gamemode;
        Caches.cachedChunks.clear();
        Caches.cachedEntities.int2ObjectEntrySet().removeIf(entityEntry -> entityEntry.getIntKey() != Caches.eid);
        Caches.cachedBossBars.clear();
        Caches.player.potionEffects.clear();
    }
}

