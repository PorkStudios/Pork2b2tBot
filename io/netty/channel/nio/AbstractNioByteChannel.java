/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioByteChannel
extends AbstractNioChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';
    private Runnable flushTask;

    protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
        super(parent, ch, 1);
    }

    protected abstract ChannelFuture shutdownInput();

    protected boolean isInputShutdown0() {
        return false;
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioByteUnsafe();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    protected final int doWrite0(ChannelOutboundBuffer in) throws Exception {
        Object msg = in.current();
        if (msg == null) {
            return 0;
        }
        return this.doWriteInternal(in, in.current());
    }

    private int doWriteInternal(ChannelOutboundBuffer in, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (!buf.isReadable()) {
                in.remove();
                return 0;
            }
            int localFlushedAmount = this.doWriteBytes(buf);
            if (localFlushedAmount > 0) {
                in.progress(localFlushedAmount);
                if (!buf.isReadable()) {
                    in.remove();
                }
                return 1;
            }
        } else if (msg instanceof FileRegion) {
            FileRegion region = (FileRegion)msg;
            if (region.transferred() >= region.count()) {
                in.remove();
                return 0;
            }
            long localFlushedAmount = this.doWriteFileRegion(region);
            if (localFlushedAmount > 0L) {
                in.progress(localFlushedAmount);
                if (region.transferred() >= region.count()) {
                    in.remove();
                }
                return 1;
            }
        } else {
            throw new Error();
        }
        return Integer.MAX_VALUE;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        int writeSpinCount = this.config().getWriteSpinCount();
        do {
            if ((msg = in.current()) != null) continue;
            this.clearOpWrite();
            return;
        } while ((writeSpinCount -= this.doWriteInternal(in, msg)) > 0);
        this.incompleteWrite(writeSpinCount < 0);
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (buf.isDirect()) {
                return msg;
            }
            return this.newDirectBuffer(buf);
        }
        if (msg instanceof FileRegion) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    protected final void incompleteWrite(boolean setOpWrite) {
        if (setOpWrite) {
            this.setOpWrite();
        } else {
            Runnable flushTask = this.flushTask;
            if (flushTask == null) {
                flushTask = this.flushTask = new Runnable(){

                    @Override
                    public void run() {
                        AbstractNioByteChannel.this.flush();
                    }
                };
            }
            this.eventLoop().execute(flushTask);
        }
    }

    protected abstract long doWriteFileRegion(FileRegion var1) throws Exception;

    protected abstract int doReadBytes(ByteBuf var1) throws Exception;

    protected abstract int doWriteBytes(ByteBuf var1) throws Exception;

    protected final void setOpWrite() {
        SelectionKey key = this.selectionKey();
        if (!key.isValid()) {
            return;
        }
        int interestOps = key.interestOps();
        if ((interestOps & 4) == 0) {
            key.interestOps(interestOps | 4);
        }
    }

    protected final void clearOpWrite() {
        SelectionKey key = this.selectionKey();
        if (!key.isValid()) {
            return;
        }
        int interestOps = key.interestOps();
        if ((interestOps & 4) != 0) {
            key.interestOps(interestOps & -5);
        }
    }

    protected class NioByteUnsafe
    extends AbstractNioChannel.AbstractNioUnsafe {
        protected NioByteUnsafe() {
        }

        private void closeOnRead(ChannelPipeline pipeline) {
            if (!AbstractNioByteChannel.this.isInputShutdown0()) {
                if (Boolean.TRUE.equals(AbstractNioByteChannel.this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                    AbstractNioByteChannel.this.shutdownInput();
                    pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                } else {
                    this.close(this.voidPromise());
                }
            } else {
                pipeline.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
            }
        }

        private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, RecvByteBufAllocator.Handle allocHandle) {
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    AbstractNioByteChannel.this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                } else {
                    byteBuf.release();
                }
            }
            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();
            pipeline.fireExceptionCaught(cause);
            if (close || cause instanceof IOException) {
                this.closeOnRead(pipeline);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public final void read() {
            ChannelConfig config = AbstractNioByteChannel.this.config();
            ChannelPipeline pipeline = AbstractNioByteChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            RecvByteBufAllocator.Handle allocHandle = this.recvBufAllocHandle();
            allocHandle.reset(config);
            ByteBuf byteBuf = null;
            boolean close = false;
            try {
                do {
                    byteBuf = allocHandle.allocate(allocator);
                    allocHandle.lastBytesRead(AbstractNioByteChannel.this.doReadBytes(byteBuf));
                    if (allocHandle.lastBytesRead() <= 0) {
                        byteBuf.release();
                        byteBuf = null;
                        boolean bl = close = allocHandle.lastBytesRead() < 0;
                        if (!close) break;
                        AbstractNioByteChannel.this.readPending = false;
                        break;
                    }
                    allocHandle.incMessagesRead(1);
                    AbstractNioByteChannel.this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = null;
                } while (allocHandle.continueReading());
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (close) {
                    this.closeOnRead(pipeline);
                }
            }
            catch (Throwable t) {
                this.handleReadException(pipeline, byteBuf, t, close, allocHandle);
            }
            finally {
                if (!AbstractNioByteChannel.this.readPending && !config.isAutoRead()) {
                    this.removeReadOp();
                }
            }
        }
    }

}

