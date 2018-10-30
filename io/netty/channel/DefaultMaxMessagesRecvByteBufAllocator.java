/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MaxMessagesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;

public abstract class DefaultMaxMessagesRecvByteBufAllocator
implements MaxMessagesRecvByteBufAllocator {
    private volatile int maxMessagesPerRead;
    private volatile boolean respectMaybeMoreData = true;

    public DefaultMaxMessagesRecvByteBufAllocator() {
        this(1);
    }

    public DefaultMaxMessagesRecvByteBufAllocator(int maxMessagesPerRead) {
        this.maxMessagesPerRead(maxMessagesPerRead);
    }

    @Override
    public int maxMessagesPerRead() {
        return this.maxMessagesPerRead;
    }

    @Override
    public MaxMessagesRecvByteBufAllocator maxMessagesPerRead(int maxMessagesPerRead) {
        if (maxMessagesPerRead <= 0) {
            throw new IllegalArgumentException("maxMessagesPerRead: " + maxMessagesPerRead + " (expected: > 0)");
        }
        this.maxMessagesPerRead = maxMessagesPerRead;
        return this;
    }

    public DefaultMaxMessagesRecvByteBufAllocator respectMaybeMoreData(boolean respectMaybeMoreData) {
        this.respectMaybeMoreData = respectMaybeMoreData;
        return this;
    }

    public final boolean respectMaybeMoreData() {
        return this.respectMaybeMoreData;
    }

    public abstract class MaxMessageHandle
    implements RecvByteBufAllocator.ExtendedHandle {
        private ChannelConfig config;
        private int maxMessagePerRead;
        private int totalMessages;
        private int totalBytesRead;
        private int attemptedBytesRead;
        private int lastBytesRead;
        private final boolean respectMaybeMoreData;
        private final UncheckedBooleanSupplier defaultMaybeMoreSupplier;

        public MaxMessageHandle() {
            this.respectMaybeMoreData = DefaultMaxMessagesRecvByteBufAllocator.this.respectMaybeMoreData;
            this.defaultMaybeMoreSupplier = new UncheckedBooleanSupplier(){

                @Override
                public boolean get() {
                    return MaxMessageHandle.this.attemptedBytesRead == MaxMessageHandle.this.lastBytesRead;
                }
            };
        }

        @Override
        public void reset(ChannelConfig config) {
            this.config = config;
            this.maxMessagePerRead = DefaultMaxMessagesRecvByteBufAllocator.this.maxMessagesPerRead();
            this.totalBytesRead = 0;
            this.totalMessages = 0;
        }

        @Override
        public ByteBuf allocate(ByteBufAllocator alloc) {
            return alloc.ioBuffer(this.guess());
        }

        @Override
        public final void incMessagesRead(int amt) {
            this.totalMessages += amt;
        }

        @Override
        public void lastBytesRead(int bytes) {
            this.lastBytesRead = bytes;
            if (bytes > 0) {
                this.totalBytesRead += bytes;
            }
        }

        @Override
        public final int lastBytesRead() {
            return this.lastBytesRead;
        }

        @Override
        public boolean continueReading() {
            return this.continueReading(this.defaultMaybeMoreSupplier);
        }

        @Override
        public boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
            return this.config.isAutoRead() && (!this.respectMaybeMoreData || maybeMoreDataSupplier.get()) && this.totalMessages < this.maxMessagePerRead && this.totalBytesRead > 0;
        }

        @Override
        public void readComplete() {
        }

        @Override
        public int attemptedBytesRead() {
            return this.attemptedBytesRead;
        }

        @Override
        public void attemptedBytesRead(int bytes) {
            this.attemptedBytesRead = bytes;
        }

        protected final int totalBytesRead() {
            return this.totalBytesRead < 0 ? Integer.MAX_VALUE : this.totalBytesRead;
        }

    }

}

