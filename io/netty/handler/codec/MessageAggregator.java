/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.MessageAggregationException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.List;

public abstract class MessageAggregator<I, S, C extends ByteBufHolder, O extends ByteBufHolder>
extends MessageToMessageDecoder<I> {
    private static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private final int maxContentLength;
    private O currentMessage;
    private boolean handlingOversizedMessage;
    private int maxCumulationBufferComponents = 1024;
    private ChannelHandlerContext ctx;
    private ChannelFutureListener continueResponseWriteListener;

    protected MessageAggregator(int maxContentLength) {
        MessageAggregator.validateMaxContentLength(maxContentLength);
        this.maxContentLength = maxContentLength;
    }

    protected MessageAggregator(int maxContentLength, Class<? extends I> inboundMessageType) {
        super(inboundMessageType);
        MessageAggregator.validateMaxContentLength(maxContentLength);
        this.maxContentLength = maxContentLength;
    }

    private static void validateMaxContentLength(int maxContentLength) {
        if (maxContentLength < 0) {
            throw new IllegalArgumentException("maxContentLength: " + maxContentLength + " (expected: >= 0)");
        }
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (!super.acceptInboundMessage(msg)) {
            return false;
        }
        Object in = msg;
        return (this.isContentMessage(in) || this.isStartMessage(in)) && !this.isAggregated(in);
    }

    protected abstract boolean isStartMessage(I var1) throws Exception;

    protected abstract boolean isContentMessage(I var1) throws Exception;

    protected abstract boolean isLastContentMessage(C var1) throws Exception;

    protected abstract boolean isAggregated(I var1) throws Exception;

    public final int maxContentLength() {
        return this.maxContentLength;
    }

    public final int maxCumulationBufferComponents() {
        return this.maxCumulationBufferComponents;
    }

    public final void setMaxCumulationBufferComponents(int maxCumulationBufferComponents) {
        if (maxCumulationBufferComponents < 2) {
            throw new IllegalArgumentException("maxCumulationBufferComponents: " + maxCumulationBufferComponents + " (expected: >= 2)");
        }
        if (this.ctx != null) {
            throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
        }
        this.maxCumulationBufferComponents = maxCumulationBufferComponents;
    }

    @Deprecated
    public final boolean isHandlingOversizedMessage() {
        return this.handlingOversizedMessage;
    }

    protected final ChannelHandlerContext ctx() {
        if (this.ctx == null) {
            throw new IllegalStateException("not added to a pipeline yet");
        }
        return this.ctx;
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception {
        if (this.isStartMessage(msg)) {
            this.handlingOversizedMessage = false;
            if (this.currentMessage != null) {
                this.currentMessage.release();
                this.currentMessage = null;
                throw new MessageAggregationException();
            }
            I m = msg;
            Object continueResponse = this.newContinueResponse(m, this.maxContentLength, ctx.pipeline());
            if (continueResponse != null) {
                ChannelFutureListener listener = this.continueResponseWriteListener;
                if (listener == null) {
                    this.continueResponseWriteListener = listener = new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                ctx.fireExceptionCaught(future.cause());
                            }
                        }
                    };
                }
                boolean closeAfterWrite = this.closeAfterContinueResponse(continueResponse);
                this.handlingOversizedMessage = this.ignoreContentAfterContinueResponse(continueResponse);
                ChannelFuture future = ctx.writeAndFlush(continueResponse).addListener(listener);
                if (closeAfterWrite) {
                    future.addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                if (this.handlingOversizedMessage) {
                    return;
                }
            } else if (this.isContentLengthInvalid(m, this.maxContentLength)) {
                this.invokeHandleOversizedMessage(ctx, m);
                return;
            }
            if (m instanceof DecoderResultProvider && !((DecoderResultProvider)m).decoderResult().isSuccess()) {
                O aggregated = m instanceof ByteBufHolder ? this.beginAggregation(m, ((ByteBufHolder)m).content().retain()) : this.beginAggregation(m, Unpooled.EMPTY_BUFFER);
                this.finishAggregation(aggregated);
                out.add(aggregated);
                return;
            }
            CompositeByteBuf content = ctx.alloc().compositeBuffer(this.maxCumulationBufferComponents);
            if (m instanceof ByteBufHolder) {
                MessageAggregator.appendPartialContent(content, ((ByteBufHolder)m).content());
            }
            this.currentMessage = this.beginAggregation(m, content);
        } else if (this.isContentMessage(msg)) {
            boolean last;
            if (this.currentMessage == null) {
                return;
            }
            CompositeByteBuf content = (CompositeByteBuf)this.currentMessage.content();
            ByteBufHolder m = (ByteBufHolder)msg;
            if (content.readableBytes() > this.maxContentLength - m.content().readableBytes()) {
                O s = this.currentMessage;
                this.invokeHandleOversizedMessage(ctx, s);
                return;
            }
            MessageAggregator.appendPartialContent(content, m.content());
            this.aggregate(this.currentMessage, m);
            if (m instanceof DecoderResultProvider) {
                DecoderResult decoderResult = ((DecoderResultProvider)((Object)m)).decoderResult();
                if (!decoderResult.isSuccess()) {
                    if (this.currentMessage instanceof DecoderResultProvider) {
                        ((DecoderResultProvider)this.currentMessage).setDecoderResult(DecoderResult.failure(decoderResult.cause()));
                    }
                    last = true;
                } else {
                    last = this.isLastContentMessage(m);
                }
            } else {
                last = this.isLastContentMessage(m);
            }
            if (last) {
                this.finishAggregation(this.currentMessage);
                out.add(this.currentMessage);
                this.currentMessage = null;
            }
        } else {
            throw new MessageAggregationException();
        }
    }

    private static void appendPartialContent(CompositeByteBuf content, ByteBuf partialContent) {
        if (partialContent.isReadable()) {
            content.addComponent(true, partialContent.retain());
        }
    }

    protected abstract boolean isContentLengthInvalid(S var1, int var2) throws Exception;

    protected abstract Object newContinueResponse(S var1, int var2, ChannelPipeline var3) throws Exception;

    protected abstract boolean closeAfterContinueResponse(Object var1) throws Exception;

    protected abstract boolean ignoreContentAfterContinueResponse(Object var1) throws Exception;

    protected abstract O beginAggregation(S var1, ByteBuf var2) throws Exception;

    protected void aggregate(O aggregated, C content) throws Exception {
    }

    protected void finishAggregation(O aggregated) throws Exception {
    }

    private void invokeHandleOversizedMessage(ChannelHandlerContext ctx, S oversized) throws Exception {
        this.handlingOversizedMessage = true;
        this.currentMessage = null;
        try {
            this.handleOversizedMessage(ctx, oversized);
        }
        finally {
            ReferenceCountUtil.release(oversized);
        }
    }

    protected void handleOversizedMessage(ChannelHandlerContext ctx, S oversized) throws Exception {
        ctx.fireExceptionCaught(new TooLongFrameException("content length exceeded " + this.maxContentLength() + " bytes."));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.currentMessage != null && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelInactive(ctx);
        }
        finally {
            this.releaseCurrentMessage();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            super.handlerRemoved(ctx);
        }
        finally {
            this.releaseCurrentMessage();
        }
    }

    private void releaseCurrentMessage() {
        if (this.currentMessage != null) {
            this.currentMessage.release();
            this.currentMessage = null;
            this.handlingOversizedMessage = false;
        }
    }

}

