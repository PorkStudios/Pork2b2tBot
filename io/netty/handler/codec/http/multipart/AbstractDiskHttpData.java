/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractHttpData;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

public abstract class AbstractDiskHttpData
extends AbstractHttpData {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
    private File file;
    private boolean isRenamed;
    private FileChannel fileChannel;

    protected AbstractDiskHttpData(String name, Charset charset, long size) {
        super(name, charset, size);
    }

    protected abstract String getDiskFilename();

    protected abstract String getPrefix();

    protected abstract String getBaseDirectory();

    protected abstract String getPostfix();

    protected abstract boolean deleteOnExit();

    private File tempFile() throws IOException {
        String diskFilename = this.getDiskFilename();
        String newpostfix = diskFilename != null ? "" + '_' + diskFilename : this.getPostfix();
        File tmpFile = this.getBaseDirectory() == null ? File.createTempFile(this.getPrefix(), newpostfix) : File.createTempFile(this.getPrefix(), newpostfix, new File(this.getBaseDirectory()));
        if (this.deleteOnExit()) {
            tmpFile.deleteOnExit();
        }
        return tmpFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setContent(ByteBuf buffer) throws IOException {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        try {
            this.size = buffer.readableBytes();
            this.checkSize(this.size);
            if (this.definedSize > 0L && this.definedSize < this.size) {
                throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
            }
            if (this.file == null) {
                this.file = this.tempFile();
            }
            if (buffer.readableBytes() == 0) {
                if (!this.file.createNewFile()) {
                    throw new IOException("file exists already: " + this.file);
                }
                return;
            }
            FileOutputStream outputStream = new FileOutputStream(this.file);
            try {
                FileChannel localfileChannel = outputStream.getChannel();
                ByteBuffer byteBuffer = buffer.nioBuffer();
                int written = 0;
                while ((long)written < this.size) {
                    written += localfileChannel.write(byteBuffer);
                }
                buffer.readerIndex(buffer.readerIndex() + written);
                localfileChannel.force(false);
            }
            finally {
                outputStream.close();
            }
            this.setCompleted();
        }
        finally {
            buffer.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        if (buffer != null) {
            try {
                int localsize = buffer.readableBytes();
                this.checkSize(this.size + (long)localsize);
                if (this.definedSize > 0L && this.definedSize < this.size + (long)localsize) {
                    throw new IOException("Out of size: " + (this.size + (long)localsize) + " > " + this.definedSize);
                }
                ByteBuffer byteBuffer = buffer.nioBufferCount() == 1 ? buffer.nioBuffer() : buffer.copy().nioBuffer();
                int written = 0;
                if (this.file == null) {
                    this.file = this.tempFile();
                }
                if (this.fileChannel == null) {
                    FileOutputStream outputStream = new FileOutputStream(this.file);
                    this.fileChannel = outputStream.getChannel();
                }
                while (written < localsize) {
                    written += this.fileChannel.write(byteBuffer);
                }
                this.size += (long)localsize;
                buffer.readerIndex(buffer.readerIndex() + written);
            }
            finally {
                buffer.release();
            }
        }
        if (last) {
            if (this.file == null) {
                this.file = this.tempFile();
            }
            if (this.fileChannel == null) {
                FileOutputStream outputStream = new FileOutputStream(this.file);
                this.fileChannel = outputStream.getChannel();
            }
            this.fileChannel.force(false);
            this.fileChannel.close();
            this.fileChannel = null;
            this.setCompleted();
        } else if (buffer == null) {
            throw new NullPointerException("buffer");
        }
    }

    @Override
    public void setContent(File file) throws IOException {
        if (this.file != null) {
            this.delete();
        }
        this.file = file;
        this.size = file.length();
        this.checkSize(this.size);
        this.isRenamed = true;
        this.setCompleted();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setContent(InputStream inputStream) throws IOException {
        int written;
        if (inputStream == null) {
            throw new NullPointerException("inputStream");
        }
        if (this.file != null) {
            this.delete();
        }
        this.file = this.tempFile();
        FileOutputStream outputStream = new FileOutputStream(this.file);
        written = 0;
        try {
            FileChannel localfileChannel = outputStream.getChannel();
            byte[] bytes = new byte[16384];
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            int read = inputStream.read(bytes);
            while (read > 0) {
                byteBuffer.position(read).flip();
                this.checkSize(written += localfileChannel.write(byteBuffer));
                read = inputStream.read(bytes);
            }
            localfileChannel.force(false);
        }
        finally {
            outputStream.close();
        }
        this.size = written;
        if (this.definedSize > 0L && this.definedSize < this.size) {
            if (!this.file.delete()) {
                logger.warn("Failed to delete: {}", (Object)this.file);
            }
            this.file = null;
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
        }
        this.isRenamed = true;
        this.setCompleted();
    }

    @Override
    public void delete() {
        if (this.fileChannel != null) {
            try {
                this.fileChannel.force(false);
                this.fileChannel.close();
            }
            catch (IOException e) {
                logger.warn("Failed to close a file.", e);
            }
            this.fileChannel = null;
        }
        if (!this.isRenamed) {
            if (this.file != null && this.file.exists() && !this.file.delete()) {
                logger.warn("Failed to delete: {}", (Object)this.file);
            }
            this.file = null;
        }
    }

    @Override
    public byte[] get() throws IOException {
        if (this.file == null) {
            return EmptyArrays.EMPTY_BYTES;
        }
        return AbstractDiskHttpData.readFrom(this.file);
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        if (this.file == null) {
            return Unpooled.EMPTY_BUFFER;
        }
        byte[] array = AbstractDiskHttpData.readFrom(this.file);
        return Unpooled.wrappedBuffer(array);
    }

    @Override
    public ByteBuf getChunk(int length) throws IOException {
        int readnow;
        int read;
        if (this.file == null || length == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        if (this.fileChannel == null) {
            FileInputStream inputStream = new FileInputStream(this.file);
            this.fileChannel = inputStream.getChannel();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        for (read = 0; read < length; read += readnow) {
            readnow = this.fileChannel.read(byteBuffer);
            if (readnow != -1) continue;
            this.fileChannel.close();
            this.fileChannel = null;
            break;
        }
        if (read == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        byteBuffer.flip();
        ByteBuf buffer = Unpooled.wrappedBuffer(byteBuffer);
        buffer.readerIndex(0);
        buffer.writerIndex(read);
        return buffer;
    }

    @Override
    public String getString() throws IOException {
        return this.getString(HttpConstants.DEFAULT_CHARSET);
    }

    @Override
    public String getString(Charset encoding) throws IOException {
        if (this.file == null) {
            return "";
        }
        if (encoding == null) {
            byte[] array = AbstractDiskHttpData.readFrom(this.file);
            return new String(array, HttpConstants.DEFAULT_CHARSET.name());
        }
        byte[] array = AbstractDiskHttpData.readFrom(this.file);
        return new String(array, encoding.name());
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean renameTo(File dest) throws IOException {
        if (dest == null) {
            throw new NullPointerException("dest");
        }
        if (this.file == null) {
            throw new IOException("No file defined so cannot be renamed");
        }
        if (!this.file.renameTo(dest)) {
            long position;
            IOException exception;
            exception = null;
            FileInputStream inputStream = null;
            FileOutputStream outputStream = null;
            long chunkSize = 8196L;
            try {
                inputStream = new FileInputStream(this.file);
                outputStream = new FileOutputStream(dest);
                FileChannel in = inputStream.getChannel();
                FileChannel out = outputStream.getChannel();
                for (position = 0L; position < this.size; position += in.transferTo((long)position, (long)chunkSize, (WritableByteChannel)out)) {
                    if (chunkSize >= this.size - position) continue;
                    chunkSize = this.size - position;
                }
            }
            catch (IOException e) {
                exception = e;
            }
            finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (IOException e) {
                        if (exception == null) {
                            exception = e;
                        }
                        logger.warn("Multiple exceptions detected, the following will be suppressed {}", e);
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    }
                    catch (IOException e) {
                        if (exception == null) {
                            exception = e;
                        }
                        logger.warn("Multiple exceptions detected, the following will be suppressed {}", e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            if (position == this.size) {
                if (!this.file.delete()) {
                    logger.warn("Failed to delete: {}", (Object)this.file);
                }
                this.file = dest;
                this.isRenamed = true;
                return true;
            }
            if (!dest.delete()) {
                logger.warn("Failed to delete: {}", (Object)dest);
            }
            return false;
        }
        this.file = dest;
        this.isRenamed = true;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static byte[] readFrom(File src) throws IOException {
        byte[] array;
        long srcsize = src.length();
        if (srcsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File too big to be loaded in memory");
        }
        FileInputStream inputStream = new FileInputStream(src);
        array = new byte[(int)srcsize];
        try {
            FileChannel fileChannel = inputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(array);
            int read = 0;
            while ((long)read < srcsize) {
                read += fileChannel.read(byteBuffer);
            }
        }
        finally {
            inputStream.close();
        }
        return array;
    }

    @Override
    public File getFile() throws IOException {
        return this.file;
    }

    @Override
    public HttpData touch() {
        return this;
    }

    @Override
    public HttpData touch(Object hint) {
        return this;
    }
}

