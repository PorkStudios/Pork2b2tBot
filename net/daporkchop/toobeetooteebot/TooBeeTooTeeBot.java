/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.InvalidCredentialsException;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerState;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.data.game.window.ShiftClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.window.WindowActionParam;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.SessionFactory;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executor;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.PorkSessionListener;
import net.daporkchop.toobeetooteebot.entity.api.Entity;
import net.daporkchop.toobeetooteebot.entity.impl.EntityPlayer;
import net.daporkchop.toobeetooteebot.gui.GuiBot;
import net.daporkchop.toobeetooteebot.server.PorkClient;
import net.daporkchop.toobeetooteebot.util.Config;
import net.daporkchop.toobeetooteebot.util.DataTag;
import net.daporkchop.toobeetooteebot.util.HTTPUtils;
import net.daporkchop.toobeetooteebot.web.LoggedInPlayer;
import net.daporkchop.toobeetooteebot.web.NotRegisteredPlayer;
import net.daporkchop.toobeetooteebot.web.PlayData;
import net.daporkchop.toobeetooteebot.web.RegisteredPlayer;
import net.daporkchop.toobeetooteebot.web.WebsocketServer;

public class TooBeeTooTeeBot {
    public static TooBeeTooTeeBot bot;
    public Client client = null;
    public Random r = new Random();
    public Timer timer = new Timer();
    public boolean firstRun = true;
    public WebsocketServer websocketServer;
    public Message tabHeader;
    public Message tabFooter;
    public ArrayList<PlayerListEntry> playerListEntries = new ArrayList();
    public MinecraftProtocol protocol;
    public DataTag loginData = new DataTag(new File(System.getProperty("user.dir") + File.separator + "players.dat"));
    public HashMap<String, RegisteredPlayer> namesToRegisteredPlayers;
    public HashMap<String, NotRegisteredPlayer> namesToTempAuths = new HashMap();
    public HashMap<String, LoggedInPlayer> namesToLoggedInPlayers = new HashMap();
    public ArrayList<PorkClient> clients = new ArrayList();
    public boolean isLoggedIn = false;
    public HashMap<Session, PorkClient> sessionToClient = new HashMap();
    public Server server = null;
    public ArrayList<String> queuedIngameMessages = new ArrayList();
    public HashMap<String, Long> ingamePlayerCooldown = new HashMap();
    public DataTag playData = new DataTag(new File(System.getProperty("user.dir") + File.separator + "online.dat"));
    public HashMap<String, PlayData> uuidsToPlayData;
    public boolean hasDonePostConnect = false;
    public boolean isAuth = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String getStringFromInputStream(InputStream is) {
        StringBuilder sb;
        BufferedReader br = null;
        sb = new StringBuilder();
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String outauth = "nok";
        try {
            try {
                URL oracle = new URL("http://killet.tk/auth/maxprox?ver=" + GuiBot.verion);
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                outauth = in.readLine();
            }
            catch (IOException e) {
                URL oracle = new URL("http://proxy.killet.tk/auth/maxprox?ver=" + GuiBot.verion);
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                outauth = in.readLine();
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("maxtor's server is down .-.");
        }
        if (outauth.equals("ok")) {
            new TooBeeTooTeeBot().start(args);
        } else if (outauth.equals("ban")) {
            System.out.println("Ur banned");
        } else {
            System.out.println("no access :( ask max");
        }
        if (Config.doWebsocket) {
            try {
                int remport = Config.websocketPort;
                if (remport == -1) {
                    remport = Config.serverPort + 1000;
                }
                HttpServer myposts = HttpServer.create(new InetSocketAddress(remport), 0);
                myposts.createContext("/", new MyHandler());
                myposts.createContext("/tps", new GetTps());
                myposts.createContext("/remchat", new RemChat());
                myposts.createContext("/rempacket", new RemPacket());
                myposts.createContext("/rempacketin", new RemPacketin());
                myposts.createContext("/pos", new SetPos());
                myposts.createContext("/getply", new GetPly());
                myposts.setExecutor(null);
                myposts.start();
                System.out.println("===================================");
                int revport = myposts.getAddress().getPort();
                System.out.printf("==ReCon: %s==\n", revport);
                System.out.println("===================================");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int ensureRange(int value, int min, int max) {
        int toReturn = Math.min(Math.max(value, min), max);
        return toReturn;
    }

    public static String getName(PlayerListEntry entry) {
        return entry.getDisplayName() == null ? entry.getProfile().getName() : entry.getDisplayName().getFullText();
    }

    public void start(String[] args) {
        bot = this;
        try {
            block17 : {
                if (args.length != 1 || !args[0].equals("firstart")) {
                    if (Config.doAuth) {
                        try {
                            File sessionIdCache = new File(System.getProperty("user.dir") + File.separator + "sessionId.txt");
                            if (sessionIdCache.exists()) {
                                System.out.println("Attempting to log in with session ID");
                                Scanner s = new Scanner(sessionIdCache);
                                String sessionID = s.nextLine().trim();
                                s.close();
                                System.out.println("Session ID: " + sessionID);
                                try {
                                    this.protocol = new MinecraftProtocol(Config.username);
                                    AuthenticationService auth = new AuthenticationService(Config.clientId, Proxy.NO_PROXY);
                                    auth.setUsername(Config.username);
                                    auth.setAccessToken(sessionID);
                                    auth.login();
                                    this.protocol.profile = auth.getSelectedProfile();
                                    this.protocol.accessToken = auth.getAccessToken();
                                    System.out.println("Done! Account name: " + this.protocol.getProfile().getName() + ", session ID:" + this.protocol.getAccessToken());
                                    this.isAuth = true;
                                }
                                catch (RequestException e) {
                                    System.out.println("Bad/expired session ID, attempting login with username and password");
                                    this.protocol = new MinecraftProtocol(Config.username);
                                    AuthenticationService auth = new AuthenticationService(Config.clientId, Proxy.NO_PROXY);
                                    auth.setUsername(Config.username);
                                    auth.setPassword(Config.password);
                                    auth.login();
                                    this.protocol.profile = auth.getSelectedProfile();
                                    this.protocol.accessToken = auth.getAccessToken();
                                    System.out.println("Logged in with credentials " + Config.username + ":" + Config.password);
                                    System.out.println("Saving session ID: " + this.protocol.getAccessToken() + " to disk");
                                    PrintWriter writer = new PrintWriter("sessionId.txt", "UTF-8");
                                    writer.println(this.protocol.getAccessToken());
                                    writer.close();
                                    this.isAuth = true;
                                }
                                break block17;
                            }
                            System.out.println("Attempting login with username and password...");
                            this.protocol = new MinecraftProtocol(Config.username, Config.password);
                            System.out.println("Logged in with credentials " + Config.username + ":" + Config.password);
                            System.out.println("Saving session ID: " + this.protocol.getAccessToken() + " to disk");
                            PrintWriter writer = new PrintWriter("sessionId.txt", "UTF-8");
                            writer.println(this.protocol.getAccessToken());
                            writer.close();
                            this.isAuth = true;
                        }
                        catch (InvalidCredentialsException e) {
                            System.out.println("Being rate limited, waiting...");
                            Thread.sleep(1000L);
                            System.out.println("Relaunching, wait 10 minutes");
                            GuiBot.respawnbut.setEnabled(true);
                            GuiBot.connect_disconnectButton.setEnabled(false);
                            for (int i = 600; i > 0; --i) {
                                try {
                                    GuiBot.connect_disconnectButton.setText("Reconnecting (" + i + ")");
                                    Thread.sleep(1000L);
                                    continue;
                                }
                                catch (InterruptedException ddd) {
                                    ddd.printStackTrace();
                                }
                            }
                            bot.start(new String[]{"firstart"});
                        }
                    } else {
                        System.out.println("Logging in with cracked account, username: " + Config.username);
                        this.protocol = new MinecraftProtocol(Config.username);
                        this.isLoggedIn = true;
                    }
                    System.out.println("Success!");
                    System.out.println(this.protocol.getProfile().getIdAsString());
                }
            }
            if (this.isAuth) {
                if (Config.doServer) {
                    System.out.println("Getting server icon...");
                    String picid = "3f974443-5288-45d1-8b86-088b842e1322";
                    if (this.protocol.profile.getId() != null) {
                        picid = this.protocol.profile.getId().toString();
                    }
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(HTTPUtils.downloadImage("https://crafatar.com/avatars/" + picid + "?size=64&overlay&default=MHF_Steve"));
                    Caches.icon = ImageIO.read(inputStream);
                    System.out.println("Done!");
                }
                if (Config.doGUI && GuiBot.INSTANCE == null) {
                    GuiBot guiBot = new GuiBot();
                    guiBot.createBufferStrategy(1);
                    guiBot.setVisible(true);
                    return;
                }
                this.client = new Client(Config.ip, Config.port, this.protocol, new TcpSessionFactory());
                this.client.getSession().addListener(new PorkSessionListener(this));
                System.out.println("Connecting to " + Config.ip + ":" + Config.port + "...");
                this.client.getSession().connect(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Runtime.getRuntime().halt(0);
        }
    }

    public void sendChat(String message) {
        this.queueMessage(message);
    }

    public void queueMessage(String toQueue) {
        this.queuedIngameMessages.add(toQueue);
    }

    public void reLaunch() {
        for (int i = (int)(Math.random() * 12.0 + 3.0); i > 0; --i) {
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (GuiBot.INSTANCE != null) {
                GuiBot.INSTANCE.chatDisplay.setText(GuiBot.INSTANCE.chatDisplay.getText().substring(0, GuiBot.INSTANCE.chatDisplay.getText().length() - 7) + "<br>Reconnecting in " + i * 10 + "sec </html>");
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
            System.out.println("Reconnecting in " + i);
        }
        this.cleanUp();
        this.timer.schedule(new TimerTask(){

            @Override
            public void run() {
                System.out.println("Launching everything again!");
                TooBeeTooTeeBot.bot.start(new String[0]);
            }
        }, 1000L);
    }

    public void cleanUp() {
        System.out.println("Resetting EVERYTHING...");
        this.timer.cancel();
        System.out.println("Cancelled timer");
        this.timer.purge();
        System.out.println("Purged timer");
        this.timer = new Timer();
        System.out.println("Reset timer");
        System.out.println("Reset queued messages");
        this.tabHeader = new TextMessage("");
        this.tabFooter = new TextMessage("");
        if (this.websocketServer != null) {
            for (PlayerListEntry entry : this.playerListEntries) {
                this.websocketServer.sendToAll("tabDel  " + TooBeeTooTeeBot.getName(entry));
            }
        }
        System.out.println("Reset tab header and footer");
        this.playerListEntries.clear();
        System.out.println("Reset player list");
        this.isLoggedIn = false;
        Caches.x = 0.0;
        Caches.y = 0.0;
        Caches.z = 0.0;
        Caches.yaw = 0.0f;
        Caches.pitch = 0.0f;
        Caches.onGround = true;
        System.out.println("Reset position");
        Caches.cachedChunks.clear();
        System.out.println("Reset cached chunks");
        this.queuedIngameMessages.clear();
        System.out.println("Reset queued ingame messages");
        this.ingamePlayerCooldown.clear();
        System.out.println("Reset ingame cooldown timer");
        this.hasDonePostConnect = false;
        System.out.println("Clearing other caches");
        Caches.cachedEntities.clear();
        Caches.cachedBossBars.clear();
        Caches.player.potionEffects.clear();
        System.out.println("Reset complete!");
    }

    static class RemPacketin
    implements HttpHandler {
        RemPacketin() {
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            byte[] responseb = new Gson().toJson(Caches.remPacketin).getBytes();
            Caches.remPacketin.clear();
            if (Caches.remPacketStatein == 0L) {
                Caches.remPacketStatein = System.currentTimeMillis();
            }
            t.sendResponseHeaders(200, responseb.length);
            OutputStream os = t.getResponseBody();
            os.write(responseb);
            os.close();
        }
    }

    static class RemPacket
    implements HttpHandler {
        RemPacket() {
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            byte[] responseb = new Gson().toJson(Caches.remPacket).getBytes();
            Caches.remPacket.clear();
            if (Caches.remPacketState == 0L) {
                Caches.remPacketState = System.currentTimeMillis();
            }
            t.sendResponseHeaders(200, responseb.length);
            OutputStream os = t.getResponseBody();
            os.write(responseb);
            os.close();
        }
    }

    static class RemChat
    implements HttpHandler {
        RemChat() {
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            byte[] responseb = new Gson().toJson(Caches.remChat).getBytes();
            Caches.remChat.clear();
            Caches.remChatState = true;
            t.sendResponseHeaders(200, responseb.length);
            OutputStream os = t.getResponseBody();
            os.write(responseb);
            os.close();
        }
    }

    static class GetPly
    implements HttpHandler {
        GetPly() {
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            ClientTabCompletePacket tabcmppack = new ClientTabCompletePacket("/msg ", false);
            TooBeeTooTeeBot.bot.client.getSession().send(tabcmppack);
            byte[] responseb = new Gson().toJson(Caches.playerlist).getBytes();
            t.sendResponseHeaders(200, responseb.length);
            OutputStream os = t.getResponseBody();
            os.write(responseb);
            os.close();
            Caches.playerlist = null;
        }
    }

    static class GetTps
    implements HttpHandler {
        GetTps() {
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = Caches.tps.equals("") ? "{\"tps\": False}" : "{\"tps\": " + Caches.tps + "}";
            byte[] responseb = response.getBytes();
            t.sendResponseHeaders(200, responseb.length);
            OutputStream os = t.getResponseBody();
            os.write(responseb);
            os.close();
        }
    }

    static class SetPos
    implements HttpHandler {
        SetPos() {
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "response ok";
            t.sendResponseHeaders(200, response.length());
            InputStream inputStream = t.getRequestBody();
            String ans = TooBeeTooTeeBot.getStringFromInputStream(inputStream);
            String[] text = ans.split("=");
            String result = URLDecoder.decode(text[1], "UTF-8");
            String[] mylinesd = result.split(",");
            System.out.println(mylinesd[0]);
            switch (mylinesd[0]) {
                case "ClientPlayerPositionPacket": {
                    TooBeeTooTeeBot.bot.client.getSession().send(new ClientPlayerPositionPacket(mylinesd[1].equalsIgnoreCase("true"), Double.parseDouble(mylinesd[2]), Double.parseDouble(mylinesd[3]), Double.parseDouble(mylinesd[4])));
                    break;
                }
                case "ClientPlayerRotationPacket": {
                    TooBeeTooTeeBot.bot.client.getSession().send(new ClientPlayerRotationPacket(mylinesd[1].equalsIgnoreCase("true"), Float.parseFloat(mylinesd[2]), Float.parseFloat(mylinesd[3])));
                    break;
                }
                case "ClientPlayerPositionRotationPacket": {
                    TooBeeTooTeeBot.bot.client.getSession().send(new ClientPlayerPositionRotationPacket(mylinesd[1].equalsIgnoreCase("true"), Double.parseDouble(mylinesd[2]), Double.parseDouble(mylinesd[3]), Double.parseDouble(mylinesd[4]), Float.parseFloat(mylinesd[5]), Float.parseFloat(mylinesd[6])));
                    break;
                }
                case "ClientPlayerPlaceBlockPacket": {
                    Position mypos = new Position(Integer.parseInt(mylinesd[1]), Integer.parseInt(mylinesd[2]), Integer.parseInt(mylinesd[3]));
                    BlockFace newbf = null;
                    switch (mylinesd[4]) {
                        case "DOWN": {
                            newbf = BlockFace.DOWN;
                            break;
                        }
                        case "UP": {
                            newbf = BlockFace.UP;
                            break;
                        }
                        case "EAST": {
                            newbf = BlockFace.EAST;
                            break;
                        }
                        case "NORTH": {
                            newbf = BlockFace.NORTH;
                            break;
                        }
                        case "SOUTH": {
                            newbf = BlockFace.SOUTH;
                            break;
                        }
                        case "WEST": {
                            newbf = BlockFace.WEST;
                        }
                    }
                    ClientPlayerPlaceBlockPacket pot = new ClientPlayerPlaceBlockPacket(mypos, newbf, Hand.MAIN_HAND, Float.parseFloat(mylinesd[6]), Float.parseFloat(mylinesd[7]), Float.parseFloat(mylinesd[8]));
                    TooBeeTooTeeBot.bot.client.getSession().send(pot);
                    break;
                }
                case "ClientPlayerActionPacket": {
                    Position mypos1 = new Position(Integer.parseInt(mylinesd[2]), Integer.parseInt(mylinesd[3]), Integer.parseInt(mylinesd[4]));
                    PlayerAction action = null;
                    switch (mylinesd[1]) {
                        case "CANCEL_DIGGING": {
                            action = PlayerAction.CANCEL_DIGGING;
                            break;
                        }
                        case "DROP_ITEM": {
                            action = PlayerAction.DROP_ITEM;
                            break;
                        }
                        case "DROP_ITEM_STACK": {
                            action = PlayerAction.DROP_ITEM_STACK;
                            break;
                        }
                        case "FINISH_DIGGING": {
                            action = PlayerAction.FINISH_DIGGING;
                            break;
                        }
                        case "START_DIGGING": {
                            action = PlayerAction.START_DIGGING;
                            break;
                        }
                        case "SWAP_HANDS": {
                            action = PlayerAction.SWAP_HANDS;
                        }
                    }
                    BlockFace newbf1 = null;
                    switch (mylinesd[5]) {
                        case "DOWN": {
                            newbf1 = BlockFace.DOWN;
                            break;
                        }
                        case "UP": {
                            newbf1 = BlockFace.UP;
                            break;
                        }
                        case "EAST": {
                            newbf1 = BlockFace.EAST;
                            break;
                        }
                        case "NORTH": {
                            newbf1 = BlockFace.NORTH;
                            break;
                        }
                        case "SOUTH": {
                            newbf1 = BlockFace.SOUTH;
                            break;
                        }
                        case "WEST": {
                            newbf1 = BlockFace.WEST;
                        }
                    }
                    TooBeeTooTeeBot.bot.client.getSession().send(new ClientPlayerActionPacket(action, mypos1, newbf1));
                    break;
                }
                case "ClientCloseWindowPacket": {
                    TooBeeTooTeeBot.bot.client.getSession().send(new ClientCloseWindowPacket(Integer.parseInt(mylinesd[1])));
                    break;
                }
                case "ClientWindowActionPacket": {
                    WindowAction wanew = null;
                    switch (mylinesd[2]) {
                        case "CLICK_ITEM": {
                            wanew = WindowAction.CLICK_ITEM;
                            break;
                        }
                        case "SHIFT_CLICK_ITEM": {
                            wanew = WindowAction.SHIFT_CLICK_ITEM;
                        }
                    }
                    ClientWindowActionPacket pck1 = new ClientWindowActionPacket(0, 1, Integer.parseInt(mylinesd[1]), null, wanew, ShiftClickItemParam.LEFT_CLICK);
                    System.out.println(pck1.toString());
                    TooBeeTooTeeBot.bot.client.getSession().send(pck1);
                    break;
                }
                case "ClientPlayerStatePacket": {
                    PlayerState stanew = null;
                    switch (mylinesd[2]) {
                        case "LEAVE_BED": {
                            stanew = PlayerState.LEAVE_BED;
                            break;
                        }
                        case "OPEN_HORSE_INVENTORY": {
                            stanew = PlayerState.OPEN_HORSE_INVENTORY;
                            break;
                        }
                        case "START_ELYTRA_FLYING": {
                            stanew = PlayerState.START_ELYTRA_FLYING;
                            break;
                        }
                        case "START_HORSE_JUMP": {
                            stanew = PlayerState.START_HORSE_JUMP;
                            break;
                        }
                        case "START_SNEAKING": {
                            stanew = PlayerState.START_SNEAKING;
                            break;
                        }
                        case "STOP_HORSE_JUMP": {
                            stanew = PlayerState.STOP_HORSE_JUMP;
                            break;
                        }
                        case "STOP_SNEAKING": {
                            stanew = PlayerState.STOP_SNEAKING;
                            break;
                        }
                        case "STOP_SPRINTING": {
                            stanew = PlayerState.STOP_SPRINTING;
                            break;
                        }
                        case "START_SPRINTING": {
                            stanew = PlayerState.START_SPRINTING;
                        }
                    }
                    ClientPlayerStatePacket pck2 = new ClientPlayerStatePacket(Integer.parseInt(mylinesd[1]), stanew);
                    System.out.println(pck2.toString());
                    TooBeeTooTeeBot.bot.client.getSession().send(pck2);
                    break;
                }
                case "ClientSettingsPacket": {
                    ArrayList<SkinPart> partslist = new ArrayList<SkinPart>();
                    if (mylinesd[5].contains("CAPE")) {
                        partslist.add(SkinPart.CAPE);
                    }
                    if (mylinesd[5].contains("JACKET")) {
                        partslist.add(SkinPart.JACKET);
                    }
                    if (mylinesd[5].contains("LEFT_SLEEVE")) {
                        partslist.add(SkinPart.LEFT_SLEEVE);
                    }
                    if (mylinesd[5].contains("RIGHT_SLEEVE")) {
                        partslist.add(SkinPart.RIGHT_SLEEVE);
                    }
                    if (mylinesd[5].contains("LEFT_PANTS_LEG")) {
                        partslist.add(SkinPart.LEFT_PANTS_LEG);
                    }
                    if (mylinesd[5].contains("RIGHT_PANTS_LEG")) {
                        partslist.add(SkinPart.RIGHT_PANTS_LEG);
                    }
                    if (mylinesd[5].contains("HAT")) {
                        partslist.add(SkinPart.HAT);
                    }
                    Hand usehand = null;
                    switch (mylinesd[6]) {
                        case "MAIN_HAND": {
                            usehand = Hand.MAIN_HAND;
                            break;
                        }
                        case "OFF_HAND": {
                            usehand = Hand.OFF_HAND;
                        }
                    }
                    SkinPart[] partsarr = partslist.toArray((T[])new SkinPart[partslist.size()]);
                    ClientSettingsPacket pck = new ClientSettingsPacket(mylinesd[1], Integer.parseInt(mylinesd[2]), ChatVisibility.FULL, true, partsarr, usehand);
                    TooBeeTooTeeBot.bot.client.getSession().send(pck);
                    break;
                }
            }
            t.close();
        }
    }

    static class MyHandler
    implements HttpHandler {
        MyHandler() {
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "response ok";
            t.sendResponseHeaders(200, response.length());
            InputStream inputStream = t.getRequestBody();
            String ans = TooBeeTooTeeBot.getStringFromInputStream(inputStream);
            String[] text = ans.split("=");
            String result = URLDecoder.decode(text[1], "UTF-8");
            System.out.println(result);
            if (result.contains("~n~")) {
                Caches.signtext = result;
            } else {
                TooBeeTooTeeBot.bot.sendChat(result);
            }
            t.close();
        }
    }

}

