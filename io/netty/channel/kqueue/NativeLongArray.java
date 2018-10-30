/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.unix.Limits;
import io.netty.util.internal.PlatformDependent;

final class NativeLongArray {
    private long memoryAddress;
    private int capacity;
    private int size;

    NativeLongArray(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1 but was " + capacity);
        }
        this.memoryAddress = PlatformDependent.allocateMemory(capacity * Limits.SIZEOF_JLONG);
        this.capacity = capacity;
    }

    void add(long value) {
        this.checkSize();
        PlatformDependent.putLong(this.memoryOffset(this.size++), value);
    }

    void clear() {
        this.size = 0;
    }

    boolean isEmpty() {
        return this.size == 0;
    }

    void free() {
        PlatformDependent.freeMemory(this.memoryAddress);
        this.memoryAddress = 0L;
    }

    long memoryAddress() {
        return this.memoryAddress;
    }

    long memoryAddressEnd() {
        return this.memoryOffset(this.size);
    }

    private long memoryOffset(int index) {
        return this.memoryAddress + (long)(index * Limits.SIZEOF_JLONG);
    }

    private void checkSize() {
        if (this.size == this.capacity) {
            this.realloc();
        }
    }

    private void realloc() {
        int newLength = this.capacity <= 65536 ? this.capacity << 1 : this.capacity + this.capacity >> 1;
        long newMemoryAddress = PlatformDependent.reallocateMemory(this.memoryAddress, newLength * Limits.SIZEOF_JLONG);
        if (newMemoryAddress == 0L) {
            throw new OutOfMemoryError("unable to allocate " + newLength + " new bytes! Existing capacity is: " + this.capacity);
        }
        this.memoryAddress = newMemoryAddress;
        this.capacity = newLength;
    }

    public String toString() {
        return "memoryAddress: " + this.memoryAddress + " capacity: " + this.capacity + " size: " + this.size;
    }
}

