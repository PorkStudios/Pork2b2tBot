/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.CompactObjectOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

@ChannelHandler.Sharable
public class ObjectEncoder
extends MessageToByteEncoder<Serializable> {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        int startIdx;
        startIdx = out.writerIndex();
        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        ObjectOutputStream oout = null;
        try {
            bout.write(LENGTH_PLACEHOLDER);
            oout = new CompactObjectOutputStream(bout);
            oout.writeObject(msg);
            oout.flush();
        }
        finally {
            if (oout != null) {
                oout.close();
            } else {
                bout.close();
            }
        }
        int endIdx = out.writerIndex();
        out.setInt(startIdx, endIdx - startIdx - 4);
    }
}

