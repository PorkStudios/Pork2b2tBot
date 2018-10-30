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
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
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
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
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

public class PorkSessionAdapter
extends SessionAdapter {
    public static PorkClient client;
    public TooBeeTooTeeBot bot;

    public PorkSessionAdapter(PorkClient client, TooBeeTooTeeBot bot) {
        PorkSessionAdapter.client = client;
        this.bot = bot;
    }

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        System.out.println(">>>>>" + event.getPacket().toString());
        if (event.getPacket() instanceof LoginStartPacket && (((MinecraftProtocol)PorkSessionAdapter.client.session.getPacketProtocol()).getSubProtocol() == SubProtocol.LOGIN || ((MinecraftProtocol)PorkSessionAdapter.client.session.getPacketProtocol()).getSubProtocol() == SubProtocol.HANDSHAKE)) {
            String line;
            Scanner scan;
            LoginStartPacket pck = (LoginStartPacket)event.getPacket();
            ArrayList<String> whitelist = new ArrayList<String>();
            ArrayList<String> blacklist = new ArrayList<String>();
            try {
                scan = new Scanner(new File("whitelist-users.txt"));
                while (scan.hasNextLine()) {
                    line = scan.nextLine();
                    whitelist.add(line);
                }
                scan.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("No file");
            }
            try {
                scan = new Scanner(new File("blacklist-users.txt"));
                while (scan.hasNextLine()) {
                    line = scan.nextLine();
                    blacklist.add(line);
                }
                scan.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("No file");
            }
            if (whitelist.size() > 0 && !whitelist.contains(pck.getUsername())) {
                event.getSession().disconnect("\u00a7dSend nudes");
            }
            if (blacklist.size() > 0 && blacklist.contains(pck.getUsername())) {
                event.getSession().disconnect("\u00a72Too bad .-.");
            }
            PorkSessionAdapter.client.username = pck.getUsername();
        } else if (PorkSessionAdapter.client.loggedIn && ((MinecraftProtocol)PorkSessionAdapter.client.session.getPacketProtocol()).getSubProtocol() == SubProtocol.GAME && !(event.getPacket() instanceof ClientKeepAlivePacket) && !(event.getPacket() instanceof ClientTeleportConfirmPacket)) {
            if (event.getPacket() instanceof ClientChatPacket) {
                ClientChatPacket pck = (ClientChatPacket)event.getPacket();
                String mymsg = pck.getMessage();
                if (pck.getMessage().startsWith("$$")) {
                    ServerPlayerListEntryPacket addnewply;
                    Object signtext;
                    PlayerListEntry[] addply;
                    if (mymsg.startsWith("$$autosign")) {
                        if (mymsg.startsWith("$$autosign ")) {
                            signtext = mymsg.replace("$$autosign ", "");
                            if (signtext.split("~n~").length != 4) {
                                PorkSessionAdapter.client.session.send(new ServerChatPacket("-> You need to have 4 line (3 ~n~ = newline)", MessageType.CHAT));
                            } else {
                                Caches.signtext = signtext;
                                PorkSessionAdapter.client.session.send(new ServerChatPacket("-> Signtext set to : " + Caches.signtext, MessageType.CHAT));
                            }
                        }
                    } else if (mymsg.startsWith("$$adduser ")) {
                        signtext = mymsg.replace("$$adduser ", "").split(" ");
                        addply = new PlayerListEntry[]{new PlayerListEntry(new GameProfile(signtext[0], signtext[1]), GameMode.CREATIVE)};
                        addnewply = new ServerPlayerListEntryPacket(PlayerListEntryAction.UPDATE_GAMEMODE, addply);
                        PorkSessionAdapter.client.session.send(addnewply);
                        PlayerListEntry[] addplya = new PlayerListEntry[]{new PlayerListEntry(new GameProfile(signtext[0], signtext[1]))};
                        ServerPlayerListEntryPacket addnewplya = new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, addply);
                        PorkSessionAdapter.client.session.send(addnewplya);
                    } else if (mymsg.startsWith("$$deluser ")) {
                        signtext = mymsg.replace("$$deluser ", "");
                        addply = new PlayerListEntry[]{new PlayerListEntry(new GameProfile((String)signtext, ""))};
                        addnewply = new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER, addply);
                        PorkSessionAdapter.client.session.send(addnewply);
                    } else if (mymsg.startsWith("$$respawndelay ")) {
                        String respawndelay = mymsg.replace("$$respawndelay ", "");
                        Caches.autorespawndelay = Integer.parseInt(respawndelay);
                    } else if (mymsg.startsWith("$$autorespawn")) {
                        Config.doAutoRespawn = !Config.doAutoRespawn;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> doAutoRespawn is: " + Config.doAutoRespawn, MessageType.CHAT));
                    } else if (mymsg.startsWith("$$changeuser ")) {
                        signtext = mymsg.replace("$$changeuser ", "").split(" ");
                        Caches.playerChange[0] = signtext[0];
                        Caches.playerChange[1] = signtext[1];
                    } else if (mymsg.startsWith("$$chestselect")) {
                        Caches.chestselect = true;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> chestselect is: " + Caches.chestselect, MessageType.CHAT));
                    } else if (mymsg.startsWith("$$autofish")) {
                        Caches.autofish = Caches.autofish == false;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> autofish is: " + Caches.autofish, MessageType.CHAT));
                        Caches.bobberownerid = "";
                        Caches.bobberid = 0;
                    } else if (mymsg.startsWith("$$antiafk")) {
                        Config.doAntiAFK = !Config.doAntiAFK;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> antiafk is: " + Config.doAntiAFK, MessageType.CHAT));
                    } else if (mymsg.startsWith("$$cancelclose")) {
                        Caches.cancleclose = !Caches.cancleclose;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> cancleclose is: " + Caches.cancleclose, MessageType.CHAT));
                    } else if (mymsg.startsWith("$$ecaccess")) {
                        Caches.ecAccess = Caches.ecAccess == false;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> enderchest access is: " + Caches.ecAccess, MessageType.CHAT));
                    } else if (mymsg.startsWith("$$hidedeathscreen")) {
                        Caches.hideDeathScreen = !Caches.hideDeathScreen;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> hideDeathScreen access is: " + Caches.hideDeathScreen, MessageType.CHAT));
                    } else if (mymsg.startsWith("$$pgm")) {
                        Caches.pgm = !Caches.pgm;
                        PorkSessionAdapter.client.session.send(new ServerChatPacket("-> pgm is: " + Caches.pgm, MessageType.CHAT));
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
            } else if (!Caches.cancleclose || !(event.getPacket() instanceof ClientCloseWindowPacket)) {
                this.bot.client.getSession().send((Packet)event.getPacket());
                if (event.getPacket() instanceof ClientPlayerPositionRotationPacket) {
                    ClientPlayerPositionRotationPacket pck = (ClientPlayerPositionRotationPacket)event.getPacket();
                    Caches.x = pck.getX();
                    Caches.y = pck.getY();
                    Caches.z = pck.getZ();
                    Caches.yaw = (float)pck.getYaw();
                    Caches.pitch = (float)pck.getPitch();
                    Caches.updatePlayerLocRot();
                    Caches.onGround = pck.isOnGround();
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
                } else if (event.getPacket() instanceof ClientPlayerRotationPacket) {
                    ClientPlayerRotationPacket pck = (ClientPlayerRotationPacket)event.getPacket();
                    Caches.x = pck.getX();
                    Caches.y = pck.getY();
                    Caches.z = pck.getZ();
                    Caches.yaw = (float)pck.getYaw();
                    Caches.pitch = (float)pck.getPitch();
                    Caches.updatePlayerLocRot();
                    Caches.onGround = pck.isOnGround();
                }
            }
        }
    }

    @Override
    public void packetSent(PacketSentEvent event) {
        System.out.println(event.getPacket().toString());
        if (event.getPacket() instanceof LoginSuccessPacket) {
            PorkSessionAdapter.client.loggedIn = true;
            System.out.println("UUID: " + ((LoginSuccessPacket)event.getPacket()).getProfile().getIdAsString() + "\nBot UUID: " + TooBeeTooTeeBot.bot.protocol.getProfile().getIdAsString() + "\nUser name: " + ((LoginSuccessPacket)event.getPacket()).getProfile().getName() + "\nBot name: " + TooBeeTooTeeBot.bot.protocol.getProfile().getName());
        } else if (event.getPacket() instanceof ServerJoinGamePacket && !PorkSessionAdapter.client.sentChunks) {
            PorkSessionAdapter.client.session.send(new ServerPluginMessagePacket("MC|Brand", RefStrings.BRAND_ENCODED));
            for (Column chunk : Caches.cachedChunks.values()) {
                PorkSessionAdapter.client.session.send(new ServerChunkDataPacket(chunk));
            }
            System.out.println("Sent " + Caches.cachedChunks.size() + " chunks!");
            Session session = event.getSession();
            ServerPlayerListEntryPacket playerListEntryPacket = new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, TooBeeTooTeeBot.bot.playerListEntries.toArray(new PlayerListEntry[TooBeeTooTeeBot.bot.playerListEntries.size()]));
            PorkSessionAdapter.client.session.send(new ServerPlayerPositionRotationPacket(Caches.x, Caches.y, Caches.z, Caches.yaw, Caches.pitch, this.bot.r.nextInt(1000) + 10, new PositionElement[0]));
            PorkSessionAdapter.client.session.send(playerListEntryPacket);
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
            PorkSessionAdapter.client.sentChunks = true;
        }
    }

}

