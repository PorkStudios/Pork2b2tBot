/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.login.server;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import net.daporkchop.toobeetooteebot.TooBeeTooTeeBot;

public class LoginSuccessPacket
extends MinecraftPacket {
    public GameProfile profile;

    public LoginSuccessPacket() {
    }

    public LoginSuccessPacket(GameProfile profile) {
        this.profile = TooBeeTooTeeBot.bot.protocol.getProfile();
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.profile = new GameProfile(in.readString(), in.readString());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeString(this.profile.getIdAsString());
        out.writeString(this.profile.getName());
    }

    @Override
    public boolean isPriority() {
        return true;
    }
}

