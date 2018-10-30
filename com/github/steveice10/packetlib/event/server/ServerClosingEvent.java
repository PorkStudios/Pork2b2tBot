/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.packetlib.event.server;

import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.event.server.ServerEvent;
import com.github.steveice10.packetlib.event.server.ServerListener;

public class ServerClosingEvent
implements ServerEvent {
    private Server server;

    public ServerClosingEvent(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    @Override
    public void call(ServerListener listener) {
        listener.serverClosing(this);
    }
}

