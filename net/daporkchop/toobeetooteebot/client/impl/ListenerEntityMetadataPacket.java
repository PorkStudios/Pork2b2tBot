/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.packetlib.Session;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;

public class ListenerEntityMetadataPacket
implements IPacketListener<ServerEntityMetadataPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityMetadataPacket pck) {
        Entity entity = Caches.getEntityByEID(pck.entityId);
        ArrayList<EntityMetadata> oldMeta = Lists.newArrayList(entity.metadata);
        ArrayList<EntityMetadata> newMeta = new ArrayList<EntityMetadata>();
        block0 : for (EntityMetadata oldCheck : oldMeta) {
            for (EntityMetadata newCheck : pck.metadata) {
                if (newCheck.id != oldCheck.id) continue;
                newMeta.add(newCheck);
                continue block0;
            }
            newMeta.add(oldCheck);
        }
        block2 : for (EntityMetadata newCheck : pck.metadata) {
            for (EntityMetadata oldCheck : oldMeta) {
                if (oldCheck.id != newCheck.id) continue;
                continue block2;
            }
            newMeta.add(newCheck);
        }
        entity.metadata = newMeta.toArray(new EntityMetadata[newMeta.size()]);
    }
}

