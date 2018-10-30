/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;

public class FixedRecvByteBufAllocator
extends DefaultMaxMessagesRecvByteBufAllocator {
    private final int bufferSize;

    public FixedRecvByteBufAllocator(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must greater than 0: " + bufferSize);
        }
        this.bufferSize = bufferSize;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl(this.bufferSize);
    }

    @Override
    public FixedRecvByteBufAllocator respectMaybeMoreData(boolean respectMaybeMoreData) {
        super.respectMaybeMoreData(respectMaybeMoreData);
        return this;
    }

    private final class HandleImpl
    extends DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle {
        private final int bufferSize;

        public HandleImpl(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        @Override
        public int guess() {
            return this.bufferSize;
        }
    }

}

