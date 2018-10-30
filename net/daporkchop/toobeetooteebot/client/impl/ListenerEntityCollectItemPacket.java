/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import com.github.steveice10.packetlib.Session;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;

public class ListenerEntityCollectItemPacket
implements IPacketListener<ServerEntityCollectItemPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityCollectItemPacket pck) {
        Caches.cachedEntities.remove(pck.collectedEntityId);
    }
}

