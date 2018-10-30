/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerVehicleMovePacket;
import com.github.steveice10.packetlib.Session;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;

public class ListenerVehicleMovePacket
implements IPacketListener<ServerVehicleMovePacket> {
    @Override
    public void handlePacket(Session session, ServerVehicleMovePacket pck) {
        Entity entity = Entity.getEntityBeingRiddenBy(Caches.eid);
    }
}

