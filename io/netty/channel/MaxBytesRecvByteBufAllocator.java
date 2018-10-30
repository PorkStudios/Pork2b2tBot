/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.RecvByteBufAllocator;
import java.util.Map;

public interface MaxBytesRecvByteBufAllocator
extends RecvByteBufAllocator {
    public int maxBytesPerRead();

    public MaxBytesRecvByteBufAllocator maxBytesPerRead(int var1);

    public int maxBytesPerIndividualRead();

    public MaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int var1);

    public Map.Entry<Integer, Integer> maxBytesPerReadPair();

    public MaxBytesRecvByteBufAllocator maxBytesPerReadPair(int var1, int var2);
}

