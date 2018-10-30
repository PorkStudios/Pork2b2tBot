/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.packetlib.Session;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.EntityRotation;

public class ListenerEntityHeadLookPacket
implements IPacketListener<ServerEntityHeadLookPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityHeadLookPacket pck) {
        EntityRotation rotation = (EntityRotation)Caches.getEntityByEID(pck.entityId);
        rotation.headYaw = pck.headYaw;
    }
}

