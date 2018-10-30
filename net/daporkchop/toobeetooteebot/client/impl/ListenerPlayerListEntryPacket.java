/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.packetlib.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.IPacketListener;
import net.daporkchop.toobeetooteebot.util.Config;
import net.daporkchop.toobeetooteebot.web.PlayData;
import net.daporkchop.toobeetooteebot.web.WebsocketServer;

public class ListenerPlayerListEntryPacket
implements IPacketListener<ServerPlayerListEntryPacket> {
    @Override
    public void handlePacket(Session session, ServerPlayerListEntryPacket pck) {
        switch (pck.getAction()) {
            case ADD_PLAYER: {
                block7 : for (PlayerListEntry entry : pck.getEntries()) {
                    PlayData data;
                    for (PlayerListEntry listEntry : TooBeeTooTeeBot.bot.playerListEntries) {
                        if (!listEntry.getProfile().getIdAsString().equals(entry.getProfile().getIdAsString())) continue;
                        continue block7;
                    }
                    TooBeeTooTeeBot.bot.playerListEntries.add(entry);
                    if (TooBeeTooTeeBot.bot.websocketServer != null) {
                        TooBeeTooTeeBot.bot.websocketServer.sendToAll("tabAdd  " + TooBeeTooTeeBot.getName(entry) + "\u2001" + entry.getPing() + "\u2001" + entry.getProfile().getIdAsString());
                    }
                    if (!Config.doStatCollection) continue;
                    String uuid = entry.getProfile().getId().toString();
                    if (TooBeeTooTeeBot.bot.uuidsToPlayData.containsKey(uuid)) {
                        data = TooBeeTooTeeBot.bot.uuidsToPlayData.get(uuid);
                        data.lastPlayed = System.currentTimeMillis();
                        continue;
                    }
                    data = new PlayData(uuid, TooBeeTooTeeBot.getName(entry));
                    TooBeeTooTeeBot.bot.uuidsToPlayData.put(data.UUID, data);
                }
                break;
            }
            case UPDATE_GAMEMODE: {
                break;
            }
            case UPDATE_LATENCY: {
                block9 : for (PlayerListEntry entry : pck.getEntries()) {
                    String uuid = entry.getProfile().getId().toString();
                    for (PlayerListEntry toChange : TooBeeTooTeeBot.bot.playerListEntries) {
                        if (!uuid.equals(toChange.getProfile().getId().toString())) continue;
                        toChange.ping = entry.getPing();
                        if (TooBeeTooTeeBot.bot.websocketServer != null) {
                            TooBeeTooTeeBot.bot.websocketServer.sendToAll("tabPing " + toChange.getDisplayName() + "\u2001" + toChange.getPing());
                        }
                        if (!Config.doStatCollection) continue block9;
                        for (PlayData playData : TooBeeTooTeeBot.bot.uuidsToPlayData.values()) {
                            int playTimeDifference = (int)(System.currentTimeMillis() - playData.lastPlayed);
                            int[] arrn = playData.playTimeByHour;
                            arrn[0] = arrn[0] + playTimeDifference;
                            int[] arrn2 = playData.playTimeByDay;
                            arrn2[0] = arrn2[0] + playTimeDifference;
                            playData.lastPlayed = System.currentTimeMillis();
                        }
                    }
                }
                break;
            }
            case UPDATE_DISPLAY_NAME: {
                break;
            }
            case REMOVE_PLAYER: {
                for (PlayerListEntry entry : pck.getEntries()) {
                    String uuid = entry.getProfile().getId().toString();
                    int removalIndex = -1;
                    for (int i = 0; i < TooBeeTooTeeBot.bot.playerListEntries.size(); ++i) {
                        PlayerListEntry player = TooBeeTooTeeBot.bot.playerListEntries.get(i);
                        if (!uuid.equals(player.getProfile().getId().toString())) continue;
                        removalIndex = i;
                        if (TooBeeTooTeeBot.bot.websocketServer != null) {
                            TooBeeTooTeeBot.bot.websocketServer.sendToAll("tabDel  " + TooBeeTooTeeBot.getName(player));
                        }
                        if (!Config.doStatCollection) break;
                        TooBeeTooTeeBot.bot.uuidsToPlayData.get((Object)uuid).lastPlayed = System.currentTimeMillis();
                        break;
                    }
                    if (removalIndex == -1) continue;
                    TooBeeTooTeeBot.bot.playerListEntries.remove(removalIndex);
                }
                break;
            }
        }
    }

}

