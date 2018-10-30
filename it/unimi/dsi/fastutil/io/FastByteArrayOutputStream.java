/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.io.MeasurableOutputStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;
import java.io.IOException;

public class FastByteArrayOutputStream
extends MeasurableOutputStream
implements RepositionableStream {
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    public byte[] array;
    public int length;
    private int position;

    public FastByteArrayOutputStream() {
        this(16);
    }

    public FastByteArrayOutputStream(int initialCapacity) {
        this.array = new byte[initialCapacity];
    }

    public FastByteArrayOutputStream(byte[] a) {
        this.array = a;
    }

    public void reset() {
        this.length = 0;
        this.position = 0;
    }

    public void trim() {
        this.array = ByteArrays.trim(this.array, this.length);
    }

    @Override
    public void write(int b) {
        if (this.position >= this.array.length) {
            this.array = ByteArrays.grow(this.array, this.position + 1, this.length);
        }
        this.array[this.position++] = (byte)b;
        if (this.length < this.position) {
            this.length = this.position;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        ByteArrays.ensureOffsetLength(b, off, len);
        if (this.position + len > this.array.length) {
            this.array = ByteArrays.grow(this.array, this.position + len, this.position);
        }
        System.arraycopy(b, off, this.array, this.position, len);
        if (this.position + len > this.length) {
            this.length = this.position += len;
        }
    }

    @Override
    public void position(long newPosition) {
        if (this.position > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Position too large: " + newPosition);
        }
        this.position = (int)newPosition;
    }

    @Override
    public long position() {
        return this.position;
    }

    @Override
    public long length() throws IOException {
        return this.length;
    }
}

