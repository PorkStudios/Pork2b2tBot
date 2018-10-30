/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.client.player;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;

public class ClientPlayerRotationPacket
extends ClientPlayerMovementPacket {
    public ClientPlayerRotationPacket() {
        this.rot = true;
    }

    public ClientPlayerRotationPacket(boolean onGround, float yaw, float pitch) {
        super(onGround);
        this.rot = true;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}

