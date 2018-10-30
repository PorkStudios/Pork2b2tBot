/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;

final class KQueueRecvByteAllocatorHandle
implements RecvByteBufAllocator.ExtendedHandle {
    private final RecvByteBufAllocator.ExtendedHandle delegate;
    private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier(){

        @Override
        public boolean get() {
            return KQueueRecvByteAllocatorHandle.this.maybeMoreDataToRead();
        }
    };
    private boolean overrideGuess;
    private boolean readEOF;
    private long numberBytesPending;

    KQueueRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle handle) {
        this.delegate = ObjectUtil.checkNotNull(handle, "handle");
    }

    @Override
    public int guess() {
        return this.overrideGuess ? this.guess0() : this.delegate.guess();
    }

    @Override
    public void reset(ChannelConfig config) {
        this.overrideGuess = ((KQueueChannelConfig)config).getRcvAllocTransportProvidesGuess();
        this.delegate.reset(config);
    }

    @Override
    public void incMessagesRead(int numMessages) {
        this.delegate.incMessagesRead(numMessages);
    }

    @Override
    public ByteBuf allocate(ByteBufAllocator alloc) {
        return this.overrideGuess ? alloc.ioBuffer(this.guess0()) : this.delegate.allocate(alloc);
    }

    @Override
    public void lastBytesRead(int bytes) {
        this.numberBytesPending = bytes < 0 ? 0L : Math.max(0L, this.numberBytesPending - (long)bytes);
        this.delegate.lastBytesRead(bytes);
    }

    @Override
    public int lastBytesRead() {
        return this.delegate.lastBytesRead();
    }

    @Override
    public void attemptedBytesRead(int bytes) {
        this.delegate.attemptedBytesRead(bytes);
    }

    @Override
    public int attemptedBytesRead() {
        return this.delegate.attemptedBytesRead();
    }

    @Override
    public void readComplete() {
        this.delegate.readComplete();
    }

    @Override
    public boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
        return this.delegate.continueReading(maybeMoreDataSupplier);
    }

    @Override
    public boolean continueReading() {
        return this.delegate.continueReading(this.defaultMaybeMoreDataSupplier);
    }

    void readEOF() {
        this.readEOF = true;
    }

    void numberBytesPending(long numberBytesPending) {
        this.numberBytesPending = numberBytesPending;
    }

    boolean maybeMoreDataToRead() {
        return this.numberBytesPending != 0L || this.readEOF;
    }

    private int guess0() {
        return (int)Math.min(this.numberBytesPending, Integer.MAX_VALUE);
    }

}

