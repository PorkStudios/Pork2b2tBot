/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.EntityType;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPlayer;

public class ListenerJoinGamePacket
implements IPacketListener<ServerJoinGamePacket> {
    @Override
    public void handlePacket(Session session, ServerJoinGamePacket pck) {
        Caches.dimension = pck.getDimension();
        Caches.eid = pck.getEntityId();
        Caches.gameMode = pck.getGameMode();
        EntityPlayer player = new EntityPlayer();
        player.type = EntityType.REAL_PLAYER;
        player.entityId = Caches.eid;
        player.uuid = Caches.uuid;
        Caches.player = player;
        Caches.cachedEntities.put(player.entityId, (Entity)player);
    }
}

