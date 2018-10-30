/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.login.client;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.util.CryptUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;

public class EncryptionResponsePacket
extends MinecraftPacket {
    public byte[] sharedKey;
    public byte[] verifyToken;

    public EncryptionResponsePacket() {
    }

    public EncryptionResponsePacket(SecretKey secretKey, PublicKey publicKey, byte[] verifyToken) {
        this.sharedKey = CryptUtil.encryptData(publicKey, secretKey.getEncoded());
        this.verifyToken = CryptUtil.encryptData(publicKey, verifyToken);
    }

    public SecretKey getSecretKey(PrivateKey publicKey) {
        return CryptUtil.decryptSharedKey(publicKey, this.sharedKey);
    }

    public byte[] getVerifyToken(PrivateKey publicKey) {
        return CryptUtil.decryptData(publicKey, this.verifyToken);
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.sharedKey = in.readBytes(in.readVarInt());
        this.verifyToken = in.readBytes(in.readVarInt());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.sharedKey.length);
        out.writeBytes(this.sharedKey);
        out.writeVarInt(this.verifyToken.length);
        out.writeBytes(this.verifyToken);
    }

    @Override
    public boolean isPriority() {
        return true;
    }
}

