/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import com.github.steveice10.packetlib.Session;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.api.EntityRotation;

public class ListenerEntityMovementPacket
implements IPacketListener<ServerEntityMovementPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityMovementPacket pck) {
        Entity entity = Caches.getEntityByEID(pck.entityId);
        if (pck.pos) {
            entity.x += pck.moveX / 4096.0;
            entity.y += pck.moveY / 4096.0;
            entity.z += pck.moveZ / 4096.0;
        }
        if (pck.rot && entity instanceof EntityRotation) {
            ((EntityRotation)entity).yaw = pck.yaw;
            ((EntityRotation)entity).pitch = pck.pitch;
        }
    }
}

