/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.github.steveice10.packetlib.Session;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.gui.GuiBot;

public class ListenerPlayerListDataPacket
implements IPacketListener<ServerPlayerListDataPacket> {
    @Override
    public void handlePacket(Session session, ServerPlayerListDataPacket pck) {
        TooBeeTooTeeBot.bot.tabHeader = pck.getHeader();
        TooBeeTooTeeBot.bot.tabFooter = pck.getFooter();
        String header = TooBeeTooTeeBot.bot.tabHeader.getFullText();
        String footer = TooBeeTooTeeBot.bot.tabFooter.getFullText();
        String tps2b2t = footer.replaceAll("\u00a7.", "");
        Pattern p = Pattern.compile(".*tps (.*?) ping.*");
        Matcher m = p.matcher(tps2b2t);
        if (m.find()) {
            String curtps = m.group(1);
            if (!Caches.tps.equals(curtps)) {
                Caches.tps = curtps;
            }
        } else {
            Caches.tps = "";
        }
        GuiBot.INSTANCE.updateTitle();
    }
}

