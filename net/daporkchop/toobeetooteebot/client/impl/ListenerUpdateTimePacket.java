/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import java.io.PrintStream;
import javax.swing.JLabel;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.gui.GuiBot;

public class ListenerUpdateTimePacket
implements IPacketListener<ServerUpdateTimePacket> {
    @Override
    public void handlePacket(Session session, ServerUpdateTimePacket pck) {
        if (!TooBeeTooTeeBot.bot.isLoggedIn) {
            int j;
            String[] split;
            String toSet;
            System.out.println("Logged in!");
            TooBeeTooTeeBot.bot.isLoggedIn = true;
            if (GuiBot.INSTANCE != null) {
                GuiBot.INSTANCE.chatDisplay.setText(GuiBot.INSTANCE.chatDisplay.getText().substring(0, GuiBot.INSTANCE.chatDisplay.getText().length() - 7) + "<br>Logged in!</html>");
                split = GuiBot.INSTANCE.chatDisplay.getText().split("<br>");
                if (split.length > 500) {
                    toSet = "<html>";
                    for (j = 1; j < split.length; ++j) {
                        toSet = toSet + split[j] + "<br>";
                    }
                    toSet = toSet.substring(toSet.length() - 4) + "</html>";
                    GuiBot.INSTANCE.chatDisplay.setText(toSet);
                }
            }
            if (!TooBeeTooTeeBot.bot.server.isListening()) {
                TooBeeTooTeeBot.bot.server.bind(true);
                System.out.println("Started server!");
                if (GuiBot.INSTANCE != null) {
                    GuiBot.INSTANCE.chatDisplay.setText(GuiBot.INSTANCE.chatDisplay.getText().substring(0, GuiBot.INSTANCE.chatDisplay.getText().length() - 7) + "<br>Started server!</html>");
                    split = GuiBot.INSTANCE.chatDisplay.getText().split("<br>");
                    if (split.length > 500) {
                        toSet = "<html>";
                        for (j = 1; j < split.length; ++j) {
                            toSet = toSet + split[j] + "<br>";
                        }
                        toSet = toSet.substring(toSet.length() - 4) + "</html>";
                        GuiBot.INSTANCE.chatDisplay.setText(toSet);
                    }
                }
            }
        }
    }
}

