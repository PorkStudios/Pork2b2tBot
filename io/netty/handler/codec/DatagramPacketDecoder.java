/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class DatagramPacketDecoder
extends MessageToMessageDecoder<DatagramPacket> {
    private final MessageToMessageDecoder<ByteBuf> decoder;

    public DatagramPacketDecoder(MessageToMessageDecoder<ByteBuf> decoder) {
        this.decoder = ObjectUtil.checkNotNull(decoder, "decoder");
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (msg instanceof DatagramPacket) {
            return this.decoder.acceptInboundMessage(((DatagramPacket)msg).content());
        }
        return false;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        this.decoder.decode(ctx, (ByteBuf)msg.content(), out);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        this.decoder.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.decoder.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.decoder.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.decoder.handlerRemoved(ctx);
    }

    @Override
    public boolean isSharable() {
        return this.decoder.isSharable();
    }
}

