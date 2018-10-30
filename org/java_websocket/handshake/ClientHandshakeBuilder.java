/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.handshake;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.HandshakeBuilder;

public interface ClientHandshakeBuilder
extends HandshakeBuilder,
ClientHandshake {
    public void setResourceDescriptor(String var1);
}

