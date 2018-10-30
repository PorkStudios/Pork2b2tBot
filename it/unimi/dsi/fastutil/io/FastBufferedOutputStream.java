/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.io.MeasurableOutputStream;
import it.unimi.dsi.fastutil.io.MeasurableStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;

public class FastBufferedOutputStream
extends MeasurableOutputStream
implements RepositionableStream {
    private static final boolean ASSERTS = false;
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    protected byte[] buffer;
    protected int pos;
    protected int avail;
    protected OutputStream os;
    private FileChannel fileChannel;
    private RepositionableStream repositionableStream;
    private MeasurableStream measurableStream;

    private static int ensureBufferSize(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Illegal buffer size: " + bufferSize);
        }
        return bufferSize;
    }

    public FastBufferedOutputStream(OutputStream os, byte[] buffer) {
        this.os = os;
        FastBufferedOutputStream.ensureBufferSize(buffer.length);
        this.buffer = buffer;
        this.avail = buffer.length;
        if (os instanceof RepositionableStream) {
            this.repositionableStream = (RepositionableStream)((Object)os);
        }
        if (os instanceof MeasurableStream) {
            this.measurableStream = (MeasurableStream)((Object)os);
        }
        if (this.repositionableStream == null) {
            try {
                this.fileChannel = (FileChannel)os.getClass().getMethod("getChannel", new Class[0]).invoke(os, new Object[0]);
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (ClassCastException classCastException) {
                // empty catch block
            }
        }
    }

    public FastBufferedOutputStream(OutputStream os, int bufferSize) {
        this(os, new byte[FastBufferedOutputStream.ensureBufferSize(bufferSize)]);
    }

    public FastBufferedOutputStream(OutputStream os) {
        this(os, 8192);
    }

    private void dumpBuffer(boolean ifFull) throws IOException {
        if (!ifFull || this.avail == 0) {
            this.os.write(this.buffer, 0, this.pos);
            this.pos = 0;
            this.avail = this.buffer.length;
        }
    }

    @Override
    public void write(int b) throws IOException {
        --this.avail;
        this.buffer[this.pos++] = (byte)b;
        this.dumpBuffer(true);
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        if (length >= this.buffer.length) {
            this.dumpBuffer(false);
            this.os.write(b, offset, length);
            return;
        }
        if (length <= this.avail) {
            System.arraycopy(b, offset, this.buffer, this.pos, length);
            this.pos += length;
            this.avail -= length;
            this.dumpBuffer(true);
            return;
        }
        this.dumpBuffer(false);
        System.arraycopy(b, offset, this.buffer, 0, length);
        this.pos = length;
        this.avail -= length;
    }

    @Override
    public void flush() throws IOException {
        this.dumpBuffer(false);
        this.os.flush();
    }

    @Override
    public void close() throws IOException {
        if (this.os == null) {
            return;
        }
        this.flush();
        if (this.os != System.out) {
            this.os.close();
        }
        this.os = null;
        this.buffer = null;
    }

    @Override
    public long position() throws IOException {
        if (this.repositionableStream != null) {
            return this.repositionableStream.position() + (long)this.pos;
        }
        if (this.measurableStream != null) {
            return this.measurableStream.position() + (long)this.pos;
        }
        if (this.fileChannel != null) {
            return this.fileChannel.position() + (long)this.pos;
        }
        throw new UnsupportedOperationException("position() can only be called if the underlying byte stream implements the MeasurableStream or RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
    }

    @Override
    public void position(long newPosition) throws IOException {
        this.flush();
        if (this.repositionableStream != null) {
            this.repositionableStream.position(newPosition);
        } else if (this.fileChannel != null) {
            this.fileChannel.position(newPosition);
        } else {
            throw new UnsupportedOperationException("position() can only be called if the underlying byte stream implements the RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
        }
    }

    @Override
    public long length() throws IOException {
        this.flush();
        if (this.measurableStream != null) {
            return this.measurableStream.length();
        }
        if (this.fileChannel != null) {
            return this.fileChannel.size();
        }
        throw new UnsupportedOperationException();
    }
}

