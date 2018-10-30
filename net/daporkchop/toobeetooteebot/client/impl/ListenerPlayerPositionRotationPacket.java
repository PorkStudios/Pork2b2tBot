/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;

public class ListenerPlayerPositionRotationPacket
implements IPacketListener<ServerPlayerPositionRotationPacket> {
    @Override
    public void handlePacket(Session session, ServerPlayerPositionRotationPacket pck) {
        Caches.x = pck.getX();
        Caches.y = pck.getY();
        Caches.z = pck.getZ();
        Caches.yaw = pck.getYaw();
        Caches.pitch = pck.getPitch();
        Caches.updatePlayerLocRot();
        TooBeeTooTeeBot.bot.client.getSession().send(new ClientTeleportConfirmPacket(pck.getTeleportId()));
    }
}

