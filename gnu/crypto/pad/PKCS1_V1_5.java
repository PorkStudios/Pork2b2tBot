/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.pad;

import gnu.crypto.pad.BasePad;
import gnu.crypto.pad.WrongPaddingException;
import gnu.crypto.sig.rsa.EME_PKCS1_V1_5;
import gnu.crypto.util.PRNG;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class PKCS1_V1_5
extends BasePad {
    private static final String NAME = "eme-pkcs1-v1.5";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 9;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private EME_PKCS1_V1_5 codec;

    private static final void debug(String s) {
        err.println(">>> eme-pkcs1-v1.5: " + s);
    }

    public void setup() {
        this.codec = EME_PKCS1_V1_5.getInstance(this.blockSize);
    }

    public byte[] pad(byte[] in, int offset, int length) {
        byte[] M = new byte[length];
        System.arraycopy(in, offset, M, 0, length);
        byte[] EM = this.codec.encode(M);
        byte[] result = new byte[this.blockSize - length];
        System.arraycopy(EM, 0, result, 0, result.length);
        return result;
    }

    public int unpad(byte[] in, int offset, int length) throws WrongPaddingException {
        byte[] EM = new byte[length];
        System.arraycopy(in, offset, EM, 0, length);
        int result = length - this.codec.decode(EM).length;
        return result;
    }

    public boolean selfTest() {
        int[] mLen = new int[]{16, 20, 32, 48, 64};
        byte[] M = new byte[mLen[mLen.length - 1]];
        PRNG.nextBytes(M);
        byte[] EM = new byte[1024];
        int bs = 256;
        while (bs < 1025) {
            this.init(bs);
            int i = 0;
            while (i < mLen.length) {
                int j = mLen[i];
                byte[] p = this.pad(M, 0, j);
                if (j + p.length != this.blockSize) {
                    new RuntimeException(this.name()).printStackTrace(System.err);
                    return false;
                }
                System.arraycopy(p, 0, EM, 0, p.length);
                System.arraycopy(M, 0, EM, p.length, j);
                try {
                    if (p.length != this.unpad(EM, 0, this.blockSize)) {
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
            bs += 256;
        }
        return true;
    }

    PKCS1_V1_5() {
        super(NAME);
    }
}

