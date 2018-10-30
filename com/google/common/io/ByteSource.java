/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.MultiInputStream;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

@GwtIncompatible
public abstract class ByteSource {
    protected ByteSource() {
    }

    public CharSource asCharSource(Charset charset) {
        return new AsCharSource(charset);
    }

    public abstract InputStream openStream() throws IOException;

    public InputStream openBufferedStream() throws IOException {
        InputStream in = this.openStream();
        return in instanceof BufferedInputStream ? (BufferedInputStream)in : new BufferedInputStream(in);
    }

    public ByteSource slice(long offset, long length) {
        return new SlicedByteSource(offset, length);
    }

    public boolean isEmpty() throws IOException {
        Optional<Long> sizeIfKnown = this.sizeIfKnown();
        if (sizeIfKnown.isPresent()) {
            return sizeIfKnown.get() == 0L;
        }
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            boolean bl = in.read() == -1;
            return bl;
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }

    @Beta
    public Optional<Long> sizeIfKnown() {
        return Optional.absent();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long size() throws IOException {
        Closer closer;
        block10 : {
            Optional<Long> sizeIfKnown = this.sizeIfKnown();
            if (sizeIfKnown.isPresent()) {
                return sizeIfKnown.get();
            }
            closer = Closer.create();
            try {
                InputStream in = closer.register(this.openStream());
                long l = this.countBySkipping(in);
                return l;
            }
            catch (IOException in) {}
            break block10;
            finally {
                closer.close();
            }
        }
        closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            long l = ByteStreams.exhaust(in);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }

    private long countBySkipping(InputStream in) throws IOException {
        long skipped;
        long count = 0L;
        while ((skipped = ByteStreams.skipUpTo(in, Integer.MAX_VALUE)) > 0L) {
            count += skipped;
        }
        return count;
    }

    @CanIgnoreReturnValue
    public long copyTo(OutputStream output) throws IOException {
        Preconditions.checkNotNull(output);
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            long l = ByteStreams.copy(in, output);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }

    @CanIgnoreReturnValue
    public long copyTo(ByteSink sink) throws IOException {
        Preconditions.checkNotNull(sink);
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            OutputStream out = closer.register(sink.openStream());
            long l = ByteStreams.copy(in, out);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }

    public byte[] read() throws IOException {
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            byte[] arrby = ByteStreams.toByteArray(in);
            return arrby;
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }

    @Beta
    @CanIgnoreReturnValue
    public <T> T read(ByteProcessor<T> processor) throws IOException {
        Preconditions.checkNotNull(processor);
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            T t = ByteStreams.readBytes(in, processor);
            return t;
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }

    public HashCode hash(HashFunction hashFunction) throws IOException {
        Hasher hasher = hashFunction.newHasher();
        this.copyTo(Funnels.asOutputStream(hasher));
        return hasher.hash();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean contentEquals(ByteSource other) throws IOException {
        Preconditions.checkNotNull(other);
        byte[] buf1 = ByteStreams.createBuffer();
        byte[] buf2 = ByteStreams.createBuffer();
        Closer closer = Closer.create();
        try {
            InputStream in1 = closer.register(this.openStream());
            InputStream in2 = closer.register(other.openStream());
            do {
                int read1;
                int read2;
                if ((read1 = ByteStreams.read(in1, buf1, 0, buf1.length)) != (read2 = ByteStreams.read(in2, buf2, 0, buf2.length)) || !Arrays.equals(buf1, buf2)) {
                    boolean bl = false;
                    return bl;
                }
                if (read1 != buf1.length) {
                    boolean bl = true;
                    return bl;
                }
                continue;
                break;
            } while (true);
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }

    public static ByteSource concat(Iterable<? extends ByteSource> sources) {
        return new ConcatenatedByteSource(sources);
    }

    public static ByteSource concat(Iterator<? extends ByteSource> sources) {
        return ByteSource.concat(ImmutableList.copyOf(sources));
    }

    public static /* varargs */ ByteSource concat(ByteSource ... sources) {
        return ByteSource.concat(ImmutableList.copyOf(sources));
    }

    public static ByteSource wrap(byte[] b) {
        return new ByteArrayByteSource(b);
    }

    public static ByteSource empty() {
        return EmptyByteSource.INSTANCE;
    }

    private static final class ConcatenatedByteSource
    extends ByteSource {
        final Iterable<? extends ByteSource> sources;

        ConcatenatedByteSource(Iterable<? extends ByteSource> sources) {
            this.sources = Preconditions.checkNotNull(sources);
        }

        @Override
        public InputStream openStream() throws IOException {
            return new MultiInputStream(this.sources.iterator());
        }

        @Override
        public boolean isEmpty() throws IOException {
            for (ByteSource source : this.sources) {
                if (source.isEmpty()) continue;
                return false;
            }
            return true;
        }

        @Override
        public Optional<Long> sizeIfKnown() {
            long result = 0L;
            for (ByteSource source : this.sources) {
                Optional<Long> sizeIfKnown = source.sizeIfKnown();
                if (!sizeIfKnown.isPresent()) {
                    return Optional.absent();
                }
                result += sizeIfKnown.get().longValue();
            }
            return Optional.of(result);
        }

        @Override
        public long size() throws IOException {
            long result = 0L;
            for (ByteSource source : this.sources) {
                result += source.size();
            }
            return result;
        }

        public String toString() {
            return "ByteSource.concat(" + this.sources + ")";
        }
    }

    private static final class EmptyByteSource
    extends ByteArrayByteSource {
        static final EmptyByteSource INSTANCE = new EmptyByteSource();

        EmptyByteSource() {
            super(new byte[0]);
        }

        @Override
        public CharSource asCharSource(Charset charset) {
            Preconditions.checkNotNull(charset);
            return CharSource.empty();
        }

        @Override
        public byte[] read() {
            return this.bytes;
        }

        @Override
        public String toString() {
            return "ByteSource.empty()";
        }
    }

    private static class ByteArrayByteSource
    extends ByteSource {
        final byte[] bytes;
        final int offset;
        final int length;

        ByteArrayByteSource(byte[] bytes) {
            this(bytes, 0, bytes.length);
        }

        ByteArrayByteSource(byte[] bytes, int offset, int length) {
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public InputStream openStream() {
            return new ByteArrayInputStream(this.bytes, this.offset, this.length);
        }

        @Override
        public InputStream openBufferedStream() throws IOException {
            return this.openStream();
        }

        @Override
        public boolean isEmpty() {
            return this.length == 0;
        }

        @Override
        public long size() {
            return this.length;
        }

        @Override
        public Optional<Long> sizeIfKnown() {
            return Optional.of(Long.valueOf(this.length));
        }

        @Override
        public byte[] read() {
            return Arrays.copyOfRange(this.bytes, this.offset, this.offset + this.length);
        }

        @Override
        public long copyTo(OutputStream output) throws IOException {
            output.write(this.bytes, this.offset, this.length);
            return this.length;
        }

        @Override
        public <T> T read(ByteProcessor<T> processor) throws IOException {
            processor.processBytes(this.bytes, this.offset, this.length);
            return processor.getResult();
        }

        @Override
        public HashCode hash(HashFunction hashFunction) throws IOException {
            return hashFunction.hashBytes(this.bytes, this.offset, this.length);
        }

        @Override
        public ByteSource slice(long offset, long length) {
            Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", offset);
            Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", length);
            offset = Math.min(offset, (long)this.length);
            length = Math.min(length, (long)this.length - offset);
            int newOffset = this.offset + (int)offset;
            return new ByteArrayByteSource(this.bytes, newOffset, (int)length);
        }

        public String toString() {
            return "ByteSource.wrap(" + Ascii.truncate(BaseEncoding.base16().encode(this.bytes, this.offset, this.length), 30, "...") + ")";
        }
    }

    private final class SlicedByteSource
    extends ByteSource {
        final long offset;
        final long length;

        SlicedByteSource(long offset, long length) {
            Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", offset);
            Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", length);
            this.offset = offset;
            this.length = length;
        }

        @Override
        public InputStream openStream() throws IOException {
            return this.sliceStream(ByteSource.this.openStream());
        }

        @Override
        public InputStream openBufferedStream() throws IOException {
            return this.sliceStream(ByteSource.this.openBufferedStream());
        }

        private InputStream sliceStream(InputStream in) throws IOException {
            if (this.offset > 0L) {
                long skipped;
                try {
                    skipped = ByteStreams.skipUpTo(in, this.offset);
                }
                catch (Throwable e) {
                    Closer closer = Closer.create();
                    closer.register(in);
                    try {
                        throw closer.rethrow(e);
                    }
                    catch (Throwable throwable) {
                        closer.close();
                        throw throwable;
                    }
                }
                if (skipped < this.offset) {
                    in.close();
                    return new ByteArrayInputStream(new byte[0]);
                }
            }
            return ByteStreams.limit(in, this.length);
        }

        @Override
        public ByteSource slice(long offset, long length) {
            Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", offset);
            Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", length);
            long maxLength = this.length - offset;
            return ByteSource.this.slice(this.offset + offset, Math.min(length, maxLength));
        }

        @Override
        public boolean isEmpty() throws IOException {
            return this.length == 0L || super.isEmpty();
        }

        @Override
        public Optional<Long> sizeIfKnown() {
            Optional<Long> optionalUnslicedSize = ByteSource.this.sizeIfKnown();
            if (optionalUnslicedSize.isPresent()) {
                long unslicedSize = optionalUnslicedSize.get();
                long off = Math.min(this.offset, unslicedSize);
                return Optional.of(Math.min(this.length, unslicedSize - off));
            }
            return Optional.absent();
        }

        public String toString() {
            return ByteSource.this.toString() + ".slice(" + this.offset + ", " + this.length + ")";
        }
    }

    class AsCharSource
    extends CharSource {
        final Charset charset;

        AsCharSource(Charset charset) {
            this.charset = Preconditions.checkNotNull(charset);
        }

        @Override
        public ByteSource asByteSource(Charset charset) {
            if (charset.equals(this.charset)) {
                return ByteSource.this;
            }
            return super.asByteSource(charset);
        }

        @Override
        public Reader openStream() throws IOException {
            return new InputStreamReader(ByteSource.this.openStream(), this.charset);
        }

        @Override
        public String read() throws IOException {
            return new String(ByteSource.this.read(), this.charset);
        }

        public String toString() {
            return ByteSource.this.toString() + ".asCharSource(" + this.charset + ")";
        }
    }

}

