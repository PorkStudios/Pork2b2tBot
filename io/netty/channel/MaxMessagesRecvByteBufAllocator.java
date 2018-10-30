/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.RecvByteBufAllocator;

public interface MaxMessagesRecvByteBufAllocator
extends RecvByteBufAllocator {
    public int maxMessagesPerRead();

    public MaxMessagesRecvByteBufAllocator maxMessagesPerRead(int var1);
}

