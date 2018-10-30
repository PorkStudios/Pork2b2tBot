/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class ByteToMessageCodec<I>
extends ChannelDuplexHandler {
    private final TypeParameterMatcher outboundMsgMatcher;
    private final MessageToByteEncoder<I> encoder;
    private final ByteToMessageDecoder decoder = new ByteToMessageDecoder(){

        @Override
        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            ByteToMessageCodec.this.decode(ctx, in, out);
        }

        @Override
        protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            ByteToMessageCodec.this.decodeLast(ctx, in, out);
        }
    };

    protected ByteToMessageCodec() {
        this(true);
    }

    protected ByteToMessageCodec(Class<? extends I> outboundMessageType) {
        this(outboundMessageType, true);
    }

    protected ByteToMessageCodec(boolean preferDirect) {
        this.ensureNotSharable();
        this.outboundMsgMatcher = TypeParameterMatcher.find(this, ByteToMessageCodec.class, "I");
        this.encoder = new Encoder(preferDirect);
    }

    protected ByteToMessageCodec(Class<? extends I> outboundMessageType, boolean preferDirect) {
        this.ensureNotSharable();
        this.outboundMsgMatcher = TypeParameterMatcher.get(outboundMessageType);
        this.encoder = new Encoder(preferDirect);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.outboundMsgMatcher.match(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.decoder.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.encoder.write(ctx, msg, promise);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelReadComplete(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelInactive(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        try {
            this.decoder.handlerAdded(ctx);
        }
        finally {
            this.encoder.handlerAdded(ctx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            this.decoder.handlerRemoved(ctx);
        }
        finally {
            this.encoder.handlerRemoved(ctx);
        }
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, ByteBuf var3) throws Exception;

    protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.isReadable()) {
            this.decode(ctx, in, out);
        }
    }

    private final class Encoder
    extends MessageToByteEncoder<I> {
        Encoder(boolean preferDirect) {
            super(preferDirect);
        }

        @Override
        public boolean acceptOutboundMessage(Object msg) throws Exception {
            return ByteToMessageCodec.this.acceptOutboundMessage(msg);
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, I msg, ByteBuf out) throws Exception {
            ByteToMessageCodec.this.encode(ctx, msg, out);
        }
    }

}

