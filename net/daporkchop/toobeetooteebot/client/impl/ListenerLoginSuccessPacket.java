/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.client.impl;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.Session;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.client.IPacketListener;

public class ListenerLoginSuccessPacket
implements IPacketListener<LoginSuccessPacket> {
    @Override
    public void handlePacket(Session session, LoginSuccessPacket pck) {
        Caches.uuid = pck.profile.getId();
    }
}

