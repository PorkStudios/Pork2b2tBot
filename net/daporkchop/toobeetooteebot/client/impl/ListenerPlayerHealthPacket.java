/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Timer;
import java.util.TimerTask;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.gui.GuiBot;
import net.daporkchop.toobeetooteebot.util.Config;

public class ListenerPlayerHealthPacket
implements IPacketListener<ServerPlayerHealthPacket> {
    public static float myhealth = 0.0f;
    public static float myfood = 0.0f;

    @Override
    public void handlePacket(Session session, ServerPlayerHealthPacket pck) {
        if (myhealth != pck.getHealth() || myfood != (float)pck.getFood()) {
            myhealth = pck.getHealth();
            myfood = pck.getFood();
            GuiBot.INSTANCE.updateTitle();
        }
        if (Config.doAutoRespawn && pck.getHealth() < 1.0f) {
            TooBeeTooTeeBot.bot.timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    TooBeeTooTeeBot.bot.client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                    Caches.cachedChunks.clear();
                }
            }, Caches.autorespawndelay);
        }
    }

}

