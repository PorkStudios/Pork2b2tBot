/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.BossBarAction;
import com.github.steveice10.mc.protocol.data.game.BossBarColor;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import com.github.steveice10.packetlib.Session;
import java.util.HashMap;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;

public class ListenerBossBarPacket
implements IPacketListener<ServerBossBarPacket> {
    @Override
    public void handlePacket(Session session, ServerBossBarPacket pck) {
        switch (pck.action) {
            case ADD: {
                Caches.cachedBossBars.put(pck.uuid, pck);
                break;
            }
            case REMOVE: {
                Caches.cachedBossBars.remove(pck.uuid);
                break;
            }
            case UPDATE_HEALTH: {
                Caches.cachedBossBars.get((Object)pck.uuid).health = pck.health;
                break;
            }
            case UPDATE_TITLE: {
                Caches.cachedBossBars.get((Object)pck.uuid).title = pck.title;
                break;
            }
            case UPDATE_STYLE: {
                Caches.cachedBossBars.get((Object)pck.uuid).color = pck.color;
                break;
            }
            case UPDATE_FLAGS: {
                Caches.cachedBossBars.get((Object)pck.uuid).darkenSky = pck.darkenSky;
            }
        }
    }

}

