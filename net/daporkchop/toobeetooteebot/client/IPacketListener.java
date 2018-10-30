/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.Session;

public interface IPacketListener<T extends MinecraftPacket> {
    public void handlePacket(Session var1, T var2);

    public static final class IgnoreListener
    implements IPacketListener {
        public void handlePacket(Session session, MinecraftPacket pck) {
        }
    }

}

