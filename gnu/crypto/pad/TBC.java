/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.pad;

import gnu.crypto.pad.BasePad;
import gnu.crypto.pad.WrongPaddingException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public final class TBC
extends BasePad {
    private static final String NAME = "tbc";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 9;
    private static final PrintWriter err = new PrintWriter(System.out, true);

    private static final void debug(String s) {
        err.println(">>> tbc: " + s);
    }

    public final void setup() {
        if (this.blockSize < 1 || this.blockSize > 256) {
            throw new IllegalArgumentException();
        }
    }

    public final byte[] pad(byte[] in, int offset, int length) {
        int padLength = this.blockSize;
        if (length % this.blockSize != 0) {
            padLength = this.blockSize - length % this.blockSize;
        }
        byte[] result = new byte[padLength];
        int lastBit = in[offset + length - 1] & 1;
        if (lastBit == 0) {
            int i = 0;
            while (i < padLength) {
                result[i++] = 1;
            }
        }
        return result;
    }

    public final int unpad(byte[] in, int offset, int length) throws WrongPaddingException {
        int limit = offset + length - 1;
        int lastBit = in[limit] & 255;
        int result = 0;
        while (lastBit == (in[limit] & 255)) {
            ++result;
            --limit;
        }
        if (result > length) {
            throw new WrongPaddingException();
        }
        return result;
    }

    TBC() {
        super(NAME);
    }
}

