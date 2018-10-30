/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractUnpooledSlicedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

@Deprecated
public class SlicedByteBuf
extends AbstractUnpooledSlicedByteBuf {
    private int length;

    public SlicedByteBuf(ByteBuf buffer, int index, int length) {
        super(buffer, index, length);
    }

    @Override
    final void initLength(int length) {
        this.length = length;
    }

    @Override
    final int length() {
        return this.length;
    }

    @Override
    public int capacity() {
        return this.length;
    }
}

