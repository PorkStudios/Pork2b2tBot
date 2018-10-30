/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.packetlib.Session;
import java.util.HashMap;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.api.EntityEquipment;

public class ListenerEntityEquipmentPacket
implements IPacketListener<ServerEntityEquipmentPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityEquipmentPacket pck) {
        try {
            EntityEquipment equipment = (EntityEquipment)Caches.getEntityByEID(pck.entityId);
            equipment.equipment.put(pck.slot, pck.item);
        }
        catch (ClassCastException equipment) {
            // empty catch block
        }
    }
}

