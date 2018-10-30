/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.gui;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.client.impl.ListenerPlayerHealthPacket;
import net.daporkchop.toobeetooteebot.util.Config;
import net.daporkchop.toobeetooteebot.util.YMLParser;

public class GuiBot
extends JFrame {
    public static GuiBot INSTANCE = null;
    public static Timer guiTimer = new Timer();
    public JPanel contentPane;
    public JTextField chatInput;
    public JTextField usernameIn;
    public JPasswordField passwordIn;
    public JTextField targetIpIn;
    public JTextField bindHostIn;
    public JCheckBox doAuthenticationIn;
    public JSpinner targetPortIn;
    public JCheckBox doAntiAfkIn;
    public JCheckBox doAutoRespawnIn;
    public JCheckBox doAutoRelogIn;
    public JCheckBox doServerIn;
    public JSpinner bindPortIn;
    public JCheckBox statCollectionIn;
    public JCheckBox processChatIn;
    public JLabel usernameLabel;
    public JLabel chatDisplay;
    public static JButton connect_disconnectButton;
    public static JButton respawnbut;
    public static String verion;
    public String titletext = "maxbounce " + verion + " .-.";

    public void updateTitle() {
        String tps = "";
        if (!Caches.tps.equals("")) {
            tps = " | tps: " + Caches.tps;
        }
        String output = String.format("%s | HP: %.0f | XYZ: %.1f, %.1f, %.1f | Food: %.0f%s", TooBeeTooTeeBot.bot.protocol.getProfile().getName(), Float.valueOf(ListenerPlayerHealthPacket.myhealth), Caches.x, Caches.y, Caches.z, Float.valueOf(ListenerPlayerHealthPacket.myfood), tps);
        this.usernameLabel.setText(output);
    }

    public GuiBot() {
        INSTANCE = this;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
            Runtime.getRuntime().exit(0);
        }
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(GuiBot.class.getResource("/DaPorkchop_.png")));
        this.setTitle(this.titletext);
        this.setDefaultCloseOperation(3);
        this.setBounds(100, 100, 500, 550);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.contentPane);
        this.usernameLabel = new JLabel(TooBeeTooTeeBot.bot.protocol.getProfile().getName());
        JTabbedPane tabbedPane = new JTabbedPane(1);
        GroupLayout gl_contentPane = new GroupLayout(this.contentPane);
        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.usernameLabel, -1, 324, 32767).addComponent(tabbedPane, -1, 324, 32767));
        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addComponent(this.usernameLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(tabbedPane, -1, 431, 32767)));
        JPanel tabMain = new JPanel();
        tabbedPane.addTab("Main", null, tabMain, null);
        respawnbut = new JButton("Respawn");
        respawnbut.setEnabled(false);
        respawnbut.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (TooBeeTooTeeBot.bot.client != null) {
                    TooBeeTooTeeBot.bot.client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                    GuiBot.respawnbut.setEnabled(false);
                    GuiBot.guiTimer.schedule(new TimerTask(){

                        @Override
                        public void run() {
                            GuiBot.respawnbut.setEnabled(true);
                        }
                    }, 1000L);
                }
            }

        });
        connect_disconnectButton = new JButton("Connect");
        connect_disconnectButton.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (TooBeeTooTeeBot.bot.client == null) {
                    GuiBot.connect_disconnectButton.setText("Disconnect");
                    GuiBot.respawnbut.setEnabled(true);
                    GuiBot.connect_disconnectButton.setEnabled(false);
                    GuiBot.guiTimer.schedule(new TimerTask(){

                        @Override
                        public void run() {
                            TooBeeTooTeeBot.bot.start(new String[]{"firstart"});
                        }
                    }, 0L);
                } else if (TooBeeTooTeeBot.bot.client.getSession().isConnected()) {
                    GuiBot.connect_disconnectButton.setText("Connect");
                    GuiBot.connect_disconnectButton.setEnabled(false);
                    GuiBot.guiTimer.schedule(new TimerTask(){

                        @Override
                        public void run() {
                            TooBeeTooTeeBot.bot.client.getSession().disconnect("User disconnected");
                        }
                    }, 0L);
                } else {
                    GuiBot.connect_disconnectButton.setText("Disconnect");
                    GuiBot.connect_disconnectButton.setEnabled(false);
                    GuiBot.respawnbut.setEnabled(true);
                    GuiBot.guiTimer.schedule(new TimerTask(){

                        @Override
                        public void run() {
                            TooBeeTooTeeBot.bot.start(new String[0]);
                        }
                    }, 0L);
                }
            }

        });
        JButton sendButton = new JButton("Send");
        this.chatInput = new JTextField();
        this.chatInput.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (TooBeeTooTeeBot.bot.client != null && TooBeeTooTeeBot.bot.client.getSession().isConnected()) {
                    String inputtextline = GuiBot.this.chatInput.getText();
                    String msg = "";
                    System.out.println(inputtextline);
                    if (inputtextline.startsWith("$$changeuser ")) {
                        String[] signtext = inputtextline.replace("$$changeuser ", "").split(" ");
                        Caches.playerChange[0] = signtext[0];
                        Caches.playerChange[1] = signtext[1];
                    } else if (inputtextline.startsWith("$$autofish")) {
                        Caches.autofish = Caches.autofish == false;
                        msg = "-> autofish is: " + Caches.autofish;
                        Caches.bobberownerid = "";
                        Caches.bobberid = 0;
                    } else if (inputtextline.startsWith("$$ecaccess")) {
                        Caches.ecAccess = Caches.ecAccess == false;
                        msg = "-> enderchest access is: " + Caches.ecAccess;
                    } else {
                        TooBeeTooTeeBot.bot.queueMessage(GuiBot.this.chatInput.getText());
                    }
                    if (!msg.equals("")) {
                        String giudate = GuiBot.INSTANCE.chatDisplay.getText().replace("<html>", "").replace("</html>", "");
                        String[] split = giudate.split("<br>");
                        int maxlinestart = 0;
                        if (split.length > 30) {
                            maxlinestart = 3;
                        }
                        giudate = "";
                        for (int i = maxlinestart; i < split.length; ++i) {
                            giudate = giudate + split[i] + "<br>";
                        }
                        GuiBot.INSTANCE.chatDisplay.setText("<html>" + giudate + msg + "</html>");
                    }
                    GuiBot.this.chatInput.setText("");
                }
            }
        });
        this.chatInput.setColumns(10);
        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_tabMain = new GroupLayout(tabMain);
        gl_tabMain.setHorizontalGroup(gl_tabMain.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(respawnbut, -1, 319, 32767).addComponent(connect_disconnectButton, -1, 319, 32767).addGroup(gl_tabMain.createSequentialGroup().addComponent(this.chatInput, -1, 256, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(sendButton)).addComponent(scrollPane, GroupLayout.Alignment.LEADING, -1, 319, 32767));
        gl_tabMain.setVerticalGroup(gl_tabMain.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(gl_tabMain.createSequentialGroup().addComponent(connect_disconnectButton).addComponent(respawnbut).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(scrollPane, -1, 345, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(gl_tabMain.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(sendButton).addComponent(this.chatInput, -2, -1, -2))));
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
        this.chatDisplay = new JLabel("<html>maxtorcd55 edited proxy</html>");
        this.chatDisplay.setVerticalAlignment(1);
        this.chatDisplay.setHorizontalAlignment(2);
        scrollPane.setViewportView(this.chatDisplay);
        tabMain.setLayout(gl_tabMain);
        this.chatDisplay.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JPanel tabConfig = new JPanel();
        tabbedPane.addTab("Config", null, tabConfig, null);
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setHorizontalScrollBarPolicy(31);
        scrollPane_1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JButton btnSave = new JButton("Save");
        btnSave.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent arg0) {
                Config.username = GuiBot.this.usernameIn.getText();
                Config.parser.set("login.username", Config.username);
                Config.password = GuiBot.this.passwordIn.getText();
                Config.parser.set("login.password", Config.password);
                Config.doAuth = GuiBot.this.doAuthenticationIn.isSelected();
                Config.parser.set("login.doAuthentication", Config.doAuth);
                Config.serverHost = GuiBot.this.bindHostIn.getText();
                Config.parser.set("server.host", Config.serverHost);
                Config.serverPort = (Integer)GuiBot.this.bindPortIn.getValue();
                Config.parser.set("server.port", Config.serverPort);
                Config.doServer = GuiBot.this.doServerIn.isSelected();
                Config.parser.set("server.doServer", Config.doServer);
                Config.doAntiAFK = GuiBot.this.doAntiAfkIn.isSelected();
                Config.parser.set("misc.antiafk", Config.doAntiAFK);
                Config.ip = GuiBot.this.targetIpIn.getText();
                Config.parser.set("client.hostIP", Config.ip);
                Config.port = (Integer)GuiBot.this.targetPortIn.getValue();
                Config.parser.set("client.hostPort", Config.port);
                Config.doAntiAFK = GuiBot.this.doAntiAfkIn.isSelected();
                Config.parser.set("misc.autorespawn", Config.doAntiAFK);
                Config.doAutoRelog = GuiBot.this.doAutoRelogIn.isSelected();
                Config.parser.set("misc.doAutoRelog", Config.doAutoRelog);
                Config.doStatCollection = GuiBot.this.statCollectionIn.isSelected();
                Config.parser.set("stats.doStats", Config.doStatCollection);
                Config.processChat = GuiBot.this.statCollectionIn.isSelected();
                Config.parser.set("chat.doProcess", Config.processChat);
                Config.parser.save();
            }
        });
        GroupLayout gl_tabConfig = new GroupLayout(tabConfig);
        gl_tabConfig.setHorizontalGroup(gl_tabConfig.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(btnSave, -1, 319, 32767).addComponent(scrollPane_1, GroupLayout.Alignment.TRAILING, -1, 319, 32767));
        gl_tabConfig.setVerticalGroup(gl_tabConfig.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, gl_tabConfig.createSequentialGroup().addComponent(scrollPane_1, -1, 374, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(btnSave)));
        JPanel panel = new JPanel();
        scrollPane_1.setViewportView(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JPanel panel1 = new JPanel();
        panel1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JSplitPane splitPane = new JSplitPane(1, panel1, panel2);
        splitPane.setEnabled(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        splitPane.setDividerSize(0);
        panel2.setLayout(new GridLayout(13, 1, 0, 0));
        JLabel labelUsername = new JLabel("Username");
        labelUsername.setToolTipText("<html>Your ingame username.<br>If you're using authentication, this needs to be the same as you would enter into the launcher.<br>If not, we'll generate a cracked account using the username only.</html>");
        panel2.add(labelUsername);
        JLabel labelPassword = new JLabel("Password");
        labelPassword.setToolTipText("<html>The password used for authenticating.<br>Only needed if authentication is enabled.</html>");
        panel2.add(labelPassword);
        JLabel labelDoAuthentication = new JLabel("Do Authentication");
        labelDoAuthentication.setToolTipText("<html>If this is enabled, then we will attempt to authenticate with Mojang using the username and password.<br>If not, we will log in using a cracked account based on the username.</html>");
        panel2.add(labelDoAuthentication);
        JLabel labelTargetIp = new JLabel("Target IP");
        labelTargetIp.setToolTipText("<html>The IP of the server to connect to.<br>If the IP is in the format of example.com:number, do <strong>NOT</strong> add the :number here!<br>Remove the : and put the number in the target port section.</html>");
        panel2.add(labelTargetIp);
        JLabel lblTargetPort = new JLabel("Target Port");
        lblTargetPort.setToolTipText("<html>The port of the server to connect to.<br>Generally can be left as is.</html>");
        panel2.add(lblTargetPort);
        JLabel lblDoAntiafk = new JLabel("Do AntiAFK");
        lblDoAntiafk.setToolTipText("<html>Rotates the player randomly to prevent getting kicked for being AFK.<br>If the server is active as well as this, AntiAFK will only run when no clients are connected.</html>");
        panel2.add(lblDoAntiafk);
        JLabel lblDoAutorespawn = new JLabel("Do AutoRespawn");
        lblDoAutorespawn.setToolTipText("<html>If this is enabled, the bot will automatically respawn after death.<br>If not, it will just sit there and do nothing, there's really no reason to disable it lol</html>");
        panel2.add(lblDoAutorespawn);
        JLabel lblDoAutorelog = new JLabel("Do AutoRelog");
        lblDoAutorelog.setToolTipText("<html>If this is enabled, the bot will try to reconnect after getting disconnected/kicked.<br>There's a 10 second wait before reconnects.</html>");
        panel2.add(lblDoAutorelog);
        JLabel lblDoServer = new JLabel("Do Server");
        lblDoServer.setToolTipText("<html>If this is enabled, a server will be opened inside the bot that you can connect to.<br>This allows you to move the bot around and such without having to shut it down.<br>Essentially just PorkProxy lawl</html>");
        panel2.add(lblDoServer);
        JLabel lblBindHost = new JLabel("Bind Host");
        lblBindHost.setToolTipText("<html>The host to bind the server to.<br>By default this is 0.0.0.0 (all requests will be accepted)</html>");
        panel2.add(lblBindHost);
        JLabel lblBindPort = new JLabel("Bind Port");
        lblBindPort.setToolTipText("<html>The port to bind to.</html>");
        panel2.add(lblBindPort);
        JLabel lblDoStatCollection = new JLabel("Do Stat Collection");
        lblDoStatCollection.setToolTipText("<html>Whether or not to collect statistics about users.<br>Experimental, don't use this lol</html>");
        panel2.add(lblDoStatCollection);
        JLabel lblProcessChat = new JLabel("Process Chat");
        lblProcessChat.setToolTipText("<html>Not going to bother documenting this, it's pretty advanced. Read the code.</html>");
        panel2.add(lblProcessChat);
        panel1.setLayout(new GridLayout(13, 1, 0, 0));
        this.usernameIn = new JTextField();
        panel1.add(this.usernameIn);
        this.usernameIn.setColumns(10);
        this.usernameIn.setText(Config.username);
        this.passwordIn = new JPasswordField();
        panel1.add(this.passwordIn);
        this.passwordIn.setText(Config.password);
        this.doAuthenticationIn = new JCheckBox("");
        this.doAuthenticationIn.setHorizontalAlignment(0);
        this.doAuthenticationIn.setSelected(Config.doAuth);
        panel1.add(this.doAuthenticationIn);
        this.targetIpIn = new JTextField();
        panel1.add(this.targetIpIn);
        this.targetIpIn.setColumns(10);
        this.targetIpIn.setText(Config.ip);
        this.targetPortIn = new JSpinner();
        this.targetPortIn.setModel(new SpinnerNumberModel(25565, 1, 65535, 1));
        this.targetPortIn.setEditor(new JSpinner.NumberEditor(this.targetPortIn, "#"));
        this.targetPortIn.setValue(Config.port);
        panel1.add(this.targetPortIn);
        this.doAntiAfkIn = new JCheckBox("");
        this.doAntiAfkIn.setHorizontalAlignment(0);
        this.doAntiAfkIn.setSelected(Config.doAntiAFK);
        panel1.add(this.doAntiAfkIn);
        this.doAutoRespawnIn = new JCheckBox("");
        this.doAutoRespawnIn.setHorizontalAlignment(0);
        this.doAutoRespawnIn.setSelected(Config.doAutoRespawn);
        panel1.add(this.doAutoRespawnIn);
        this.doAutoRelogIn = new JCheckBox("");
        this.doAutoRelogIn.setHorizontalAlignment(0);
        this.doAutoRelogIn.setSelected(Config.doAutoRelog);
        panel1.add(this.doAutoRelogIn);
        this.doServerIn = new JCheckBox("");
        this.doServerIn.setHorizontalAlignment(0);
        this.doServerIn.setSelected(Config.doServer);
        panel1.add(this.doServerIn);
        this.bindHostIn = new JTextField();
        panel1.add(this.bindHostIn);
        this.bindHostIn.setColumns(10);
        this.bindHostIn.setText(Config.serverHost);
        this.bindPortIn = new JSpinner();
        this.bindPortIn.setModel(new SpinnerNumberModel(10293, 1, 65535, 1));
        this.bindPortIn.setEditor(new JSpinner.NumberEditor(this.bindPortIn, "#"));
        this.bindPortIn.setValue(Config.serverPort);
        panel1.add(this.bindPortIn);
        this.statCollectionIn = new JCheckBox("");
        this.statCollectionIn.setHorizontalAlignment(0);
        this.statCollectionIn.setSelected(Config.doStatCollection);
        panel1.add(this.statCollectionIn);
        this.processChatIn = new JCheckBox("");
        this.processChatIn.setHorizontalAlignment(0);
        this.processChatIn.setSelected(Config.doStatCollection);
        panel1.add(this.processChatIn);
        splitPane.setResizeWeight(0.5);
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(splitPane, -1, 317, 32767));
        gl_panel.setVerticalGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(splitPane, -1, 401, 32767));
        panel.setLayout(gl_panel);
        tabConfig.setLayout(gl_tabConfig);
        JPanel tabAbout = new JPanel();
        tabbedPane.addTab("About", null, tabAbout, null);
        JLabel labelAbout = new JLabel("<html><h1>About</h1><br>Made by <strong>DaPorkchop_</strong><br>Powered by <strong>MCProtocolLib</strong></html>");
        JLabel lblsourceCode = new JLabel("<html><a href=\"https://github.com/DaMatrix/Pork2b2tBot\">Source Code</a></html>");
        lblsourceCode.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/DaMatrix/Pork2b2tBot"));
                }
                catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        GroupLayout gl_tabAbout = new GroupLayout(tabAbout);
        gl_tabAbout.setHorizontalGroup(gl_tabAbout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(gl_tabAbout.createSequentialGroup().addGroup(gl_tabAbout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(labelAbout).addComponent(lblsourceCode)).addContainerGap(197, 32767)));
        gl_tabAbout.setVerticalGroup(gl_tabAbout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(gl_tabAbout.createSequentialGroup().addComponent(labelAbout).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(lblsourceCode).addContainerGap(278, 32767)));
        tabAbout.setLayout(gl_tabAbout);
        sendButton.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (TooBeeTooTeeBot.bot.client != null && TooBeeTooTeeBot.bot.client.getSession().isConnected()) {
                    String inputtextline = GuiBot.this.chatInput.getText();
                    String msg = "";
                    System.out.println(inputtextline);
                    if (inputtextline.startsWith("$$changeuser ")) {
                        String[] signtext = inputtextline.replace("$$changeuser ", "").split(" ");
                        Caches.playerChange[0] = signtext[0];
                        Caches.playerChange[1] = signtext[1];
                    } else if (inputtextline.startsWith("$$autofish")) {
                        Caches.autofish = Caches.autofish == false;
                        msg = "-> autofish is: " + Caches.autofish;
                        Caches.bobberownerid = "";
                        Caches.bobberid = 0;
                    } else if (inputtextline.startsWith("$$ecaccess")) {
                        Caches.ecAccess = Caches.ecAccess == false;
                        msg = "-> enderchest access is: " + Caches.ecAccess;
                    } else {
                        TooBeeTooTeeBot.bot.queueMessage(GuiBot.this.chatInput.getText());
                    }
                    if (!msg.equals("")) {
                        String giudate = GuiBot.INSTANCE.chatDisplay.getText().replace("<html>", "").replace("</html>", "");
                        String[] split = giudate.split("<br>");
                        int maxlinestart = 0;
                        if (split.length > 30) {
                            maxlinestart = 3;
                        }
                        giudate = "";
                        for (int i = maxlinestart; i < split.length; ++i) {
                            giudate = giudate + split[i] + "<br>";
                        }
                        GuiBot.INSTANCE.chatDisplay.setText("<html>" + giudate + msg + "</html>");
                    }
                    GuiBot.this.chatInput.setText("");
                }
            }
        });
        this.contentPane.setLayout(gl_contentPane);
        guiTimer.schedule(new TimerTask(){

            @Override
            public void run() {
                if (TooBeeTooTeeBot.bot.client == null) {
                    GuiBot.connect_disconnectButton.setText("Connect");
                } else if (!TooBeeTooTeeBot.bot.client.getSession().isConnected()) {
                    GuiBot.connect_disconnectButton.setText("Connect");
                } else {
                    GuiBot.connect_disconnectButton.setText("Disconnect");
                }
            }
        }, 0L, 1200L);
    }

    static {
        verion = "v0.057P";
    }

}

