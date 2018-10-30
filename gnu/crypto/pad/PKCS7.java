/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.pad;

import gnu.crypto.pad.BasePad;
import gnu.crypto.pad.WrongPaddingException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public final class PKCS7
extends BasePad {
    private static final String NAME = "pkcs7";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 9;
    private static final PrintWriter err = new PrintWriter(System.out, true);

    private static final void debug(String s) {
        err.println(">>> pkcs7: " + s);
    }

    public final void setup() {
        if (this.blockSize < 2 || this.blockSize > 256) {
            throw new IllegalArgumentException();
        }
    }

    public final byte[] pad(byte[] in, int offset, int length) {
        int padLength = this.blockSize;
        if (length % this.blockSize != 0) {
            padLength = this.blockSize - length % this.blockSize;
        }
        byte[] result = new byte[padLength];
        int i = 0;
        while (i < padLength) {
            result[i++] = (byte)padLength;
        }
        return result;
    }

    public final int unpad(byte[] in, int offset, int length) throws WrongPaddingException {
        int limit = offset + length;
        int result = in[limit - 1] & 255;
        int i = 0;
        while (i < result) {
            if (result != (in[--limit] & 255)) {
                throw new WrongPaddingException();
            }
            ++i;
        }
        return result;
    }

    PKCS7() {
        super(NAME);
    }
}

