/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.SaslEncodingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslServer;

public class SaslInputStream
extends InputStream {
    private static final String NAME = "SaslOutputStream";
    private static final String ERROR = "ERROR";
    private static final String WARN = " WARN";
    private static final String TRACE = "DEBUG";
    private static final boolean DEBUG = true;
    private static final int debuglevel = 3;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private SaslClient client;
    private SaslServer server;
    private int maxRawSendSize;
    private InputStream source;
    private byte[] internalBuf;

    private static final void debug(String level, Object obj) {
        err.println("[" + level + "] SaslOutputStream: " + String.valueOf(obj));
    }

    public int available() throws IOException {
        int n = 0;
        if (this.internalBuf != null) {
            n = this.internalBuf.length;
        }
        return n;
    }

    public void close() throws IOException {
        this.source.close();
    }

    public int read() throws IOException {
        int result = -1;
        if (this.internalBuf != null && this.internalBuf.length > 0) {
            result = this.internalBuf[0] & 255;
            if (this.internalBuf.length == 1) {
                this.internalBuf = new byte[0];
            } else {
                byte[] tmp = new byte[this.internalBuf.length - 1];
                System.arraycopy(this.internalBuf, 1, tmp, 0, tmp.length);
                this.internalBuf = tmp;
            }
        } else {
            byte[] buf = new byte[1];
            int check = this.read(buf);
            result = check > 0 ? buf[0] & 255 : -1;
        }
        return result;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException("b");
        }
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException("off=" + String.valueOf(off) + ", len=" + String.valueOf(len) + ", b.length=" + String.valueOf(b.length));
        }
        if (len == 0) {
            return 0;
        }
        int result = 0;
        if (this.internalBuf == null || this.internalBuf.length < 1) {
            try {
                this.internalBuf = this.readSaslBuffer();
                if (this.internalBuf == null) {
                    return -1;
                }
            }
            catch (InterruptedIOException x) {
                return -1;
            }
        }
        if (len <= this.internalBuf.length) {
            result = len;
            System.arraycopy(this.internalBuf, 0, b, off, len);
            if (len == this.internalBuf.length) {
                this.internalBuf = null;
            } else {
                byte[] tmp = new byte[this.internalBuf.length - len];
                System.arraycopy(this.internalBuf, len, tmp, 0, tmp.length);
                this.internalBuf = tmp;
            }
        } else {
            result = this.internalBuf.length;
            System.arraycopy(this.internalBuf, 0, b, off, result);
            this.internalBuf = null;
            off += result;
            while ((len -= result) > 0) {
                byte[] data;
                if (this.source.available() <= 3 || (data = this.readSaslBuffer()) == null) break;
                int datalen = data.length;
                int n = 0;
                if (datalen > len) {
                    n = datalen - len;
                }
                int remaining = n;
                int delta = datalen - remaining;
                System.arraycopy(data, 0, b, off, delta);
                if (remaining > 0) {
                    this.internalBuf = new byte[remaining];
                    System.arraycopy(data, delta, this.internalBuf, 0, remaining);
                }
                off += delta;
                result += delta;
                len -= delta;
            }
        }
        return result;
    }

    private final byte[] readSaslBuffer() throws IOException {
        int realLength;
        byte[] result = new byte[4];
        try {
            realLength = this.source.read(result);
            if (realLength == -1) {
                return null;
            }
        }
        catch (IOException x) {
            SaslInputStream.debug(ERROR, x);
            throw x;
        }
        if (realLength != 4) {
            throw new IOException("Was expecting 4 but found " + String.valueOf(realLength));
        }
        int bufferLength = result[0] << 24 | (result[1] & 255) << 16 | (result[2] & 255) << 8 | result[3] & 255;
        if (bufferLength > this.maxRawSendSize || bufferLength < 0) {
            throw new SaslEncodingException("SASL buffer (security layer) too long");
        }
        result = new byte[bufferLength];
        try {
            realLength = this.source.read(result);
        }
        catch (IOException x) {
            SaslInputStream.debug(ERROR, x);
            throw x;
        }
        if (realLength != bufferLength) {
            throw new IOException("Was expecting " + String.valueOf(bufferLength) + " but found " + String.valueOf(realLength));
        }
        result = this.client != null ? this.client.unwrap(result, 0, realLength) : this.server.unwrap(result, 0, realLength);
        return result;
    }

    public SaslInputStream(SaslClient client, InputStream source) throws IOException {
        this.client = client;
        this.maxRawSendSize = Integer.parseInt((String)client.getNegotiatedProperty("javax.security.sasl.rawsendsize"));
        this.server = null;
        this.source = source;
    }

    public SaslInputStream(SaslServer server, InputStream source) throws IOException {
        this.server = server;
        this.maxRawSendSize = Integer.parseInt((String)server.getNegotiatedProperty("javax.security.sasl.rawsendsize"));
        this.client = null;
        this.source = source;
    }
}

