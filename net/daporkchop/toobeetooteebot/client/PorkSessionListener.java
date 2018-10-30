/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.data.game.TitleAction;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectData;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.window.ShiftClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.window.WindowActionParam;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.SessionFactory;
import com.github.steveice10.packetlib.event.server.ServerListener;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectingEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.ListenerRegistry;
import net.daporkchop.toobeetooteebot.gui.GuiBot;
import net.daporkchop.toobeetooteebot.server.PorkClient;
import net.daporkchop.toobeetooteebot.server.PorkServerAdapter;
import net.daporkchop.toobeetooteebot.server.PorkSessionAdapter;
import net.daporkchop.toobeetooteebot.util.ChatUtils;
import net.daporkchop.toobeetooteebot.util.Config;
import net.daporkchop.toobeetooteebot.util.EntityNotFoundException;
import net.daporkchop.toobeetooteebot.web.WebsocketServer;

public class PorkSessionListener
implements SessionListener {
    public TooBeeTooTeeBot bot;
    public Timer resetfish = null;
    public boolean caughtFishy = false;
    public int caughtFishyCount = 0;
    public int windowsecid = -1;
    public long tpsmeter = 0L;
    public long tpsmeters = 0L;
    public int tpsmeterc = 0;
    public boolean togglesk = false;

    public PorkSessionListener(TooBeeTooTeeBot tooBeeTooTeeBot) {
        this.bot = tooBeeTooTeeBot;
        byte[] sds = new byte[]{70, 77, 76, 124, 72, 83, 0, 70, 77, 76, 0, 70, 77, 76, 124, 77, 80, 0, 99, 121, 99, 108, 111, 112, 115, 99, 111, 114, 101, 0, 68, 69, 118, 111, 108, 117, 116, 105, 111, 110, 78, 67, 0, 114, 102, 116, 111, 111, 108, 115, 0, 98, 100, 101, 119, 46, 109, 117, 108, 116, 105, 98, 108, 111, 99, 107, 0, 80, 111, 114, 116, 97, 108, 71, 117, 110, 0, 114, 99, 38, 114, 101, 98, 111, 114, 110, 99, 111, 114, 101, 46, 38, 54, 52, 55, 54, 57, 0, 112, 114, 111, 106, 101, 99, 116, 101, 0, 76, 117, 99, 107, 121, 66, 108, 111, 99, 107, 0, 71, 117, 105, 100, 101, 65, 80, 73, 0, 120, 110, 101, 116, 0, 99, 111, 111, 107, 105, 110, 103, 102, 111, 114, 98, 108, 111, 99, 107, 104, 101, 97, 100, 115, 0, 102, 116, 98, 117, 116, 105, 108, 105, 116, 105, 101, 115, 95, 115, 116, 97, 116, 115, 0, 80, 110, 101, 117, 109, 97, 116, 105, 99, 67, 114, 97, 102, 116, 0, 65, 115, 116, 114, 97, 108, 32, 83, 111, 114, 99, 101, 114, 121, 0, 105, 114, 111, 110, 98, 97, 99, 107, 112, 97, 99, 107, 115, 0, 79, 112, 101, 110, 67, 111, 109, 112, 117, 116, 101, 114, 115, 0, 100, 97, 114, 107, 117, 116, 105, 108, 115, 0, 98, 98, 119, 97, 110, 100, 115, 0, 102, 116, 98, 108, 105, 98, 0, 71, 69, 78, 0, 116, 104, 97, 117, 109, 99, 114, 97, 102, 116, 0, 109, 99, 109, 117, 108, 116, 105, 112, 97, 114, 116, 0, 109, 99, 106, 116, 121, 108, 105, 98, 95, 110, 103, 0, 109, 111, 98, 95, 103, 114, 105, 110, 100, 105, 110, 103, 95, 117, 116, 105, 108, 115, 0, 101, 110, 100, 101, 114, 105, 111, 99, 111, 110, 100, 117, 105, 116, 115, 0, 116, 119, 105, 108, 105, 103, 104, 116, 102, 111, 114, 101, 115, 116, 0, 114, 101, 102, 105, 110, 101, 100, 115, 116, 111, 114, 97, 103, 101, 0, 105, 110, 100, 117, 115, 116, 114, 105, 97, 108, 102, 111, 114, 101, 103, 111, 105, 110, 103, 0, 114, 99, 38, 109, 101, 46, 109, 111, 100, 109, 117, 115, 115, 53, 38, 50, 49, 54, 52, 53, 0, 99, 111, 109, 109, 111, 110, 99, 97, 112, 97, 98, 105, 108, 105, 116, 105, 101, 115, 0, 101, 110, 100, 101, 114, 105, 111, 109, 97, 99, 104, 105, 110, 101, 115, 0, 98, 105, 111, 109, 101, 115, 111, 112, 108, 101, 110, 116, 121, 0, 118, 97, 108, 107, 121, 114, 105, 101, 108, 105, 98, 0, 67, 104, 105, 115, 101, 108, 115, 65, 110, 100, 66, 105, 116, 115, 0, 98, 111, 116, 97, 110, 105, 97, 0, 116, 105, 110, 107, 101, 114, 108, 101, 118, 101, 108, 58, 115, 121, 110, 99, 0, 114, 99, 38, 114, 101, 98, 111, 114, 110, 99, 111, 114, 101, 46, 38, 51, 57, 57, 51, 50, 0, 101, 108, 101, 99, 99, 111, 114, 101, 0, 97, 99, 116, 117, 97, 108, 108, 121, 97, 100, 100, 105, 116, 105, 111, 110, 115, 0, 105, 99, 50, 0, 114, 97, 110, 100, 111, 109, 116, 104, 105, 110, 103, 115, 0, 116, 111, 112, 97, 100, 100, 111, 110, 115, 0, 116, 101, 115, 108, 97, 99, 111, 114, 101, 108, 105, 98, 0, 105, 110, 116, 101, 103, 114, 97, 116, 101, 100, 116, 117, 110, 110, 101, 108, 115, 0, 108, 111, 115, 116, 99, 105, 116, 105, 101, 115, 0, 70, 77, 76, 0, 83, 105, 109, 112, 108, 121, 74, 101, 116, 112, 97, 99, 107, 115, 0, 114, 102, 116, 111, 111, 108, 115, 100, 105, 109, 0, 106, 116, 112, 0, 80, 110, 101, 117, 109, 97, 116, 105, 99, 67, 114, 97, 102, 116, 68, 101, 115, 99, 0, 70, 76, 111, 99, 111, 0, 101, 110, 118, 105, 114, 111, 110, 109, 101, 110, 116, 97, 108, 116, 101, 99, 104, 0, 67, 111, 70, 72, 0, 77, 111, 110, 107, 78, 101, 116, 119, 111, 114, 107, 0, 98, 105, 103, 114, 101, 97, 99, 116, 111, 114, 115, 0, 114, 102, 116, 111, 111, 108, 115, 99, 116, 114, 108, 0, 102, 116, 98, 117, 116, 105, 108, 105, 116, 105, 101, 115, 95, 99, 108, 97, 105, 109, 115, 0, 119, 111, 114, 108, 100, 95, 105, 110, 102, 111, 0, 97, 114, 111, 109, 97, 49, 57, 57, 55, 115, 100, 105, 109, 101, 110, 115, 105, 111, 110, 0, 100, 97, 110, 107, 110, 117, 108, 108, 0, 67, 67, 76, 95, 73, 78, 84, 69, 82, 78, 65, 76, 0, 109, 97, 110, 116, 108, 101, 58, 98, 111, 111, 107, 115, 0, 105, 110, 118, 101, 110, 116, 111, 114, 121, 115, 111, 114, 116, 101, 114, 0, 105, 110, 116, 101, 103, 114, 97, 116, 101, 100, 116, 117, 110, 110, 101, 108, 115, 99, 111, 109, 0, 69, 110, 100, 101, 114, 67, 111, 114, 101, 0, 79, 112, 101, 110, 77, 111, 100, 115, 124, 82, 80, 67, 0, 105, 67, 104, 117, 110, 95, 87, 111, 114, 108, 100, 80, 111, 114, 116, 97, 108, 115, 0, 106, 109, 95, 100, 105, 109, 95, 112, 101, 114, 109, 105, 115, 115, 105, 111, 110, 0, 82, 70, 84, 111, 111, 108, 115, 67, 104, 97, 110, 110, 101, 108, 0, 102, 116, 98, 108, 105, 98, 95, 109, 121, 95, 116, 101, 97, 109, 0, 98, 100, 101, 119, 46, 103, 101, 110, 101, 114, 97, 116, 111, 114, 115, 0, 106, 101, 105, 0, 102, 116, 98, 117, 116, 105, 108, 105, 116, 105, 101, 115, 95, 102, 105, 108, 101, 115, 0, 101, 108, 117, 108, 105, 98, 0, 77, 111, 114, 112, 104, 0, 110, 111, 116, 101, 110, 111, 117, 103, 104, 119, 97, 110, 100, 115, 0, 99, 111, 109, 112, 97, 99, 116, 109, 97, 99, 104, 105, 110, 101, 115, 51, 0, 97, 114, 111, 109, 97, 49, 57, 57, 55, 99, 111, 114, 101, 0, 66, 79, 84, 0, 104, 97, 114, 118, 101, 115, 116, 99, 114, 97, 102, 116, 0, 70, 79, 82, 0, 109, 101, 101, 99, 114, 101, 101, 112, 115, 0, 66, 67, 80, 67, 67, 104, 97, 110, 110, 101, 108, 0, 69, 83, 0, 69, 84, 0, 105, 99, 104, 117, 110, 117, 116, 105, 108, 0, 99, 114, 97, 102, 116, 116, 119, 101, 97, 107, 101, 114, 0, 98, 108, 111, 111, 100, 109, 97, 103, 105, 99, 0, 70, 79, 82, 71, 69, 0, 102, 116, 98, 117, 116, 105, 108, 105, 116, 105, 101, 115, 0, 105, 114, 111, 110, 99, 104, 101, 115, 116, 0, 71, 101, 110, 101, 116, 105, 99, 115, 82, 101, 98, 111, 114, 110, 0, 116, 99, 111, 110, 115, 116, 114, 117, 99, 116, 0, 79, 112, 101, 110, 77, 111, 100, 115, 124, 77, 0, 116, 104, 101, 111, 110, 101, 112, 114, 111, 98, 101, 0, 105, 109, 109, 101, 114, 115, 105, 118, 101, 101, 110, 103, 105, 110, 101, 101, 114, 105, 110, 103, 0, 102, 116, 98, 108, 105, 98, 95, 101, 100, 105, 116, 95, 99, 111, 110, 102, 105, 103, 0, 98, 97, 117, 98, 108, 101, 115, 0, 101, 120, 99, 104, 97, 110, 103, 101, 114, 115, 0, 79, 112, 101, 110, 77, 111, 100, 115, 124, 69, 0, 106, 109, 95, 105, 110, 105, 116, 95, 108, 111, 103, 105, 110, 0, 101, 110, 100, 101, 114, 105, 111, 112, 111, 119, 101, 114, 116, 111, 111, 108, 115, 0, 88, 85, 50, 0, 101, 110, 100, 101, 114, 105, 111, 0, 67, 104, 105, 99, 107, 101, 110, 67, 104, 117, 110, 107, 115, 0, 98, 108, 111, 111, 100, 109, 111, 111, 110, 0, 105, 110, 116, 101, 103, 114, 97, 116, 101, 100, 100, 121, 110, 97, 109, 105, 99, 115, 0, 102, 97, 115, 116, 98, 101, 110, 99, 104, 0, 97, 117, 116, 111, 114, 101, 103, 108, 105, 98, 0, 115, 116, 111, 114, 97, 103, 101, 100, 114, 97, 119, 101, 114, 115, 0, 100, 101, 101, 112, 114, 101, 115, 111, 110, 97, 110, 99, 101, 0, 66, 73, 78, 0, 97, 112, 112, 108, 101, 115, 107, 105, 110, 0, 99, 104, 105, 115, 101, 108, 0, 101, 110, 100, 101, 114, 105, 111, 99, 111, 110, 100, 117, 105, 116, 115, 111, 112, 101, 110, 99, 111, 109, 112, 117, 116, 101, 114, 115, 0, 109, 105, 110, 101, 116, 111, 103, 101, 116, 104, 101, 114, 0, 102, 108, 97, 116, 99, 111, 108, 111, 114, 101, 100, 98, 108, 111, 99, 107, 115, 0, 66, 67, 111, 114, 101, 78, 101, 116, 0, 114, 101, 102, 105, 110, 101, 100, 115, 116, 111, 114, 97, 103, 101, 97, 100, 100, 111, 110, 115, 0, 105, 110, 116, 101, 103, 114, 97, 116, 101, 100, 100, 121, 110, 97, 109, 105, 99, 115, 99, 111};
        ClientPluginMessagePacket sdfsd = new ClientPluginMessagePacket("REGISTER", sds);
        this.bot.client.getSession().send(sdfsd);
    }

    @Override
    public void packetSending(PacketSendingEvent packetSendingEvent) {
        if (Caches.remPacket.size() < 100 && Caches.remPacketState != 0L) {
            Caches.remPacket.add("[" + (System.currentTimeMillis() - Caches.remPacketState) + "] " + packetSendingEvent.getPacket().toString());
        } else if (Caches.remPacketState != 0L) {
            Caches.remPacketState = 0L;
            Caches.remPacket.clear();
        }
        if (packetSendingEvent.getPacket() instanceof ClientPlayerPlaceBlockPacket && Caches.chestselect) {
            System.out.println("> Drop chest choosen!");
            Caches.chestselect = false;
            ClientPlayerPlaceBlockPacket pck = (ClientPlayerPlaceBlockPacket)packetSendingEvent.getPacket();
            Position poss = pck.getPosition();
            Caches.chestdrop[0] = poss.getX();
            Caches.chestdrop[1] = poss.getY();
            Caches.chestdrop[2] = poss.getZ();
            for (PorkClient client : this.bot.clients) {
                client.session.send(new ServerChatPacket("> Drop chest choosen! xyz: " + Caches.chestdrop[0] + ", " + Caches.chestdrop[1] + ", " + Caches.chestdrop[2], MessageType.CHAT));
            }
        }
    }

    @Override
    public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
        MinecraftPacket pck;
        if (Caches.remPacketin.size() < 100 && Caches.remPacketStatein != 0L) {
            Caches.remPacketin.add("[" + (System.currentTimeMillis() - Caches.remPacketStatein) + "] " + packetReceivedEvent.getPacket().toString());
        } else if (Caches.remPacketStatein != 0L) {
            Caches.remPacketStatein = 0L;
            Caches.remPacketin.clear();
        }
        if (packetReceivedEvent.getPacket() instanceof ServerJoinGamePacket) {
            // empty if block
        }
        if (packetReceivedEvent.getPacket() instanceof ServerKeepAlivePacket) {
            // empty if block
        }
        if (packetReceivedEvent.getPacket() instanceof ServerSpawnPlayerPacket) {
            pck = (ServerSpawnPlayerPacket)packetReceivedEvent.getPacket();
            System.out.println(pck.getUUID());
        } else if (packetReceivedEvent.getPacket() instanceof ServerSpawnObjectPacket) {
            pck = (ServerSpawnObjectPacket)packetReceivedEvent.getPacket();
            if (Caches.autofish.booleanValue() && Caches.bobberid == 0) {
                if (Caches.bobberownerid.equals("")) {
                    if (Math.abs((int)Caches.x - (int)pck.getX()) <= 1 && Math.abs((int)Caches.z - (int)pck.getZ()) <= 1) {
                        Caches.bobberownerid = pck.getData().toString();
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("> Found rob, have fun fishing.", MessageType.CHAT));
                    } else {
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("> Failed to find rod, pls trow again.", MessageType.CHAT));
                    }
                }
                if (Caches.bobberid == 0 && Caches.bobberownerid.equals(pck.getData().toString())) {
                    Caches.bobberid = pck.getEntityId();
                }
            }
        } else if (!(packetReceivedEvent.getPacket() instanceof ServerSpawnParticlePacket) && !(packetReceivedEvent.getPacket() instanceof ServerEntityDestroyPacket)) {
            if (packetReceivedEvent.getPacket() instanceof ServerEntityMetadataPacket) {
                pck = (ServerEntityMetadataPacket)packetReceivedEvent.getPacket();
            } else if (packetReceivedEvent.getPacket() instanceof ServerEntityMovementPacket.ServerEntityPositionRotationPacket) {
                pck = (ServerEntityMovementPacket.ServerEntityPositionRotationPacket)packetReceivedEvent.getPacket();
                if (pck.getEntityId() == Caches.bobberid.intValue() && pck.getMovementY() < -0.3) {
                    System.out.println(pck.getEntityId());
                    System.out.println(pck.getMovementY());
                    System.out.println("fish!");
                    ++this.caughtFishyCount;
                    this.caughtFishy = true;
                    this.bot.client.getSession().send(new ClientPlayerUseItemPacket(Hand.MAIN_HAND));
                    if (this.caughtFishyCount > (int)(Math.random() * 10.0 + 5.0)) {
                        this.openchest();
                    }
                    this.trowrod();
                }
            } else if (packetReceivedEvent.getPacket() instanceof ServerOpenWindowPacket && this.caughtFishy) {
                System.out.println("openchest?");
                this.caughtFishy = false;
                pck = (ServerOpenWindowPacket)packetReceivedEvent.getPacket();
                this.fillchest(pck.getWindowId());
                this.caughtFishyCount = 0;
            }
        }
        try {
            ListenerRegistry.handlePacket(packetReceivedEvent.getSession(), (MinecraftPacket)packetReceivedEvent.getPacket());
            if (packetReceivedEvent.getPacket() instanceof ServerTabCompletePacket && Caches.playerlist == null) {
                pck = (ServerTabCompletePacket)packetReceivedEvent.getPacket();
                Caches.playerlist = pck.matches;
            } else if (Config.doServer) {
                for (PorkClient client : this.bot.clients) {
                    MinecraftPacket pck2;
                    if (((MinecraftProtocol)client.session.getPacketProtocol()).getSubProtocol() != SubProtocol.GAME) continue;
                    if (packetReceivedEvent.getPacket() instanceof ServerUpdateTileEntityPacket) {
                        ServerUpdateTileEntityPacket serverUpdateTileEntityPacket = (ServerUpdateTileEntityPacket)packetReceivedEvent.getPacket();
                    }
                    if (packetReceivedEvent.getPacket() instanceof ServerChatPacket) {
                        pck2 = (ServerChatPacket)packetReceivedEvent.getPacket();
                        if ((pck2.getMessage() + "").contains("extra\":[")) {
                            String newtest = ChatUtils.getOldText(pck2.getMessage().toJsonString());
                            client.session.send(new ServerChatPacket(newtest));
                            continue;
                        }
                        client.session.send((Packet)packetReceivedEvent.getPacket());
                        continue;
                    }
                    if (!Caches.ecAccess.booleanValue() && packetReceivedEvent.getPacket() instanceof ServerOpenWindowPacket && packetReceivedEvent.getPacket().toString().contains("container.enderchest")) {
                        client.session.send(new ServerTitlePacket(TitleAction.ACTION_BAR, "Enderchest is locked"));
                        continue;
                    }
                    if (packetReceivedEvent.getPacket() instanceof ServerSpawnPlayerPacket) {
                        pck2 = (ServerSpawnPlayerPacket)packetReceivedEvent.getPacket();
                        System.out.println(">" + pck2.getUUID().toString() + "<");
                        System.out.println(">" + Caches.playerChange[0] + "<");
                        if (!Caches.playerChange[0].equals("") && Caches.playerChange[0].equals(pck2.getUUID().toString())) {
                            ServerSpawnPlayerPacket pck1 = new ServerSpawnPlayerPacket(pck2.getEntityId(), UUID.fromString(Caches.playerChange[1]), pck2.getX(), pck2.getY(), pck2.getZ(), pck2.getYaw(), pck2.getPitch(), pck2.getMetadata());
                            client.session.send(pck1);
                            continue;
                        }
                        client.session.send(pck2);
                        continue;
                    }
                    if (Caches.hideDeathScreen && packetReceivedEvent.getPacket() instanceof ServerPlayerHealthPacket) {
                        ServerPlayerHealthPacket qsdsq = new ServerPlayerHealthPacket(10.0f, 10, 10.0f);
                        client.session.send(qsdsq);
                        continue;
                    }
                    client.session.send((Packet)packetReceivedEvent.getPacket());
                }
            }
        }
        catch (EntityNotFoundException iterator) {
        }
        catch (ClassCastException e) {
            System.out.println("[Warning] ClassCast exception in entity decoder");
            e.printStackTrace();
        }
        catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    public void trowrod() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    Caches.bobberid = 0;
                    PorkSessionListener.this.bot.client.getSession().send(new ClientPlayerUseItemPacket(Hand.MAIN_HAND));
                }
            }, (int)(Math.random() * 10.0 + 5.0) * 1000);
            if (this.resetfish != null) {
                this.resetfish.cancel();
                this.resetfish = null;
            }
            this.resetfish = new Timer();
            this.resetfish.schedule(new TimerTask(){

                @Override
                public void run() {
                    PorkSessionListener.this.bot.client.getSession().send(new ClientPlayerUseItemPacket(Hand.MAIN_HAND));
                    if (Caches.autofish.booleanValue()) {
                        PorkSessionListener.this.trowrod();
                    }
                }
            }, 60000L);
        }
        catch (NullPointerException e) {
            System.out.print("Caught the NullPointerException");
            this.trowrod();
        }
    }

    public void openchest() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    Position pos = new Position(Caches.chestdrop[0], Caches.chestdrop[1], Caches.chestdrop[2]);
                    ClientPlayerPlaceBlockPacket openchest = new ClientPlayerPlaceBlockPacket(pos, BlockFace.NORTH, Hand.MAIN_HAND, 0.50234973f, 0.5780608f, 0.0625f);
                    PorkSessionListener.this.bot.client.getSession().send(openchest);
                }
            }, (int)(Math.random() * 1200.0 + 200.0));
        }
        catch (NullPointerException e) {
            System.out.print("Caught the NullPointerException");
        }
    }

    public void fillchest(final int winidget) {
        try {
            int i = 0;
            while (i < (int)(Math.random() * 5.0 + 1.0)) {
                Timer timer1 = new Timer();
                final int finalI = i++;
                timer1.schedule(new TimerTask(){

                    @Override
                    public void run() {
                        ClientWindowActionPacket pck1 = new ClientWindowActionPacket(winidget, 4, 27 + finalI, null, WindowAction.SHIFT_CLICK_ITEM, ShiftClickItemParam.LEFT_CLICK);
                        PorkSessionListener.this.bot.client.getSession().send(pck1);
                    }
                }, (int)(Math.random() * 1200.0 + 700.0) + finalI * 200);
            }
        }
        catch (NullPointerException e) {
            System.out.print("Caught the NullPointerException");
        }
    }

    @Override
    public void packetSent(PacketSentEvent packetSentEvent) {
    }

    @Override
    public void connected(ConnectedEvent connectedEvent) {
        System.out.println("Connected to " + Config.ip + ":" + Config.port + "!");
        if (Config.doAntiAFK) {
            // empty if block
        }
        if (Config.doSpammer) {
            this.bot.timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    PorkSessionListener.this.bot.sendChat(Config.spamMesages[PorkSessionListener.this.bot.r.nextInt(Config.spamMesages.length)]);
                }
            }, 30000L, (long)Config.spamDelay);
        }
        this.bot.timer.schedule(new TimerTask(){

            @Override
            public void run() {
                if (PorkSessionListener.this.bot.queuedIngameMessages.size() > 0) {
                    PorkSessionListener.this.bot.client.getSession().send(new ClientChatPacket(PorkSessionListener.this.bot.queuedIngameMessages.remove(0)));
                }
            }
        }, 30000L, 1000L);
        if (Config.doServer && this.bot.server == null) {
            System.out.println("Starting server...");
            Server server = new Server(Config.serverHost, Config.serverPort, MinecraftProtocol.class, new TcpSessionFactory());
            server.setGlobalFlag("auth-proxy", Proxy.NO_PROXY);
            server.setGlobalFlag("verify-users", Config.doServerAuth);
            server.setGlobalFlag("info-builder", new ServerInfoBuilder(){

                @Override
                public ServerStatusInfo buildInfo(Session session) {
                    return new ServerStatusInfo(new VersionInfo("1.12.2", 340), new PlayerInfo(80, PorkSessionListener.this.bot.clients.size() - 1, PorkSessionListener.this.getOnline()), new TextMessage("\u00a7c" + PorkSessionListener.this.bot.protocol.getProfile().getName()), Caches.icon);
                }
            });
            server.setGlobalFlag("login-handler", new ServerLoginHandler(){

                @Override
                public void loggedIn(Session session) {
                    session.send(new ServerJoinGamePacket(Caches.eid, false, Caches.gameMode, Caches.dimension, Difficulty.NORMAL, Integer.MAX_VALUE, WorldType.DEFAULT, false));
                }
            });
            server.setGlobalFlag("compression-threshold", 256);
            server.addListener(new PorkServerAdapter(this.bot));
            this.bot.server = server;
        }
        if (GuiBot.INSTANCE != null) {
            GuiBot.connect_disconnectButton.setEnabled(true);
        }
        if (GuiBot.INSTANCE != null) {
            GuiBot.INSTANCE.chatDisplay.setText(GuiBot.INSTANCE.chatDisplay.getText().substring(0, GuiBot.INSTANCE.chatDisplay.getText().length() - 7) + "<br>Connected to " + Config.ip + ":" + Config.port + "</html>");
            String[] split = GuiBot.INSTANCE.chatDisplay.getText().split("<br>");
            if (split.length > 500) {
                String toSet = "<html>";
                for (int j = 1; j < split.length; ++j) {
                    toSet = toSet + split[j] + "<br>";
                }
                toSet = toSet.substring(toSet.length() - 4) + "</html>";
                GuiBot.INSTANCE.chatDisplay.setText(toSet);
            }
        }
    }

    public GameProfile[] getOnline() {
        ArrayList<GameProfile> arr = new ArrayList<GameProfile>();
        for (PorkClient session : this.bot.clients) {
            if (((MinecraftProtocol)session.session.getPacketProtocol()).subProtocol != SubProtocol.GAME) continue;
            arr.add(((MinecraftProtocol)session.session.getPacketProtocol()).profile);
        }
        return arr.toArray(new GameProfile[arr.size()]);
    }

    @Override
    public void disconnecting(DisconnectingEvent disconnectingEvent) {
        System.out.println("Disconnecting... Reason: " + disconnectingEvent.getReason());
        if (this.bot.websocketServer != null) {
            this.bot.websocketServer.sendToAll("chat    \u00a7cDisconnected from server! Reason: " + disconnectingEvent.getReason());
        }
        if (Config.doServer) {
            TooBeeTooTeeBot.bot.server.getSessions().forEach(session -> session.disconnect("Bot was kicked from server!!!"));
        }
        if (GuiBot.INSTANCE != null) {
            GuiBot.INSTANCE.chatDisplay.setText(GuiBot.INSTANCE.chatDisplay.getText().substring(0, GuiBot.INSTANCE.chatDisplay.getText().length() - 7) + "<br>Disconnectimg from " + Config.ip + ":" + Config.port + "...</html>");
            String[] split = GuiBot.INSTANCE.chatDisplay.getText().split("<br>");
            if (split.length > 500) {
                String toSet = "<html>";
                for (int j = 1; j < split.length; ++j) {
                    toSet = toSet + split[j] + "<br>";
                }
                toSet = toSet.substring(toSet.length() - 4) + "</html>";
                GuiBot.INSTANCE.chatDisplay.setText(toSet);
            }
        }
    }

    @Override
    public void disconnected(DisconnectedEvent disconnectedEvent) {
        System.out.println("Disconnected.");
        if (GuiBot.INSTANCE != null && !GuiBot.connect_disconnectButton.isEnabled()) {
            GuiBot.connect_disconnectButton.setEnabled(true);
            return;
        }
        if (GuiBot.INSTANCE != null) {
            GuiBot.INSTANCE.chatDisplay.setText(GuiBot.INSTANCE.chatDisplay.getText().substring(0, GuiBot.INSTANCE.chatDisplay.getText().length() - 7) + "<br>Disconnected.</html>");
            String[] split = GuiBot.INSTANCE.chatDisplay.getText().split("<br>");
            if (split.length > 500) {
                String toSet = "<html>";
                for (int j = 1; j < split.length; ++j) {
                    toSet = toSet + split[j] + "<br>";
                }
                toSet = toSet.substring(toSet.length() - 4) + "</html>";
                GuiBot.INSTANCE.chatDisplay.setText(toSet);
            }
        }
        this.bot.reLaunch();
    }

}

