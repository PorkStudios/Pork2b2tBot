/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.Effect;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import com.github.steveice10.packetlib.Session;
import java.util.ArrayList;
import java.util.Iterator;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.PotionEffect;
import net.daporkchop.toobeetooteebot.entity.api.EntityEquipment;

public class ListenerEntityRemoveEffectPacket
implements IPacketListener<ServerEntityRemoveEffectPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityRemoveEffectPacket pck) {
        EntityEquipment equipment = (EntityEquipment)Caches.getEntityByEID(pck.entityId);
        Iterator<PotionEffect> iterator = equipment.potionEffects.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().effect != pck.effect) continue;
            iterator.remove();
            break;
        }
    }
}

