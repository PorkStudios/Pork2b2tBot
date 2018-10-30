/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.nio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel
extends AbstractNioChannel {
    boolean inputShutdown;

    protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent, ch, readInterestOp);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioMessageUnsafe();
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (this.inputShutdown) {
            return;
        }
        super.doBeginRead();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        SelectionKey key = this.selectionKey();
        int interestOps = key.interestOps();
        do {
            Object msg;
            if ((msg = in.current()) == null) {
                if ((interestOps & 4) == 0) break;
                key.interestOps(interestOps & -5);
                break;
            }
            try {
                boolean done = false;
                for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                    if (!this.doWriteMessage(msg, in)) continue;
                    done = true;
                    break;
                }
                if (done) {
                    in.remove();
                    continue;
                }
                if ((interestOps & 4) != 0) break;
                key.interestOps(interestOps | 4);
            }
            catch (Exception e) {
                if (this.continueOnWriteError()) {
                    in.remove(e);
                    continue;
                }
                throw e;
            }
            break;
        } while (true);
    }

    protected boolean continueOnWriteError() {
        return false;
    }

    protected boolean closeOnReadError(Throwable cause) {
        return cause instanceof IOException && !(cause instanceof PortUnreachableException) && !(this instanceof ServerChannel);
    }

    protected abstract int doReadMessages(List<Object> var1) throws Exception;

    protected abstract boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception;

    private final class NioMessageUnsafe
    extends AbstractNioChannel.AbstractNioUnsafe {
        private final List<Object> readBuf;

        private NioMessageUnsafe() {
            super(AbstractNioMessageChannel.this);
            this.readBuf = new ArrayList<Object>();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void read() {
            assert (AbstractNioMessageChannel.this.eventLoop().inEventLoop());
            ChannelConfig config = AbstractNioMessageChannel.this.config();
            ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
            RecvByteBufAllocator.Handle allocHandle = AbstractNioMessageChannel.this.unsafe().recvBufAllocHandle();
            allocHandle.reset(config);
            boolean closed = false;
            Throwable exception = null;
            try {
                try {
                    int localRead;
                    while ((localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf)) != 0) {
                        if (localRead < 0) {
                            closed = true;
                        } else {
                            allocHandle.incMessagesRead(localRead);
                            if (allocHandle.continueReading()) continue;
                        }
                        break;
                    }
                }
                catch (Throwable t) {
                    exception = t;
                }
                int size = this.readBuf.size();
                for (int i = 0; i < size; ++i) {
                    AbstractNioMessageChannel.this.readPending = false;
                    pipeline.fireChannelRead(this.readBuf.get(i));
                }
                this.readBuf.clear();
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    closed = AbstractNioMessageChannel.this.closeOnReadError(exception);
                    pipeline.fireExceptionCaught(exception);
                }
                if (closed) {
                    AbstractNioMessageChannel.this.inputShutdown = true;
                    if (AbstractNioMessageChannel.this.isOpen()) {
                        this.close(this.voidPromise());
                    }
                }
            }
            finally {
                if (!AbstractNioMessageChannel.this.readPending && !config.isAutoRead()) {
                    this.removeReadOp();
                }
            }
        }
    }

}

