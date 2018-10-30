/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.server;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.SessionListener;
import java.util.ArrayList;
import java.util.HashMap;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;
import net.daporkchop.toobeetooteebot.server.PorkClient;
import net.daporkchop.toobeetooteebot.server.PorkSessionAdapter;

public class PorkServerAdapter
extends ServerAdapter {
    public TooBeeTooTeeBot bot;

    public PorkServerAdapter(TooBeeTooTeeBot tooBeeTooTeeBot) {
        this.bot = tooBeeTooTeeBot;
    }

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        if (this.bot.isLoggedIn) {
            PorkClient newClient = new PorkClient(event.getSession(), this.bot.clients.size());
            this.bot.clients.add(newClient);
            this.bot.sessionToClient.put(event.getSession(), newClient);
            event.getSession().addListener(new PorkSessionAdapter(newClient, this.bot));
        } else {
            event.getSession().disconnect("Not logged in yet, please wait a moment!");
        }
    }

    @Override
    public void sessionRemoved(SessionRemovedEvent event) {
        PorkClient toRemove = this.bot.sessionToClient.remove(event.getSession());
        this.bot.clients.remove(toRemove.arrayIndex);
        if (this.bot.clients.size() > 0) {
            for (int i = toRemove.arrayIndex; i < this.bot.clients.size(); ++i) {
                --this.bot.clients.get((int)i).arrayIndex;
            }
        }
    }
}

