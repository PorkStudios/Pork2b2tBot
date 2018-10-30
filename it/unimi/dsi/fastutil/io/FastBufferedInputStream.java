/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.io.MeasurableInputStream;
import it.unimi.dsi.fastutil.io.MeasurableStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.EnumSet;

public class FastBufferedInputStream
extends MeasurableInputStream
implements RepositionableStream {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final EnumSet<LineTerminator> ALL_TERMINATORS = EnumSet.allOf(LineTerminator.class);
    protected InputStream is;
    protected byte[] buffer;
    protected int pos;
    protected long readBytes;
    protected int avail;
    private FileChannel fileChannel;
    private RepositionableStream repositionableStream;
    private MeasurableStream measurableStream;

    private static int ensureBufferSize(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Illegal buffer size: " + bufferSize);
        }
        return bufferSize;
    }

    public FastBufferedInputStream(InputStream is, byte[] buffer) {
        this.is = is;
        FastBufferedInputStream.ensureBufferSize(buffer.length);
        this.buffer = buffer;
        if (is instanceof RepositionableStream) {
            this.repositionableStream = (RepositionableStream)((Object)is);
        }
        if (is instanceof MeasurableStream) {
            this.measurableStream = (MeasurableStream)((Object)is);
        }
        if (this.repositionableStream == null) {
            try {
                this.fileChannel = (FileChannel)is.getClass().getMethod("getChannel", new Class[0]).invoke(is, new Object[0]);
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

    public FastBufferedInputStream(InputStream is, int bufferSize) {
        this(is, new byte[FastBufferedInputStream.ensureBufferSize(bufferSize)]);
    }

    public FastBufferedInputStream(InputStream is) {
        this(is, 8192);
    }

    protected boolean noMoreCharacters() throws IOException {
        if (this.avail == 0) {
            this.avail = this.is.read(this.buffer);
            if (this.avail <= 0) {
                this.avail = 0;
                return true;
            }
            this.pos = 0;
        }
        return false;
    }

    @Override
    public int read() throws IOException {
        if (this.noMoreCharacters()) {
            return -1;
        }
        --this.avail;
        ++this.readBytes;
        return this.buffer[this.pos++] & 255;
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        if (length <= this.avail) {
            System.arraycopy(this.buffer, this.pos, b, offset, length);
            this.pos += length;
            this.avail -= length;
            this.readBytes += (long)length;
            return length;
        }
        int head = this.avail;
        System.arraycopy(this.buffer, this.pos, b, offset, head);
        this.avail = 0;
        this.pos = 0;
        this.readBytes += (long)head;
        if (length > this.buffer.length) {
            int result = this.is.read(b, offset + head, length - head);
            if (result > 0) {
                this.readBytes += (long)result;
            }
            return result < 0 ? (head == 0 ? -1 : head) : result + head;
        }
        if (this.noMoreCharacters()) {
            return head == 0 ? -1 : head;
        }
        int toRead = Math.min(length - head, this.avail);
        this.readBytes += (long)toRead;
        System.arraycopy(this.buffer, 0, b, offset + head, toRead);
        this.pos = toRead;
        this.avail -= toRead;
        return toRead + head;
    }

    public int readLine(byte[] array) throws IOException {
        return this.readLine(array, 0, array.length, ALL_TERMINATORS);
    }

    public int readLine(byte[] array, EnumSet<LineTerminator> terminators) throws IOException {
        return this.readLine(array, 0, array.length, terminators);
    }

    public int readLine(byte[] array, int off, int len) throws IOException {
        return this.readLine(array, off, len, ALL_TERMINATORS);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public int readLine(byte[] array, int off, int len, EnumSet<LineTerminator> terminators) throws IOException {
        ByteArrays.ensureOffsetLength(array, off, len);
        if (len == 0) {
            return 0;
        }
        if (this.noMoreCharacters()) {
            return -1;
        }
        k = 0;
        remaining = len;
        read = 0;
        do lbl-1000: // 6 sources:
        {
            for (i = 0; i < this.avail && i < remaining && (k = this.buffer[this.pos + i]) != 10 && k != 13; ++i) {
            }
            System.arraycopy(this.buffer, this.pos, array, off + read, i);
            this.pos += i;
            this.avail -= i;
            read += i;
            if ((remaining -= i) == 0) {
                this.readBytes += (long)read;
                return read;
            }
            if (this.avail > 0) {
                if (k == 10) {
                    ++this.pos;
                    --this.avail;
                    if (terminators.contains((Object)LineTerminator.LF)) {
                        this.readBytes += (long)(read + 1);
                        return read;
                    }
                    array[off + read++] = 10;
                    --remaining;
                    ** continue;
                }
                if (k != 13) ** continue;
                ++this.pos;
                --this.avail;
                if (terminators.contains((Object)LineTerminator.CR_LF)) {
                    if (this.avail > 0) {
                        if (this.buffer[this.pos] == 10) {
                            ++this.pos;
                            --this.avail;
                            this.readBytes += (long)(read + 2);
                            return read;
                        }
                    } else {
                        if (this.noMoreCharacters()) {
                            if (!terminators.contains((Object)LineTerminator.CR)) {
                                array[off + read++] = 13;
                                --remaining;
                                this.readBytes += (long)read;
                                return read;
                            }
                            this.readBytes += (long)(read + 1);
                            return read;
                        }
                        if (this.buffer[0] == 10) {
                            ++this.pos;
                            --this.avail;
                            this.readBytes += (long)(read + 2);
                            return read;
                        }
                    }
                }
                if (terminators.contains((Object)LineTerminator.CR)) {
                    this.readBytes += (long)(read + 1);
                    return read;
                }
                array[off + read++] = 13;
                --remaining;
                ** continue;
            }
            if (this.noMoreCharacters()) break;
        } while (true);
        this.readBytes += (long)read;
        return read;
    }

    @Override
    public void position(long newPosition) throws IOException {
        long position = this.readBytes;
        if (newPosition <= position + (long)this.avail && newPosition >= position - (long)this.pos) {
            this.pos = (int)((long)this.pos + (newPosition - position));
            this.avail = (int)((long)this.avail - (newPosition - position));
            this.readBytes = newPosition;
            return;
        }
        if (this.repositionableStream != null) {
            this.repositionableStream.position(newPosition);
        } else if (this.fileChannel != null) {
            this.fileChannel.position(newPosition);
        } else {
            throw new UnsupportedOperationException("position() can only be called if the underlying byte stream implements the RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
        }
        this.readBytes = newPosition;
        this.pos = 0;
        this.avail = 0;
    }

    @Override
    public long position() throws IOException {
        return this.readBytes;
    }

    @Override
    public long length() throws IOException {
        if (this.measurableStream != null) {
            return this.measurableStream.length();
        }
        if (this.fileChannel != null) {
            return this.fileChannel.size();
        }
        throw new UnsupportedOperationException();
    }

    private long skipByReading(long n) throws IOException {
        long toSkip;
        int len;
        for (toSkip = n; toSkip > 0L && (len = this.is.read(this.buffer, 0, (int)Math.min((long)this.buffer.length, toSkip))) > 0; toSkip -= (long)len) {
        }
        return n - toSkip;
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= (long)this.avail) {
            int m = (int)n;
            this.pos += m;
            this.avail -= m;
            this.readBytes += n;
            return n;
        }
        long toSkip = n - (long)this.avail;
        long result = 0L;
        this.avail = 0;
        while (toSkip != 0L && (result = this.is == System.in ? this.skipByReading(toSkip) : this.is.skip(toSkip)) < toSkip) {
            if (result == 0L) {
                if (this.is.read() == -1) break;
                --toSkip;
                continue;
            }
            toSkip -= result;
        }
        long t = n - (toSkip - result);
        this.readBytes += t;
        return t;
    }

    @Override
    public int available() throws IOException {
        return (int)Math.min((long)this.is.available() + (long)this.avail, Integer.MAX_VALUE);
    }

    @Override
    public void close() throws IOException {
        if (this.is == null) {
            return;
        }
        if (this.is != System.in) {
            this.is.close();
        }
        this.is = null;
        this.buffer = null;
    }

    public void flush() {
        if (this.is == null) {
            return;
        }
        this.readBytes += (long)this.avail;
        this.pos = 0;
        this.avail = 0;
    }

    @Deprecated
    @Override
    public void reset() {
        this.flush();
    }

    public static enum LineTerminator {
        CR,
        LF,
        CR_LF;
        

        private LineTerminator() {
        }
    }

}

