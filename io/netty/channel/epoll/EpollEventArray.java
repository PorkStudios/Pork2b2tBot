/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.epoll.Native;
import io.netty.util.internal.PlatformDependent;

final class EpollEventArray {
    private static final int EPOLL_EVENT_SIZE = Native.sizeofEpollEvent();
    private static final int EPOLL_DATA_OFFSET = Native.offsetofEpollData();
    private long memoryAddress;
    private int length;

    EpollEventArray(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be >= 1 but was " + length);
        }
        this.length = length;
        this.memoryAddress = EpollEventArray.allocate(length);
    }

    private static long allocate(int length) {
        return PlatformDependent.allocateMemory(length * EPOLL_EVENT_SIZE);
    }

    long memoryAddress() {
        return this.memoryAddress;
    }

    int length() {
        return this.length;
    }

    void increase() {
        this.length <<= 1;
        this.free();
        this.memoryAddress = EpollEventArray.allocate(this.length);
    }

    void free() {
        PlatformDependent.freeMemory(this.memoryAddress);
    }

    int events(int index) {
        return PlatformDependent.getInt(this.memoryAddress + (long)(index * EPOLL_EVENT_SIZE));
    }

    int fd(int index) {
        return PlatformDependent.getInt(this.memoryAddress + (long)(index * EPOLL_EVENT_SIZE) + (long)EPOLL_DATA_OFFSET);
    }
}

