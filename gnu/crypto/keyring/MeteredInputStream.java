/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MeteredInputStream
extends FilterInputStream {
    private int count;
    private final int limit;

    public final boolean limitReached() {
        boolean bl = false;
        if (this.count == this.limit) {
            bl = true;
        }
        return bl;
    }

    public final int available() throws IOException {
        return Math.min(this.in.available(), this.limit - this.count);
    }

    public final void close() throws IOException {
        this.in.close();
    }

    public final void mark(int readLimit) {
    }

    public final boolean markSupported() {
        return false;
    }

    public final int read() throws IOException {
        if (this.limitReached()) {
            return -1;
        }
        int i = this.in.read();
        if (i != -1) {
            ++this.count;
        }
        return i;
    }

    public final int read(byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    public final int read(byte[] buf, int off, int len) throws IOException {
        if (this.limitReached()) {
            return -1;
        }
        int i = this.in.read(buf, off, Math.min(len, this.limit - this.count));
        if (i != -1) {
            this.count += i;
        }
        return i;
    }

    public final void reset() throws IOException {
    }

    public final long skip(long len) throws IOException {
        if (this.limitReached()) {
            return 0L;
        }
        len = Math.min(len, (long)(this.limit - this.count));
        len = this.in.skip(len);
        this.count += (int)len;
        return len;
    }

    MeteredInputStream(InputStream in, int limit) {
        super(in);
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be nonnegative");
        }
        this.limit = limit;
        this.count = 0;
    }
}

