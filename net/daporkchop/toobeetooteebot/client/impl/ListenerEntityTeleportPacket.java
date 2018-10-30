/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.packetlib.Session;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.api.EntityRotation;

public class ListenerEntityTeleportPacket
implements IPacketListener<ServerEntityTeleportPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityTeleportPacket pck) {
        Entity entity = Caches.getEntityByEID(pck.entityId);
        entity.x = pck.x;
        entity.y = pck.y;
        entity.z = pck.z;
        if (entity instanceof EntityRotation) {
            ((EntityRotation)entity).yaw = pck.yaw;
            ((EntityRotation)entity).pitch = pck.pitch;
        }
    }
}

