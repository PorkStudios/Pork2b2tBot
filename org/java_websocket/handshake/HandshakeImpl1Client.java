/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.handshake;

import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakedataImpl1;

public class HandshakeImpl1Client
extends HandshakedataImpl1
implements ClientHandshakeBuilder {
    private String resourceDescriptor = "*";

    public void setResourceDescriptor(String resourceDescriptor) throws IllegalArgumentException {
        if (resourceDescriptor == null) {
            throw new IllegalArgumentException("http resource descriptor must not be null");
        }
        this.resourceDescriptor = resourceDescriptor;
    }

    public String getResourceDescriptor() {
        return this.resourceDescriptor;
    }
}

