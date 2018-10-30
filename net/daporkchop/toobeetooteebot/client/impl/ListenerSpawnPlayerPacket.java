/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.EntityType;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPlayer;

public class ListenerSpawnPlayerPacket
implements IPacketListener<ServerSpawnPlayerPacket> {
    @Override
    public void handlePacket(Session session, ServerSpawnPlayerPacket pck) {
        EntityPlayer mob = new EntityPlayer();
        mob.type = EntityType.PLAYER;
        mob.entityId = pck.entityId;
        mob.uuid = pck.uuid;
        mob.x = pck.x;
        mob.y = pck.y;
        mob.z = pck.z;
        mob.pitch = pck.pitch;
        mob.yaw = pck.yaw;
        mob.metadata = pck.metadata;
        Caches.cachedEntities.put(pck.entityId, (Entity)mob);
    }
}

