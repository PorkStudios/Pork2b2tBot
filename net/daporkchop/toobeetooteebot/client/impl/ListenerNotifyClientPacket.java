/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.world.notify.ClientNotification;
import com.github.steveice10.mc.protocol.data.game.world.notify.ClientNotificationValue;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import com.github.steveice10.packetlib.Session;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;

public class ListenerNotifyClientPacket
implements IPacketListener<ServerNotifyClientPacket> {
    @Override
    public void handlePacket(Session session, ServerNotifyClientPacket pck) {
        if (pck.notification == ClientNotification.CHANGE_GAMEMODE) {
            Caches.gameMode = (GameMode)pck.value;
        }
    }
}

