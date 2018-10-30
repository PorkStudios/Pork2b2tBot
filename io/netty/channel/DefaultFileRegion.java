/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.FileRegion;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class DefaultFileRegion
extends AbstractReferenceCounted
implements FileRegion {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
    private final File f;
    private final long position;
    private final long count;
    private long transferred;
    private FileChannel file;

    public DefaultFileRegion(FileChannel file, long position, long count) {
        if (file == null) {
            throw new NullPointerException("file");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position must be >= 0 but was " + position);
        }
        if (count < 0L) {
            throw new IllegalArgumentException("count must be >= 0 but was " + count);
        }
        this.file = file;
        this.position = position;
        this.count = count;
        this.f = null;
    }

    public DefaultFileRegion(File f, long position, long count) {
        if (f == null) {
            throw new NullPointerException("f");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position must be >= 0 but was " + position);
        }
        if (count < 0L) {
            throw new IllegalArgumentException("count must be >= 0 but was " + count);
        }
        this.position = position;
        this.count = count;
        this.f = f;
    }

    public boolean isOpen() {
        return this.file != null;
    }

    public void open() throws IOException {
        if (!this.isOpen() && this.refCnt() > 0) {
            this.file = new RandomAccessFile(this.f, "r").getChannel();
        }
    }

    @Override
    public long position() {
        return this.position;
    }

    @Override
    public long count() {
        return this.count;
    }

    @Deprecated
    @Override
    public long transfered() {
        return this.transferred;
    }

    @Override
    public long transferred() {
        return this.transferred;
    }

    @Override
    public long transferTo(WritableByteChannel target, long position) throws IOException {
        long count = this.count - position;
        if (count < 0L || position < 0L) {
            throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')');
        }
        if (count == 0L) {
            return 0L;
        }
        if (this.refCnt() == 0) {
            throw new IllegalReferenceCountException(0);
        }
        this.open();
        long written = this.file.transferTo(this.position + position, count, target);
        if (written > 0L) {
            this.transferred += written;
        }
        return written;
    }

    @Override
    protected void deallocate() {
        block3 : {
            FileChannel file = this.file;
            if (file == null) {
                return;
            }
            this.file = null;
            try {
                file.close();
            }
            catch (IOException e) {
                if (!logger.isWarnEnabled()) break block3;
                logger.warn("Failed to close a file.", e);
            }
        }
    }

    @Override
    public FileRegion retain() {
        super.retain();
        return this;
    }

    @Override
    public FileRegion retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public FileRegion touch() {
        return this;
    }

    @Override
    public FileRegion touch(Object hint) {
        return this;
    }
}

