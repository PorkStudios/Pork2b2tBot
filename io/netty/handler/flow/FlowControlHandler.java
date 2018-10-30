/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.flow;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;

public class FlowControlHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(FlowControlHandler.class);
    private final boolean releaseMessages;
    private RecyclableArrayDeque queue;
    private ChannelConfig config;
    private boolean shouldConsume;

    public FlowControlHandler() {
        this(true);
    }

    public FlowControlHandler(boolean releaseMessages) {
        this.releaseMessages = releaseMessages;
    }

    boolean isQueueEmpty() {
        return this.queue.isEmpty();
    }

    private void destroy() {
        if (this.queue != null) {
            if (!this.queue.isEmpty()) {
                logger.trace("Non-empty queue: {}", (Object)this.queue);
                if (this.releaseMessages) {
                    Object msg;
                    while ((msg = this.queue.poll()) != null) {
                        ReferenceCountUtil.safeRelease(msg);
                    }
                }
            }
            this.queue.recycle();
            this.queue = null;
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.config = ctx.channel().config();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
        ctx.fireChannelInactive();
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (this.dequeue(ctx, 1) == 0) {
            this.shouldConsume = true;
            ctx.read();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.queue == null) {
            this.queue = RecyclableArrayDeque.newInstance();
        }
        this.queue.offer(msg);
        int minConsume = this.shouldConsume ? 1 : 0;
        this.shouldConsume = false;
        this.dequeue(ctx, minConsume);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    private int dequeue(ChannelHandlerContext ctx, int minConsume) {
        if (this.queue != null) {
            Object msg;
            int consumed;
            for (consumed = 0; (consumed < minConsume || this.config.isAutoRead()) && (msg = this.queue.poll()) != null; ++consumed) {
                ctx.fireChannelRead(msg);
            }
            if (this.queue.isEmpty() && consumed > 0) {
                ctx.fireChannelReadComplete();
            }
            return consumed;
        }
        return 0;
    }

    private static final class RecyclableArrayDeque
    extends ArrayDeque<Object> {
        private static final long serialVersionUID = 0L;
        private static final int DEFAULT_NUM_ELEMENTS = 2;
        private static final Recycler<RecyclableArrayDeque> RECYCLER = new Recycler<RecyclableArrayDeque>(){

            @Override
            protected RecyclableArrayDeque newObject(Recycler.Handle<RecyclableArrayDeque> handle) {
                return new RecyclableArrayDeque(2, handle);
            }
        };
        private final Recycler.Handle<RecyclableArrayDeque> handle;

        public static RecyclableArrayDeque newInstance() {
            return RECYCLER.get();
        }

        private RecyclableArrayDeque(int numElements, Recycler.Handle<RecyclableArrayDeque> handle) {
            super(numElements);
            this.handle = handle;
        }

        public void recycle() {
            this.clear();
            this.handle.recycle(this);
        }

    }

}

