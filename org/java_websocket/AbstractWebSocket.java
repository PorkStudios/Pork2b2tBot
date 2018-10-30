/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractWebSocket
extends WebSocketAdapter {
    private boolean tcpNoDelay;
    private boolean reuseAddr;
    private Timer connectionLostTimer;
    private TimerTask connectionLostTimerTask;
    private int connectionLostTimeout = 60;

    public int getConnectionLostTimeout() {
        return this.connectionLostTimeout;
    }

    public void setConnectionLostTimeout(int connectionLostTimeout) {
        this.connectionLostTimeout = connectionLostTimeout;
        if (this.connectionLostTimeout <= 0) {
            this.stopConnectionLostTimer();
        }
        if (this.connectionLostTimer != null || this.connectionLostTimerTask != null) {
            if (WebSocketImpl.DEBUG) {
                System.out.println("Connection lost timer restarted");
            }
            this.restartConnectionLostTimer();
        }
    }

    protected void stopConnectionLostTimer() {
        if (this.connectionLostTimer != null || this.connectionLostTimerTask != null) {
            if (WebSocketImpl.DEBUG) {
                System.out.println("Connection lost timer stopped");
            }
            this.cancelConnectionLostTimer();
        }
    }

    protected void startConnectionLostTimer() {
        if (this.connectionLostTimeout <= 0) {
            if (WebSocketImpl.DEBUG) {
                System.out.println("Connection lost timer deactivated");
            }
            return;
        }
        if (WebSocketImpl.DEBUG) {
            System.out.println("Connection lost timer started");
        }
        this.restartConnectionLostTimer();
    }

    private void restartConnectionLostTimer() {
        this.cancelConnectionLostTimer();
        this.connectionLostTimer = new Timer();
        this.connectionLostTimerTask = new TimerTask(){
            private ArrayList<WebSocket> connections = new ArrayList();

            public void run() {
                this.connections.clear();
                this.connections.addAll(AbstractWebSocket.this.connections());
                long current = System.currentTimeMillis() - (long)(AbstractWebSocket.this.connectionLostTimeout * 1500);
                for (WebSocket conn : this.connections) {
                    if (!(conn instanceof WebSocketImpl)) continue;
                    WebSocketImpl webSocketImpl = (WebSocketImpl)conn;
                    if (webSocketImpl.getLastPong() < current) {
                        if (WebSocketImpl.DEBUG) {
                            System.out.println("Closing connection due to no pong received: " + conn.toString());
                        }
                        webSocketImpl.closeConnection(1006, false);
                        continue;
                    }
                    if (webSocketImpl.isOpen()) {
                        webSocketImpl.sendPing();
                        continue;
                    }
                    if (!WebSocketImpl.DEBUG) continue;
                    System.out.println("Trying to ping a non open connection: " + conn.toString());
                }
                this.connections.clear();
            }
        };
        this.connectionLostTimer.scheduleAtFixedRate(this.connectionLostTimerTask, this.connectionLostTimeout * 1000, (long)(this.connectionLostTimeout * 1000));
    }

    protected abstract Collection<WebSocket> connections();

    private void cancelConnectionLostTimer() {
        if (this.connectionLostTimer != null) {
            this.connectionLostTimer.cancel();
            this.connectionLostTimer = null;
        }
        if (this.connectionLostTimerTask != null) {
            this.connectionLostTimerTask.cancel();
            this.connectionLostTimerTask = null;
        }
    }

    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isReuseAddr() {
        return this.reuseAddr;
    }

    public void setReuseAddr(boolean reuseAddr) {
        this.reuseAddr = reuseAddr;
    }

}

