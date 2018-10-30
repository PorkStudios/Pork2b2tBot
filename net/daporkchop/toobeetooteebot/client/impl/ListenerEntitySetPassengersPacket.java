/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import com.github.steveice10.packetlib.Session;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;

public class ListenerEntitySetPassengersPacket
implements IPacketListener<ServerEntitySetPassengersPacket> {
    @Override
    public void handlePacket(Session session, ServerEntitySetPassengersPacket pck) {
        Entity entity = Caches.getEntityByEID(pck.entityId);
        entity.passengerIds = pck.passengerIds == null || pck.passengerIds.length == 0 ? null : pck.passengerIds;
    }
}

