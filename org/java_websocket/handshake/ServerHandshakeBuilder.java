/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.handshake;

import org.java_websocket.handshake.HandshakeBuilder;
import org.java_websocket.handshake.ServerHandshake;

public interface ServerHandshakeBuilder
extends HandshakeBuilder,
ServerHandshake {
    public void setHttpStatus(short var1);

    public void setHttpStatusMessage(String var1);
}

