/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.mac.IMac;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MacInputStream
extends FilterInputStream {
    private boolean digesting;
    private IMac mac;

    public IMac getMac() {
        return this.mac;
    }

    public void setMac(IMac mac) {
        if (mac == null) {
            throw new NullPointerException();
        }
        this.mac = mac;
    }

    public void on(boolean flag) {
        this.digesting = flag;
    }

    public int read() throws IOException {
        int i = this.in.read();
        if (this.digesting && i != -1) {
            this.mac.update((byte)i);
        }
        return i;
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        int i = this.in.read(buf, off, len);
        if (this.digesting && i != -1) {
            this.mac.update(buf, off, i);
        }
        return i;
    }

    public MacInputStream(InputStream in, IMac mac) {
        super(in);
        if (mac == null) {
            throw new NullPointerException();
        }
        this.mac = mac;
        this.digesting = true;
    }
}

