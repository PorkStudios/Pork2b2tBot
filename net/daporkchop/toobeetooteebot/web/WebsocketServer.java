/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.web;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.web.LoggedInPlayer;
import net.daporkchop.toobeetooteebot.web.NotRegisteredPlayer;
import net.daporkchop.toobeetooteebot.web.RegisteredPlayer;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebsocketServer
extends WebSocketServer {
    public WebsocketServer(int port) {
        super(new InetSocketAddress(port));
        TooBeeTooTeeBot.bot.timer.schedule(new TimerTask(){

            @Override
            public void run() {
                long maxIdleTime = System.currentTimeMillis() - 600000L;
                Iterator<LoggedInPlayer> iterator = TooBeeTooTeeBot.bot.namesToLoggedInPlayers.values().iterator();
                while (iterator.hasNext()) {
                    LoggedInPlayer next = iterator.next();
                    if (next.lastUsed >= maxIdleTime) continue;
                    iterator.remove();
                }
            }
        }, 10000L, 10000L);
    }

    @Override
    public void onOpen(final WebSocket conn, ClientHandshake handshake) {
        if (conn.isOpen()) {
            conn.send("connect ");
        }
        TooBeeTooTeeBot.bot.timer.schedule(new TimerTask(){

            @Override
            public void run() {
                String header;
                if (TooBeeTooTeeBot.bot.tabHeader != null && TooBeeTooTeeBot.bot.tabFooter != null) {
                    header = TooBeeTooTeeBot.bot.tabHeader.getFullText();
                    String footer = TooBeeTooTeeBot.bot.tabFooter.getFullText();
                    if (conn.isOpen()) {
                        conn.send("tabDiff " + header + "\u2001" + footer);
                    }
                } else if (TooBeeTooTeeBot.bot.tabHeader != null) {
                    header = TooBeeTooTeeBot.bot.tabHeader.getFullText();
                    if (conn.isOpen()) {
                        conn.send("tabDiff " + header + "\u2001 ");
                    }
                } else if (TooBeeTooTeeBot.bot.tabFooter != null) {
                    String footer = TooBeeTooTeeBot.bot.tabFooter.getFullText();
                    if (conn.isOpen()) {
                        conn.send("tabDiff  \u2001" + (String)footer);
                    }
                }
                for (PlayerListEntry entry : TooBeeTooTeeBot.bot.playerListEntries) {
                    if (!conn.isOpen()) continue;
                    conn.send("tabAdd  " + TooBeeTooTeeBot.getName(entry) + "\u2001" + entry.getPing() + "\u2001" + entry.getProfile().getIdAsString());
                }
            }
        }, 1000L);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            if (message.startsWith("login   ")) {
                message = message.substring(8);
                String[] split = message.split("\u2001");
                String username = split[0];
                String passwordHash = split[1];
                if (TooBeeTooTeeBot.bot.namesToRegisteredPlayers.containsKey(username)) {
                    RegisteredPlayer player = TooBeeTooTeeBot.bot.namesToRegisteredPlayers.get(username);
                    passwordHash = Hashing.sha256().hashString(passwordHash, Charsets.UTF_8).toString();
                    if (passwordHash.equals(player.passwordHash)) {
                        LoggedInPlayer toAdd = new LoggedInPlayer(player, conn);
                        TooBeeTooTeeBot.bot.namesToLoggedInPlayers.put(username, toAdd);
                        if (conn.isOpen()) {
                            conn.send("loginOk " + username + "\u2001" + passwordHash);
                        }
                        return;
                    }
                    if (conn.isOpen()) {
                        conn.send("loginErrInvalid password!");
                    }
                    return;
                }
                if (TooBeeTooTeeBot.bot.namesToTempAuths.containsKey(username)) {
                    NotRegisteredPlayer toAdd = TooBeeTooTeeBot.bot.namesToTempAuths.get(username);
                    if (conn.isOpen()) {
                        conn.send("loginErrThis account isn't registered! To register, please join 2b2t with the account and use <strong>/msg 2pork2bot register " + toAdd.tempAuthUUID + "</strong>! This registration information will expire after 10 minutes, but you can start the registration cycle again after that time expires.");
                    }
                    return;
                }
                NotRegisteredPlayer toAdd = new NotRegisteredPlayer();
                toAdd.name = username;
                toAdd.pwd = passwordHash;
                toAdd.tempAuthUUID = UUID.randomUUID().toString();
                TooBeeTooTeeBot.bot.namesToTempAuths.put(toAdd.name, toAdd);
                if (conn.isOpen()) {
                    conn.send("loginErrThis account isn't registered! To register, please join 2b2t with the account and use <strong>/msg 2pork2bot register " + toAdd.tempAuthUUID + "</strong>! This registration information will expire after 10 minutes, but you can start the registration cycle again after that time expires.");
                }
                return;
            }
            if (message.startsWith("sendChat")) {
                message = message.substring(8);
                String[] split = message.split("\u2001");
                String text = split[0];
                String targetName = split[1];
                String username = split[2];
                String password = split[3];
                LoggedInPlayer player = TooBeeTooTeeBot.bot.namesToLoggedInPlayers.getOrDefault(username, null);
                if (player == null) {
                    RegisteredPlayer registeredPlayer = TooBeeTooTeeBot.bot.namesToRegisteredPlayers.getOrDefault(username, null);
                    if (registeredPlayer == null) {
                        if (conn.isOpen()) {
                            conn.send("loginErrSomething is SERIOUSLY wrong. Please report this to DaPorkchop_ ASAP. A RegisteredPlayer was null! (or you broke the script lol)");
                        }
                        return;
                    }
                    LoggedInPlayer toAdd = new LoggedInPlayer(registeredPlayer, conn);
                    TooBeeTooTeeBot.bot.namesToLoggedInPlayers.put(toAdd.player.name, toAdd);
                    player = toAdd;
                }
                if (player != null) {
                    if (player.player.passwordHash.equals(password)) {
                        if (player.lastSentMessage + 5000L > System.currentTimeMillis()) {
                            if (conn.isOpen()) {
                                conn.send("loginErrPlease slow down! Max. 1 message per 5 seconds!");
                            }
                            return;
                        }
                        player.lastSentMessage = System.currentTimeMillis();
                        if (conn.isOpen()) {
                            conn.send("chat    " + new StringBuilder().append("\u00a7dTo ").append(targetName).append(": ").append(text).toString().replace("<", "&lt;").replace(">", "&gt;"));
                        }
                        if (conn.isOpen()) {
                            conn.send("chatSent");
                        }
                        TooBeeTooTeeBot.bot.queueMessage("/msg " + targetName + " " + username + ": " + text);
                        return;
                    }
                    if (conn.isOpen()) {
                        conn.send("loginErrSomething is SERIOUSLY wrong. Please report this to DaPorkchop_ ASAP. Invalid password sent with chat! (or you broke the script lol)");
                    }
                    return;
                }
                if (conn.isOpen()) {
                    conn.send("loginErrSomething is SERIOUSLY wrong. Please report this to DaPorkchop_ ASAP. A message was sent for an invalid user! (or you broke the script lol)");
                }
                return;
            }
        }
        catch (Exception e) {
            if (conn.isOpen()) {
                conn.send("loginErr" + e.getMessage());
            }
            return;
        }
    }

    @Override
    public void onFragment(WebSocket conn, Framedata fragment) {
        System.out.println("received fragment: " + fragment);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null && conn.isOpen()) {
            conn.send("error   ");
        }
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket started!");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendToAll(String text) {
        Collection<WebSocket> con;
        Collection<WebSocket> collection = con = this.connections();
        synchronized (collection) {
            for (WebSocket c : con) {
                if (!c.isOpen()) continue;
                c.send(text);
            }
        }
    }

}

