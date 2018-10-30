/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class RefStrings {
    public static final String BRAND = "max_Bounce";
    public static final byte[] BRAND_ENCODED;

    public static void writeUTF8(ByteBuf buf, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= 32767) {
            throw new IOException("Attempt to write a string with a length greater than Short.MAX_VALUE to ByteBuf!");
        }
        RefStrings.writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        do {
            byte part = (byte)(value & 127);
            if ((value >>>= 7) != 0) {
                part = (byte)(part | 128);
            }
            buf.writeByte(part);
        } while (value != 0);
    }

    static {
        ByteBuf buf = Unpooled.buffer(5 + BRAND.length());
        try {
            RefStrings.writeUTF8(buf, BRAND);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        BRAND_ENCODED = buf.array();
    }
}

