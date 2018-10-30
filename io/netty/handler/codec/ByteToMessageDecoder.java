/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ByteToMessageDecoder
extends ChannelInboundHandlerAdapter {
    public static final Cumulator MERGE_CUMULATOR = new Cumulator(){

        @Override
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            ByteBuf buffer = cumulation.writerIndex() > cumulation.maxCapacity() - in.readableBytes() || cumulation.refCnt() > 1 || cumulation.isReadOnly() ? ByteToMessageDecoder.expandCumulation(alloc, cumulation, in.readableBytes()) : cumulation;
            buffer.writeBytes(in);
            in.release();
            return buffer;
        }
    };
    public static final Cumulator COMPOSITE_CUMULATOR = new Cumulator(){

        @Override
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            ByteBuf buffer;
            if (cumulation.refCnt() > 1) {
                buffer = ByteToMessageDecoder.expandCumulation(alloc, cumulation, in.readableBytes());
                buffer.writeBytes(in);
                in.release();
            } else {
                CompositeByteBuf composite;
                if (cumulation instanceof CompositeByteBuf) {
                    composite = (CompositeByteBuf)cumulation;
                } else {
                    composite = alloc.compositeBuffer(Integer.MAX_VALUE);
                    composite.addComponent(true, cumulation);
                }
                composite.addComponent(true, in);
                buffer = composite;
            }
            return buffer;
        }
    };
    private static final byte STATE_INIT = 0;
    private static final byte STATE_CALLING_CHILD_DECODE = 1;
    private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
    ByteBuf cumulation;
    private Cumulator cumulator = MERGE_CUMULATOR;
    private boolean singleDecode;
    private boolean decodeWasNull;
    private boolean first;
    private byte decodeState = 0;
    private int discardAfterReads = 16;
    private int numReads;

    protected ByteToMessageDecoder() {
        this.ensureNotSharable();
    }

    public void setSingleDecode(boolean singleDecode) {
        this.singleDecode = singleDecode;
    }

    public boolean isSingleDecode() {
        return this.singleDecode;
    }

    public void setCumulator(Cumulator cumulator) {
        if (cumulator == null) {
            throw new NullPointerException("cumulator");
        }
        this.cumulator = cumulator;
    }

    public void setDiscardAfterReads(int discardAfterReads) {
        if (discardAfterReads <= 0) {
            throw new IllegalArgumentException("discardAfterReads must be > 0");
        }
        this.discardAfterReads = discardAfterReads;
    }

    protected int actualReadableBytes() {
        return this.internalBuffer().readableBytes();
    }

    protected ByteBuf internalBuffer() {
        if (this.cumulation != null) {
            return this.cumulation;
        }
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (this.decodeState == 1) {
            this.decodeState = (byte)2;
            return;
        }
        ByteBuf buf = this.cumulation;
        if (buf != null) {
            this.cumulation = null;
            int readable = buf.readableBytes();
            if (readable > 0) {
                ByteBuf bytes = buf.readBytes(readable);
                buf.release();
                ctx.fireChannelRead(bytes);
            } else {
                buf.release();
            }
            this.numReads = 0;
            ctx.fireChannelReadComplete();
        }
        this.handlerRemoved0(ctx);
    }

    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            ctx.fireChannelRead(msg);
            return;
        }
        CodecOutputList out = CodecOutputList.newInstance();
        try {
            ByteBuf data = (ByteBuf)msg;
            this.first = this.cumulation == null;
            this.cumulation = this.first ? data : this.cumulator.cumulate(ctx.alloc(), this.cumulation, data);
            this.callDecode(ctx, this.cumulation, out);
        }
        catch (DecoderException e) {
            try {
                throw e;
                catch (Exception e2) {
                    throw new DecoderException(e2);
                }
            }
            catch (Throwable throwable) {
                if (this.cumulation != null && !this.cumulation.isReadable()) {
                    this.numReads = 0;
                    this.cumulation.release();
                    this.cumulation = null;
                } else if (++this.numReads >= this.discardAfterReads) {
                    this.numReads = 0;
                    this.discardSomeReadBytes();
                }
                int size = out.size();
                this.decodeWasNull = !out.insertSinceRecycled();
                ByteToMessageDecoder.fireChannelRead(ctx, out, size);
                out.recycle();
                throw throwable;
            }
        }
        if (this.cumulation != null && !this.cumulation.isReadable()) {
            this.numReads = 0;
            this.cumulation.release();
            this.cumulation = null;
        } else if (++this.numReads >= this.discardAfterReads) {
            this.numReads = 0;
            this.discardSomeReadBytes();
        }
        int size = out.size();
        this.decodeWasNull = !out.insertSinceRecycled();
        ByteToMessageDecoder.fireChannelRead(ctx, out, size);
        out.recycle();
        return;
    }

    static void fireChannelRead(ChannelHandlerContext ctx, List<Object> msgs, int numElements) {
        if (msgs instanceof CodecOutputList) {
            ByteToMessageDecoder.fireChannelRead(ctx, (CodecOutputList)msgs, numElements);
        } else {
            for (int i = 0; i < numElements; ++i) {
                ctx.fireChannelRead(msgs.get(i));
            }
        }
    }

    static void fireChannelRead(ChannelHandlerContext ctx, CodecOutputList msgs, int numElements) {
        for (int i = 0; i < numElements; ++i) {
            ctx.fireChannelRead(msgs.getUnsafe(i));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.numReads = 0;
        this.discardSomeReadBytes();
        if (this.decodeWasNull) {
            this.decodeWasNull = false;
            if (!ctx.channel().config().isAutoRead()) {
                ctx.read();
            }
        }
        ctx.fireChannelReadComplete();
    }

    protected final void discardSomeReadBytes() {
        if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
            this.cumulation.discardSomeReadBytes();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channelInputClosed(ctx, true);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ChannelInputShutdownEvent) {
            this.channelInputClosed(ctx, false);
        }
        super.userEventTriggered(ctx, evt);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void channelInputClosed(ChannelHandlerContext ctx, boolean callChannelInactive) throws Exception {
        CodecOutputList out = CodecOutputList.newInstance();
        try {
            this.channelInputClosed(ctx, out);
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DecoderException(e);
        }
        finally {
            try {
                if (this.cumulation != null) {
                    this.cumulation.release();
                    this.cumulation = null;
                }
                int size = out.size();
                ByteToMessageDecoder.fireChannelRead(ctx, out, size);
                if (size > 0) {
                    ctx.fireChannelReadComplete();
                }
                if (callChannelInactive) {
                    ctx.fireChannelInactive();
                }
            }
            finally {
                out.recycle();
            }
        }
    }

    void channelInputClosed(ChannelHandlerContext ctx, List<Object> out) throws Exception {
        if (this.cumulation != null) {
            this.callDecode(ctx, this.cumulation, out);
            this.decodeLast(ctx, this.cumulation, out);
        } else {
            this.decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
        }
    }

    protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            while (in.isReadable()) {
                int outSize = out.size();
                if (outSize > 0) {
                    ByteToMessageDecoder.fireChannelRead(ctx, out, outSize);
                    out.clear();
                    if (ctx.isRemoved()) break;
                    outSize = 0;
                }
                int oldInputLength = in.readableBytes();
                this.decodeRemovalReentryProtection(ctx, in, out);
                if (!ctx.isRemoved()) {
                    if (outSize == out.size()) {
                        if (oldInputLength != in.readableBytes()) continue;
                    } else {
                        if (oldInputLength == in.readableBytes()) {
                            throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                        }
                        if (!this.isSingleDecode()) continue;
                    }
                }
                break;
            }
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception cause) {
            throw new DecoderException(cause);
        }
    }

    protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void decodeRemovalReentryProtection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        boolean removePending;
        this.decodeState = 1;
        try {
            this.decode(ctx, in, out);
            removePending = this.decodeState == 2;
        }
        catch (Throwable throwable) {
            boolean removePending2 = this.decodeState == 2;
            this.decodeState = 0;
            if (removePending2) {
                this.handlerRemoved(ctx);
            }
            throw throwable;
        }
        this.decodeState = 0;
        if (removePending) {
            this.handlerRemoved(ctx);
        }
    }

    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.isReadable()) {
            this.decodeRemovalReentryProtection(ctx, in, out);
        }
    }

    static ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf cumulation, int readable) {
        ByteBuf oldCumulation = cumulation;
        cumulation = alloc.buffer(oldCumulation.readableBytes() + readable);
        cumulation.writeBytes(oldCumulation);
        oldCumulation.release();
        return cumulation;
    }

    public static interface Cumulator {
        public ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);
    }

}

