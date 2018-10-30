/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.attribute.Attribute;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import com.github.steveice10.packetlib.Session;
import java.util.List;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.EntityRotation;

public class ListenerEntityPropertiesPacket
implements IPacketListener<ServerEntityPropertiesPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityPropertiesPacket pck) {
        ((EntityRotation)Caches.getEntityByEID((int)pck.entityId)).properties = pck.attributes;
    }
}

