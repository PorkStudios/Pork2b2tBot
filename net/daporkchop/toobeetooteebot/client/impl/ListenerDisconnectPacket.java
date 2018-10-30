/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import java.io.PrintStream;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;

public class ListenerDisconnectPacket
implements IPacketListener<ServerDisconnectPacket> {
    @Override
    public void handlePacket(Session session, ServerDisconnectPacket pck) {
        System.out.println("Kicked during login! Reason: " + pck.getReason().getFullText());
        TooBeeTooTeeBot.bot.client.getSession().disconnect(pck.getReason().getFullText());
    }
}

