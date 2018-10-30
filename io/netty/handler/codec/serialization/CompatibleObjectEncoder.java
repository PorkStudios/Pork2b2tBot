/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class CompatibleObjectEncoder
extends MessageToByteEncoder<Serializable> {
    private final int resetInterval;
    private int writtenObjects;

    public CompatibleObjectEncoder() {
        this(16);
    }

    public CompatibleObjectEncoder(int resetInterval) {
        if (resetInterval < 0) {
            throw new IllegalArgumentException("resetInterval: " + resetInterval);
        }
        this.resetInterval = resetInterval;
    }

    protected ObjectOutputStream newObjectOutputStream(OutputStream out) throws Exception {
        return new ObjectOutputStream(out);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        ObjectOutputStream oos = this.newObjectOutputStream(new ByteBufOutputStream(out));
        try {
            if (this.resetInterval != 0) {
                ++this.writtenObjects;
                if (this.writtenObjects % this.resetInterval == 0) {
                    oos.reset();
                }
            }
            oos.writeObject(msg);
            oos.flush();
        }
        finally {
            oos.close();
        }
    }
}

