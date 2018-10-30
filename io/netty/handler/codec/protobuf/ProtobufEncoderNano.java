/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.nano.CodedOutputByteBufferNano
 *  com.google.protobuf.nano.MessageNano
 */
package io.netty.handler.codec.protobuf;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufEncoderNano
extends MessageToMessageEncoder<MessageNano> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageNano msg, List<Object> out) throws Exception {
        int size = msg.getSerializedSize();
        ByteBuf buffer = ctx.alloc().heapBuffer(size, size);
        byte[] array = buffer.array();
        CodedOutputByteBufferNano cobbn = CodedOutputByteBufferNano.newInstance((byte[])array, (int)buffer.arrayOffset(), (int)buffer.capacity());
        msg.writeTo(cobbn);
        buffer.writerIndex(size);
        out.add(buffer);
    }
}

