/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.Effect;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import com.github.steveice10.packetlib.Session;
import java.util.ArrayList;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.entity.PotionEffect;
import net.daporkchop.toobeetooteebot.entity.api.EntityEquipment;

public class ListenerEntityEffectPacket
implements IPacketListener<ServerEntityEffectPacket> {
    @Override
    public void handlePacket(Session session, ServerEntityEffectPacket pck) {
        PotionEffect effect = new PotionEffect();
        effect.effect = pck.effect;
        effect.amplifier = pck.amplifier;
        effect.duration = pck.duration;
        effect.ambient = pck.ambient;
        effect.showParticles = pck.showParticles;
        ((EntityEquipment)Caches.getEntityByEID((int)pck.entityId)).potionEffects.add(effect);
    }
}

