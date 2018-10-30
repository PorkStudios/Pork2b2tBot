/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslServer;

public class SaslOutputStream
extends OutputStream {
    private static final String NAME = "SaslOutputStream";
    private static final String TRACE = "DEBUG";
    private static final boolean DEBUG = true;
    private static final int debuglevel = 3;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private SaslClient client;
    private SaslServer server;
    private int maxRawSendSize;
    private OutputStream dest;

    private static final void debug(String level, Object obj) {
        err.println("[" + level + "] SaslOutputStream: " + String.valueOf(obj));
    }

    public void close() throws IOException {
        this.dest.flush();
        this.dest.close();
    }

    public void flush() throws IOException {
        this.dest.flush();
    }

    public void write(int b) throws IOException {
        this.write(new byte[]{(byte)b});
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException("b");
        }
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException("off=" + String.valueOf(off) + ", len=" + String.valueOf(len) + ", b.length=" + String.valueOf(b.length));
        }
        if (len == 0) {
            return;
        }
        int chunck = 1;
        byte[] output = null;
        while (len > 0) {
            int chunckSize = len > this.maxRawSendSize ? this.maxRawSendSize : len;
            output = this.client != null ? this.client.wrap(b, off, chunckSize) : this.server.wrap(b, off, chunckSize);
            int length = output.length;
            byte[] result = new byte[length + 4];
            result[0] = (byte)(length >>> 24);
            result[1] = (byte)(length >>> 16);
            result[2] = (byte)(length >>> 8);
            result[3] = (byte)length;
            System.arraycopy(output, 0, result, 4, length);
            this.dest.write(result);
            off += chunckSize;
            len -= chunckSize;
            ++chunck;
        }
        this.dest.flush();
    }

    public SaslOutputStream(SaslClient client, OutputStream dest) throws IOException {
        this.client = client;
        this.maxRawSendSize = Integer.parseInt((String)client.getNegotiatedProperty("javax.security.sasl.rawsendsize"));
        this.server = null;
        this.dest = dest;
    }

    public SaslOutputStream(SaslServer server, OutputStream dest) throws IOException {
        this.server = server;
        this.maxRawSendSize = Integer.parseInt((String)server.getNegotiatedProperty("javax.security.sasl.rawsendsize"));
        this.client = null;
        this.dest = dest;
    }
}

