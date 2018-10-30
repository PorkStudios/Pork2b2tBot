/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPlayer;
import net.daporkchop.toobeetooteebot.util.EntityNotFoundException;

public class Caches {
    public static double x = 0.0;
    public static double y = 0.0;
    public static double z = 0.0;
    public static float yaw = 0.0f;
    public static float pitch = 0.0f;
    public static int dimension = 0;
    public static int eid = 0;
    public static GameMode gameMode = GameMode.SURVIVAL;
    public static boolean onGround;
    public static EntityPlayer player;
    public static UUID uuid;
    public static Long2ObjectMap<Column> cachedChunks;
    public static Int2ObjectMap<Entity> cachedEntities;
    public static HashMap<UUID, ServerBossBarPacket> cachedBossBars;
    public static BufferedImage icon;
    public static String tps;
    public static String signtext;
    public static Boolean autotext;
    public static Boolean autofish;
    public static Integer bobberid;
    public static String bobberownerid;
    public static String tempsessionID;
    public static ArrayList<String> remChat;
    public static Boolean remChatState;
    public static ArrayList<String> remPacket;
    public static long remPacketState;
    public static ArrayList<String> remPacketin;
    public static long remPacketStatein;
    public static Boolean ecAccess;
    public static String[] playerChange;
    public static int[] chestdrop;
    public static boolean chestselect;
    public static boolean hideDeathScreen;
    public static boolean cancleclose;
    public static int autorespawndelay;
    public static String[] playerlist;
    public static boolean pgm;

    public static void updatePlayerLocRot() {
        Caches.player.x = x;
        Caches.player.y = y;
        Caches.player.z = z;
        Caches.player.yaw = yaw;
        Caches.player.pitch = pitch;
    }

    public static Entity getEntityByEID(int eid) {
        Entity entity = cachedEntities.get(eid);
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        return entity;
    }

    static {
        cachedChunks = new Long2ObjectOpenHashMap<Column>();
        cachedEntities = new Int2ObjectOpenHashMap<Entity>();
        cachedBossBars = new HashMap();
        icon = new BufferedImage(64, 64, 2);
        tps = "";
        signtext = "~n~ ~n~ ~n~ ";
        autotext = false;
        autofish = false;
        bobberid = 0;
        bobberownerid = "";
        tempsessionID = "";
        remChat = new ArrayList();
        remChatState = false;
        remPacket = new ArrayList();
        remPacketState = 0L;
        remPacketin = new ArrayList();
        remPacketStatein = 0L;
        ecAccess = true;
        playerChange = new String[]{"", ""};
        chestdrop = new int[]{0, 0, 0};
        chestselect = false;
        hideDeathScreen = false;
        cancleclose = false;
        autorespawndelay = 60000;
        playerlist = null;
        pgm = false;
        Graphics g2 = icon.getGraphics();
        g2.setColor(new Color(0, 0, 0, 1));
        g2.fillRect(0, 0, 256, 256);
    }
}

