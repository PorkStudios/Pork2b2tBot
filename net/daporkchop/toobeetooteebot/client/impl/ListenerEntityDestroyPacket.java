/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;

public class ListenerEntityDestroyPacket
implements IPacketListener<ServerEntityDestroyPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityDestroyPacket pck) {
        for (int eid : pck.entityIds) {
            if (Caches.cachedEntities.remove(eid) != null) continue;
        }
    }
}

