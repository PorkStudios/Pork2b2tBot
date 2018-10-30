/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;

final class EpollRecvByteAllocatorStreamingHandle
extends EpollRecvByteAllocatorHandle {
    public EpollRecvByteAllocatorStreamingHandle(RecvByteBufAllocator.ExtendedHandle handle) {
        super(handle);
    }

    @Override
    boolean maybeMoreDataToRead() {
        return this.lastBytesRead() == this.attemptedBytesRead() || this.isReceivedRdHup();
    }
}

