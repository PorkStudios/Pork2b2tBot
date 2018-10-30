/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import com.github.steveice10.packetlib.Session;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.EntityRotation;

public class ListenerEntityAttachPacket
implements IPacketListener<ServerEntityAttachPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityAttachPacket pck) {
        EntityRotation entityRotation = (EntityRotation)Caches.getEntityByEID(pck.entityId);
        if (pck.attachedToId == -1) {
            entityRotation.isLeashed = false;
        } else {
            entityRotation.isLeashed = true;
            entityRotation.leashedID = pck.attachedToId;
        }
    }
}

