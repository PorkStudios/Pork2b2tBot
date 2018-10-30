/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.server;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.Effect;
import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.attribute.Attribute;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.mc.protocol.data.game.entity.type.PaintingType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.HangingDirection;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectData;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectType;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.entity.EntityType;
import net.daporkchop.toobeetooteebot.entity.PotionEffect;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.api.EntityRotation;
import net.daporkchop.toobeetooteebot.entity.impl.EntityMob;
import net.daporkchop.toobeetooteebot.entity.impl.EntityObject;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPainting;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPlayer;
import net.daporkchop.toobeetooteebot.gui.GuiBot;
import net.daporkchop.toobeetooteebot.server.PorkClient;
import net.daporkchop.toobeetooteebot.util.Config;
import net.daporkchop.toobeetooteebot.util.RefStrings;

public class PorkSessionAdapterBecked
extends SessionAdapter {
    public static PorkClient client;
    public TooBeeTooTeeBot bot;

    public PorkSessionAdapterBecked(PorkClient client, TooBeeTooTeeBot bot) {
        PorkSessionAdapterBecked.client = client;
        this.bot = bot;
    }

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        if (event.getPacket() instanceof LoginStartPacket && (((MinecraftProtocol)PorkSessionAdapterBecked.client.session.getPacketProtocol()).getSubProtocol() == SubProtocol.LOGIN || ((MinecraftProtocol)PorkSessionAdapterBecked.client.session.getPacketProtocol()).getSubProtocol() == SubProtocol.HANDSHAKE)) {
            LoginStartPacket pck = (LoginStartPacket)event.getPacket();
            if (Config.doServerWhitelist && !Config.whitelistedNames.contains(pck.getUsername())) {
                event.getSession().send(new ServerDisconnectPacket("\u00a76why tf do you thonk you can just use my bot??? reeeeeeeeee       - DaPorkchop_"));
                event.getSession().disconnect(null);
                try {
                    Files.write(Paths.get("whitelist.txt", new String[0]), ("\n" + pck.getUsername() + " just tried to connect!!! ip:" + event.getSession().getHost()).getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PorkSessionAdapterBecked.client.username = pck.getUsername();
        } else if (PorkSessionAdapterBecked.client.loggedIn && ((MinecraftProtocol)PorkSessionAdapterBecked.client.session.getPacketProtocol()).getSubProtocol() == SubProtocol.GAME && !(event.getPacket() instanceof ClientKeepAlivePacket) && !(event.getPacket() instanceof ClientTeleportConfirmPacket)) {
            if (PorkSessionAdapterBecked.client.arrayIndex == 90) {
                if (event.getPacket() instanceof ClientPlayerPositionRotationPacket) {
                    ClientPlayerPositionRotationPacket pck = (ClientPlayerPositionRotationPacket)event.getPacket();
                    Caches.x = pck.getX();
                    Caches.y = pck.getY();
                    Caches.z = pck.getZ();
                    Caches.yaw = (float)pck.getYaw();
                    Caches.pitch = (float)pck.getPitch();
                    Caches.updatePlayerLocRot();
                    Caches.onGround = pck.isOnGround();
                    ServerPlayerPositionRotationPacket toSend = new ServerPlayerPositionRotationPacket(Caches.x, Caches.y, Caches.z, Caches.yaw, Caches.pitch, this.bot.r.nextInt(100), new PositionElement[0]);
                    for (PorkClient porkClient : this.bot.clients) {
                        if (porkClient.arrayIndex == 0 || ((MinecraftProtocol)porkClient.session.getPacketProtocol()).getSubProtocol() == SubProtocol.GAME) continue;
                        porkClient.session.send(toSend);
                    }
                    this.bot.client.getSession().send(pck);
                } else if (event.getPacket() instanceof ClientPlayerPositionPacket) {
                    ClientPlayerPositionPacket pck = (ClientPlayerPositionPacket)event.getPacket();
                    if (Caches.x != pck.getX() || Caches.y != pck.getY() || Caches.z != pck.getZ()) {
                        GuiBot.INSTANCE.updateTitle();
                    }
                    Caches.x = pck.getX();
                    Caches.y = pck.getY();
                    Caches.z = pck.getZ();
                    Caches.updatePlayerLocRot();
                    Caches.onGround = pck.isOnGround();
                    ServerPlayerPositionRotationPacket toSend = new ServerPlayerPositionRotationPacket(Caches.x, Caches.y, Caches.z, Caches.yaw, Caches.pitch, this.bot.r.nextInt(100), new PositionElement[0]);
                    for (PorkClient porkClient : this.bot.clients) {
                        if (porkClient.arrayIndex == 0 || ((MinecraftProtocol)porkClient.session.getPacketProtocol()).getSubProtocol() == SubProtocol.GAME) continue;
                        porkClient.session.send(toSend);
                    }
                    this.bot.client.getSession().send(pck);
                } else if (event.getPacket() instanceof ClientPlayerRotationPacket) {
                    ClientPlayerRotationPacket pck = (ClientPlayerRotationPacket)event.getPacket();
                    Caches.x = pck.getX();
                    Caches.y = pck.getY();
                    Caches.z = pck.getZ();
                    Caches.yaw = (float)pck.getYaw();
                    Caches.pitch = (float)pck.getPitch();
                    Caches.updatePlayerLocRot();
                    Caches.onGround = pck.isOnGround();
                    ServerPlayerPositionRotationPacket toSend = new ServerPlayerPositionRotationPacket(Caches.x, Caches.y, Caches.z, Caches.yaw, Caches.pitch, this.bot.r.nextInt(100), new PositionElement[0]);
                    for (PorkClient porkClient : this.bot.clients) {
                        if (porkClient.arrayIndex == 0 || ((MinecraftProtocol)porkClient.session.getPacketProtocol()).getSubProtocol() == SubProtocol.GAME) continue;
                        porkClient.session.send(toSend);
                    }
                    this.bot.client.getSession().send(pck);
                } else {
                    if (event.getPacket() instanceof ClientChatPacket) {
                        ClientChatPacket pck = (ClientChatPacket)event.getPacket();
                        String mymsg = pck.getMessage();
                        if (pck.getMessage().startsWith("$$")) {
                            if (pck.getMessage().equals("$$setmainclient")) {
                                PorkSessionAdapterBecked.client.session.send(new ServerChatPacket("You're already at index 0!", MessageType.CHAT));
                                return;
                            }
                            if (pck.getMessage().startsWith("$$!")) {
                                this.bot.client.getSession().send(new ClientChatPacket(pck.message.substring(1)));
                                return;
                            }
                            if (mymsg.startsWith("$$autosign")) {
                                if (mymsg.startsWith("$$autosign ")) {
                                    String signtext = mymsg.replace("$$autosign ", "");
                                    if (signtext.split("~n~").length != 4) {
                                        PorkSessionAdapterBecked.client.session.send(new ServerChatPacket("-> You need to have 4 line (3 ~n~ = newline)", MessageType.CHAT));
                                    } else {
                                        Caches.signtext = signtext;
                                        PorkSessionAdapterBecked.client.session.send(new ServerChatPacket("-> Signtext set to : " + Caches.signtext, MessageType.CHAT));
                                    }
                                }
                            } else if (mymsg.startsWith("$$autofish")) {
                                Caches.autofish = Caches.autofish == false;
                                PorkSessionAdapterBecked.client.session.send(new ServerChatPacket("-> autofish is: " + Caches.autofish, MessageType.CHAT));
                                Caches.bobberownerid = "";
                                Caches.bobberid = 0;
                            } else if (mymsg.startsWith("$$ecaccess")) {
                                Caches.ecAccess = Caches.ecAccess == false;
                                PorkSessionAdapterBecked.client.session.send(new ServerChatPacket("-> enderchest access is: " + Caches.ecAccess, MessageType.CHAT));
                            } else if (pck.message.startsWith("$$dc")) {
                                this.bot.client.getSession().disconnect("Reboot!");
                                this.bot.server.close();
                                if (pck.message.startsWith("$$dchard")) {
                                    Runtime.getRuntime().exit(0);
                                }
                                return;
                            }
                            ServerChatPacket toSend = new ServerChatPacket(pck.getMessage(), MessageType.CHAT);
                            for (PorkClient client : this.bot.clients) {
                                if (((MinecraftProtocol)client.session.getPacketProtocol()).getSubProtocol() != SubProtocol.GAME) continue;
                                client.session.send(toSend);
                            }
                            return;
                        }
                        this.bot.client.getSession().send((Packet)event.getPacket());
                        return;
                    }
                    if (event.getPacket() instanceof ClientUpdateSignPacket) {
                        ClientUpdateSignPacket singpck = (ClientUpdateSignPacket)event.getPacket();
                        String[] mylines = new String[4];
                        String[] preset = new String[4];
                        for (int line = 0; line < 4; ++line) {
                            mylines[line] = singpck.getLines()[line].replace("$$", "\u00a7\u00a7\u00a7\u00a7\u00a711");
                            preset = (Caches.signtext + " ").replace("$$", "\u00a7\u00a7\u00a7\u00a7\u00a711").split("~n~");
                        }
                        if (mylines[0].equals("1:")) {
                            ClientUpdateSignPacket newsingpck = new ClientUpdateSignPacket(singpck.getPosition(), preset);
                            System.out.println("singa: " + newsingpck.toString());
                            this.bot.client.getSession().send(newsingpck);
                        } else if (!mylines[0].equals("")) {
                            ClientUpdateSignPacket newsingpck = new ClientUpdateSignPacket(singpck.getPosition(), mylines);
                            this.bot.client.getSession().send(newsingpck);
                        }
                    } else {
                        this.bot.client.getSession().send((Packet)event.getPacket());
                    }
                }
            } else {
                this.bot.client.getSession().send((Packet)event.getPacket());
            }
        }
    }

    @Override
    public void packetSent(PacketSentEvent event) {
        if (event.getPacket() instanceof LoginSuccessPacket) {
            PorkSessionAdapterBecked.client.loggedIn = true;
            System.out.println("UUID: " + ((LoginSuccessPacket)event.getPacket()).getProfile().getIdAsString() + "\nBot UUID: " + TooBeeTooTeeBot.bot.protocol.getProfile().getIdAsString() + "\nUser name: " + ((LoginSuccessPacket)event.getPacket()).getProfile().getName() + "\nBot name: " + TooBeeTooTeeBot.bot.protocol.getProfile().getName());
        } else if (event.getPacket() instanceof ServerJoinGamePacket && !PorkSessionAdapterBecked.client.sentChunks) {
            PorkSessionAdapterBecked.client.session.send(new ServerPluginMessagePacket("MC|Brand", RefStrings.BRAND_ENCODED));
            for (Column chunk : Caches.cachedChunks.values()) {
                PorkSessionAdapterBecked.client.session.send(new ServerChunkDataPacket(chunk));
            }
            System.out.println("Sent " + Caches.cachedChunks.size() + " chunks!");
            Session session = event.getSession();
            ServerPlayerListEntryPacket playerListEntryPacket = new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, TooBeeTooTeeBot.bot.playerListEntries.toArray(new PlayerListEntry[TooBeeTooTeeBot.bot.playerListEntries.size()]));
            PorkSessionAdapterBecked.client.session.send(new ServerPlayerPositionRotationPacket(Caches.x, Caches.y, Caches.z, Caches.yaw, Caches.pitch, this.bot.r.nextInt(1000) + 10, new PositionElement[0]));
            PorkSessionAdapterBecked.client.session.send(playerListEntryPacket);
            for (Entity entity : Caches.cachedEntities.values()) {
                switch (entity.type) {
                    case MOB: {
                        EntityMob mob = (EntityMob)entity;
                        session.send(new ServerSpawnMobPacket(entity.entityId, entity.uuid, mob.mobType, entity.x, entity.y, entity.z, mob.yaw, mob.pitch, mob.headYaw, mob.motX, mob.motY, mob.motZ, mob.metadata));
                        for (PotionEffect effect : mob.potionEffects) {
                            session.send(new ServerEntityEffectPacket(entity.entityId, effect.effect, effect.amplifier, effect.duration, effect.ambient, effect.showParticles));
                        }
                        for (Map.Entry entry : mob.equipment.entrySet()) {
                            session.send(new ServerEntityEquipmentPacket(entity.entityId, (EquipmentSlot)((Object)entry.getKey()), (ItemStack)entry.getValue()));
                        }
                        if (mob.properties.size() <= 0) break;
                        session.send(new ServerEntityPropertiesPacket(entity.entityId, mob.properties));
                        break;
                    }
                    case PLAYER: {
                        EntityPlayer playerSending = (EntityPlayer)entity;
                        session.send(new ServerSpawnPlayerPacket(entity.entityId, entity.uuid, entity.x, entity.y, entity.z, playerSending.yaw, playerSending.pitch, playerSending.metadata));
                    }
                    case REAL_PLAYER: {
                        EntityPlayer player = (EntityPlayer)entity;
                        for (PotionEffect effect : player.potionEffects) {
                            session.send(new ServerEntityEffectPacket(entity.entityId, effect.effect, effect.amplifier, effect.duration, effect.ambient, effect.showParticles));
                        }
                        for (Map.Entry entry : player.equipment.entrySet()) {
                            session.send(new ServerEntityEquipmentPacket(entity.entityId, (EquipmentSlot)((Object)entry.getKey()), (ItemStack)entry.getValue()));
                        }
                        if (player.properties.size() <= 0) break;
                        session.send(new ServerEntityPropertiesPacket(entity.entityId, player.properties));
                        break;
                    }
                    case OBJECT: {
                        EntityObject object = (EntityObject)entity;
                        session.send(new ServerSpawnObjectPacket(entity.entityId, entity.uuid, object.objectType, object.data, entity.x, entity.y, entity.z, object.yaw, object.pitch));
                        break;
                    }
                    case PAINTING: {
                        EntityPainting painting = (EntityPainting)entity;
                        session.send(new ServerSpawnPaintingPacket(entity.entityId, entity.uuid, painting.paintingType, new Position((int)entity.x, (int)entity.y, (int)entity.z), painting.direction));
                    }
                }
            }
            for (Entity entity : Caches.cachedEntities.values()) {
                if (entity.passengerIds.length > 0) {
                    session.send(new ServerEntitySetPassengersPacket(entity.entityId, entity.passengerIds));
                }
                if (!(entity instanceof EntityRotation)) continue;
                EntityRotation rotation = (EntityRotation)entity;
                if (!rotation.isLeashed) continue;
                session.send(new ServerEntityAttachPacket(entity.entityId, rotation.leashedID));
            }
            System.out.println("Sent " + Caches.cachedEntities.size() + " entities!");
            for (ServerBossBarPacket packet : Caches.cachedBossBars.values()) {
                session.send(packet);
            }
            System.out.println("Sent " + Caches.cachedBossBars.size() + " boss bars!");
            PorkSessionAdapterBecked.client.sentChunks = true;
        }
    }

}

