/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.pad;

import gnu.crypto.pad.IPad;
import gnu.crypto.pad.WrongPaddingException;
import java.io.PrintStream;

public abstract class BasePad
implements IPad {
    protected String name;
    protected int blockSize;

    public String name() {
        StringBuffer sb = new StringBuffer(this.name);
        if (this.blockSize != -1) {
            sb.append('-').append(String.valueOf(8 * this.blockSize));
        }
        return sb.toString();
    }

    public void init(int bs) throws IllegalStateException {
        if (this.blockSize != -1) {
            throw new IllegalStateException();
        }
        this.blockSize = bs;
        this.setup();
    }

    public void reset() {
        this.blockSize = -1;
    }

    public boolean selfTest() {
        int offset = 5;
        int limit = 1024;
        byte[] in = new byte[1024];
        int bs = 2;
        while (bs < 256) {
            this.init(bs);
            int i = 0;
            while (i < 1019 - this.blockSize) {
                byte[] padBytes = this.pad(in, 5, i);
                if ((i + padBytes.length) % this.blockSize != 0) {
                    new RuntimeException(this.name()).printStackTrace(System.err);
                    return false;
                }
                System.arraycopy(padBytes, 0, in, 5 + i, padBytes.length);
                try {
                    if (padBytes.length != this.unpad(in, 5, i + padBytes.length)) {
                        new RuntimeException(this.name()).printStackTrace(System.err);
                        return false;
                    }
                }
                catch (WrongPaddingException x) {
                    x.printStackTrace(System.err);
                    return false;
                }
                ++i;
            }
            this.reset();
            ++bs;
        }
        return true;
    }

    public abstract void setup();

    public abstract byte[] pad(byte[] var1, int var2, int var3);

    public abstract int unpad(byte[] var1, int var2, int var3) throws WrongPaddingException;

    protected BasePad(String name) {
        this.name = name;
        this.blockSize = -1;
    }
}

