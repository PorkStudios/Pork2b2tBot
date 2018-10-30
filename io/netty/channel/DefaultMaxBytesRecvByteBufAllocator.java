/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MaxBytesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import java.util.AbstractMap;
import java.util.Map;

public class DefaultMaxBytesRecvByteBufAllocator
implements MaxBytesRecvByteBufAllocator {
    private volatile int maxBytesPerRead;
    private volatile int maxBytesPerIndividualRead;

    public DefaultMaxBytesRecvByteBufAllocator() {
        this(65536, 65536);
    }

    public DefaultMaxBytesRecvByteBufAllocator(int maxBytesPerRead, int maxBytesPerIndividualRead) {
        DefaultMaxBytesRecvByteBufAllocator.checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
        this.maxBytesPerRead = maxBytesPerRead;
        this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl();
    }

    @Override
    public int maxBytesPerRead() {
        return this.maxBytesPerRead;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefaultMaxBytesRecvByteBufAllocator maxBytesPerRead(int maxBytesPerRead) {
        if (maxBytesPerRead <= 0) {
            throw new IllegalArgumentException("maxBytesPerRead: " + maxBytesPerRead + " (expected: > 0)");
        }
        DefaultMaxBytesRecvByteBufAllocator defaultMaxBytesRecvByteBufAllocator = this;
        synchronized (defaultMaxBytesRecvByteBufAllocator) {
            int maxBytesPerIndividualRead = this.maxBytesPerIndividualRead();
            if (maxBytesPerRead < maxBytesPerIndividualRead) {
                throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead);
            }
            this.maxBytesPerRead = maxBytesPerRead;
        }
        return this;
    }

    @Override
    public int maxBytesPerIndividualRead() {
        return this.maxBytesPerIndividualRead;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefaultMaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int maxBytesPerIndividualRead) {
        if (maxBytesPerIndividualRead <= 0) {
            throw new IllegalArgumentException("maxBytesPerIndividualRead: " + maxBytesPerIndividualRead + " (expected: > 0)");
        }
        DefaultMaxBytesRecvByteBufAllocator defaultMaxBytesRecvByteBufAllocator = this;
        synchronized (defaultMaxBytesRecvByteBufAllocator) {
            int maxBytesPerRead = this.maxBytesPerRead();
            if (maxBytesPerIndividualRead > maxBytesPerRead) {
                throw new IllegalArgumentException("maxBytesPerIndividualRead cannot be greater than maxBytesPerRead (" + maxBytesPerRead + "): " + maxBytesPerIndividualRead);
            }
            this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
        }
        return this;
    }

    @Override
    public synchronized Map.Entry<Integer, Integer> maxBytesPerReadPair() {
        return new AbstractMap.SimpleEntry<Integer, Integer>(this.maxBytesPerRead, this.maxBytesPerIndividualRead);
    }

    private static void checkMaxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
        if (maxBytesPerRead <= 0) {
            throw new IllegalArgumentException("maxBytesPerRead: " + maxBytesPerRead + " (expected: > 0)");
        }
        if (maxBytesPerIndividualRead <= 0) {
            throw new IllegalArgumentException("maxBytesPerIndividualRead: " + maxBytesPerIndividualRead + " (expected: > 0)");
        }
        if (maxBytesPerRead < maxBytesPerIndividualRead) {
            throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefaultMaxBytesRecvByteBufAllocator maxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
        DefaultMaxBytesRecvByteBufAllocator.checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
        DefaultMaxBytesRecvByteBufAllocator defaultMaxBytesRecvByteBufAllocator = this;
        synchronized (defaultMaxBytesRecvByteBufAllocator) {
            this.maxBytesPerRead = maxBytesPerRead;
            this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
        }
        return this;
    }

    private final class HandleImpl
    implements RecvByteBufAllocator.ExtendedHandle {
        private int individualReadMax;
        private int bytesToRead;
        private int lastBytesRead;
        private int attemptBytesRead;
        private final UncheckedBooleanSupplier defaultMaybeMoreSupplier = new UncheckedBooleanSupplier(){

            @Override
            public boolean get() {
                return HandleImpl.this.attemptBytesRead == HandleImpl.this.lastBytesRead;
            }
        };

        private HandleImpl() {
        }

        @Override
        public ByteBuf allocate(ByteBufAllocator alloc) {
            return alloc.ioBuffer(this.guess());
        }

        @Override
        public int guess() {
            return Math.min(this.individualReadMax, this.bytesToRead);
        }

        @Override
        public void reset(ChannelConfig config) {
            this.bytesToRead = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerRead();
            this.individualReadMax = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerIndividualRead();
        }

        @Override
        public void incMessagesRead(int amt) {
        }

        @Override
        public void lastBytesRead(int bytes) {
            this.lastBytesRead = bytes;
            this.bytesToRead -= bytes;
        }

        @Override
        public int lastBytesRead() {
            return this.lastBytesRead;
        }

        @Override
        public boolean continueReading() {
            return this.continueReading(this.defaultMaybeMoreSupplier);
        }

        @Override
        public boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
            return this.bytesToRead > 0 && maybeMoreDataSupplier.get();
        }

        @Override
        public void readComplete() {
        }

        @Override
        public void attemptedBytesRead(int bytes) {
            this.attemptBytesRead = bytes;
        }

        @Override
        public int attemptedBytesRead() {
            return this.attemptBytesRead;
        }

    }

}

