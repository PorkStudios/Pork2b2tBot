/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.unix.Limits;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public final class IovArray
implements ChannelOutboundBuffer.MessageProcessor {
    private static final int ADDRESS_SIZE = PlatformDependent.addressSize();
    private static final int IOV_SIZE = 2 * ADDRESS_SIZE;
    private static final int CAPACITY = Limits.IOV_MAX * IOV_SIZE;
    private final long memoryAddress = PlatformDependent.allocateMemory(CAPACITY);
    private int count;
    private long size;
    private long maxBytes = Limits.SSIZE_MAX;

    public void clear() {
        this.count = 0;
        this.size = 0L;
    }

    public boolean add(ByteBuf buf) {
        ByteBuffer[] buffers;
        ByteBuffer nioBuffer;
        int len;
        if (this.count == Limits.IOV_MAX) {
            return false;
        }
        if (buf.nioBufferCount() == 1) {
            int len2 = buf.readableBytes();
            return len2 == 0 || this.add(buf.memoryAddress(), buf.readerIndex(), len2);
        }
        ByteBuffer[] arrbyteBuffer = buffers = buf.nioBuffers();
        int n = arrbyteBuffer.length;
        for (int i = 0; i < n && ((len = (nioBuffer = arrbyteBuffer[i]).remaining()) == 0 || this.add(PlatformDependent.directBufferAddress(nioBuffer), nioBuffer.position(), len) && this.count != Limits.IOV_MAX); ++i) {
        }
        return true;
    }

    private boolean add(long addr, int offset, int len) {
        if (len == 0) {
            return true;
        }
        long baseOffset = this.memoryAddress(this.count);
        long lengthOffset = baseOffset + (long)ADDRESS_SIZE;
        if (this.maxBytes - (long)len < this.size && this.count > 0) {
            return false;
        }
        this.size += (long)len;
        ++this.count;
        if (ADDRESS_SIZE == 8) {
            PlatformDependent.putLong(baseOffset, addr + (long)offset);
            PlatformDependent.putLong(lengthOffset, len);
        } else {
            assert (ADDRESS_SIZE == 4);
            PlatformDependent.putInt(baseOffset, (int)addr + offset);
            PlatformDependent.putInt(lengthOffset, len);
        }
        return true;
    }

    public int count() {
        return this.count;
    }

    public long size() {
        return this.size;
    }

    public void maxBytes(long maxBytes) {
        this.maxBytes = Math.min(Limits.SSIZE_MAX, ObjectUtil.checkPositive(maxBytes, "maxBytes"));
    }

    public long maxBytes() {
        return this.maxBytes;
    }

    public long memoryAddress(int offset) {
        return this.memoryAddress + (long)(IOV_SIZE * offset);
    }

    public void release() {
        PlatformDependent.freeMemory(this.memoryAddress);
    }

    @Override
    public boolean processMessage(Object msg) throws Exception {
        return msg instanceof ByteBuf && this.add((ByteBuf)msg);
    }
}

