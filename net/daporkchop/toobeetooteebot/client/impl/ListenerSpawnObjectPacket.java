/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectData;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectType;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.EntityType;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.impl.EntityObject;

public class ListenerSpawnObjectPacket
implements IPacketListener<ServerSpawnObjectPacket> {
    @Override
    public void handlePacket(Session session, ServerSpawnObjectPacket pck) {
        EntityObject mob = new EntityObject();
        mob.type = EntityType.OBJECT;
        mob.entityId = pck.entityId;
        mob.uuid = pck.uuid;
        mob.objectType = pck.type;
        mob.x = pck.x;
        mob.y = pck.y;
        mob.z = pck.z;
        mob.pitch = pck.pitch;
        mob.yaw = pck.yaw;
        mob.data = pck.data;
        mob.motX = pck.motX;
        mob.motY = pck.motY;
        mob.motZ = pck.motZ;
        Caches.cachedEntities.put(pck.entityId, (Entity)mob);
    }
}

