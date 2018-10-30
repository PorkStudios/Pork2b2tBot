/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.mac.IMac;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MacOutputStream
extends FilterOutputStream {
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

    public void write(int b) throws IOException {
        if (this.digesting) {
            this.mac.update((byte)b);
        }
        this.out.write(b);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        if (this.digesting) {
            this.mac.update(buf, off, len);
        }
        this.out.write(buf, off, len);
    }

    public MacOutputStream(OutputStream out, IMac mac) {
        super(out);
        if (mac == null) {
            throw new NullPointerException();
        }
        this.mac = mac;
        this.digesting = true;
    }
}

