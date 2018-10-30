/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.client.player;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;

public class ClientPlayerPositionPacket
extends ClientPlayerMovementPacket {
    public ClientPlayerPositionPacket() {
        this.pos = true;
    }

    public ClientPlayerPositionPacket(boolean onGround, double x, double y, double z) {
        super(onGround);
        this.pos = true;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

