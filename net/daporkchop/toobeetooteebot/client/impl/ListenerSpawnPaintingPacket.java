/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.type.PaintingType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.HangingDirection;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.EntityType;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPainting;

public class ListenerSpawnPaintingPacket
implements IPacketListener<ServerSpawnPaintingPacket> {
    @Override
    public void handlePacket(Session session, ServerSpawnPaintingPacket pck) {
        EntityPainting mob = new EntityPainting();
        mob.type = EntityType.PAINTING;
        mob.entityId = pck.entityId;
        mob.uuid = pck.uuid;
        mob.paintingType = pck.paintingType;
        mob.x = pck.position.x;
        mob.y = pck.position.y;
        mob.z = pck.position.z;
        mob.direction = pck.direction;
        Caches.cachedEntities.put(pck.entityId, (Entity)mob);
    }
}

