/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

public interface WebSocketListener {
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket var1, Draft var2, ClientHandshake var3) throws InvalidDataException;

    public void onWebsocketHandshakeReceivedAsClient(WebSocket var1, ClientHandshake var2, ServerHandshake var3) throws InvalidDataException;

    public void onWebsocketHandshakeSentAsClient(WebSocket var1, ClientHandshake var2) throws InvalidDataException;

    public void onWebsocketMessage(WebSocket var1, String var2);

    public void onWebsocketMessage(WebSocket var1, ByteBuffer var2);

    @Deprecated
    public void onWebsocketMessageFragment(WebSocket var1, Framedata var2);

    public void onWebsocketOpen(WebSocket var1, Handshakedata var2);

    public void onWebsocketClose(WebSocket var1, int var2, String var3, boolean var4);

    public void onWebsocketClosing(WebSocket var1, int var2, String var3, boolean var4);

    public void onWebsocketCloseInitiated(WebSocket var1, int var2, String var3);

    public void onWebsocketError(WebSocket var1, Exception var2);

    public void onWebsocketPing(WebSocket var1, Framedata var2);

    public void onWebsocketPong(WebSocket var1, Framedata var2);

    public void onWriteDemand(WebSocket var1);

    public InetSocketAddress getLocalSocketAddress(WebSocket var1);

    public InetSocketAddress getRemoteSocketAddress(WebSocket var1);
}

